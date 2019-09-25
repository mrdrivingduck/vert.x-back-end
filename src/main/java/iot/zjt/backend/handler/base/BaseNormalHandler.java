/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * Base class for normal handler (executed by event loop thread)
 */

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
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;

public abstract class BaseNormalHandler {

    private Logger logger = LogManager.getLogger(BaseNormalHandler.class);

    public abstract void register(final Router router);

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
        route.handler(routeContext -> this.handle(routeContext));

        sb.deleteCharAt(sb.length() - 1);
        logger.info("API end point ready: " + sb.toString() + " " + url);
    }

    protected abstract void handle(final RoutingContext routingContext);
}