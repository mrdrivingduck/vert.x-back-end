<div align="center">

# Vert.x Backend Template

[![platform-java11](https://img.shields.io/badge/Java%2011-blue?style=for-the-badge&logo=java)](https://openjdk.java.net/projects/jdk/11/)
[![vertx-logo](https://img.shields.io/badge/Vert.x-purple?style=for-the-badge&logo=eclipsevertdotx)](https://vertx.io)
<!-- [![database-mysql](https://img.shields.io/badge/MySQL-yellow?style=for-the-badge&logo=mysql)](https://www.mysql.com/) -->

> This application was generated using http://start.vertx.io

</div>

## 构建与运行

### 运行测试

```bash
./mvnw clean test
```

### 打包程序

```bash
./mvnw clean package
```

### 运行程序

直接运行源码：

```bash
./mvnw clean compile exec:java
```

运行打包好的文件：

```bash
java -jar xxx-<version>-fat.jar
```

