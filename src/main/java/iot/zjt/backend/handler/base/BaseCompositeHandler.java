/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * Base class for composite handler (more than 1 handler needs to be executed)
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
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;

public abstract class BaseCompositeHandler {

    private Logger logger = LogManager.getLogger(BaseCompositeHandler.class);

    public abstract void register(final Router router);

    protected void registerDetail(final Router router, Class<? extends BaseCompositeHandler> clazz) {
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
        this.handle(route);

        sb.deleteCharAt(sb.length() - 1);
        logger.info("API end point ready: " + sb.toString() + " " + url);
    }

    protected abstract void handle(final Route route);
}