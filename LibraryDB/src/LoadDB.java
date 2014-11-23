import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class LoadDB {
	String FILE_NAME;
	ArrayList<String> quries;

	void loadDataBook() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			StringTokenizer tokenizer;
			String bookid = null;
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			while ((line = br.readLine()) != null) {
				tokenizer = new StringTokenizer(line, ",");
				flag = true;
				sb = new StringBuilder();
				bookid = tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					if (flag == false) {
						sb.append(",");
					}
					sb.append(tokenizer.nextToken());
					flag = false;
				}
				// # Insert into books values(LPAD('asdf',10,'0'),'Stranger in
				// the Woods');
				this.quries.add("Insert into book values(LPAD('" + bookid
						+ "',10,'0')," + sb.toString() + ");");
				// System.out.println(quries);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadBookCopies() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			StringTokenizer tokenizer;
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			while ((line = br.readLine()) != null) {
				tokenizer = new StringTokenizer(line, "	");
				flag = true;
				sb = new StringBuilder();
				String bookid = tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					if (flag == false) {
						sb.append(",");
					}
					sb.append(tokenizer.nextToken());
					flag = false;
				}
				// System.out.println(sb.toString());
				this.quries.add("Insert into book_copies values(LPAD('"
						+ bookid + "',10,'0')," + sb.toString() + ");");
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadBookAuthors() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			StringTokenizer tokenizer;
			StringTokenizer tokenizer2;
			StringTokenizer tokenizer3;
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			String author = null;

			while ((line = br.readLine()) != null) {
				tokenizer = new StringTokenizer(line, "	");
				sb = new StringBuilder();
				if (tokenizer.hasMoreTokens()) {
					sb.append(tokenizer.nextToken());
				}
				if (tokenizer.hasMoreTokens()) {
					// sb.append(",");

					tokenizer2 = new StringTokenizer(tokenizer.nextToken(), ",");
					while (tokenizer2.hasMoreElements()) {
						sb2 = new StringBuilder();
						author = tokenizer2.nextToken();
						sb2.append("\'" + author + "\'" + ",");
						tokenizer3 = new StringTokenizer(author, " ");
						if (tokenizer3.countTokens() == 2) {
							sb2.append("\'" + tokenizer3.nextToken() + "\'"
									+ ",NULL," + "\'" + tokenizer3.nextToken()
									+ "\'");
						} else if (tokenizer3.countTokens() == 3) {
							sb2.append("\'" + tokenizer3.nextToken() + "\'"
									+ "," + "\'" + tokenizer3.nextToken()
									+ "\'" + "," + "\'"
									+ tokenizer3.nextToken() + "\'");
						}
						this.quries
								.add("Insert into book_authors values(LPAD('"
										+ sb.toString() + "',10,'0'),"
										+ sb2.toString() + ");");
					}
				}

				// System.out.println(sb.toString());
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadBorrowers() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			StringTokenizer tokenizer;
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			while ((line = br.readLine()) != null) {
				tokenizer = new StringTokenizer(line, "	");
				flag = true;
				sb = new StringBuilder();
				while (tokenizer.hasMoreTokens()) {
					if (flag == false) {
						sb.append(",'");
						sb.append(tokenizer.nextToken());
						sb.append("'");
					} else {
						sb.append(tokenizer.nextToken());
						flag = false;
					}
				}
				// System.out.println(sb.toString());
				this.quries.add("Insert into BORROWER values(" + sb.toString()
						+ ");");
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadLoans() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			StringTokenizer tokenizer;
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			String token = null;
			while ((line = br.readLine()) != null) {
				tokenizer = new StringTokenizer(line, "	");
				flag = true;
				sb = new StringBuilder();
				while (tokenizer.hasMoreTokens()) {
					if (flag == false) {
						token = tokenizer.nextToken();
						if ((token.toUpperCase()).compareTo("NULL") != 0) {
							sb.append(",'");
							sb.append(token);
							sb.append("'");
						} else {
							sb.append(",");
							sb.append(token);
							sb.append("");
						}

					} else {
						sb.append(tokenizer.nextToken());
						flag = false;
					}
				}
				// System.out.println(sb.toString());
				this.quries.add("Insert into BOOK_LOANS values("
						+ sb.toString() + ");");
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void print(String outfile) {
		try {
			File file = new File(outfile);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator<String> iterator = this.quries.iterator();

			while (iterator.hasNext()) {
				bw.write(iterator.next() + "\n");
				// System.out.println(iterator.next());
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assign the filename
	 */
	LoadDB(String fname) throws IOException {
		FILE_NAME = fname;
		this.quries = new ArrayList<String>();
	}

	void setFileName(String fname) {
		this.FILE_NAME = fname;
	}

	/**
	 * 
	 * @param filename
	 *            name of the file as command line argument
	 */
	public static void main(String[] args) throws IOException {

		// LoadDB.printintoonefile();

		LoadDB queries = new LoadDB(
				"C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\Book.csv");
		// queries.loadDataBook();
		// queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\Book.sql");
		// queries.quries = new ArrayList<String>();
		queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_copies.csv");
		queries.loadBookCopies();
		queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_copies.sql");
		// queries.quries = new ArrayList<String>();
		// queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\books_authors.csv");
		// queries.loadBookAuthors();
		// queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\books_authors.sql");
		// queries.quries = new ArrayList<String>();
		// queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\borrowers.csv");
		// queries.loadBorrowers();
		// queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\borrowers.sql");
		// queries.quries = new ArrayList<String>();
		// queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_loans.csv");
		// queries.loadLoans();
		// queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_loans.sql");

	}

	static void printintoonefile() throws IOException {
		LoadDB queries = new LoadDB(
				"C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\Book.csv");

		queries.loadDataBook();
		queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\final.sql");
		queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_copies.csv");
		queries.loadBookCopies();
		queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\final.sql");
		queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\books_authors.csv");
		queries.loadBookAuthors();
		queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\final.sql");
		// queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\borrowers.csv");
		// queries.loadBorrowers();
		// queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\final.sql");
		queries.setFileName("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\book_loans.csv");
		queries.loadLoans();
		queries.print("C:\\Users\\Satyam\\Dropbox\\dbbbbbbbbbb\\final.sql");

	}
}
