/**
 * @author Mr Dk.
 * @since 2019/09/25
 * 
 * A testing class for composite handler implementation
 */

package iot.zjt.backend.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import iot.zjt.backend.handler.annotation.ApiInfo;
import iot.zjt.backend.handler.annotation.RequestType;
import iot.zjt.backend.handler.base.BaseCompositeHandler;

@ApiInfo(url = "/test/composite")
@RequestType(array = { HttpMethod.GET, HttpMethod.PUT, HttpMethod.GET })
public class TestCompositeHandler extends BaseCompositeHandler {

    @Override
    public void register(final Router router) {
        super.registerDetail(router, TestCompositeHandler.class);
    }

    @Override
    protected void handle(final Route route) {
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