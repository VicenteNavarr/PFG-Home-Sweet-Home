/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homeSweetHome.dataPersistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Metodo para conectar con la BBDD
 *
 * @author Usuario
 */
public class MySQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/SweetHome";
    private static final String USER = "root"; // 
    private static final String PASSWORD = "administrador";

    // Método para obtener una conexión
    public static Connection getConnection() throws SQLException {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {

            System.err.println("Error: Driver de MySQL no encontrado.");
            throw new SQLException(e);
        }
    }

    /**
     * Metodo para cerrar una conexión
     *
     * @param connection
     */
    public static void closeConnection(Connection connection) {

        if (connection != null) {

            try {

                connection.close();

            } catch (SQLException e) {

                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

}
