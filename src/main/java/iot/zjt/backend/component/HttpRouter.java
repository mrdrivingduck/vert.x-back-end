/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * The HTTP router component.
 */

package iot.zjt.backend.component;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import iot.zjt.backend.handler.TestBlockHandler;
import iot.zjt.backend.handler.TestCompositeHandler;
import iot.zjt.backend.handler.TestNormalHandler;

public class HttpRouter {

    private static HttpRouter instance = null;
    private HttpRouter() {}

    private Router router = null;

    public static HttpRouter getInstance() {
        if (instance == null) {
            instance = new HttpRouter();
        }
        return instance;
    }
    
    public void init(final Vertx vertx) {
        router = Router.router(vertx);

        new TestNormalHandler().register(router, HttpMethod.GET, "/test/normal");
        new TestBlockHandler().register(router, HttpMethod.GET, "/test/block");
        new TestCompositeHandler().register(router, HttpMethod.GET, "/test/composite");
    }

    public Router getRouter() {
        return router;
    }

}