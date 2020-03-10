package iot.zjt.backend.handler;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import iot.zjt.backend.component.JwtToken;
import iot.zjt.backend.handler.annotation.ApiUrl;
import iot.zjt.backend.handler.annotation.RequestType;
import iot.zjt.backend.handler.base.BaseNormalHandler;

/**
 * A testing class for token component.
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 */
@ApiUrl(url = "/test/token")
@RequestType(array = {HttpMethod.GET})
public class TestTokenHandler extends BaseNormalHandler {

    private Logger logger = LogManager.getLogger(TestTokenHandler.class);

    @Override
    public void register(Router router) {
        super.registerDetail(router, TestTokenHandler.class);
    }

    @Override
    protected void handle(RoutingContext routingContext) {
        JwtBuilder jwt = Jwts.builder()
            .setExpiration(new Date(System.currentTimeMillis() + 10000))
            .claim("emm", "wodema")
            .claim("cao", "tainanle");
        String token = JwtToken.signToken(jwt);
        routingContext.response().setChunked(true);
        routingContext.response().write(token);

        /* ---------------------------------------------------- */

        JwtParserBuilder unsignedJws = Jwts.parserBuilder()
            .require("emm", "wodema");
        JwtParser jws = JwtToken.verifyToken(unsignedJws);
        
        try {
            jws.parse(token);
        } catch (Exception e) {
            logger.error(e.getMessage());
            routingContext.response()
                .setStatusCode(401)
                .end(e.getMessage());
        }

        routingContext.response().end();
    }
}