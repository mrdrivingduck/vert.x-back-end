package cn.iot.zjt.backend.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Database connection pool initialization.
 * 
 * @version 2021/10/18
 */
public class Database {
  private static final Logger logger = LogManager.getLogger(Database.class);

  /**
   * MySQL client tool sets.
   * 
   * 1. Connection pool of clients.
   * 2. Function for initializing client pools.
   * 3. Function for debugging.
   */
  public static MySQLPool mysqlClient;

  public static Future<MySQLPool> initMysqlPool(final Vertx vertx,
                                                final JsonObject config) {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setHost(config.getString("mysql.host"))
      .setPort(config.getInteger("mysql.port"))
      .setUser(config.getString("mysql.user"))
      .setPassword(config.getString("mysql.password"))
      .setDatabase(config.getString("mysql.db"))
      .setConnectTimeout(config.getInteger("mysql.timeout.second") * 1000);

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(config.getInteger("mysql.pool.size"));

    mysqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

    /* test accessibility */
    return mysqlClient
      .query("SELECT COUNT(*) FROM " +
              config.getString("mysql.testing.table.name") + ";")
      .execute()
      .compose(rows -> {
        logQueryRowCount(rows);
        return Future.succeededFuture(mysqlClient);
      });
  }

  public static void logQueryRowCount(final RowSet<Row> rowSet) {
    logger.debug("MySQL returns with " + rowSet.size() + " rows.");
  }

  /**
   * More client to be extend...
   * 
   */
}
