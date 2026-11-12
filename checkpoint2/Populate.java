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

		populateUser(50);
		populateFollows(50);
		// A quirk of the system is that a user may follow and block the same user
		populateBlock(50);
		populateVertexcanon(7);
		populateArccanon(7);
		populateVertex(50);
		populateArc(50);
		populateInternallink(50);
		populateFavorites(50);
		populateForumthread(7);
		// Want possibility of multiple messages in a thread by one user
		for (int i = 0; i < 10; ++i) {
			populateMessage(10);
		}
		populateLikes(50);

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
	// Random user + id from some other relation
	private String randomUserPlusFormatString =
		"SELECT %1$sid, username\n" +
		"FROM %1$s, user\n" +
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
		String queryString = String.format(randomUserPlusFormatString, "vertexcanon");
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

	private void populateArc(int insertCount) {
		String countString = String.format(countFormatString, "arc");
		String queryString = String.format(randomUserPlusFormatString, "arccanon");
		String insertString = "INSERT INTO arc VALUES(?, ?, ?, ?, ?, ?)";
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
				insert.setInt(2, rs.getInt("arccanonid"));
				insert.setString(3, "arc " + incrementingId);
				insert.setString(4, "...");
				insert.setString(5, rs.getString("username"));
				insert.setInt(6, simTime++);
				insert.executeUpdate();
				incrementingId++;
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateInternallink(int insertCount) {
		String countString = String.format(countFormatString, "internallink");
		String queryString =
			"SELECT arcid, T1.vertexid AS v1, T2.vertexid AS v2, username\n" +
			"FROM arc, vertex AS T1, vertex AS T2, user\n" +
			"WHERE T1.vertexid <> T2.vertexid\n" +
			"ORDER BY RANDOM() LIMIT ?";
		String insertString = "INSERT INTO internallink VALUES(?, ?, ?, ?, ?, ?)";
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
				insert.setInt(2, rs.getInt("arcid"));
				insert.setInt(3, rs.getInt("v1"));
				insert.setInt(4, rs.getInt("v2"));
				insert.setString(5, rs.getString("username"));
				insert.setInt(6, simTime++);
				insert.executeUpdate();
				incrementingId++;
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateFavorites(int insertCount) {
		String queryString = String.format(randomUserPlusFormatString, "vertex");
		String insertString = "INSERT INTO favorites VALUES(?, ?, ?)";
		try (PreparedStatement query = c.prepareStatement(queryString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setString(1, rs.getString("username"));
				insert.setInt(2, rs.getInt("vertexid"));
				insert.setInt(3, simTime++);
				insert.executeUpdate();
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateForumthread(int insertCount) {
		String countString = String.format(countFormatString, "forumthread");
		String queryString = String.format(randomUserPlusFormatString, "internallink");
		String insertString = "INSERT INTO forumthread VALUES(?, ?, ?, ?, ?)";
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
				insert.setString(2, "forumthread " + incrementingId);
				insert.setString(3, rs.getString("username"));
				insert.setInt(4, simTime++);
				insert.setInt(5, rs.getInt("internallinkid"));
				insert.executeUpdate();
				incrementingId++;
			}
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private void populateMessage(int insertCount) {
		String countString = String.format(countFormatString, "message");
		String queryString = String.format(randomUserPlusFormatString, "forumthread");
		String insertString = "INSERT INTO message VALUES(?, ?, ?, ?, ?)";
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
				insert.setInt(2, rs.getInt("forumthreadid"));
				insert.setString(3, "message " + incrementingId + " body");
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

	private void populateLikes(int insertCount) {
		String queryString = String.format(randomUserPlusFormatString, "message");
		String insertString = "INSERT INTO likes VALUES(?, ?, ?)";
		try (PreparedStatement query = c.prepareStatement(queryString);
				PreparedStatement insert = c.prepareStatement(insertString)) {
			query.setInt(1, insertCount);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				insert.setString(1, rs.getString("username"));
				insert.setInt(2, rs.getInt("messageid"));
				insert.setInt(3, simTime++);
				insert.executeUpdate();
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
