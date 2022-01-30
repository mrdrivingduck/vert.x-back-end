package cn.iot.zjt.backend.handler.web;

import cn.iot.zjt.backend.handler.AbstractHttpHandler;
import cn.iot.zjt.backend.handler.annotation.EndPoint;
import io.vertx.ext.web.RoutingContext;

/**
 * Check the status of API. No need to login so no need
 * to authenticate JWT token.
 *
 * @version 2021/10/13
 */
@EndPoint(
  path = "/status",
  methods = {"GET"},
  jwtAuth = false
)
public class StatusHandler extends AbstractHttpHandler {

  @Override
  protected void handle(RoutingContext ctx) {
    endRequestWithMessage(ctx, 200, "API status ok.");
  }
}
