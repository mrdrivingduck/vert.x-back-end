package iot.zjt.backend.handler.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerHandler;
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;

/**
 * Base class for normal handler (executed by event loop thread).
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 */
public abstract class BaseNormalHandler extends BaseHandler {

    private static Logger logger = LogManager.getLogger(BaseNormalHandler.class);

    /**
     * Register the handler for all sub-class.
     * 
     * @param router The router.
     * @param clazz The class of sub-class.
     */
    protected void registerDetail(final Router router, Class<? extends BaseNormalHandler> clazz) {
        String url = clazz.getAnnotation(ApiUrl.class).url();
        HttpMethod[] methods = clazz.getAnnotation(RequestType.class).array();
        Set<HttpMethod> unique = new HashSet<>(Arrays.asList(methods));

        StringBuilder sb = new StringBuilder();
        Route route = router.route(url);
        for (HttpMethod method : unique) {
            route.method(method);
            sb.append(method.name());
            sb.append("/");
        }
        route.handler(LoggerHandler.create())
            .failureHandler(routingContext -> this.handleFailure(routingContext))
            .handler(routingContext -> this.handle(routingContext));

        sb.deleteCharAt(sb.length() - 1);
        logger.warn("API end point ready: " + sb.toString() + " " + url);
    }

    /**
     * The method is left for sub-class to override.
     * Implement the detailed processing logic.
     * 
     * @param routingContext The context.
     */
    protected abstract void handle(final RoutingContext routingContext);

    @Override
    protected void handleFailure(final RoutingContext routingContext) {
        routingContext.response().setStatusCode(500).end();
        logger.error(routingContext.failure().getMessage());
    }
}