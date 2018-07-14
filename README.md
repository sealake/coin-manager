## 运行

环境要求：
+ jdk1.8
+ maven3


构建
```bash
mvn clean package
```

将项目根目录下的`run.sh`和 target/ 目录下的 `application.yml` `coin-manager-1.0.0-SNAPSHOT.jar`拷贝到同一目录。执行：

```bash
./run.sh start

# Usage ./run.sh { start | stop | restart | status }
```

即可运行。



