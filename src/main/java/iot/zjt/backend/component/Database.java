/**
 * @author Mr Dk.
 * @version 2019/09/06
 * 
 * The database component.
 */

package iot.zjt.backend.component;

import io.vertx.core.Promise;

public class Database {

    public static void init() {

    }

    public static void test(Promise<Void> promise) {
        // Testing
        promise.complete();
    }
}