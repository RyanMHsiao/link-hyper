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

		System.out.println("(TODO: write code to populate all tables)");
		populateUser(50);
		populateFollows(50);
		// A quirk of the system is that a user may follow and block the same user
		populateBlock(50);
		populateVertexcanon(7);
		populateArccanon(7);
		populateVertex(50);
		// TODO populateForumthread
		// TODO populateMessage
		// TODO populateLikes
		// TODO populateFavorites
		// TODO populateArc
		// TODO populateInternalLink

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

	// Unix time to use, incremented for each insertion
	private int simTime = 1794395471;

	// String.format is generally insecure for SQL, but in this file only
	// literal values are used with String.format, which should be safe
	private String countFormatString = "SELECT COUNT(*) AS cnt FROM %s";
	private String randomUserPairsString =
		"SELECT T1.username AS u1, T2.username AS u2\n" +
		"FROM user AS T1, user AS T2\n" +
		"WHERE u1 <> u2\n" +
		"ORDER BY RANDOM() LIMIT ?";
	private String randomUsersString = "SELECT username FROM user ORDER BY RANDOM() LIMIT ?";
	private String randomCanonUserPairsFormatString =
		"SELECT %1$scanonid, username\n" +
		"FROM %1$scanon, user\n" +
		"ORDER BY RANDOM() LIMIT ?";

	private void populateUser(int insertCount) {
		String countString = String.format(countFormatString, "user");
		String insertString = "INSERT INTO user VALUES(?, ?, ?)";
		try (Statement s = c.createStatement();
				PreparedStatement insert = c.prepareStatement(insertString)) {
			ResultSet countSet = s.executeQuery(countString);
			countSet.next();
			int incrementingId = countSet.getInt("cnt"); // Not a key in this case
			for (int i = 0; i < insertCount; ++i, ++incrementingId) {
				insert.setString(1, "user " + incrementingId);
				insert.setString(2, "insecure " + incrementingId);
				insert.setInt(3, simTime++);
				insert.executeUpdate();
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateFollows(int insertCount) {
		String insertString = "INSERT INTO follows VALUES(?, ?, ?)";
		try (PreparedStatement query = c.prepareStatement(randomUserPairsString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setString(1, rs.getString("u1"));
				insert.setString(2, rs.getString("u2"));
				insert.setInt(3, simTime++);
				insert.executeUpdate();
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateBlock(int insertCount) {
		String insertString = "INSERT INTO block VALUES(?, ?, ?)";
		try (PreparedStatement query = c.prepareStatement(randomUserPairsString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setString(1, rs.getString("u1"));
				insert.setString(2, rs.getString("u2"));
				insert.setInt(3, simTime++);
				insert.executeUpdate();
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateVertexcanon(int insertCount) {
		String countString = String.format(countFormatString, "vertexcanon");
		String insertString = "INSERT INTO vertexcanon VALUES(?, ?, ?, ?)";
		try (Statement s = c.createStatement();
				PreparedStatement query = c.prepareStatement(randomUsersString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			ResultSet countSet = s.executeQuery(countString);
			countSet.next();
			int incrementingId = countSet.getInt("cnt");

			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setInt(1, incrementingId);
				insert.setString(2, "vertexcanon " + incrementingId);
				insert.setString(3, rs.getString("username"));
				insert.setInt(4, simTime++);
				insert.executeUpdate();
				incrementingId++;
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateArccanon(int insertCount) {
		String countString = String.format(countFormatString, "arccanon");
		String insertString = "INSERT INTO arccanon VALUES(?, ?, ?, ?)";
		try (Statement s = c.createStatement();
				PreparedStatement query = c.prepareStatement(randomUsersString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			ResultSet countSet = s.executeQuery(countString);
			countSet.next();
			int incrementingId = countSet.getInt("cnt");

			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setInt(1, incrementingId);
				insert.setString(2, "arccanon " + incrementingId);
				insert.setString(3, rs.getString("username"));
				insert.setInt(4, simTime++);
				insert.executeUpdate();
				incrementingId++;
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateVertex(int insertCount) {
		String countString = String.format(countFormatString, "vertex");
		String queryString = String.format(randomCanonUserPairsFormatString, "vertex");
		String insertString = "INSERT INTO vertex VALUES(?, ?, ?, ?, ?, NULL, NULL)";
		try (Statement s = c.createStatement();
				PreparedStatement query = c.prepareStatement(queryString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			ResultSet countSet = s.executeQuery(countString);
			countSet.next();
			int incrementingId = countSet.getInt("cnt");

			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setInt(1, incrementingId);
				insert.setInt(2, rs.getInt("vertexcanonid"));
				insert.setString(3, "vertex " + incrementingId);
				insert.setString(4, rs.getString("username"));
				insert.setInt(5, simTime++);
				insert.executeUpdate();
				incrementingId++;
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
