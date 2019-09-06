/**
 * @author Mr Dk.
 * @version 2019/09/06
 * 
 * The HTTP router component.
 */

package iot.zjt.backend.component;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class HttpRouter {

    private static Router router = null;

    public static Router getRouter() {
        return router;
    }

    public static void init(final Vertx vertx) {
        router = Router.router(vertx);
        // router.route()
    }
}