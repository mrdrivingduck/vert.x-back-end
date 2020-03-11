mvn clean compile
mvn exec:java -Dexec.mainClass="iot.zjt.backend.StartUp" \
    -Dexec.args="'D:\local repositories\vertx-async-backend\config\config.ini' 'D:\local repositories\vertx-async-backend\config\log4j2.xml'"