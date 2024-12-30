# OJR (OpenTelemetry Receivers by Java)


**[Changelog](CHANGELOG.md)** | **[Semantic Conventions](docs/semconv/README.md)** | **[Contributing](CONTRIBUTING.md)** | **[License](LICENSE)**

 
---

## Introduction of OJR

OJR ([OpenTelemetry](https://opentelemetry.io/) [Receivers](https://opentelemetry.io/docs/collector/configuration/#receivers) by Java) is a collection of stanalone OpenTelemetry receivers written with Java. A standard [OTLP](https://opentelemetry.io/docs/specs/otel/protocol/) [exporter](https://opentelemetry.io/docs/collector/configuration/#exporters) is provided to forward the data from the receivers to a Telemetry backend or an [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/).

OpenTelemetry's goals are real and its realization has a realistic basis, although the problems it has to solve are indeed complex. The achievement of OpenTelemetry is amazing. The number of users is also increasing significantly. However, there are still challenges along with the design of OpenTelemetry. One problem is the over-complexity of some of the SDKs, which creates obstacles to the efforts of product vendors to include support for OpenTelemetry. The auto-instrumentation is really good while sometimes we still need manual enhancements from the product or app providers to add built-in OpenTelemetry support. There are other minor issues. For example, OpenTelemetry Collector is a fantastic tool. While small or medium systems can be difficult to be selected to have an official receiver inside the official released OpenTelemetry Collectors.

There are several goals of this project:
- Provide a simplified SDK for Java developers to adopt OpenTelemetry.
- Provide standalone receivers for diverse systems and components. Compare with official OpenTelemetry Collector which is primarily designed for mainstream products on the market. There are great potential to support various OpenTelemetry data signals from different sources of whatever any type.
- Propose and promote related [OpenTelemetry semantic conventions](https://opentelemetry.io/docs/concepts/semantic-conventions/) which can greatly improve the usability of OpenTelemetry.
 
The "SDK" component of this project is the encapsulation of the OpenTelemetry Java SDK which can simplify the development process. The "Data Collector (DC)" component is the receiver implementation which can be used to collect OpenTelemetry data signals (metrics, traces, logs) from various sources. The "Agent" component is the standalone engine to run the Data Collectors. Users can also slightly revise the Agent code to have this functionality built into their own applications or products. The "Agent" component includes a thread pool to run the Data Collectors and a stanbdard otlp exporter to forward the data to the backend. There are several addional components including a built-in batch processor for traces and logs collection, and an optional Prometheus exportor for metrics collection.

There are "Templates" in this project which can be used as templates to build your own Date Collectors and Agents for different categories of systems. The Template is an optional component to be simplify the creation of Data Collectors and Agents.

We highly respect the existing official semantic specifications of OpenTelemetry. While we also have some new proposed OpenTelemetry semantic conventions which can be used to standardize the data models.

Note: The project is partially from [ODCD](https://github.com/instana/otel-dc) with [MIT license](https://github.com/instana/otel-dc/blob/main/LICENSE). I am also the creator and the kernel code author of ODCD.

<br>


## Receivers provided by OJR besides the SDK

We will provide more receivers in the future and welcome contributions. Right now there are only metrics receivers available, while the OJR SDK itself can also support traces and logs.
  
| Receiver name | Type | Status | JDK required |
|---------------|------|--------|--------------|
| [Dameng Database](ojr-dameng-db/README.md)       | metrics | beta  | Java 8+  |
| [Oceanbase Database](ojr-oceanbase-db/README.md) | metrics | alpha | Java 8+  |
| [Informix Database](ojr-informix-db/README.md)   | metrics | alpha | Java 8+  |
| [Linux Host](ojr-linux-host/README.md)           | metrics | alpha | Java 8+  |
| [SNMP Host](ojr-snmp-host/README.md)             | metrics | alpha | Java 11+ |
| [IBM MQ Appliance](ojr-mq-appliance/README.md)   | metrics | beta  | Java 8+  |
| [IBM MQ](ojr-ibmmq/README.md)                    | metrics | beta  | Java 8+  |


## Common Parameters for Receivers/Agents

| Parameter | Scope | Description | Default |Examples |
|-----------|-------|-------------|---------|---------|
| otel.poll.interval | instance | The time interval to query metrics in seconds | 25 | 50 |
| otel.callback.interval | instance | The time interval to post data to backend in seconds | 30 | 60 |
| otel.backend.url | instance | The URL of the OTel Backend. | http://127.0.0.1:4318 | http://127.0.0.1:4318  https://my-server:4318 |
| otel.transport | instance | The transport protocol. | http | http grpc prometheus grpc+prometheus http+prometheus |
| otel.service.name | instance | The OTel Service name | OJR | MyDataService |
| otel.service.instance.id | instance | The OTel Service instance ID (optional) | N/A | Instana-01 |
| otel.transport.timeout | instance | The transport timeout in milliseconds (optional) | 10000 | 10000 |
| otel.transport.delay | instance | The transport delay (used for Batch processor) in milliseconds (optional) | 100 | 100 |
| otel.restricted.metrics | instance | The metrics list to be omitted to be sent to backend | N/A | db.sql.elapsed_time,process_cpu_usage |
| prometheus.port | instance | The port of Prometheus endpoint if Prometheus is enabled (optional) | 16543 | 16543 |
| prometheus.host | instance | The host of Prometheus endpoint if Prometheus is enabled (optional) | N/A | localhost |
| prometheus.restricted.metrics | instance | The metrics list to be omitted for Prometheus (optional, separated by ",") | N/A | db.sql.elapsed_time,process_cpu_usage |


## File paths of configuration files for Receivers/Agents

By default, the configuration file for an OJR agent/receiver is located at `config/config.yaml`. 

OJR is using Java byilt-in JUL for logging which does not have impact on the choice of logging framework used chosed by user when user have OJR built into their own applications. The logging configuration file is by default located at `config/logging.properties`.

User can use `OJR_CONFIG` environment variable or Java system property to specify the path of the configuration file directly. For example, `OJR_CONFIG=/path/to/config1.yaml`.

According to the Java convention, the logging configuration file can also be specified by `java.util.logging.config.file` system property.

User can also just specify the file directory of the OJR configuration file and logging configuration file by `OJR_DIR` environment variable or Java system property. For example, `OJR_DIR=/path/to/config/dir`.


## Some advanced configurations

OJR recognizes the following standard OpenTelemetry environment variables:

- `OTEL_RESOURCE_ATTRIBUTES` is used to add more OpenTelemetry resource attributes. For example, OTEL_RESOURCE_ATTRIBUTES="custom.bizID=xxx,customer.bizName=yyy".
- `OTEL_EXPORTER_OTLP_HEADERS` is used to add more HTTP headers to the built-in OTLP exporter. For example, OTEL_EXPORTER_OTLP_HEADERS="api-key=key,other-config-value=value".
- `OTEL_EXPORTER_OTLP_CERTIFICATE` is used to specify the path of the certificate file for the built-in OTLP exporter. For example, OTEL_EXPORTER_OTLP_CERTIFICATE='/tmp/lr/servercert.cer'.