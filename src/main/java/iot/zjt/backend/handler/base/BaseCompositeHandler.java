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
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import iot.zjt.backend.handler.annotation.ApiInfo;
import iot.zjt.backend.handler.annotation.RequestType;

/**
 * Base class for composite handler (more than 1 handler needs to be executed).
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 * @version 2020/03/13
 */
public abstract class BaseCompositeHandler extends BaseHandler {

    private static Logger logger = LogManager.getLogger(BaseCompositeHandler.class);

    /**
     * Register the handler for all sub-class.
     * 
     * @param router The router.
     * @param clazz The class of sub-class.
     */
    protected void registerDetail(final Router router, Class<? extends BaseCompositeHandler> clazz) {
        String url = clazz.getAnnotation(ApiInfo.class).url();
        HttpMethod[] methods = clazz.getAnnotation(RequestType.class).array();
        Set<HttpMethod> unique = new HashSet<>(Arrays.asList(methods));

        StringBuilder sb = new StringBuilder();
        Route route = router.route(url);
        for (HttpMethod method : unique) {
            route.method(method);
            sb.append(method.name());
            sb.append("/");
        }
        route
            .handler(LoggerHandler.create())
            .handler(BodyHandler.create())
            .failureHandler(routingContext -> this.handleFailure(routingContext));
        this.handle(route);

        sb.deleteCharAt(sb.length() - 1);
        logger.warn("API end point ready: " + sb.toString() + " " + url);
    }

    /**
     * More than one handler can be registered on this route.
     * 
     * @param route The route context.
     */
    protected abstract void handle(final Route route);

    @Override
    protected void handleFailure(final RoutingContext routingContext) {
        routingContext.response().setStatusCode(500).end();
        logger.error(routingContext.failure().getMessage());
    }
}