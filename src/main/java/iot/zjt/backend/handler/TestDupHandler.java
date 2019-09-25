/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * A testing class for duplicated URL path
 */

package iot.zjt.backend.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;
import iot.zjt.backend.handler.base.BaseNormalHandler;

@ApiUrl(url = "/test/normal")
@RequestType(array = { HttpMethod.DELETE })
public class TestDupHandler extends BaseNormalHandler {
    
    @Override
    public void register(final Router router) {
        super.registerDetail(router, TestDupHandler.class);
    }

    @Override
    protected void handle(RoutingContext routingContext) {
        routingContext.response().end("Duplicated normal handler OK.");
    }
}