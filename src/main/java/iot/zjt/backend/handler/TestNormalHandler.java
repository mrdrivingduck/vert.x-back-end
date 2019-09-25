/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * A testing class for normal handler implementation
 */

package iot.zjt.backend.handler;

import io.vertx.ext.web.RoutingContext;
import iot.zjt.backend.handler.base.BaseNormalHandler;

public class TestNormalHandler extends BaseNormalHandler {

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.response().end("Normal handler OK.");
    }
}