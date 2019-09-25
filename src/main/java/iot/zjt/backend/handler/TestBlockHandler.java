/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * A testing class for block handler implementation
 */

package iot.zjt.backend.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;
import iot.zjt.backend.handler.base.BaseBlockHandler;

@ApiUrl(url = "/test/block")
@RequestType(array = {HttpMethod.GET, HttpMethod.POST})
public class TestBlockHandler extends BaseBlockHandler {

    @Override
    public void register(Router router) {
        super.registerDetail(router, TestBlockHandler.class);
    }

    @Override
    protected void handle(RoutingContext routingContext) {
        routingContext.response().end("Block handler OK.");
    }
}