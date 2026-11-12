// File uses provided base code from CSE 111 instructors, sqlite JDBC examples, and [Oracle JDBC tutorial](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)

// STEP: Import required packages
import java.sql.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;

public class Populate {

    private void populateTable() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Populate table");

		System.out.println("(TODO, only user for now)");
		populateUser("generic_user", 50);

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

	private void populateUser(String prefix, int count) {
		String insertUserString  = "INSERT INTO user VALUES(?, ?, ?)";
		try (Statement s = c.createStatement();
				PreparedStatement insertUser = c.prepareStatement(insertUserString)) {
			for (int i = 0; i < count; ++i) {
				insertUser.setString(1, prefix + i);
				insertUser.setString(2, "bad password " + i);
				insertUser.setInt(3, 1794395471);
				insertUser.executeUpdate();
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

    public static void main(String args[]) {
        Populate sj = new Populate();
        
        sj.openConnection("db.sqlite");

        sj.populateTable();

        sj.closeConnection();
    }

	// Below is boilerplate included in Lab7

    private Connection c = null;
    private String dbName;
    private boolean isConnected = false;

    private void openConnection(String _dbName) {
        dbName = _dbName;

        if (false == isConnected) {
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println("Open database: " + _dbName);

            try {
                String connStr = new String("jdbc:sqlite:");
                connStr = connStr + _dbName;

                // STEP: Register JDBC driver
                Class.forName("org.sqlite.JDBC");

                // STEP: Open a connection
                c = DriverManager.getConnection(connStr);

                // STEP: Diable auto transactions
                c.setAutoCommit(false);

                isConnected = true;
                System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }

    private void closeConnection() {
        if (true == isConnected) {
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println("Close database: " + dbName);

            try {
                // STEP: Close connection
                c.close();

                isConnected = false;
                dbName = "";
                System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }
}
