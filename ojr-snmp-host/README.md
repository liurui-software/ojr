# OpenTelemetry Receiver/Agent for SNMP hosts

## Introduction 

[SNMP](https://en.wikipedia.org/wiki/Simple_Network_Management_Protocol) is an Internet Standard protocol for collecting and organizing information about managed devices on IP networks. We provide this OpenTelemetry Receiver implementation tested with hosts supporting SNMP v2c or v3.

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
wget https://github.com/liurui-software/ojr/releases/download/xxx/ojr-snmp-host-xxx.tar
```

**Extract the package to the desired deployment location:**
```script
tar vxf ojr-snmp-host-xxx.tar
cd ojr-snmp-host-xxx
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
./bin/ojr-snmp-host
```

**Or Run the Receiver/Agent asynchronously:**
```script
nohup ./bin/ojr-snmp-host &
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
| host.system | global | The logical name of the host system | - | linux |
| snmp.host | instance | The endpoint of the host instance supporting SNMP | - | udp:9.112.252.102/161 |
| host.name | instance | Optional: use this to overwrite the value got by SNMP | - | myhost1.mycompany.com |
| os.type | instance | Optional: use this to overwrite the value got by SNMP | - | linux |
| community | instance | Optional: The community string (SNMP v1 or v2c) (default: public) | public | public1 |
| retries | instance | Optional: times to retry | 3 | 3 |
| timeout | instance | Optional: timeout in ms | 450 | 450 |
| version | instance | Optional: version of SNMP | 2c | 1, 2, 2c, 3 |
| securityLevel | instance | Optional: Choose 1:NOAUTH_NOPRIV 2:AUTH_NOPRIV 3:AUTH_PRIV | 1 | 1, 2, 3 |
| authPassword | instance | Optional: Auth password | - | password1 |
| privacyPassword | instance | Optional: Privacy password | - | password1 |
| securityName | instance | Security name | user | user1 |
| authType | instance | OID of the Protocol for Auth for SNMP v3 | - | 1.3.6.1.6.3.10.1.1.3 |
| privacyType | instance | Optional: OID of the Protocol for Privacy for SNMP v3 | - | 1.3.6.1.6.3.10.1.2.2 |

**Note:** We support SNMP version 1, 2c, 3 (USM mode)

**OID of the Protocol for Authentication (SNMP version 3)**

| Protocol          | OID                  |
|-------------------|----------------------|
| Auth-NONE         | 1.3.6.1.6.3.10.1.1.1 |
| AuthMD5           | 1.3.6.1.6.3.10.1.1.2 |
| AuthSHA           | 1.3.6.1.6.3.10.1.1.3 |
| AuthHMAC128SHA224 | 1.3.6.1.6.3.10.1.1.4 |
| AuthHMAC192SHA256 | 1.3.6.1.6.3.10.1.1.5 |
| AuthHMAC256SHA384 | 1.3.6.1.6.3.10.1.1.6 |
| AuthHMAC384SHA512 | 1.3.6.1.6.3.10.1.1.7 |

**OID of the Protocol for Privacy (SNMP version 3)**

| Protocol   | OID                        |
|------------|----------------------------|
| Priv-NONE  | 1.3.6.1.6.3.10.1.2.1       |
| PrivDES    | 1.3.6.1.6.3.10.1.2.2       |
| Priv3DES   | 1.3.6.1.6.3.10.1.2.3       |
| PrivAES128 | 1.3.6.1.6.3.10.1.2.4       |
| PrivAES192 | 1.3.6.1.4.1.4976.2.2.1.1.1 |
| PrivAES256 | 1.3.6.1.4.1.4976.2.2.1.1.2 |
