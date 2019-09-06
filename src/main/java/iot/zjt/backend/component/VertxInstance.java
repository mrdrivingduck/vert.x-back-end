/**
 * @author Mr Dk.
 * @version 2019/09/06
 * 
 * The Vert.x instance.
 */

package iot.zjt.backend.component;

import io.vertx.core.Vertx;

public class VertxInstance {

    private static Vertx vertx = null;

    public static void init() {
        vertx = Vertx.vertx();
    }

    public static Vertx getInstance() {
        return vertx;
    }
}