package cn.iot.zjt.backend.handler;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * Base class of all handlers.
 *
 * @version 2021/10/29
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

  /**
   * Register current handler (and also some other assistant handlers)
   * on the router. The handler information is extracted from annotations
   * in sub-handler's class definition.
   *
   * @param router The Vert.x router to be registered on.
   */
  public void register(final Router router) {
    Class<? extends AbstractHttpHandler> handlerType = this.getClass();
    String   routePath       =  handlerType.getAnnotation(EndPoint.class).path();
    String   apiVersion      =  handlerType.getAnnotation(EndPoint.class).version();
    String[] requestMethods  =  handlerType.getAnnotation(EndPoint.class).methods();
    boolean  needJwtAuth     =  handlerType.getAnnotation(EndPoint.class).jwtAuth();
    boolean  execByEventLoop = !handlerType.getAnnotation(EndPoint.class).block();

    /* Route path initialization */
    Route route = router.route(routePath);

    /* JWT authentication */
    if (needJwtAuth) {
      route.handler(JWTAuthHandler.create(Token.tokenProvider));
    }

    /* Logging handle */
    route.handler(LoggerHandler.create());
    
    /* CORS handle */
    CorsHandler corsHandler = CorsHandler
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
    for (String methodStr : requestMethods) {
      HttpMethod method = HttpMethod.valueOf(methodStr);
      route.method(method);
      corsHandler.allowedMethod(method);
    }
    route.method(HttpMethod.OPTIONS);  // allow HTTP pre-flight.
    route.handler(corsHandler);

    /* Retrieving request body */
    route.handler(BodyHandler.create());

    /* Some common logic of APIs */
    route.handler(ctx -> {
      ctx.response().putHeader("Api-Version", apiVersion);  // inject API version
      ctx.next();
    });

    /* Handler execution handler (exception handling) */
    if (execByEventLoop) {  // executed by a event-loop thread
      route.handler(this::handleWithException);
    } else {                // executed by a worker thread
      route.blockingHandler(this::handleWithException);
    }

    /* Failure handling */
    route
      .failureHandler(ctx -> {
        logger.error(ctx.failure().getMessage(), ctx.failure());
        endRequestWithMessage(ctx, ctx.statusCode(),
                              ctx.failure().getMessage());
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
  protected abstract void handle(final RoutingContext ctx) throws Exception;

  /**
   * Deal with the exceptions thrown by the handler,
   * and fail the routing context.
   * 
   * @param ctx The routing context.
   */
  private void handleWithException(final RoutingContext ctx) {
    try {
      handle(ctx);
    } catch (Exception e) {
      ctx.fail(e);
    }
  }
}
