/**
 * @author Mr Dk.
 * @since 2019/09/06
 * 
 * The Vert.x instance.
 */

package iot.zjt.backend.component;

import io.vertx.core.Vertx;

public class VertxInstance {

    private static Vertx vertx = Vertx.vertx();

    public static Vertx getInstance() {
        return vertx;
    }
}