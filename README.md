<div align="center">

# Vert.x Backend Template

🏎️ Java backend template of [Vert.x](https://vertx.io/)

[![platform-java11](https://img.shields.io/badge/Java%2011-blue?style=for-the-badge&logo=java)](https://openjdk.java.net/projects/jdk/11/)
[![vertx-logo](https://img.shields.io/badge/Vert.x-purple?style=for-the-badge&logo=eclipsevertdotx)](https://vertx.io)

> This application was generated using http://start.vertx.io

</div>

## Build and Run

### Run the Tests

```bash
./mvnw clean test
```

### Package the Project

```bash
./mvnw clean package
```

### Run the Project

Run the source directly:

```bash
./mvnw clean compile exec:java
```

Run executable JAR file:

```bash
java -jar xxx-<version>-fat.jar
```

## Components and How to Extend

### Configuration

More configurations can be added into [`conf/config.json`](conf/config.json) in JSON format. The key of JSON object is named as `xx.xx.xx.xx` which can be used for categorization.

The configuration will be loaded by **LauncherVerticle** and passed as parameter to **MainVerticle**. Then, MainVerticle is able to get configuration object by `AbstractVerticle.config()`.

The configuration component is implemented by [Vert.x Config](https://vertx.io/docs/vertx-config/java/), which supports **listening configuration changes** and **runtime reconfiguring**. But currently it is not implemented in this template.

### Logger

[Apache Log4J 2](https://logging.apache.org/log4j/2.x/) is used as logging backend in this template. The [initialization code](src/main/java/cn/iot/zjt/backend/component/LoggerInitializer.java) follows [Vert.x Core's suggestions](https://vertx.io/docs/vertx-core/java/#_logging) and completes the connection between Vert.x and Log4J2. The configuration file of logger is placed as [`log4j2.xml`](src/main/resources/log4j2.xml) under resource path and is free to customize. The usage of logger is to declare a Logger in each class and free to log anything:

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

private static final Logger logger = LogManager.getLogger(Foo.class);

logger.info("Hello world!");
```

### Web

[Vert.x Web](https://vertx.io/docs/vertx-web/java/) is used to implement web component. All of the handler functions should be registered to a **Vert.x Web Router**, and the router will be bind to a **Vert.x HTTP Server**. The [initialization code](src/main/java/cn/iot/zjt/backend/component/WebServer.java) loads server configurations like listening port from configuration (`conf/config.json`), and initialize the web router. Finally, it starts the server with the web router and server configurations.

**HTTPS** is supported by a HTTP server option. If enabled, the file path of public key certificate and private key should be provided in the configuration file.

For handling HTTP requests, the template defines the [`EndPoint`](src/main/java/cn/iot/zjt/backend/handler/annotation/EndPoint.java) annotation to configure each API end point handler. The end point information includes:

- URL path of the end point
- End point version
- HTTP request type to access the end point
- Whether accessing this end point needs authentication
- Whether handler function needs to execute blocking function (because we cannot block Vert.x's eventloop)

The **AbstractHandler** is defined to handle the common logic like end point initialization. When extending a new handler, just write a class which extends **AbstractHandler** class and override `handle()` function. Remember to configure end point information with `EndPoint` annotation:

```java
@EndPoint(
  path = "/status",
  version = "0.0.1",
  methods = {"GET", "POST"},
  jwtAuth = false,
  block = false
)
public class StatusHandler extends AbstractHttpHandler {

  @Override
  protected void handle(RoutingContext ctx) {
    endRequestWithMessage(ctx, 200, "API status ok.");
  }
}
```

### JWT Authentication

JSON Web Token authentication is supported with the help of [Vert.x JWT Auth Provider](https://vertx.io/docs/vertx-auth-jwt/java/). A JWT provider instance is initialized with file path of public key certificate and private key, which can be configured in `conf/config.json`. Then, the provider instance can be used **globally** for generating or verifying tokens:

```java
public static JWTAuth tokenProvider;

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
```

Vert.x provides more authentication methods like OAuth2. Extension can be done in a similar way like JWT.

### Database Connection

Currently, only MySQL client is supported with the help of [Vert.x MySQL Client](https://vertx.io/docs/vertx-mysql-client/java/). The instance of MySQL client is initialized when the backend starts up, with **connect options** and **pool options** of MySQL server from `conf/config.json`. Then, the MySQL client can be used **globally** for queries, as well as transactions.

```java
/* instance */
public static MySQLPool mysqlClient;

/* configurations */
MySQLConnectOptions connectOptions = new MySQLConnectOptions()
  .setHost          (config.getString ("mysql.host"))
  .setPort          (config.getInteger("mysql.port"))
  .setUser          (config.getString ("mysql.user"))
  .setPassword      (config.getString ("mysql.password"))
  .setDatabase      (config.getString ("mysql.db"))
  .setConnectTimeout(config.getInteger("mysql.timeout.second") * 1000);
PoolOptions poolOptions = new PoolOptions()
  .setMaxSize(config.getInteger("mysql.pool.size"));

/* initialization */
mysqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

/* query */
mysqlClient
  .query("SELECT COUNT(*) FROM table;")
  .execute()
  .compose(rows -> {
    logQueryRowCount(rows);
    return Future.succeededFuture();
  });
```

More DBMS clients can be extended in a similar way, as long as Vert.x supports: [PostgreSQL](https://vertx.io/docs/vertx-pg-client/java/), [MongoDB](https://vertx.io/docs/vertx-mongo-client/java/), [Redis](https://vertx.io/docs/vertx-redis-client/java/), [DB2](https://vertx.io/docs/vertx-db2-client/java/), ...

## License

Copyright © 2021 Jingtang Zhang ([Apache License 2.0](LICENSE))

