package com.ojr.informix.metrics;

import com.ojr.core.metric.MetricQueryResult;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsDataQueryConfig {
    private static final Logger logger = Logger.getLogger(MetricsDataQueryConfig.class.getName());

    private final String query;
    private final Class<?> returnType;
    private String metricKey;
    private String scriptName;
    private final BasicDataSource dataSource;
    private ResultSet rs;
    private final String[] attr;

    private final HashMap<String, List<MetricQueryResult>> results;


    public MetricsDataQueryConfig(String query, Class<?> returnType, BasicDataSource dataSource, String... attr) {
        this.query = query;
        this.returnType = returnType;
        this.attr = attr;
        this.dataSource = dataSource;
        this.results = new HashMap<>();
        for (int attrIndex = 0;attrIndex<attr.length;attrIndex++) {
            this.results.put(this.attr[attrIndex],new ArrayList<>());
        }
    }

    public void fetchQueryResults() {
        try (Connection connection = this.dataSource.getConnection()) {

            ResultSet rs = executeQuery(connection, this.query);
            if (rs.isClosed()) {
                logger.severe("getMetricWithSql: ResultSet is closed");
            }
            while (rs.next()) {
                Object obj = rs.getObject(2);
                if (obj == null) {
                    obj = "null";
                }
                if (obj instanceof String) {
                    obj = ((String) obj).trim();
                }
                for(int attrIndex = 0;attrIndex<this.attr.length;attrIndex++) {
                    if(attrIndex == 1) {
                        continue;
                    }
                    MetricQueryResult result = new MetricQueryResult((Number) rs.getObject(attrIndex+1));
                    result.setAttribute(this.attr[1], obj);
                    result.setKey(obj.toString());
                    List<MetricQueryResult> ls = this.results.get(this.attr[attrIndex]);
                    ls.add(result);
                    this.results.put(this.attr[attrIndex],ls);
                }
            }
        }
        catch (SQLException exp) {
            logger.log(Level.SEVERE, "Unable to execute the sql command, Exception: " + exp);
        }
    }

    public static ResultSet executeQuery(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }


    public String getMetricKey() {
        return metricKey;
    }

    public String[] getAttr() {
        return attr;
    }

    public String getQuery() {
        return query;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public List<MetricQueryResult> getResults(String key) {
        return this.results.get(key);
    }
}
