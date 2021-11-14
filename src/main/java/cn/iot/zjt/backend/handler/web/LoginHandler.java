package cn.iot.zjt.backend.handler.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.iot.zjt.backend.component.Config;
import cn.iot.zjt.backend.component.Database;
import cn.iot.zjt.backend.component.Token;
import cn.iot.zjt.backend.handler.AbstractHttpHandler;
import cn.iot.zjt.backend.handler.annotation.EndPoint;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

/**
 * Log in to get token.
 *
 * @version 2021/11/02
 */
@EndPoint(
  path = "/login",
  version = Config.API_VERSION,
  methods = {"POST"},
  jwtAuth = false
)
public class LoginHandler extends AbstractHttpHandler {

  private static final Logger logger = LogManager.getLogger(LoginHandler.class);

  @Override
  protected void handle(RoutingContext ctx) {
    JsonObject requestBody = ctx.getBodyAsJson();
    if (requestBody == null) {
      endRequestWithMessage(ctx, 400, "Missing request body.");
      return;
    }

    String username = requestBody.getString("username");
    String password = requestBody.getString("password");

    if (username == null || password == null) {
      endRequestWithMessage(ctx, 400, "Missing parameters.");
      return;
    }

    Database.mysqlClient
      .preparedQuery("SELECT user_id,user_password,SHA2(?,512) as user_input " + 
                     "FROM tianxuan_user WHERE user_username=? LIMIT 1;")
      .execute(Tuple.of(password, username))
      .compose(rowSet -> {
        // handler user not exist
        if (rowSet.size() == 0) {
          endRequestWithMessage(ctx, 404, "User not exist.");
          return Future.succeededFuture();
        }
        
        // handler incorrect password
        Row row = rowSet.iterator().next();
        String passwordHash      = row.getString("user_password");
        String inputPasswordHash = row.getString("user_input");
        if (!passwordHash.equals(inputPasswordHash)) {
          endRequestWithMessage(ctx, 401, "Incorrect password.");
          return Future.succeededFuture();
        }

        // login success, put user_id into token
        int userId = rowSet.iterator().next().getInteger(0);
        String token = Token.generateToken(new JsonObject().put("user_id", userId));
        ctx.response().end(new JsonObject()
          .put("token", token)
          .put("user_id", userId)
          .toString()
        );

        return Future.succeededFuture();
      })
      .onFailure(error -> {
        logger.error(error.getMessage(), error);
        endRequestWithMessage(ctx, 500, error.getMessage());
      });
  }
}
