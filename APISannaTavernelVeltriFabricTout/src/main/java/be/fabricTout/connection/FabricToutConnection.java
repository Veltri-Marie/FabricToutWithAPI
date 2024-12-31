package be.fabricTout.connection;

import java.sql.*;
import javax.servlet.ServletContext;

public class FabricToutConnection {

    private static Connection instance = null;

    private FabricToutConnection(ServletContext context) {
        try {
            String url = context.getInitParameter("db.url");
            String user = context.getInitParameter("db.user");
            String password = context.getInitParameter("db.password");

            System.out.println("Tentative de connexion à la base de données...");

            Class.forName("oracle.jdbc.OracleDriver");

            instance = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException ex) {
            System.out.println("Classe de driver introuvable : " + ex.getMessage());
            System.exit(0);
        } catch (SQLException ex) {
            System.out.println("Erreur JDBC : " + ex.getMessage());
        }
        
        if (instance == null) {
            System.out.println("La base de données est inaccessible, fermeture du programme.");
            System.exit(0);
        }
    }

    public static Connection getInstance(ServletContext context) {
        if (instance == null) {
            new FabricToutConnection(context);
        }
        System.out.println("FabricToutConnection.getInstance() : instance : " + instance);
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null;
                System.out.println("Connexion fermée !");
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + ex.getMessage());
            }
        }
    }
}
