/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.connection;

import java.sql.DriverManager;

/**
 *
 * @author IceCube
 */
public class DatabaseConnection {
        public static java.sql.Connection getConnection() {
        java.sql.Connection con = null;
        String uname="nbuser";   //database username
        String pass="nbuser";    //databse password
        String connectionURL = "jdbc:derby://localhost:1527/e-commerce-system";
        try {
            con = DriverManager.getConnection(connectionURL, uname, pass);
            System.out.println("Connect successfully ! "); 
        } catch (Exception ae) {
            System.out.println(ae);
            System.out.println("Connect failed ! ");
        }
        return con;
    }
}
