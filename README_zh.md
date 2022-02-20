<div align="center">

# Vert.x Backend Template

🏎️ 基于 [Vert.x](https://vertx.io/) 开发的后端工程模板

[![platform-java17](https://img.shields.io/badge/Java%2017-blue?style=for-the-badge&logo=java)](https://openjdk.java.net/projects/jdk/17/)
[![vertx-logo](https://img.shields.io/badge/Vert.x-purple?style=for-the-badge&logo=eclipsevertdotx)](https://vertx.io)

> This application was generated using http://start.vertx.io

</div>

## 编译运行

### 运行测试

```bash
./mvnw clean test
```

### 工程打包

```bash
./mvnw clean package
```

### 工程运行

直接从主类运行源代码:

```bash
./mvnw clean compile exec:java
```

直接执行 JAR 包:

```bash
java -jar xxx-<version>-fat.jar
```

## 组件介绍与扩展指南

### 配置模块

更多配置项可以以 JSON 格式加入到配置文件 [`conf/config.json`](conf/config.json) 中。JSON 对象中的键值被命名为 `xx.xx.xx.xx` 的形式，以便按配置项的功能进行归类。

所有配置项将会被 **LauncherVerticle** 加载，并作为参数传递给 **MainVerticle**。随后，MainVerticle 可以通过 `AbstractVerticle.config()` 拿到所有的配置项。

配置模块是借助 [Vert.x Config](https://vertx.io/docs/vertx-config/java/) 实现的，支持 **配置更改监听** 和 **运行时配置热加载**。当后端程序监听到配置文件更改时，将立刻解除当前 MainVerticle 的部署，然后基于修改后的配置重新部署一个新的 MainVerticle。

### 日志模块

[Apache Log4J 2](https://logging.apache.org/log4j/2.x/) 在这个模板中被用作日志后端。日志模块的 [初始化代码](src/main/java/cn/iot/zjt/backend/component/LoggerInitializer.java) 遵循了 [Vert.x Core 的推荐方法](https://vertx.io/docs/vertx-core/java/#_logging)，完成了 Vert.x 和 Log4J2 的日志连接。日志模块的配置文件被放置在资源路径下的 [`log4j2.xml`](src/main/resources/log4j2.xml) 文件中，可以被任意定制。日志模块的使用方式是，在每一个类中声明一个 Logger 对象，然后自由使用这个对象即可。

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

private static final Logger logger = LogManager.getLogger(Foo.class);

logger.info("Hello world!");
```

### Web 模块

[Vert.x Web](https://vertx.io/docs/vertx-web/java/) 被用于实现 Web 模块。所有的句柄函数都应当被注册到 **Vert.x Web Router** 路由上，然后 web router 将会被绑定到 **Vert.x HTTP Server** 上。[初始化代码](src/main/java/cn/iot/zjt/backend/component/WebServer.java) 从配置文件 `conf/config.json` 中加载诸如监听端口之类的服务器配置项，并对 web router 进行路由初始化。最终，服务器以配置项和 web router 作为参数启动。

HTTP 服务器可选支持 **HTTPS**。如果启用，那么公钥证书和私钥所在的文件路径需要在配置文件中提供。

对于 HTTP 请求的处理，本模板封装定义了 [`EndPoint`](src/main/java/cn/iot/zjt/backend/handler/annotation/EndPoint.java) 注解类，用于配置每一个 API 端点及其句柄函数。API 端点的信息包含：

- API 的 URL 路由
- API 版本号
- 访问 API 的 HTTP 请求类型
- 访问该 API 是否需要认证
- 该 API 的句柄函数中是否会执行阻塞函数（因为不能阻塞 Vert.x 的事件循环）

类 **AbstractHandler** 中实现了对所有 API 的通用处理逻辑，比如初始化。如果想要扩展实现新的 API 类，只需要继承 **AbstractHandler** 类并重写 `handle()` 函数即可。别忘记在 API 的实现类中对 `EndPoint` 注解进行配置：

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

### JWT 认证模块

JSON Web Token 认证在 [Vert.x JWT Auth Provider](https://vertx.io/docs/vertx-auth-jwt/java/) 的帮助下实现。一个 JWT provider 实例由 `conf/config.json` 中配置的公钥证书和私钥的文件路径初始化，然后该实例可以被 **全局使用**，产生或验证 token:

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

Vert.x 还提供 OAuth2 等其它认证的支持。其扩展方式（初始化、使用）可以用与 JWT 类似的方式实现。

### 数据库连接模块

目前，模板在 [Vert.x MySQL Client](https://vertx.io/docs/vertx-mysql-client/java/) 的支持下只实现了与 MySQL 数据库的连接。当后端启动时，MySQL 客户端接受 `conf/config.json` 中 MySQL 服务器的 **连接选项** 和 **连接池选项** 而被初始化。之后，MySQL 客户端可以被 **全局使用** 于查询或事务处理。

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

更多 DBMS 客户端可以以类似的方式被扩展，只要 Vert.x 支持：[PostgreSQL](https://vertx.io/docs/vertx-pg-client/java/)、[MongoDB](https://vertx.io/docs/vertx-mongo-client/java/)、[Redis](https://vertx.io/docs/vertx-redis-client/java/)、[DB2](https://vertx.io/docs/vertx-db2-client/java/) 等。

## 开源许可证

Copyright © 2021 Jingtang Zhang ([Apache License 2.0](LICENSE))
