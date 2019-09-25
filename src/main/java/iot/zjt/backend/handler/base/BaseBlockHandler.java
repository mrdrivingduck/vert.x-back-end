/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * Base class for block handler (executed by worker thread)
 */

package iot.zjt.backend.handler.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public abstract class BaseBlockHandler {

    private Logger logger = LogManager.getLogger(BaseBlockHandler.class);

    public void register(final Router router, HttpMethod httpMethod, String url) {
        Route route = router.route(httpMethod, url);
        route.blockingHandler(routeContext -> this.handle(routeContext));
        
        logger.info("API end point ready: " + httpMethod.name() + " " + url);
    }

    public abstract void handle(final RoutingContext routingContext);
}