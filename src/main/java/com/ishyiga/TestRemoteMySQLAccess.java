package com.ishyiga;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestRemoteMySQLAccess {
    public static void main(String[] args) {
        String url = "jdbc:mysql://68.183.43.111::3306/ishyiga_api";
        String user = "root";
        String password = "root@123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user")) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Email: " + rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

