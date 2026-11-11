// Ryan Hsiao submission for CSE 111 Lab 7
// File uses provided base code from CSE 111 instructors, sqlite JDBC examples, and [Oracle JDBC tutorial](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)

// STEP: Import required packages
import java.sql.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;

// This is currently a placeholder with some boilerplate and example code to prepare for the actual Populate code
public class Populate {
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

    private void createTable() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Create table");
		String createString =
			"CREATE TABLE IF NOT EXISTS warehouse (\n" +
			"w_warehousekey decimal(9,0) not null,\n" +
			"w_name char(100) not null,\n" +
			"w_capacity decimal(6,0) not null,\n" +
			"w_suppkey decimal(9,0) not null,\n" +
			"w_nationkey decimal(2,0) not null);";
		try (Statement s = c.createStatement()) {
			s.executeUpdate(createString);
			c.commit();
		} catch (SQLException e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void populateTable() {
		// In the first attempt, the capacity value was shared by nation rather than by supplier
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Populate table");
		String locateWarehouses =
			"WITH valid_pairs AS (\n" +
			"    SELECT s_name, s_suppkey, n_name, n_nationkey\n" +
			"    FROM supplier AS S, nation AS N1\n" +
			"    WHERE\n" +
			"        N1.n_nationkey IN (SELECT c_nationkey\n" +
			"            FROM lineitem, orders, customer, nation AS N2\n" +
			"            WHERE\n" +
			"                S.s_suppkey = l_suppkey AND\n" +
			"                l_orderkey = o_orderkey AND\n" +
			"                o_custkey = c_custkey AND\n" +
			"                c_nationkey = N2.n_nationkey\n" +
			"            GROUP BY c_nationkey\n" +
			"            ORDER BY COUNT(*) DESC, N2.n_name ASC\n" +
			"            LIMIT 3)),\n" +
			"nation_capacity AS (\n" +
			"    SELECT s_suppkey, n_nationkey, SUM(p_size) AS n_capacity\n" +
            "    FROM supplier, lineitem, part, orders, customer, nation\n" +
			"    WHERE\n" +
			"        s_suppkey = l_suppkey AND\n" +
			"        l_partkey = p_partkey AND\n" +
			"        l_orderkey = o_orderkey AND\n" +
			"        o_custkey = c_custkey AND\n" +
			"        c_nationkey = n_nationkey\n" +
			"    GROUP BY s_suppkey, n_nationkey),\n" +
			"supplier_capacity AS (\n" +
			"    SELECT s_suppkey, 3*MAX(n_capacity) AS w_capacity\n" +
			"    FROM nation_capacity\n" +
			"    GROUP BY s_suppkey)\n" +
			"SELECT\n" +
			"    V.s_name AS s_name, V.s_suppkey AS s_suppkey, V.n_name AS n_name, V.n_nationkey AS n_nationkey,\n" +
			"    w_capacity\n" +
			"FROM valid_pairs AS V, supplier_capacity AS S\n" +
			"WHERE V.s_suppkey = S.s_suppkey";
		/*
		   // Previous version, changed to try and improve perforamnce
		   // Performance feels about the same with new method, but code is cleaner
			"    (SELECT SUM(p_size)\n" +
			"    FROM customer, orders, lineitem, part\n" +
			"    WHERE\n" +
			"        n_nationkey = c_nationkey AND\n" +
			"        c_custkey = o_custkey AND\n" +
			"        o_orderkey = l_orderkey AND\n" +
			"        l_partkey = p_partkey AND\n" +
			"        l_suppkey = s_suppkey) AS w_capacity\n" +
			"FROM valid_pairs)\n" +
			"" +
			"SELECT s_name, s_suppkey, n_name, n_nationkey, 3 AS w_capacity FROM valid_pairs";
			*/
			/*
			"SELECT\n" +
			"    s_name, s_suppkey, n_name, n_nationkey,\n" +
			"    3*(SELECT MAX(sum_size)\n" +
			"    FROM (SELECT SUM(p_size) AS sum_size\n" +
			"        FROM customer, orders, lineitem, part\n" +
			"        WHERE\n" +
			"            c_custkey = o_custkey AND\n" +
			"            o_orderkey = l_orderkey AND\n" +
			"            l_partkey = p_partkey AND\n" +
			"            l_suppkey = s_suppkey\n" +
			"        GROUP BY c_nationkey\n" +
			"        HAVING EXISTS (SELECT * FROM valid_pairs AS V WHERE V.s_suppkey = s_suppkey AND V.n_nationkey = c_nationkey))\n" +
			"    ) AS w_capacity\n" +
			"FROM valid_pairs";
		*/
		String warehouse = "INSERT INTO warehouse values(?, ?, ?, ?, ?)";

		try (Statement s = c.createStatement();
			 PreparedStatement insertWarehouse = c.prepareStatement(warehouse)) {
			int incrementingID = 1;
			ResultSet rs = s.executeQuery(locateWarehouses);
			while (rs.next()) {
				insertWarehouse.setInt(1, incrementingID);
				insertWarehouse.setString(2, rs.getString("s_name") + "____" + rs.getString("n_name"));
				insertWarehouse.setInt(3, rs.getInt("w_capacity"));
				insertWarehouse.setInt(4, rs.getInt("s_suppkey"));
				insertWarehouse.setInt(5, rs.getInt("n_nationkey"));
				insertWarehouse.executeUpdate();
				incrementingID += 1;
			}
			c.commit();
		} catch (SQLException e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void dropTable() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Drop table");
		String dropString = "DROP TABLE IF EXISTS warehouse";
		try (Statement s = c.createStatement()) {
			s.executeUpdate(dropString);
			c.commit();
		} catch (SQLException e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void Q1() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Q1");
		String tau = "SELECT * FROM warehouse ORDER BY w_warehousekey ASC";
        try (Statement s = c.createStatement()) {
            FileWriter writer = new FileWriter("output/1.out", false);
            PrintWriter printer = new PrintWriter(writer);
                
            printer.printf("%10s %-40s %10s %10s %10s\n", "wId", "wName", "wCap", "sId", "nId");

			ResultSet rs = s.executeQuery(tau);
			while (rs.next()) {
				printer.printf("%10s %-40s %10s %10s %10s\n",
						rs.getInt("w_warehousekey"),
						rs.getString("w_name"),
						rs.getInt("w_capacity"),
						rs.getInt("w_suppkey"),
						rs.getInt("w_nationkey"));
			}

            printer.close();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void Q2() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Q2");
		String gammaTau =
			"SELECT n_name, COUNT(*) AS numW, SUM(w_capacity) AS totCap\n" +
			"FROM nation, warehouse\n" +
			"WHERE n_nationkey = w_nationkey\n" +
			"GROUP BY n_name\n" +
			"ORDER BY numW DESC, totCap DESC, n_name ASC";
        try (Statement s = c.createStatement()) {
            FileWriter writer = new FileWriter("output/2.out", false);
            PrintWriter printer = new PrintWriter(writer);

            printer.printf("%-40s %10s %10s\n", "nation", "numW", "totCap");
			ResultSet rs = s.executeQuery(gammaTau);
			while (rs.next()) {
				printer.printf("%-40s %10s %10s\n",
						rs.getString("n_name"),
						rs.getInt("numW"),
						rs.getInt("totCap"));
			}

            printer.close();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void Q3() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Q3");
		String sigmaTau =
			"SELECT s_name, SN.n_name AS n_name, w_name\n" +
			"FROM supplier, nation AS SN, warehouse, nation AS WN\n" +
			"WHERE\n" +
			"    s_nationkey = SN.n_nationkey AND\n" +
			"    s_suppkey = w_suppkey AND\n" +
			"    w_nationkey = WN.n_nationkey AND\n" +
			"    WN.n_name = ?\n" +
			"ORDER BY s_name ASC";
		try (PreparedStatement s = c.prepareStatement(sigmaTau)) {
            File fn = new File("input/3.in");
            FileReader reader = new FileReader(fn);
            BufferedReader in = new BufferedReader(reader);
            String nation = in.readLine();
            in.close();

            FileWriter writer = new FileWriter("output/3.out", false);
            PrintWriter printer = new PrintWriter(writer);

            printer.printf("%-20s %-20s %-40s\n", "supplier", "nation", "warehouse");
			s.setString(1, nation);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				printer.printf("%-20s %-20s %-40s\n",
						rs.getString("s_name"),
						rs.getString("n_name"),
						rs.getString("w_name"));
			}

            printer.close();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void Q4() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Q4");
		String sigmaTau =
			"SELECT w_name, w_capacity\n" +
			"FROM warehouse, nation, region\n" +
			"WHERE\n" +
			"    w_nationkey = n_nationkey AND\n" +
			"    n_regionkey = r_regionkey AND\n" +
			"    r_name = ? AND\n" +
			"    w_capacity > ?\n" +
			"ORDER BY w_capacity DESC";
		try (PreparedStatement s = c.prepareStatement(sigmaTau)) {
            File fn = new File("input/4.in");
            FileReader reader = new FileReader(fn);
            BufferedReader in = new BufferedReader(reader);
            String region = in.readLine();
            int cap = Integer.parseInt(in.readLine());
            in.close();

            FileWriter writer = new FileWriter("output/4.out", false);
            PrintWriter printer = new PrintWriter(writer);

            printer.printf("%-40s %10s\n", "warehouse", "capacity");
			s.setString(1, region);
			s.setInt(2, cap);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				printer.printf("%-40s %10s\n",
						rs.getString("w_name"),
						rs.getInt("w_capacity"));
			}
            printer.close();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("++++++++++++++++++++++++++++++++++");
    }

    private void Q5() {
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("Q5");
		String sigmaGamma =
			"SELECT\n" +
			"    r_name,\n" +
			"    COALESCE(SUM(w_capacity), 0) AS capacity\n" +
			"FROM\n" +
			"    nation AS SN, supplier,\n" +
			"    warehouse, nation AS WN, region\n" +
			"WHERE\n" +
			"    SN.n_name = ? AND\n" +
			"    SN.n_nationkey = s_nationkey AND\n" +
			"    s_suppkey = w_suppkey AND\n" +
			"    w_nationkey = WN.n_nationkey AND\n" +
			"    WN.n_regionkey = r_regionkey\n" +
			"GROUP BY r_name\n" +
			"ORDER BY r_name ASC";
        try (PreparedStatement s = c.prepareStatement(sigmaGamma)) {
            File fn = new File("input/5.in");
            FileReader reader = new FileReader(fn);
            BufferedReader in = new BufferedReader(reader);
            String nation = in.readLine();
            in.close();

            FileWriter writer = new FileWriter("output/5.out", false);
            PrintWriter printer = new PrintWriter(writer);

            printer.printf("%-20s %20s\n", "region", "capacity");
			s.setString(1, nation);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				printer.printf("%-20s %20s\n",
						rs.getString("r_name"),
						rs.getInt("capacity"));
			}
            printer.close();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("++++++++++++++++++++++++++++++++++");
    }


    public static void main(String args[]) {
        Populate sj = new Populate();
        
        sj.openConnection("tpch.sqlite");

        sj.dropTable();
        sj.createTable();
        sj.populateTable();

        sj.Q1();
        sj.Q2();
        sj.Q3();
        sj.Q4();
        sj.Q5();

        sj.closeConnection();
    }
}
