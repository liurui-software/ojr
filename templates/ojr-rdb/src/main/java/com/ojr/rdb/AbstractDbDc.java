package com.ojr.rdb;

import com.ojr.core.AbstractDc;
import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.RawMetric;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Predicate;


public abstract class AbstractDbDc<Cfg extends DbDcConfig> extends AbstractDc<Cfg> {
    private String dbSystem;
    private String dbDriver;
    private String dbAddress;
    private int dbPort;
    private String dbConnUrl;
    private String dbUserName;
    private String dbPassword;
    private String dbName;
    private String serverName;
    private String dbPath;
    private String dbVersion;
    private String dbEntityType;
    private String dbTenantId;
    private String dbTenantName;
    private String dbEntityParentId;

    @Override
    public Map<String, RawMetric> provideInitRawMetricsMap() {
        return new DbRawMetricRegistry().getMap();
    }

    @Override
    public void readExtraParameters(Map<String, Object> properties, Cfg dcConfig) {
        dbSystem = dcConfig.getDbSystem();
        dbDriver = dcConfig.getDbDriver();
        dbEntityParentId = (String) properties.get(DbDcUtil.DB_ENTITY_PARENT_ID);

        dbAddress = (String) properties.get(DbDcUtil.DB_ADDRESS);
        dbPort = (Integer) properties.getOrDefault(DbDcUtil.DB_PORT, 0);
        dbConnUrl = (String) properties.get(DbDcUtil.DB_CONN_URL);
        dbUserName = (String) properties.get(DbDcUtil.DB_USERNAME);
        dbPassword = DcUtil.base64Decode((String) properties.get(DbDcUtil.DB_PASSWORD));
        serverName = (String) properties.get(DbDcUtil.DB_SERVER_NAME);
        dbPath = (String) properties.get(DbDcUtil.DB_SERVER_PATH);
        dbEntityType = (String) properties.get(DbDcUtil.DB_ENTITY_TYPE);
        if (dbEntityType == null) {
            dbEntityType = DbDcUtil.DEFAULT_DB_ENTITY_TYPE;
        }
        dbEntityType = dbEntityType.toUpperCase();
        dbTenantId = (String) properties.get(DbDcUtil.DB_TENANT_ID);
        dbTenantName = (String) properties.get(DbDcUtil.DB_TENANT_NAME);
        dbName = (String) properties.get(DbDcUtil.DB_NAME);
        dbVersion = (String) properties.get(DbDcUtil.DB_VERSION);
    }

    /**
     * Run this initialization just one time for this Data Collector of whatever any number of instances.
     */
    @Override
    public void initOnce() throws Exception {
        if (dbDriver != null) {
            Class.forName(dbDriver);
        }
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        enricher.enrich(SemanticAttributes.DB_SYSTEM, dbSystem);
        enricher.enrich(SemanticAttributes.DB_NAME, dbName);
        enricher.enrich(SemanticAttributes.DB_VERSION, dbVersion);
        enricher.enrich(SemanticAttributes.DB_ENTITY_TYPE, dbEntityType);
        enricher.enrich(SemanticAttributes.DB_TENANT_NAME, dbTenantName);
        enricher.enrich(SemanticAttributes.DB_ENTITY_PARENT_ID, dbEntityParentId);
    }

    @Override
    public Predicate<String> updateResAttrsFilterForPrometheus(Predicate<String> oldFilter) {
        return oldFilter.or(key -> key.equals(SemanticAttributes.DB_NAME.getKey()));
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbConnUrl(), getDbUserName(), getDbPassword());
    }

    /*
     * Getters and Setters for extra parameters:
     **/

    public String getDbSystem() {
        return dbSystem;
    }

    public void setDbSystem(String dbSystem) {
        this.dbSystem = dbSystem;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbConnUrl() {
        return dbConnUrl;
    }

    public void setDbConnUrl(String dbConnUrl) {
        this.dbConnUrl = dbConnUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getDbEntityType() {
        return dbEntityType;
    }

    public void setDbEntityType(String dbEntityType) {
        this.dbEntityType = dbEntityType;
    }

    public String getDbTenantId() {
        return dbTenantId;
    }

    public void setDbTenantId(String dbTenantId) {
        this.dbTenantId = dbTenantId;
    }

    public String getDbTenantName() {
        return dbTenantName;
    }

    public void setDbTenantName(String dbTenantName) {
        this.dbTenantName = dbTenantName;
    }

    public String getDbEntityParentId() {
        return dbEntityParentId;
    }

    public void setDbEntityParentId(String dbEntityParentId) {
        this.dbEntityParentId = dbEntityParentId;
    }
}
