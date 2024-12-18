# OpenTelemetry Receiver/Agent for IBM MQ Appliance

## Introduction 

[IBM MQ Appliance](https://www.ibm.com/products/mq/appliance) is an IBM production based on [IBM MQ](https://www.ibm.com/products/mq). We provide this OpenTelemetry Receiver implementation tested with latest releases of IBM MQ Appliance.

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
|  metrics      | alpha   | JDK v8+      | [link](https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/receiver/hostmetricsreceiver) |

</div>


## Deployment Information (for end users)

**Download the installation package from [OJR releases](https://github.com/liurui-software/ojr/releases/):**
```script
wget https://github.com/liurui-software/ojr/releases/download/xxx/ojr-mq-appliance-xxx.tar
```

**Extract the package to the desired deployment location:**
```script
tar vxf ojr-mq-appliance-xxx.tar
cd ojr-mq-appliance-xxx
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
./bin/ojr-mq-appliance
```

**Or Run the Receiver/Agent asynchronously:**
```script
nohup ./bin/ojr-mq-appliance &
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
| host.system | global | The logical name of the host system | - | mq_appliance |
| appliance.host | instance | The host name or IP address of the IBM MQ appliance | - | testbox1.mqappliance.com |
| appliance.user | instance | The user name of the IBM MQ appliance | - | admin |
| appliance.password | instance | The password of the IBM MQ appliance | - | passw0rd |

