package com.ojr.dameng;

import com.ojr.rdb.DbDcConfig;

import java.sql.*;
import java.util.List;

public class DamengSimpIntegrationTest {
    private DamengAgent agent;

    public void init() throws Exception {
        agent = new DamengAgent();
        agent.initEnv(DbDcConfig.class, DamengDc.class);
    }

    public void testSqlStatement(Connection conn) throws SQLException {
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        stmt = conn.createStatement();

        String dropTableSQL = "DROP TABLE IF EXISTS ojr_users";
        stmt.executeUpdate(dropTableSQL);

        String createTableSQL = "CREATE TABLE IF NOT EXISTS ojr_users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100), " +
                "email VARCHAR(100) UNIQUE)";
        stmt.executeUpdate(createTableSQL);

        String insertSQL = "INSERT INTO ojr_users (name, email) VALUES (?, ?)";
        pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, "John Doe");
        pstmt.setString(2, "john.doe@example.com");
        pstmt.executeUpdate();

        pstmt.setString(1, "Jane Smith");
        pstmt.setString(2, "jane.smith@example.com");
        pstmt.executeUpdate();

        String updateSQL = "UPDATE ojr_users SET email = ? WHERE name = ?";
        pstmt = conn.prepareStatement(updateSQL);
        pstmt.setString(1, "john.newemail@example.com");
        pstmt.setString(2, "John Doe");
        pstmt.executeUpdate();

        String querySQL = "SELECT id, name, email FROM ojr_users";
        pstmt = conn.prepareStatement(querySQL);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");

            System.out.printf("ID: %d, Name: %s, Email: %s%n", id, name, email);
        }

        stmt.executeUpdate(dropTableSQL);

        rs.close();
        pstmt.close();
        stmt.close();
    }

    public void test() throws Exception {
        List<DamengDc> dcs = agent.getDcs();
        if (dcs.isEmpty()) {
            System.err.println("No Dcs found");
            return;
        }
        DamengDc dc = dcs.get(0);
        Connection conn = dc.getConnection();
        for (int i = 0; i < 1000; i++)
            testSqlStatement(conn);
        conn.close();
    }

    public static void main(String[] args) throws Exception {
        DamengSimpIntegrationTest test = new DamengSimpIntegrationTest();
        test.init();
        test.test();
    }
}
