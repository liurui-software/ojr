# OpenTelemetry Receiver/Agent for Informix Database

## Introduction 

[IBM® Informix® Database](https://www.ibm.com/products/informix) is a relational database management system (RDBMS). We provide this OpenTelemetry Receiver implementation tested with Informix version 12.x.

<style>
.center 
{
  width: auto;
  display: table;
  margin-left: auto;
  margin-right: auto;
}
</style>

<p align="center">Status of the Receiver</p>
<div class="center">

| Receiver type | Status | JDK required | Semantic Conventions |
|---------------|--------|--------------|----------------------|
|  metrics      | alpha   | JDK v8+      | [link](/docs/semconv/relational-database.md) |

</div>


## Deployment Information (for end users)

**Download the installation package from [OJR releases](https://github.com/liurui-software/ojr/releases/):**
```script
wget https://github.com/liurui-software/ojr/releases/download/xxx/ojr-informix-db-xxx.tar
```

**Extract the package to the desired deployment location:**
```script
tar vxf ojr-informix-db-xxx.tar
cd ojr-informix-db-xxx
```

**Make sure following configuration files are correct for your environment:**
- config/config.yaml
- config/logging.properties

**Refine configuration file (config/config.yaml) according to your own database.** 

**Note:** You can use DC_CONFIG environment variable to specify other configuration file instead of the default "config/config.yaml". For example:
```script
export DC_CONFIG=config/my-config.yaml
```

**Run the Receiver/Agent synchronously:**
```script
./bin/ojr-informix-db
```

**Or Run the Receiver/Agent asynchronously:**
```script
nohup ./bin/ojr-informix-db &
```

**Note:** Query the Receiver/Agent
```script
ps -ef | grep ojr | grep -v grep
```

**Note:** Stop DataCollector
```script
ps -ef | grep ojr | grep -v grep | awk '{printf " "$2" "}' | xargs kill -9
```


## Build and Run (for developers)

**Make sure Java SDK is installed.**
```script
java -version
```

**Get the source code from [github.com](https://github.com/liurui-software/ojr.git).**
```script
git clone https://github.com/liurui-software/ojr.git
cd ojr-xxx
```

**Build with Gradle**
```script
./gradlew clean build
```

**Run the Agent with Gradle**
```script
./gradlew run
```

## References of configuration parameters

| Parameter | Scope | Description | Default |Examples |
|-----------|-------|-------------|---------|---------|
| db.system | global | The logical name of the DB system | - | informix |
| db.driver | global | The JDBC driver of the DB system | - | com.informix.jdbc.IfxDriver |
| db.address | instance | The IP address of the DB instance | - | 192.168.3.14 |
| db.port | instance | The internet port of the DB instance | - | 9088 |
| db.username | instance | The user name of the DB instance | - | SYSDBA |
| db.password | instance | The BASE64 encoded password of the DB instance | - | TXlQYXNzd29yZA== |
| db.name | instance | The name of the DB instance | - | myDB1 |
| db.serverName | instance | The server name of the DB instance | - | ol_informix1410 |
| db.path | instance | The path of the DB instance | - | /opt/IBM/Informix_Software_Bundle |


