/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * A testing class for composite handler implementation
 */

package iot.zjt.backend.handler;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import iot.zjt.backend.handler.base.BaseCompositeHandler;

public class TestCompositeHandler extends BaseCompositeHandler {

    @Override
    public void handle(Route route) {
        route.handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true); // necessary
            response.write("Composite 1\n");
            routingContext.next();
        });
        route.handler(routingContext -> {
            routingContext.response().end("Composite 2");
        });
    }
}