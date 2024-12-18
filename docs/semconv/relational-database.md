# Semantic Conventions for Database Metrics

Status: [Experimental](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.26.0/specification/document-status.md)

The Semantic Conventions define a common set of (semantic) attributes which provide meaning to data when collecting, producing and consuming it. The benefit to using Semantic Conventions is in following a common naming scheme that can be standardized across a codebase, libraries, and platforms. This allows easier correlation and consumption of data. For more information, see [OpenTelemetry Semantic Conventions](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/README.md).

OpenTelemetry has defined Semantic Conventions for a couple of areas, database is one of them.  See more information at [Semantic Conventions for Database Calls and Systems](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/database/README.md).

Currently, OpenTelemetry official released [DB Metrics](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/database/database-metrics.md) page only defines metrics specific to SQL and NoSQL clients (connection pools related), there is no semantic conventions defined for database server activities, such as number of sessions, etc. 
We are working with the community to push forward a more comprehensive Semantic Convention with a common description for all databases starting from RDBMS.  

This page tries to describe a semantic convention for the attributes and metrics of generic database activities, the definition will be used to develop generic database sensor. We are considering to propose this to the community.

- [Resource attributes](#resource-attributes)
  - [Database](#database)
- [Availability Metrics](#availability-metrics)
  - [Metric `db.status`](#metric-dbstatus)
  - [Metric `db.instance.count`](#metric-dbinstancecount)
  - [Metric `db.instance.active.count`](#metric-dbinstanceactivecount)
- [Throughput Metrics](#throughput-metrics)
  - [Metric `db.session.count`](#metric-dbsessioncount)
  - [Metric `db.session.active.count`](#metric-dbsessionactivecount)
  - [Metric `db.transaction.count`](#metric-dbtransactioncount)
  - [Metric `db.transaction.rate`](#metric-dbtransactionrate)
  - [Metric `db.transaction.latency`](#metric-dbtransactionlatency)
  - [Metric `db.sql.count`](#metric-dbsqlcount)
  - [Metric `db.sql.rate`](#metric-dbsqlrate)
  - [Metric `db.sql.latency`](#metric-dbsqllatency)
  - [Metric `db.io.read.rate`](#metric-dbioreadrate)
  - [Metric `db.io.write.rate`](#metric-dbiowriterate)
  - [Metric `db.task.wait_count`](#metric-dbtaskwaitcount)
  - [Metric `db.task.avg_wait_time`](#metric-dbtaskavgwaittime)
- [Performance Metrics](#performance-metrics)
  - [Metric `db.cache.hit`](#metric-dbcachehit)
  - [Metric `db.sql.elapsed_time`](#metric-dbsqlelapsedtime)
  - [Metric `db.lock.count`](#metric-dblockcount)
  - [Metric `db.lock.time`](#metric-dblocktime)
  - [Metric `db.seq.scan.count`](#metric-dbseqscancount)
  - [Metric `db.seq.scan.table.count`](#metric-dbseqscantablecount)
  - [Metric `db.overflow.lock.count`](#metric-dboverflowlockcount)
  - [Metric `db.overflow.transaction.count`](#metric-dboverflowtransactioncount)
  - [Metric `db.overflow.user.count`](#metric-dboverflowusercount)
  - [Metric `db.lock.waits`](#metric-dblockwaits)
  - [Metric `db.cache.read.ratio`](#metric-dbcachereadratio)
  - [Metric `db.cache.write.ratio`](#metric-dbcachewriteratio)
  - [Metric `db.lru.writes`](#metric-dblruwrites)
- [Resource Usage Metrics](#resource-usage-metrics)
  - [Metric `db.disk.usage`](#metric-dbdiskusage)
  - [Metric `db.disk.utilization`](#metric-dbdiskutilization)
  - [Metric `db.cpu.utilization`](#metric-dbcpuutilization)
  - [Metric `db.mem.utilization`](#metric-dbmemutilization)
  - [Metric `db.tablespace.size`](#metric-dbtablespacesize)
  - [Metric `db.tablespace.used`](#metric-dbtablespaceused)
  - [Metric `db.tablespace.max`](#metric-dbtablespacemax)
  - [Metric `db.tablespace.utilization`](#metric-dbtablespaceutilization)
  - [Metric `db.disk.write.count`](#metric-dbdiskwritecount)
  - [Metric `db.disk.read.count`](#metric-dbdiskreadcount)
- [Maintenance Metrics](#maintenance-metrics)
  - [Metric `db.backup.cycle`](#metric-dbbackupcycle)
- [Settings Metrics](#settings-metrics)
  - [Metric `db.database.log.enabled`](#metric-dbdatabaselogenabled)
  - [Metric `db.database.buff.log.enabled`](#metric-dbdatabasebufflogenabled)
  - [Metric `db.database.ansi.compliant`](#metric-databaseansicompliant)
  - [Metric `db.database.nls.enabled`](#metric-dbdatabasenlsenable)
  - [Metric `db.database.case.insensitive`](#metric-databasecaseinsensitive)
- [Custom metrics](#custom-metrics)


## Resource attributes
### Database
Resource attributes for database and instance entity.

| Attribute Key         | Type   | Description                                                                                                                                                      | Example                       | Requirement Level                          |
|-----------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|--------------------------------------------|
| `server.address`      | string | Name of the database host.                                                                                                                                       | db.testdb.com                 | Recommended                                |
| `server.port`         | int    | database listen port                                                                                                                                             | 50000,5236                    | Recommended                                |
| `db.name`             | string | This attribute is used to report the name of the database being accessed. For commands that switch the database, this should be set to the target database       | BLUDB, DAMENG                 | Conditionally Required: If applicable.     |
| `db.system`           | string | An identifier for the database management system (DBMS) product being used.                                                                                      | db2, damengdb                 | Required                                   |
| `db.version`          | string | The version of the database                                                                                                                                      | V11.5, V8                     | Required                                   |
| `service.name`        | string | This attribute is used to describe the entity name.                                                                                                              | damengdb@DAMENG               | Required                                   |
| `service.instance.id` | string | This attribute is used to describe the entity ID of the current object and consists of server.address, server.port, and db.name.                                 | db.testdb.com:5236@DAMENG     | Conditionally Required: If applicable.     |
| `db.entity.parent.id` | string | This attribute is used to describe the parent entity ID of the current object and consists of server.address, server.port and db.name or instance.name together. | db.testdb.com:5236@db2inst1   | Conditionally Required: If applicable.     |
| `db.entity.type`      | string | This attribute is used to describe the type of the current object.                                                                                               | DATABASE, INSTANCE            | Conditionally Required: If applicable.     |
### Notes:

- All metrics in `db.database` instruments should be attached to a Database resource and therefore inherit its attributes, like `db.database.system`.
- All metrics in `db.instance` instruments should be attached to a [Instance resource](../database/instance-metrics.md) and therefore inherit its attributes, like `db.instance.name`.
## Availability Metrics
### Metric: `db.status`
This metric is [required](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#required).

| Name                       | Instrument Type | Units (UCUM) | Description                                          |
|----------------------------|-----------------|--------------|------------------------------------------------------|
| `db.status`                | Gauge           | `{status}`   | The status of the database. 1 (Active), 0 (Inactive) |

### Metric: `db.instance.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                 | Instrument Type | Units (UCUM)  | Description                                 |
|----------------------|-----------------|---------------|---------------------------------------------|
| `db.instance.count`  | UpDownCounter   | `{instance}`  | The total number of instances of database.  |

### Metric: `db.instance.active.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                        | Instrument Type | Units (UCUM)  | Description                                        |
|-----------------------------|-----------------|---------------|----------------------------------------------------|
| `db.instance.active.count`  | UpDownCounter   | `{instance}`  | The total number of active instances of database.  |
## Throughput Metrics
### Metric: `db.session.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                       | Instrument Type | Units (UCUM) | Description                       |
|----------------------------|-----------------|--------------|-----------------------------------|
| `db.session.count`         | UpDownCounter   | `{session}`  | The number of database sessions.  |
### Metric: `db.session.active.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                       | Instrument Type | Units (UCUM)  | Description                              |
|----------------------------|-----------------|---------------|------------------------------------------|
| `db.session.active.count`  | UpDownCounter   | `{session}`   | The number of active database sessions.  |
### Metric: `db.transaction.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                     | Instrument Type | Units (UCUM)     | Description                            |
|--------------------------|-----------------|------------------|----------------------------------------|
| `db.transaction.count`   | UpDownCounter   | `{transaction}`  | The number of completed transactions.  |
### Metric: `db.transaction.rate`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                  | Instrument Type | Units (UCUM)     | Description                           |
|-----------------------|-----------------|------------------|---------------------------------------|
| `db.transaction.rate` | UpDownCounter   | `{transaction}`  | The number of completed transactions. |
### Metric: `db.transaction.latency`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                      | Instrument Type | Units (UCUM) | Description                      |
|---------------------------|-----------------|--------------|----------------------------------|
| `db.transaction.latency`  | Gauge           | `s`          | The average transaction latency. |
### Metric: `db.sql.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name             | Instrument Type | Units (UCUM) | Description          |
|------------------|-----------------|--------------|----------------------|
| `db.sql.count`   | Gauge           | `{sql}`      | The number of SQLs   |
### Metric: `db.sql.rate`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name          | Instrument Type | Units (UCUM) | Description                   |
|---------------|-----------------|--------------|-------------------------------|
| `db.sql.rate` | Gauge           | `{sql/s}`    | The number of SQL per second. |
### Metric: `db.sql.latency`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name              | Instrument Type | Units (UCUM) | Description               |
|-------------------|-----------------|--------------|---------------------------|
| `db.sql.latency`  | Gauge           | `s`          | The average SQL latency.  |
### Metric: `db.io.read.rate`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name               | Instrument Type | Units (UCUM) | Description                   |
|--------------------|-----------------|--------------|-------------------------------|
| `db.io.read.rate`  | Gauge           | `By`         | The physical read per second. |
### Metric: `db.io.write.rate`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name               | Instrument Type | Units (UCUM) | Description                     |
|--------------------|-----------------|--------------|---------------------------------|
| `db.io.write.rate` | Gauge           | `By`         | The physical write per second.  |
### Metric: `db.task.wait_count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                  | Instrument Type | Units (UCUM) | Description                |
|-----------------------|-----------------|--------------|----------------------------|
| `db.task.wait_count`  | UpDownCounter   | `{task}`     | Number of waiting tasks.   |
### Metric: `db.task.avg_wait_time`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                 | Instrument Type | Units (UCUM) | Description             |
|----------------------|-----------------|--------------|-------------------------|
| `db.task.wait_count` | Gauge           | `s`          | Average task wait time. |
## Performance Metrics
### Metric: `db.cache.hit`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name            | Instrument Type | Units (UCUM) | Description                     |
|-----------------|-----------------|--------------|---------------------------------|
| `db.cache.hit`  | Gauge           | `1`          | The cache hit ratio/percentage  |

| Attribute | Type   | Description        | Example                           | Requirement Level |
|-----------|--------|--------------------|-----------------------------------|-------------------|
| `type`    | string | The type of cache. | `NORMAL`;`RECYCLE`;`FAST`;`KEEP`  | Recommended       |
### Metric: `db.sql.elapsed_time`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                   | Instrument Type | Units (UCUM) | Description                              |
|------------------------|-----------------|--------------|------------------------------------------|
| `db.sql.elapsed_time`  | UpDownCounter   | `s`          | The elapsed time in second of the query. |

| Attribute    | Type   | Description                 | Example                                                    | Requirement Level |
|--------------|--------|-----------------------------|------------------------------------------------------------|-------------------|
| `sql_id`     | string | The sql statement id.       | `A758H`                                                    | Required          |
| `sql_text`   | string | The text of sql statement.  | `select count(*) from gv$instance where status$ = 'OPEN'`  | Recommended       |
### Metric: `db.lock.count`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name             | Instrument Type | Units (UCUM) | Description                    |
|------------------|-----------------|--------------|--------------------------------|
| `db.lock.count`  | UpDownCounter   | `{lock}`     | The number of database locks.  |

| Attribute | Type   | Description        | Example           | Requirement Level |
|-----------|--------|--------------------|-------------------|-------------------|
| `type`    | string | The type of lock.  | `Row-level Lock`  | Recommended       |
### Metric: `db.lock.time`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name            | Instrument Type | Units (UCUM) | Description             |
|-----------------|-----------------|--------------|-------------------------|
| `db.lock.time`  | UpDownCounter   | `s`          | The lock elapsed time.  |

| Attribute          | Type   | Description                      | Example        | Requirement Level |
|--------------------|--------|----------------------------------|----------------|-------------------|
| `lock_id`          | string | The lock ID.                     | `0x987654321`  | Required          |
| `blocking_sess_id` | string | The blocking session identifier. | `28871001`     | Recommended       |
| `blocker_sess_id`  | string | The blocker session identifier.  | `28871041`     | Recommended       |
| `locked_obj_name`  | string | The locked object name.          | `gv$instance`  | Recommended       |

### Metric: `db.seq.scan.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                | Instrument Type | Units (UCUM) | Description                            |
|---------------------|-----------------|--------------|----------------------------------------|
| `db.seq.scan.count` | UpDownCounter   | `{scan}`     | Count of Sequential Scans for a table. |

| Attribute    | Type   | Description        | Example            | Requirement Level |
|--------------|--------|--------------------|--------------------|-------------------|
| `table_name` | string | Name of the table. | `user` ; `product` | Required          |

### Metric: `db.seq.scan.table.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                      | Instrument Type | Units (UCUM)  | Description                                       |
|---------------------------|-----------------|---------------|---------------------------------------------------|
| `db.seq.scan.table.count` | UpDownCounter   | `{tableScan}` | Number of tables which is having sequential scan. |

### Metric: `db.overflow.lock.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                     | Instrument Type | Units (UCUM)     | Description                                                                        |
|--------------------------|-----------------|------------------|------------------------------------------------------------------------------------|
| `db.overflow.lock.count` | UpDownCounter   | `{overflowLock}` | Number of times a thread attempted to acquire a lock when no locks were available. |


### Metric: `db.overflow.transaction.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                            | Instrument Type | Units (UCUM)            | Description                                                                                                                                      |
|---------------------------------|-----------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `db.overflow.transaction.count` | UpDownCounter   | `{overflowTransaction}` | Number of times a thread attempted to allocate an entry in the transaction table when no entries in the shared-memory table were available.      |

### Metric: `db.overflow.user.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                     | Instrument Type | Units (UCUM)     | Description                                                                                  |
|--------------------------|-----------------|------------------|----------------------------------------------------------------------------------------------|
| `db.overflow.user.count` | UpDownCounter   | `{overflowUser}` | Number of times a User thread attempted to acquire a lock when no locks were available.      |

### Metric: `db.lock.waits`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name              | Instrument Type | Units (UCUM)  | Description                         |
|-------------------|-----------------|---------------|-------------------------------------|
| `db.lock.waits`   | UpDownCounter   | `{lockWaits}` | Number of threads waiting for lock. |

### Metric: `db.cache.read.ratio`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                  | Instrument Type | Units (UCUM) | Description                                                                              |
|-----------------------|-----------------|--------------|------------------------------------------------------------------------------------------|
| `db.cache.read.ratio` | Gauge           | `1`          | Percentage of page reads for this buffer pool that were satisfied by a cached page image |


### Metric: `db.cache.write.ratio`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                   | Instrument Type | Units (UCUM) | Description                                                                               |
|------------------------|-----------------|--------------|-------------------------------------------------------------------------------------------|
| `db.cache.write.ratio` | Gauge           | `1`          | Percentage of page Writes for this buffer pool that were satisfied by a cached page image |

### Metric: `db.lru.writes`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name            | Instrument Type | Units (UCUM)  | Description                            |
|-----------------|-----------------|---------------|----------------------------------------|
| `db.lru.writes` | UpDownCounter   | `{lruWrites}` | Number of Least Recently Used  Writes. |

### Notes:

- The database server performs LRU (Least Recently Used) writes as background writes that typically occur when the percentage of dirty buffers (pages that are not accessed) exceeds the percent that is specified for lru_max_dirty in the BUFFERPOOL configuration parameter.

## Resource Usage Metrics
### Metric: `db.disk.usage`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name            | Instrument Type | Units (UCUM) | Description                                 |
|-----------------|-----------------|--------------|---------------------------------------------|
| `db.disk.usage` | UpDownCounter   | `By`         | The size (in bytes) of the used disk space. |

| Attribute   | Type   | Description     | Example        | Requirement Level |
|-------------|--------|-----------------|----------------|-------------------|
| `path`      | String | The disk path.  | `/dev`         | Recommended       |
| `direction` | String | Write or read.  | `read`;`write` | Recommended       |
### Metric: `db.disk.utilization`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                   | Instrument Type | Units (UCUM) | Description                         |
|------------------------|-----------------|--------------|-------------------------------------|
| `db.disk.utilization`  | Gauge           | `1`          | The percentage of used disk space.  |

| Attribute   | Type   | Description     | Example        | Requirement Level |
|-------------|--------|-----------------|----------------|-------------------|
| `path`      | String | The disk path.  | `/dev`         | Recommended       |
| `direction` | String | Write or read.  | `read`;`write` | Recommended       |
### Metric: `db.cpu.utilization`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                 | Instrument Type | Units (UCUM) | Description                 |
|----------------------|-----------------|--------------|-----------------------------|
| `db.cpu.utilization` | Gauge           | `1`          | The percentage of used CPU. |

| Attribute | Type   | Description          | Example               | Requirement Level |
|-----------|--------|----------------------|-----------------------|-------------------|
| `state`   | String | The CPU load state.  | `idle`; `interrupt`   | Recommended       |

`state` has the following list of well-known values. If one of them applies, then the respective value MUST be used, otherwise a custom value MAY be used.

| Value       | Description | 
|-------------|-------------|
| `user`      | user        | 
| `system`    | system      | 
| `nice`      | nice        | 
| `idle`      | idle        | 
| `iowait`    | iowait      | 
| `interrupt` | interrupt   | 
| `steal`     | steal       | 
### Metric: `db.mem.utilization`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name                 | Instrument Type | Units (UCUM) | Description                     |
|----------------------|-----------------|--------------|---------------------------------|
| `db.mem.utilization` | Gauge           | `1`          | The percentage of used memory.  |

| Attribute            | Type   | Description                 | Example                | Requirement Level |
|----------------------|--------|-----------------------------|------------------------|-------------------|
| `tablespace_name`    | String | Tablespace name identifier. | `default`; `interrupt` | Required          |
### Metric: `db.tablespace.size`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                 | Instrument Type  | Units (UCUM) | Description                             |
|----------------------|------------------|--------------|-----------------------------------------|
| `db.tablespace.size` | UpDownCounter    | `By`         | The size (in bytes) of the tablespace.  |

| Attribute            | Type   | Description                 | Example                | Requirement Level |
|----------------------|--------|-----------------------------|------------------------|-------------------|
| `tablespace_name`    | String | Tablespace name identifier. | `default`; `sysmaster` | Required          |
### Metric: `db.tablespace.used`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                 | Instrument Type   | Units (UCUM) | Description                                  |
|----------------------|-------------------|--------------|----------------------------------------------|
| `db.tablespace.used` | UpDownCounter     | `By`         | The used size (in bytes) of the tablespace.  |

| Attribute            | Type   | Description                 | Example                | Requirement Level |
|----------------------|--------|-----------------------------|------------------------|-------------------|
| `tablespace_name`    | String | Tablespace name identifier. | `default`; `sysmaster` | Required          |
### Metric: `db.tablespace.max`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                 | Instrument Type   | Units (UCUM) | Description                                |
|----------------------|-------------------|--------------|--------------------------------------------|
| `db.tablespace.max`  | UpDownCounter     | `By`         | The max size (in bytes) of the tablespace. |

| Attribute            | Type   | Description                 | Example                | Requirement Level |
|----------------------|--------|-----------------------------|------------------------|-------------------|
| `tablespace_name`    | String | Tablespace name identifier. | `default`; `sysmaster` | Required          |
### Metric: `db.tablespace.utilization`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                         | Instrument Type | Units (UCUM) | Description                            |
|------------------------------|-----------------|--------------|----------------------------------------|
| `db.tablespace.utilization`  | Gauge           | `1`          | The used percentage of the tablespace. |

| Attribute            | Type   | Description                 | Example                | Requirement Level |
|----------------------|--------|-----------------------------|------------------------|-------------------|
| `tablespace_name`    | String | Tablespace name identifier. | `default`; `sysmaster` | Required          |

### Metric: `db.disk.write.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                    | Instrument Type  | Units (UCUM)  | Description                               |
|-------------------------|------------------|---------------|-------------------------------------------|
| `db.disk.write.count`   | UpDownCounter    | {write}       | Actual number of physical writes to disk. |

### Metric: `db.disk.read.count`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                 | Instrument Type    | Units (UCUM) | Description                              |
|----------------------|--------------------|--------------|------------------------------------------|
| `db.disk.read.count` | UpDownCounter      | {read}       | Actual number of physical reads to disk. |

## Maintenance Metrics
### Metric: `db.backup.cycle`
This metric is [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended).

| Name              | Instrument Type | Units (UCUM) | Description    |
|-------------------|-----------------|--------------|----------------|
| `db.backup.cycle` | UpDownCounter   | `s`          | Backup cycle.  |


## Settings Metrics

### Metric: `db.database.log.enabled`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                       | Instrument Type | Units (UCUM) | Description                                                   |
|----------------------------|-----------------|--------------|---------------------------------------------------------------|
| `db.database.log.enabled`  | Gauge           | by           | Database logging is enabled or not. 1 (Active), 0 (Inactive). |

| Attribute            | Type   | Description               | Example                | Requirement Level |
|----------------------|--------|---------------------------|------------------------|-------------------|
| `database_name`      | String | Database name identifier. | `prod_db`; `sysmaster` | Required          |


### Metric: `db.database.buff.log.enabled`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                           | Instrument Type | Units (UCUM) | Description                                                            |
|--------------------------------|-----------------|--------------|------------------------------------------------------------------------|
| `db.database.buff.log.enabled` | Gauge           | by           | Database Buffered logging is enabled or not. 1 (Active), 0 (Inactive). |

| Attribute             | Type   | Description               | Example                | Requirement Level |
|-----------------------|--------|---------------------------|------------------------|-------------------|
| `database_name`       | String | Database name identifier. | `prod_db`; `sysmaster` | Required          |


### Metric: `db.database.ansi.compliant`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                         | Instrument Type | Units (UCUM) | Description                                                       |
|------------------------------|-----------------|--------------|-------------------------------------------------------------------|
| `db.database.ansi.compliant` | Gauge           | by           | Database is ANSI/ISO-compliant or not. 1 (Active), 0 (Inactive).  |

| Attribute             | Type   | Description               | Example                | Requirement Level |
|-----------------------|--------|---------------------------|------------------------|-------------------|
| `database_name`       | String | Database name identifier. | `prod_db`; `sysmaster` | Required          |


### Metric: `db.database.nls.enabled`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                      | Instrument Type | Units (UCUM) | Description                                               |
|---------------------------|-----------------|--------------|-----------------------------------------------------------|
| `db.database.nls.enabled` | Gauge           | by           | Database is GLS-enabled or not. 1 (Active), 0 (Inactive). |

| Attribute             | Type   | Description               | Example                | Requirement Level |
|-----------------------|--------|---------------------------|------------------------|-------------------|
| `database_name`       | String | Database name identifier. | `prod_db`; `sysmaster` | Required          |


### Metric: `db.database.case.insensitive`
This metric is [optional](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in).

| Name                           | Instrument Type | Units (UCUM) | Description                                                                                   |
|--------------------------------|-----------------|--------------|-----------------------------------------------------------------------------------------------|
| `db.database.case.insensitive` | Gauge           | by           | Database is case-insensitive for NCHAR and NVARCHAR columns or not. 1 (Active), 0 (Inactive). |

| Attribute             | Type   | Description               | Example                | Requirement Level |
|-----------------------|--------|---------------------------|------------------------|-------------------|
| `database_name`       | String | Database name identifier. | `prod_db`; `sysmaster` | Required          |


## Custom metrics
Please follow the guidebook if custom metrics sent, follow this specification to name the custom metrics.
1. [Instrument Naming](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.22.0/specification/common/attribute-naming.md)
2. [Attribute Naming](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.22.0/specification/common/attribute-naming.md)

e.g.
`db.metrics.db_engine.type`,
`db.metrics.data_compress.ratio`
## Related knowledge
- General Attributes
  - [server attributes](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/attributes.md#server-attributes)
  - [call level attributes](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/database/database-spans.md#call-level-attributes)
  - [document status](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.26.0/specification/document-status.md) 
  - [connection level attributes](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/database/database-spans.md#connection-level-attributes)
- Requirement level 
  - [required](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#required)
  - [recommended](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#recommended)
  - [opt-in](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metric-requirement-level.md#opt-in)
- Units
  - [Units](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metrics.md#units) 
  - [UCUM](https://unitsofmeasure.org/ucum)
## Reference
- [Metrics Data Model](https://opentelemetry.io/docs/specs/otel/metrics/data-model/)
- [OpenTelemetry Semantic Conventions](https://github.com/open-telemetry/semantic-conventions/tree/main/docs)
- [Resource Semantic Conventions](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/resource/README.md)
- [Trace Semantic Conventions](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/trace.md)
- [Metrics Semantic Conventions](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metrics.md)
- [General Guidelines](https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/metrics.md#general-guidelines)
- [Attribute Requirement Levels for Semantic Conventions](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.22.0/specification/common/attribute-requirement-level.md)
- [Metric Requirement Levels for Semantic Conventions](https://github.com/open-telemetry/opentelemetry-specification/blob/v1.22.0/specification/metrics/metric-requirement-level.md)