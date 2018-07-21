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

## 其他

+ 如果要更新bourseAccount账户配置，切换ak、sk没问题，如果要换到新的账户，需要按照以下操作执行：
  - 先将bourseAccount的账户状态置为不可用
  - 待后台将该交易所订单都处理完毕
  - 更新bourseAccount配置，切换到新的交易所账号，激活bourseAccount
