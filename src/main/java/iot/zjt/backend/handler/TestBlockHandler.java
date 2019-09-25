/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * A testing class for block handler implementation
 */

package iot.zjt.backend.handler;

import io.vertx.ext.web.RoutingContext;
import iot.zjt.backend.handler.base.BaseBlockHandler;

public class TestBlockHandler extends BaseBlockHandler {

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.response().end("Block handler OK.");
    }
}