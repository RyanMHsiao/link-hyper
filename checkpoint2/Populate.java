// File uses provided base code from CSE 111 instructors, sqlite JDBC examples, and [Oracle JDBC tutorial](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)

// STEP: Import required packages
import java.sql.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;

public class Populate {

	// Unix time to use, incremented for each insertion
	private int simTime = 1794395471;

    private void populateTable() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Populate table");

		System.out.println("(TODO, only user for now)");
		populateUser(50);

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

	private void populateUser(int count) {
		String countUserString = "SELECT COUNT(*) AS cnt FROM user";
		String insertUserString  = "INSERT INTO user VALUES(?, ?, ?)";
		try (Statement s = c.createStatement();
				PreparedStatement insertUser = c.prepareStatement(insertUserString)) {
			ResultSet countSet = s.executeQuery(countUserString);
			countSet.next();
			int incrementingId = countSet.getInt("cnt"); // Not a key in this case
			for (int i = 0; i < count; ++i, ++incrementingId) {
				insertUser.setString(1, "user " + incrementingId);
				insertUser.setString(2, "insecure " + incrementingId);
				insertUser.setInt(3, simTime++);
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
