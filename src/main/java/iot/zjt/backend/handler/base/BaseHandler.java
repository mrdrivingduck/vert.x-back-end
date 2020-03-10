package iot.zjt.backend.handler.base;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The base class of the handlers.
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 */
public abstract class BaseHandler {

    /**
     * MUST BE OVERRIDE by sub-class.
     * 
     * @param router The router used for registing the sub-class detail.
     */
    public abstract void register(final Router router);

    /**
     * The default logic when an error occurs.
     * The method could be override by sub-class to customize behavior.
     * 
     * @param routingContext The context.
     */
    protected abstract void handleFailure(final RoutingContext routingContext);
}