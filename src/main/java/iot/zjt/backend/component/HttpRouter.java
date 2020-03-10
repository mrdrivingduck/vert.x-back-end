package iot.zjt.backend.component;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import iot.zjt.backend.handler.TestBlockHandler;
import iot.zjt.backend.handler.TestCompositeHandler;
import iot.zjt.backend.handler.TestDupHandler;
import iot.zjt.backend.handler.TestNormalHandler;
import iot.zjt.backend.handler.TestTokenHandler;

/**
 * The HTTP router component.
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 */
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

        new TestNormalHandler().register(router);
        new TestBlockHandler().register(router);
        new TestCompositeHandler().register(router);
        new TestDupHandler().register(router);
        new TestTokenHandler().register(router);
    }

    public Router getRouter() {
        return router;
    }

}