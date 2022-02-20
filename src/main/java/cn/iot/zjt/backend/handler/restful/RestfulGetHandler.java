package cn.iot.zjt.backend.handler.restful;

import cn.iot.zjt.backend.handler.AbstractHttpHandler;
import cn.iot.zjt.backend.handler.annotation.EndPoint;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@EndPoint(
  path = "/restful",
  methods = {"GET"},
  jwtAuth = false
)
public class RestfulGetHandler extends AbstractHttpHandler {

  public RestfulGetHandler(final Vertx vertx, final JsonObject config) {
    super(vertx, config);
  }

  @Override
  protected void handle(final RoutingContext ctx) {
    ctx.response().end();
  }
}
