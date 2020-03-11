package iot.zjt.backend.component;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * The database component.
 * 
 * @author Mr Dk.
 * @since 2020/03/11
 */
public class Database {

    private static Logger logger = LogManager.getLogger(Database.class);;

    public static MySQLPool mySQLPool = null;

    private final static String MYSQL = "mysql";

    /**
     * Scan in the configuration file. Initialize databases on by one.
     * 
     * @param vertx The Vert.x instance.
     */
    public static void init(final Vertx vertx) {
        if (Config.getConfig().containsKey(MYSQL)) {
            logger.warn("Configuration of MySQL database detected. Initializing...");
            initMysql(vertx);
        } 
        if (Config.getConfig().containsKey("mongodb")) {
            // 
        }
    }

    /**
     * To test every database detected.
     * 
     * @param promise The future of the testing results of databases.
     */
    public static void test(Promise<Void> promise) {
        // Testing MySQL.
        Future<Void> mySQLFuture = Future.future(mySQLPromise -> {
            if (Config.getConfig().containsKey(MYSQL)) {
                logger.warn("Testing MySQL database...");
                String sql = "SELECT * FROM " + Config.getConfig().get(MYSQL, "testingTable") + ";";
                mySQLPool.query(sql, queryResult -> {
                    if (queryResult.succeeded()) {
                        logger.warn("MySQL database ok.");
                        mySQLPromise.complete();
                    } else {
                        logger.error(queryResult.cause().getMessage());
                        mySQLPromise.fail(queryResult.cause().getMessage());
                    }
                });
            } else {
                // Skip for MySQL if absent.
                mySQLPromise.complete();
            }
        });
        // Wait for all tests complete...
        CompositeFuture.all(Arrays.asList(mySQLFuture)).setHandler(res -> {
            if (res.succeeded()) {
                promise.complete();
            } else {
                promise.fail("Database testing error.");
            }
        });
    }

    /**
     * Initialize MySQL connection pool.
     * 
     * @param vertx The Vert.x instance.
     */
    private static void initMysql(final Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(Integer.parseInt(Config.getConfig().get(MYSQL, "port")))
            .setHost(Config.getConfig().get(MYSQL, "host"))
            .setDatabase(Config.getConfig().get(MYSQL, "database"))
            .setUser(Config.getConfig().get(MYSQL, "user"))
            .setPassword(Config.getConfig().get(MYSQL, "password"));

        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(Integer.parseInt(Config.getConfig().get(MYSQL, "poolSize")));

        mySQLPool = MySQLPool.pool(vertx, connectOptions, poolOptions);
    }
}