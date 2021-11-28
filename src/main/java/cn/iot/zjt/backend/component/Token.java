package cn.iot.zjt.backend.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/**
 * Initialize token provider from key store in configuration.
 *
 * @version 2021/10/13
 */
public class Token {

  private static final Logger logger = LogManager.getLogger(Token.class);

  public static JWTAuth tokenProvider;

  public static Future<JWTAuth> initTokenProvider(final Vertx vertx, final JsonObject config) {
    String privatePemPath = config.getString("token.private");
    String publicPemPath  = config.getString("token.public");

    FileSystem fs = vertx.fileSystem();

    Future<Buffer> privateKey = fs.readFile(Config.CONFIG_BASE + privatePemPath);
    Future<Buffer> publicKey  = fs.readFile(Config.CONFIG_BASE + publicPemPath);

    return CompositeFuture
      .all(privateKey, publicKey)
      .compose(v -> {
        JWTAuthOptions authOptions = new JWTAuthOptions()
          .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("RS256")
            .setBuffer(privateKey.result()))
          .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("RS256")
            .setBuffer(publicKey.result()));

        JWTAuth provider = JWTAuth.create(vertx, authOptions);
        tokenProvider = provider;
        return Future.succeededFuture(provider);
      })
      .onFailure(error -> {
        logger.error(error.getMessage(), error);
      });
  }

  public static String generateToken(JsonObject param) {
    return tokenProvider.generateToken(param,
      new JWTOptions()
        .setAlgorithm("RS256")
        .setExpiresInMinutes(60)
    );
  }

  public static Future<User> authenticate(String token) {
    return tokenProvider.authenticate(
      new JsonObject().put("token", token)
    );
  }
}
