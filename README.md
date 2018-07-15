## 运行

环境要求：
+ jdk1.8
+ maven3

构建
```bash
mvn clean package
```

将项目根目录下的`run.sh`和 target/ 目录下的 `application.yml` `coin-manager-1.0.0-SNAPSHOT.jar`拷贝到同一目录。所有配置信息都在`application.yml` 文件中，**需要事先创建好mysql数据库，并将连接字符串、用户名、密码等信息配置到该配置文件中。**

配置完成后，执行：

```bash
./run.sh start

# Usage ./run.sh { start | stop | restart | status }
```

即可运行。

## 扩展

对于第三方接口的支持都在 `net.sealake.coin.service.integration` 包目录下。
一期只实现了对 binance api的支持，后续若要添加其他接口的支持，需要：

+ 实现 BaseApiClient 接口
+ ExchangeService 中添加转发机制。

