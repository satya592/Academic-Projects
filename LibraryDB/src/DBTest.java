import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.ResultSetMetaData;

public class DBTest {
	static Connection conn = null;
	ResultSet results = null;
	Statement stmt = null;

	DBTest() {
		try {// Create a connection to the local MySQL server, with the library
				// db

			// Create a SQL statement object and execute the query.
			// Set the current database, if not already set in the getConnection
			// Execute a SQL statement
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/library", "root", "root");
			stmt = conn.createStatement();
			stmt.execute("use library;");
			System.out.println("Connection establised...");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void closeConnection() {
		try {
			if (results != null)
				results.close();
			conn.close();
			System.out.println("Connection ended.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void executeQuery(String query) {

		try {
			// Execute a SQL query using SQL as a String object
			results = stmt.executeQuery(query);
			System.out.println("Success!!");
		} catch (SQLException ex) {
			System.out.println("Error in connection: " + ex.getMessage());
		}

	}

	int executeUpdate(String query) {

		try {
			// Execute a SQL query using SQL as a String object
			int results = stmt.executeUpdate(query);
			System.out.println("Success!!");
			return results;
		} catch (SQLException ex) {
			System.out.println("Error in connection: " + ex.getMessage());
		}
		return -1;
	}

	void diplayResults() {
		// Iterate through the result set using ResultSet class's next()
		// method
		String card_no;
		String firstName;
		int linect = 0;
		try {
			if (results != null) {

				int rowcount = 0;
				int colcount = 0;
				if (results.last()) {
					rowcount = results.getRow();
					results.beforeFirst(); // not rs.first() because the
											// rs.next()
											// below will move on, missing the
											// first
											// element
					ResultSetMetaData rsmd = (ResultSetMetaData) results
							.getMetaData();
					colcount = rsmd.getColumnCount();
				}

				String[][] res = new String[rowcount][];
				// ArrayList<ArrayList<String>> a = new
				// ArrayList<ArrayList<String>>();

				ArrayList<String> row = new ArrayList<String>();
				int i = 0, j = 0;
				while (results.next()) {
					res[i] = new String[colcount];
					j = 0;
					System.out.print(linect + "\t");
					while (j < colcount) {
						res[i][j++] = results.getString(j);
						System.out.print(res[i][j - 1] + ".\t");
					}
					System.out.println();
					i++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // End while(rs.next())

		// Always close the recordset and connection.
	}

	String[][] getResults() {
		// Iterate through the result set using ResultSet class's next()
		// method
		try {
			if (results != null && !results.wasNull()) {
				System.out.println("Get results has something..");
				int rowcount = 0;
				int colcount = 0;
				if (results.last()) {
					rowcount = results.getRow();
					results.beforeFirst(); // not rs.first() because the
											// rs.next()
											// below will move on, missing the
											// first
											// element
					ResultSetMetaData rsmd = (ResultSetMetaData) results
							.getMetaData();
					colcount = rsmd.getColumnCount();
				}

				String[][] res = new String[rowcount][];
				// ArrayList<ArrayList<String>> a = new
				// ArrayList<ArrayList<String>>();

				ArrayList<String> row = new ArrayList<String>();
				String card_no;
				String firstName;
				int linect = 0;
				int i = 0, j = 0;
				while (results.next()) {
					res[i] = new String[colcount];
					j = 0;
					// Keep track of the line/tuple count
					linect++;
					// Populate field variables

					// card_no = results.getString("book_id");
					// title = results.getString("title");
					// lastName = rs.getString("lname");
					// results.getst
					while (j < colcount)
						res[i][j++] = results.getString(j);

					// res[s] = (String[]) row.toArray();
					// Do something with the data
					System.out.print(linect + ".\t");
					// System.out.print(card_no + "\t");
					// System.out.print(firstName + "\t");
					// System.out.print(lastName + "\t");
					System.out.println();
					i++;
				}
				System.out.println("i" + i + ",j" + j);
				return res;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // End while(rs.next())
		return null;
		// Always close the recordset and connection.
	}

	public static void main(String[] args) {

		// Initialize variables for fields by data type
		DBTest t = new DBTest();
		t.executeQuery("select book_id,title from book");
		t.diplayResults();
		String[][] res = t.getResults();
		System.out.println("Row,col" + res.length + "," + res[0].length);
		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[i].length; j++)
				System.out.print(res[i][j] + " ");
			System.out.println();
		}
		t.closeConnection();

	}

	static String[][] getData(String query) {

		// Initialize variables for fields by data type
		DBTest t = new DBTest();
		t.executeQuery(query);
		// t.diplayResults();
		String[][] res = t.getResults();
		if (res != null) {
			// System.out.println("Row,col" + res.length + "," + res[0].length);
			for (int i = 0; i < res.length; i++) {
				for (int j = 0; j < res[i].length; j++)
					System.out.print(res[i][j] + " ");
				System.out.println();
			}
		} else {
			System.out.println("NULL Is returing..");
		}
		t.closeConnection();
		return res;
	}

	static int updateData(String query) {
		// Initialize variables for fields by data type
		DBTest t = new DBTest();
		int res = t.executeUpdate(query);
		t.closeConnection();
		return res;
	}

}