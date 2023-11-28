package com.mycompany.crudcondockerypostgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class CrudConDockerYPostgresql {
    
    private String jdbc;
    private String username;
    private String password;
    
    public CrudConDockerYPostgresql(String jdbc, String username, String password){
        this.jdbc = jdbc;
        this.username = username;
        this.password = password;
    }
    
    public void select(){
          try (Connection connection = DriverManager.getConnection(jdbc, username, password)) {
            String sql = "SELECT * FROM tabla_prueba";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String descripcion = resultSet.getString("descripcion");

                    System.out.println("ID: " + id + ", Descripción: " + descripcion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void insert(int id, String Descripcion){
        try (Connection connection = DriverManager.getConnection(jdbc, username, password)) {
            String sql = "INSERT INTO tabla_prueba VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, Descripcion);

                int filasAfectadas = preparedStatement.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Inserción exitosa");
                } else {
                    System.out.println("No se pudo realizar la inserción");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void update(int id, String nuevaDescripcion) {
        try (Connection connection = DriverManager.getConnection(jdbc, username, password)) {
            String sql = "UPDATE tabla_prueba SET descripcion = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, nuevaDescripcion);
                preparedStatement.setInt(2, id);

                int filasAfectadas = preparedStatement.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Actualización exitosa");
                } else {
                    System.out.println("No se pudo realizar la actualización. El ID no existe.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void delete(int id){
            try (Connection connection = DriverManager.getConnection(jdbc, username, password)) {
            String sql = "DELETE FROM tabla_prueba WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                

                int filasAfectadas = preparedStatement.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Eliminacion exitosa");
                } else {
                    System.out.println("No se pudo realizar la eliminacion");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
       // Parámetros de conexión a la base de datos
        String jdbc = "jdbc:postgresql://localhost:5432/prueba";
        String username = "postgres";
        String password = "erick";
        int opc;
        int id;
        String Descripcion;
        boolean ciclo = true;
        
        Scanner sc = new Scanner(System.in);

        // Crear una instancia de CrudConDockerYPostgresql (osea del constructor ps)
        CrudConDockerYPostgresql databaseManager = new CrudConDockerYPostgresql(jdbc, username, password);
        
        while (ciclo == true){
        
        System.out.println("ELIJA UNA OPCION");
        System.out.println("[1] SELECT");
        System.out.println("[2] INSERT");
        System.out.println("[3] UPDATE");
        System.out.println("[4] DELETE");
        System.out.println("[5] SALIR");
        opc = sc.nextInt();
        
        
        switch (opc) {
            case 1:
                    databaseManager.select();
                break;
                
            case 2:
                    System.out.println("Ingrese los datos a ingresar a la base de datos");
                    System.out.println("NOTA: NO INGRESE UN CARACTER QUE NO SEA UN NUMERO ENTERO EN ID");
                    System.out.println("Ingrese el id");
                    id = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Ingrese la descripcion");
                    Descripcion = sc.nextLine();
                    
                    // Llamar a la función de inserción
                    databaseManager.insert(id, Descripcion);
                    break;
                
            case 3: 
                    System.out.println("Seleccione el id");
                    id = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Ingrese la nueva descripcion");
                    Descripcion = sc.nextLine();
                    databaseManager.update(id, Descripcion);
                    break;
            
            case 4:
                    System.out.println("Elija un id a eliminar");
                    id = sc.nextInt();
                    databaseManager.delete(id);
                    break;
            
            case 5: System.exit(0);
                         ciclo = false;
                        break;
        }
     }
   }
}
