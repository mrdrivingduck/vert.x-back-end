/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * Base class for composite handler (more than 1 handler needs to be executed)
 */

package iot.zjt.backend.handler.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public abstract class BaseCompositeHandler {

    private Logger logger = LogManager.getLogger(BaseCompositeHandler.class);

    public void register(final Router router, HttpMethod httpMethod, String url) {
        Route route = router.route(httpMethod, url);
        this.handle(route);
        
        logger.info("API end point ready: " + httpMethod.name() + " " + url);
    }

    public abstract void handle(final Route route);
}