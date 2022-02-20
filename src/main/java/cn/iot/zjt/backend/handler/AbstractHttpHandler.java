package cn.iot.zjt.backend.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.iot.zjt.backend.component.Config;
import cn.iot.zjt.backend.component.Token;
import cn.iot.zjt.backend.handler.annotation.EndPoint;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

/**
 * Base class of all handlers.
 *
 * @version 2022/02/20
 */
public abstract class AbstractHttpHandler {

  private static final Logger logger = LogManager.getLogger(AbstractHttpHandler.class);

  /* Reference to Vert.x instance */
  protected final Vertx vertx;

  /* Reference to global configuration */
  protected final JsonObject config;

  /**
   * Abstract constructor (only called by sub-class instances).
   * Set a reference to the global configuration and Vert.x instance.
   * 
   * @param vertx
   * @param config
   */
  public AbstractHttpHandler(final Vertx vertx, final JsonObject config) {
    this.config = config;
    this.vertx = vertx;
  }

  public AbstractHttpHandler() {
    this.config = null;
    this.vertx = null;
  }

  private static final Set<HttpMethod> preflightMethods = Set.of(
    HttpMethod.PUT,
    HttpMethod.DELETE
  );

  private CorsHandler createCorsHandler() {
    return CorsHandler
      .create(".*.")
      .allowedHeader("X-Requested-With")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Origin")
      .allowedHeader("Content-Type")
      .allowedHeader("Accept")
      .allowedHeader("Authorization")
      .allowCredentials(true);
  }

  /**
   * Register current handler (and also some other assistant handlers)
   * on the router. The handler information is extracted from annotations
   * in sub-handler's class definition.
   *
   * @param router The Vert.x router to be registered on.
   */
  public void register(final Router router) {
    Class<? extends AbstractHttpHandler> handlerType = this.getClass();
    String   routePath          =  handlerType.getAnnotation(EndPoint.class).path();
    String   explicitApiVersion =  handlerType.getAnnotation(EndPoint.class).version();
    String[] requestMethods     =  handlerType.getAnnotation(EndPoint.class).methods();
    boolean  needJwtAuth        =  handlerType.getAnnotation(EndPoint.class).jwtAuth();
    boolean  execByEventLoop    = !handlerType.getAnnotation(EndPoint.class).block();

    /* Route path initialization */
    Route route = router.route(routePath);

    /* JWT authentication */
    if (needJwtAuth) {
      route.handler(JWTAuthHandler.create(Token.tokenProvider));
    }

    /* Logging handling */
    route.handler(LoggerHandler.create());

    /* Response time handling */
    route.handler(ResponseTimeHandler.create());
    
    /* CORS handling */
    /* CORS handler for current route */
    CorsHandler corsHandler = createCorsHandler();

    for (String methodStr : requestMethods) {
      HttpMethod method = HttpMethod.valueOf(methodStr);
      route.method(method);
      corsHandler.allowedMethod(method);
    }
    route.handler(corsHandler);

    /*
     * If current route must be pre-flighted, then add an extra 
     * OPTIONS route of this path with CORS handler allowing
     * pre-flight HTTP methods.
     * 
     * There should only be one OPTIONS route on this path.
     * We should do something to keep it unique.
     */
    Set<HttpMethod> routeMethods = new HashSet<>(route.methods());
    routeMethods.retainAll(preflightMethods);
    if (!routeMethods.isEmpty()) {
      boolean allowPreFlight = false;

      /* Find if this path has an OPTIONS route */
      for (Route r : router.getRoutes()) {
        if (r.getPath().equals(routePath) &&
            r.methods().contains(HttpMethod.OPTIONS)) {
          allowPreFlight = true;
          break;
        }
      }

      /* No OPTIONS route of this path, add one */
      if (!allowPreFlight) {
        router
          .route(HttpMethod.OPTIONS, routePath)
          .handler(createCorsHandler().allowedMethods(preflightMethods));
      }
    }

    /* Retrieving request body */
    route.handler(BodyHandler.create());

    /* Some common logic of APIs */
    route.handler(ctx -> {
      String apiVersion = explicitApiVersion;
      if (apiVersion.equals("")) {
        apiVersion = Config.API_VERSION();
      }
      ctx.response().putHeader("Api-Version", apiVersion);  // inject API version
      ctx.next();
    });

    /* Handler execution handler */
    if (execByEventLoop) {
      /* executed by a event-loop thread */
      route.handler(this::handle);
    } else {
      /* executed by a worker thread */
      route.blockingHandler(this::handle);
    }

    /* Failure handling */
    route
      .failureHandler(ctx -> {
        logger.error(ctx.failure().toString(), ctx.failure());
        endRequestWithMessage(ctx, ctx.statusCode(),
                              ctx.failure().toString());
      });

    logger.warn("API end point ready: " +
                Arrays.toString(requestMethods) + " " +
                routePath);
  }

  /**
   * End the request with specific HTTP status code, and a JSON object with message.
   *
   * @param ctx The routing context.
   * @param statusCode The HTTP status code.
   * @param message The message to respond.
   */
  public static void endRequestWithMessage(final RoutingContext ctx,
                                           final int statusCode,
                                           final String message) {
    ctx
      .response()
      .setStatusCode(statusCode)
      .end(new JsonObject().put("message", message).toString());
  }

  public static void endRequestWithObject(final RoutingContext ctx,
                                          final int statusCode,
                                          final JsonObject object) {
    ctx.response().setStatusCode(statusCode).end(object.toString());
  }

  public static void endRequestWithArray(final RoutingContext ctx,
                                         final int statusCode,
                                         final JsonArray array) {
    ctx.response().setStatusCode(statusCode).end(array.toString());
  }

  /**
   * The concrete logic implemented by sub-classes.
   *
   * @param ctx The routing context.
   */
  protected abstract void handle(final RoutingContext ctx);
}
