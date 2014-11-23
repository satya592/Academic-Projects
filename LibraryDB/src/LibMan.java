import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LibMan {

	private JFrame frmLibraryManagementSystem;
	private JTable table;
	private JTable table1;
	private JTextField bookID;
	private JTextField title;
	private JTextField author;
	private JTextField fName;
	private JTextField mName;
	private JTextField lName;
	private StringBuffer query = null;
	private JTextField scardno;
	private String bAndTCond = " and ";
	private String tAndACond = " and ";
	private JTextField chinbook_id;
	private JTextField cardno;
	private JTextField bname;
	private JTextField month;
	private JTextField date;
	private JTextField year;
	private JTextField bfname;
	private JTextField blname;
	private JTextField badd1;
	private JTextField badd2;
	private JTextField city;
	private JTextField state;
	private JTextField phone;
	private JLabel lblPleaseEnterValid;
	private JLabel lblPleaseFindA;
	JScrollPane scrollPane_1 = new JScrollPane();
	private JLabel checkinfail;
	private JLabel invaliddate;
	JRadioButton bAndT;
	JRadioButton tAndA;
	JRadioButton bOrT;
	JRadioButton tOrA;
	String dataValues[][];
	private JScrollPane sp;
	private JLabel lblBorrMsg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LibMan window = new LibMan();
					window.frmLibraryManagementSystem.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LibMan() {
		initialize();
	}

	void populatet2() {
		invaliddate.setVisible(false);
		checkinfail.setVisible(false);

		String bookid = chinbook_id.getText();
		String card_no = cardno.getText();
		String b_name = bname.getText();

		StringBuffer query1 = new StringBuffer();
		// select loan_id,book_id,branch_id,card_no,date_out,due_date
		// from book_loans where date_in is null and book_id like '%%'
		// and card_no like '%%' and
		// card_no in (select card_no from borrower where fname = '%%'
		// or lname = '%%' or fname+lname = '%%');
		query1.append("select loan_id,book_id,branch_id,card_no,date_out,due_date from book_loans where date_in is null ");
		if (bookid != null) {
			query1.append(" and book_id Like '%" + bookid.trim() + "%'");
			if (card_no != null) {
				query1.append(" and card_no Like '%" + card_no.trim() + "%'");
				if (b_name != null) {
					query1.append(" and card_no in (select card_no from borrower where fname like '%"
							+ b_name.trim()
							+ "%'  or lname like '%"
							+ b_name.trim()
							+ "%' or concat(fname,lname) like '%"
							+ b_name.trim() + "%')");
				}
			} else if (b_name != null) {
				query1.append(" and card_no in (select card_no from borrower where fname like '%"
						+ b_name.trim()
						+ "%'  or lname like '%"
						+ b_name.trim()
						+ "%' or concat(fname,lname) like '%"
						+ b_name.trim() + "%')");
			}

		} else if (card_no != null) {
			query1.append(" and card_no Like '%" + card_no.trim() + "%'");
			if (b_name != null) {
				query1.append(" and card_no in (select card_no from borrower where fname like '%"
						+ b_name.trim()
						+ "%'  or lname like '%"
						+ b_name.trim()
						+ "%' or concat(fname,lname) like '%"
						+ b_name.trim() + "%')");
			}
		} else if (b_name != null) {
			query1.append(" and card_no in (select card_no from borrower where fname like '%"
					+ b_name.trim()
					+ "%'  or lname like '%"
					+ b_name.trim()
					+ "%' or concat(fname,lname) like '%"
					+ b_name.trim()
					+ "%')");
		}
		query1.append(";");
		System.out.println(query1.toString());
		String dataValues[][] = DBTest.getData(query1.toString());
		if (dataValues != null) {
			String columnNames[] = { "Loan ID", "Book ID", "Branch ID",
					"Card No", "Date Out", "Due Date" };

			// String columnNames[] = { "book_id", "title" };

			table1 = new JTable();
			table1 = new JTable(dataValues, columnNames);
			for (int c = 0; c < table1.getColumnCount(); c++) {
				Class<?> col_class = table1.getColumnClass(c);
				table1.setDefaultEditor(col_class, null); // remove
															// editor
			}
			table1.setRowSelectionAllowed(true);
			table1.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							// TODO Auto-generated method stub

						}
					});
			sp.setViewportView(table1);
		} else {
			// empty.....
			sp.setViewportView(null);
		}

	}

	void populatet1() {
		dataValues = DBTest.getData(query.toString());
		String columnNames[] = { "Book ID", "Title", "Author(s)", "Branch ID",
				"NO.Of copies at each branch",
				"NO.Of available copies at each branch" };
		// String columnNames[] = { "book_id", "title" };

		if (dataValues != null) {
			table = new JTable();
			table = new JTable(dataValues, columnNames);
			for (int c = 0; c < table.getColumnCount(); c++) {
				Class<?> col_class = table.getColumnClass(c);
				table.setDefaultEditor(col_class, null); // remove
															// editor
			}
			table.setRowSelectionAllowed(true);
			table.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							// TODO Auto-generated method stub

						}
					});
			scrollPane_1.setViewportView(table);
		} else {
			// empty.....
			scrollPane_1.setViewportView(null);
		}
		System.out.println(query);

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLibraryManagementSystem = new JFrame();
		frmLibraryManagementSystem.setTitle("Library Management System");
		frmLibraryManagementSystem.setBounds(100, 100, 920, 670);
		frmLibraryManagementSystem.setResizable(false);
		frmLibraryManagementSystem
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLibraryManagementSystem.getContentPane().setLayout(
				new GridLayout(0, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmLibraryManagementSystem.getContentPane().add(tabbedPane);

		final JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Search&CheckOut", (Icon) null, panel_1, null);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		final JPanel panel_2 = new JPanel();

		JButton search = new JButton("Search");
		search.setFont(new Font("Tahoma", Font.BOLD, 12));
		search.setBackground(Color.LIGHT_GRAY);
		search.addActionListener(new ActionListener() {
			@SuppressWarnings("serial")
			public void actionPerformed(ActionEvent arg0) {
				// /******************
				lblPleaseFindA.setVisible(false);
				lblPleaseEnterValid.setVisible(false);
				// dataValues = DBTest
				// .getData("select book_id,title from book");

				query = new StringBuffer();
				query.append("select temp.book_id as book_id,temp.title as title, temp.author_name as authors, temp.branch_id as branch_id, no_of_copies as total,no_of_copies - count(book_loans.book_id) as available from book_loans right join (select title,book.book_id,author_name,fname,minit,lname,branch_id,no_of_copies from book_copies natural join book left join book_authors on book.book_id=book_authors.book_id where '1'='1' ");
				boolean flag = true;
				String bookid = bookID.getText();
				if (!bookid.isEmpty()) {
					// if (flag) {
					query.append(" and ");
					flag = false;
					System.out.println("Bookid add....");
					// }
					query.append("book.book_id LIKE '%" + bookid.trim() + "%' ");
				}
				String titl = title.getText();
				String fulln = null, fp = null, mp = null, lp = null;
				if (!titl.isEmpty()) {
					if (flag) {
						query.append(" and ");
						flag = false;
						System.out.println("title add....");
					}
					// System.out.println("bnt action commadn"
					// + bAndT.getActionCommand());
					if (!titl.isEmpty() && !bookid.isEmpty())
						query.append(bAndTCond + " title LIKE '%" + titl.trim()
								+ "%' ");
					else
						query.append(" title LIKE '%" + titl.trim() + "%' ");
				}
				if (author.isEditable()) {

					System.out.println("full.....");
					fulln = author.getText();
					if (!fulln.isEmpty()) {
						// if (flag) {
						query.append(" and ");
						// flag = false;
						System.out.println("author add....");
						// }
						if (!titl.isEmpty() && !fulln.isEmpty())
							query.append(tAndACond + " author_name LIKE '%"
									+ fulln.trim() + "%' ");
						else if (!bookid.isEmpty() && !fulln.isEmpty())
							query.append(bAndTCond + " author_name LIKE '%"
									+ fulln.trim() + "%' ");
						else
							query.append(" author_name LIKE '%" + fulln.trim()
									+ "%' ");
					}
				} else if (fName.isEditable()) {
					fp = fName.getText();
					mp = mName.getText();
					lp = lName.getText();
					System.out.println("parts.....");
					if (!fp.isEmpty()) {
						if (flag) {
							query.append(" and ");
							flag = false;
						}
						if (!titl.isEmpty() && !fp.isEmpty())
							query.append(tAndACond + " fname LIKE '%"
									+ fp.trim() + "%' ");
						else if (!bookid.isEmpty() && !fp.isEmpty())
							query.append(bAndTCond + " fname LIKE '%"
									+ fp.trim() + "%' ");

						else
							query.append(" fname LIKE '%" + fp.trim() + "%' ");
					}
					if (!mp.isEmpty()) {
						if (flag) {
							query.append(" and ");
							flag = false;
						}
						if (!titl.isEmpty() && !mp.isEmpty())
							query.append(tAndACond + "  mname LIKE '%"
									+ mp.trim() + "%' ");
						else if (!bookid.isEmpty() && !mp.isEmpty())
							query.append(bAndTCond + " mname LIKE '%"
									+ mp.trim() + "%' ");

						else if (!fp.isEmpty())
							query.append(" and mname LIKE '%" + mp.trim()
									+ "%' ");
						else
							query.append(" mname LIKE '%" + mp.trim() + "%' ");
					}
					if (!lp.isEmpty()) {
						if (flag) {
							query.append(" and ");
							flag = false;
						}
						if (!titl.isEmpty() && !lp.isEmpty())
							query.append(tAndACond + " lname LIKE '%"
									+ lp.trim() + "%' ");
						else if (!bookid.isEmpty() && !lp.isEmpty())
							query.append(bAndTCond + " lname LIKE '%"
									+ lp.trim() + "%' ");

						else if (!fp.isEmpty() || !mp.isEmpty())
							query.append("and lname LIKE '%" + lp.trim()
									+ "%' ");
						else
							query.append("lname LIKE '%" + lp.trim() + "%' ");
					}
				}
				query.append("group by branch_id,book_id) as temp on book_loans.book_id=temp.book_id and book_loans.branch_id=temp.branch_id and book_loans.date_in is null group by temp.book_id,temp.branch_id,no_of_copies;");
				System.out.println(bookid);
				System.out.println(titl);
				System.out.println(fulln);
				System.out.println(fp);
				System.out.println(mp);
				System.out.println(lp);
				System.out.println(query);
				populatet1();
			}
		});

		bookID = new JTextField();
		bookID.setToolTipText("Enter book id");
		bookID.setColumns(10);
		panel_1.add(panel_2);

		JButton btnClear = new JButton("Clear All");
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnClear.setBackground(Color.LIGHT_GRAY);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				author.setText("");
				fName.setText("");
				mName.setText("");
				lName.setText("");
				bookID.setText("");
				title.setText("");
				scrollPane_1.setViewportView(null);
				lblPleaseFindA.setVisible(false);
				lblPleaseEnterValid.setVisible(false);
				scardno.setText("");
				bAndT.setSelected(true);
				tAndA.setSelected(true);
				bOrT.setSelected(false);
				tOrA.setSelected(false);
			}
		});

		title = new JTextField();
		title.setToolTipText("Enter title ");
		title.setColumns(10);

		author = new JTextField();
		author.setColumns(10);

		JLabel lblTitle = new JLabel("Title");

		JLabel lblBookId = new JLabel("Book ID");

		fName = new JTextField();
		fName.setEditable(false);
		fName.setColumns(8);

		mName = new JTextField();
		mName.setEditable(false);
		mName.setColumns(10);

		lName = new JTextField();
		lName.setEditable(false);
		lName.setColumns(10);

		JRadioButton rdbtnFullName = new JRadioButton("Auther's Full Name");
		rdbtnFullName.setActionCommand("fullname");
		rdbtnFullName.setSelected(true);

		JRadioButton rdbtnFname = new JRadioButton("First M Last Names");
		rdbtnFname.setActionCommand("partsname");

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnFullName);
		group.add(rdbtnFname);

		rdbtnFullName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fName.setEditable(false);
				fName.setText("");
				mName.setEditable(false);
				mName.setText("");
				lName.setEditable(false);
				lName.setText("");
				author.setEditable(true);
			}
		});
		rdbtnFname.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fName.setEditable(true);
				mName.setEditable(true);
				lName.setEditable(true);
				author.setEditable(false);
				author.setText("");
			}
		});

		JButton btnCheckOut = new JButton("Check Out");
		btnCheckOut.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCheckOut.setBackground(Color.LIGHT_GRAY);
		lblPleaseEnterValid = new JLabel(
				"Please enter valid card number to checkout");
		lblPleaseEnterValid.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPleaseEnterValid.setForeground(Color.RED);
		lblPleaseEnterValid.setVisible(false);

		lblPleaseFindA = new JLabel("Please search a book to checkout");
		lblPleaseFindA.setForeground(Color.RED);
		lblPleaseFindA.setVisible(false);

		btnCheckOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table == null) {
					lblPleaseFindA.setText("Please search a book to checkout");
					lblPleaseFindA.setForeground(Color.RED);
					lblPleaseFindA.setVisible(true);
					return;
				}
				if (scardno.getText().isEmpty()) {
					lblPleaseEnterValid
							.setText("Card no is needed to check out");
					lblPleaseEnterValid.setForeground(Color.RED);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				lblPleaseFindA.setVisible(false);
				lblPleaseEnterValid.setVisible(false);
				int row = table.getSelectedRow();
				if (row == -1) {
					lblPleaseFindA.setText("Please select a row to checkout");
					lblPleaseFindA.setForeground(Color.RED);
					lblPleaseFindA.setVisible(true);
					return;
				}

				System.out.println(table.getValueAt(row, 0));
				String bookid = (String) table.getValueAt(row, 0);
				String brachid = (String) table.getValueAt(row, 3);
				String total = (String) table.getValueAt(row, 4);
				String available = (String) table.getValueAt(row, 5);

				System.out.println(bookid + " " + brachid + " " + total + " "
						+ available);
				if (Integer.parseInt(available) == 0) {
					lblPleaseEnterValid
							.setText("Selected book is not available, Please select another row to checkout");
					lblPleaseEnterValid.setForeground(Color.RED);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				// check card validity
				dataValues = DBTest
						.getData("select count(*) from borrower where card_no ="
								+ scardno.getText() + ";");

				if (dataValues == null) {
					lblPleaseEnterValid
							.setText("Invalid card no, Please enter valid card no");
					lblPleaseEnterValid.setForeground(Color.RED);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				// Max checkouts is reached
				dataValues = DBTest
						.getData("select count(*) from book_loans where card_no = "
								+ scardno.getText()
								+ " and  book_loans.date_in is null;");

				if (dataValues != null
						&& Integer.parseInt(dataValues[0][0]) >= 3) {
					lblPleaseEnterValid
							.setText("Reached maximum number of Checkouts on this card!");
					lblPleaseEnterValid.setForeground(Color.RED);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				// curdate(),
				String inquery = "insert into Book_Loans(book_id,branch_id,card_no,date_out,due_date) values('"
						+ bookid
						+ "',"
						+ brachid
						+ ",'"
						+ scardno.getText()
						+ "', curdate() , date_add(curdate(),Interval 14 Day)"
						+ ");";
				int res = DBTest.updateData(inquery);
				System.out.println("No of rows updated/insterd are " + res);
				if (res > 0) {
					populatet1();
					lblPleaseEnterValid.setText("Successfully checked out!");
					lblPleaseEnterValid.setForeground(Color.GREEN);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				// Do check out and update table

			}
		});

		scardno = new JTextField();
		scardno.setColumns(10);
		scardno.setToolTipText("Enter card no");
		// textField.set
		JLabel label = new JLabel("Card No");

		bAndT = new JRadioButton("And");
		bAndT.setSelected(true);

		bOrT = new JRadioButton("Or");

		tOrA = new JRadioButton("Or");

		tAndA = new JRadioButton("And");
		tAndA.setSelected(true);

		ButtonGroup groupBAndT = new ButtonGroup();
		groupBAndT.add(bAndT);
		groupBAndT.add(bOrT);

		ButtonGroup groupTAndA = new ButtonGroup();
		groupTAndA.add(tOrA);
		groupTAndA.add(tAndA);

		bAndT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("bAndTCond OR");
				bAndTCond = " and ";
			}
		});
		bOrT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("bAndTCond OR");
				bAndTCond = " or ";
			}
		});

		tAndA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("tAndACond AND");
				tAndACond = " and ";
			}
		});
		tOrA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("tAndACond OR");
				tAndACond = " or ";
			}
		});

		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2
				.setHorizontalGroup(gl_panel_2
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_2
										.createSequentialGroup()
										.addGap(217)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																label,
																GroupLayout.PREFERRED_SIZE,
																115,
																GroupLayout.PREFERRED_SIZE)
														.addGroup(
																gl_panel_2
																		.createParallelGroup(
																				Alignment.LEADING)
																		.addComponent(
																				lblTitle)
																		.addComponent(
																				lblBookId)
																		.addGroup(
																				gl_panel_2
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addComponent(
																								rdbtnFname)
																						.addComponent(
																								rdbtnFullName))))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panel_2
																		.createSequentialGroup()
																		.addGap(0)
																		.addGroup(
																				gl_panel_2
																						.createParallelGroup(
																								Alignment.LEADING,
																								false)
																						.addComponent(
																								title)
																						.addComponent(
																								bookID,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								gl_panel_2
																										.createSequentialGroup()
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												fName,
																												GroupLayout.PREFERRED_SIZE,
																												164,
																												GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												mName,
																												GroupLayout.PREFERRED_SIZE,
																												177,
																												GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								gl_panel_2
																										.createSequentialGroup()
																										.addComponent(
																												bAndT)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												bOrT)))
																		.addGap(10)
																		.addComponent(
																				lName,
																				GroupLayout.PREFERRED_SIZE,
																				167,
																				GroupLayout.PREFERRED_SIZE))
														.addComponent(
																author,
																GroupLayout.PREFERRED_SIZE,
																330,
																GroupLayout.PREFERRED_SIZE)
														.addGroup(
																gl_panel_2
																		.createSequentialGroup()
																		.addComponent(
																				tAndA)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				tOrA))
														.addGroup(
																gl_panel_2
																		.createParallelGroup(
																				Alignment.TRAILING,
																				false)
																		.addComponent(
																				lblPleaseEnterValid,
																				Alignment.LEADING,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addGroup(
																				Alignment.LEADING,
																				gl_panel_2
																						.createSequentialGroup()
																						.addGroup(
																								gl_panel_2
																										.createParallelGroup(
																												Alignment.LEADING,
																												false)
																										.addComponent(
																												btnClear,
																												GroupLayout.DEFAULT_SIZE,
																												GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addGroup(
																												gl_panel_2
																														.createSequentialGroup()
																														.addGroup(
																																gl_panel_2
																																		.createParallelGroup(
																																				Alignment.TRAILING,
																																				false)
																																		.addComponent(
																																				search,
																																				Alignment.LEADING,
																																				GroupLayout.DEFAULT_SIZE,
																																				GroupLayout.DEFAULT_SIZE,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				scardno,
																																				Alignment.LEADING))
																														.addPreferredGap(
																																ComponentPlacement.RELATED)
																														.addComponent(
																																btnCheckOut)))
																						.addPreferredGap(
																								ComponentPlacement.RELATED)
																						.addComponent(
																								lblPleaseFindA))))
										.addContainerGap(47, Short.MAX_VALUE)));
		gl_panel_2
				.setVerticalGroup(gl_panel_2
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_2
										.createSequentialGroup()
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblBookId)
														.addComponent(
																bookID,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(bAndT)
														.addComponent(bOrT))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																title,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblTitle))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(tAndA)
														.addComponent(tOrA))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																rdbtnFullName)
														.addComponent(
																author,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.UNRELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																rdbtnFname)
														.addComponent(
																fName,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																mName,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lName,
																GroupLayout.PREFERRED_SIZE,
																22,
																GroupLayout.PREFERRED_SIZE))
										.addGap(19)
										.addComponent(search)
										.addPreferredGap(
												ComponentPlacement.UNRELATED)
										.addGroup(
												gl_panel_2
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																scardno,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(label)
														.addComponent(
																btnCheckOut)
														.addComponent(
																lblPleaseFindA))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(lblPleaseEnterValid)
										.addGap(15).addComponent(btnClear)
										.addContainerGap(25, Short.MAX_VALUE)));
		panel_2.setLayout(gl_panel_2);
		panel_1.add(scrollPane_1);

		JPanel Checkin = new JPanel();
		tabbedPane.addTab("Checkin", null, Checkin, null);
		Checkin.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel = new JPanel();
		Checkin.add(panel);
		sp = new JScrollPane();
		chinbook_id = new JTextField();
		chinbook_id.setColumns(10);

		JLabel lblBookId_1 = new JLabel("Book ID");

		cardno = new JTextField();
		cardno.setColumns(10);

		JLabel lblCardNo = new JLabel("Card No");

		bname = new JTextField();
		bname.setColumns(10);

		JLabel lblName = new JLabel("Borrower Name");

		month = new JTextField();
		month.setColumns(10);
		invaliddate = new JLabel("Please enter the Date in valide format");
		invaliddate.setForeground(Color.RED);
		invaliddate.setVisible(false);

		checkinfail = new JLabel("Please search to do check in");
		checkinfail.setForeground(Color.RED);
		checkinfail.setVisible(false);

		JLabel lblCheckInDate = new JLabel("Check In Date");

		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populatet2();
			}
		});

		JButton btncheckIn = new JButton("Check In");
		btncheckIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (table1 == null) {
					checkinfail.setText("Please search a book to check in");
					checkinfail.setForeground(Color.RED);
					checkinfail.setVisible(true);
					return;
				}
				String m = month.getText().trim();
				String y = year.getText().trim();
				String d = date.getText().trim();
				if (m.isEmpty() || y.isEmpty() || d.isEmpty()) {
					invaliddate.setText("Please fill all the date fields");
					invaliddate.setForeground(Color.RED);
					invaliddate.setVisible(true);
					return;
				}
				invaliddate.setVisible(false);
				checkinfail.setVisible(false);
				int row = table1.getSelectedRow();
				if (row == -1) {
					checkinfail.setText("Please select a row to check in");
					checkinfail.setForeground(Color.RED);
					checkinfail.setVisible(true);
					return;
				}

				System.out.println(table1.getValueAt(row, 0));
				String loanid = (String) table1.getValueAt(row, 0);
				String bookid = (String) table1.getValueAt(row, 1);
				String branchid = (String) table1.getValueAt(row, 2);
				String cardno = (String) table1.getValueAt(row, 3);
				String dateout = (String) table1.getValueAt(row, 4);
				System.out.println(loanid + " " + bookid + " " + branchid + " "
						+ cardno + "  " + dateout);
				// curdate(),

				String inquery = "update Book_Loans set date_in = '" + y + "-"
						+ m + "-" + d + "' where loan_id = " + loanid + ";";
				System.out.println(inquery);

				int res = DBTest.updateData(inquery);
				System.out.println("No of rows updated/insterd are " + res);
				if (res > 0) {
					populatet2();
					lblPleaseEnterValid.setText("Successfully checked out!");
					lblPleaseEnterValid.setForeground(Color.GREEN);
					lblPleaseEnterValid.setVisible(true);
					return;
				}
				// Do check out and update table
			}
		});

		JLabel lblMmddyyyy = new JLabel("MM\\DD\\YYYY");

		JLabel label_1 = new JLabel("\\");

		date = new JTextField();
		date.setColumns(10);

		JLabel label_2 = new JLabel("\\");

		year = new JTextField();
		year.setColumns(10);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGap(147)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(lblBookId_1)
												.addComponent(lblCardNo)
												.addComponent(lblName)
												.addComponent(lblCheckInDate))
								.addGap(27)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(checkinfail)
												.addComponent(
														btncheckIn,
														GroupLayout.PREFERRED_SIZE,
														86,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(invaliddate)
												.addGroup(
														gl_panel.createParallelGroup(
																Alignment.LEADING,
																false)
																.addComponent(
																		cardno)
																.addComponent(
																		chinbook_id)
																.addComponent(
																		btnSearch,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		bname))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addComponent(
																		month,
																		GroupLayout.PREFERRED_SIZE,
																		25,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		label_1)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		date,
																		GroupLayout.PREFERRED_SIZE,
																		26,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		label_2)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		year,
																		GroupLayout.PREFERRED_SIZE,
																		54,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(18)
																.addComponent(
																		lblMmddyyyy)))
								.addContainerGap(434, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														chinbook_id,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblBookId_1))
								.addGap(18)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														cardno,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblCardNo))
								.addGap(18)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														bname,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblName))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnSearch)
								.addGap(8)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblCheckInDate)
												.addComponent(
														month,
														GroupLayout.PREFERRED_SIZE,
														20,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(label_1)
												.addComponent(
														date,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(label_2)
												.addComponent(
														year,
														GroupLayout.PREFERRED_SIZE,
														20,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblMmddyyyy))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(invaliddate)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btncheckIn).addGap(18)
								.addComponent(checkinfail)
								.addContainerGap(63, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		Checkin.add(sp);

		JPanel Borrower = new JPanel();
		tabbedPane.addTab("Add Borrower", null, Borrower, null);
		Borrower.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_3 = new JPanel();
		Borrower.add(panel_3);

		lblBorrMsg = new JLabel(
				"Please enter valid data/ User already exsits in the data base");
		lblBorrMsg.setForeground(Color.RED);
		lblBorrMsg.setVisible(false);

		bfname = new JTextField();
		bfname.setColumns(10);

		JLabel lblFirstName = new JLabel("First Name");

		blname = new JTextField();
		blname.setColumns(10);

		JLabel lblLastName = new JLabel("Last Name");

		JLabel lblAddress = new JLabel("Address line 1");

		badd1 = new JTextField();
		badd1.setColumns(10);

		JLabel lblAddressLine = new JLabel("Address line 2");

		badd2 = new JTextField();
		badd2.setColumns(10);

		city = new JTextField();
		city.setColumns(10);

		state = new JTextField();
		state.setColumns(10);

		JLabel lblCity = new JLabel("City");

		JLabel lblState = new JLabel("State");

		phone = new JTextField();
		phone.setColumns(10);

		JLabel lblPhoneNo = new JLabel("Phone no");

		JButton btnNewButton = new JButton("Submit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ph = phone.getText().trim();
				String st = state.getText().trim();
				String ct = city.getText().trim();
				String a2 = badd2.getText().trim();
				String a1 = badd1.getText().trim();
				String bl = blname.getText().trim();
				String bf = bfname.getText().trim();

				if (ph.isEmpty() || st.isEmpty() || ct.isEmpty()
						|| a2.isEmpty() || a1.isEmpty() || bl.isEmpty()
						|| bf.isEmpty()) {
					lblBorrMsg
							.setText("Please enter valid data into all the fields");
					lblBorrMsg.setForeground(Color.RED);
					lblBorrMsg.setVisible(true);
					System.out.println("Please enter all the fields");
					return;
				}

				// String q = "select * from borrower where fname= '" + bf
				// + "' and lname= '" + bl + "' and address= '" + a1 + " "
				// + a2 + "' and city= '" + ct + "' and state= '" + st
				// + "' and phone= '" + ph + "';";
				String q = "insert into borrower(fname,lname,address,city,state,phone) values( '"
						+ bf
						+ "','"
						+ bl
						+ "','"
						+ a1
						+ " "
						+ a2
						+ "','"
						+ ct
						+ "','" + st + "','" + ph + "');";

				System.out.println(q);
				int res = DBTest.updateData(q);

				System.out.println("Trying to insert data...");
				System.out.println("reslut " + res);
				if (res < 0) {
					lblBorrMsg.setText("User already exsits in the data base");
					lblBorrMsg.setForeground(Color.RED);
					lblBorrMsg.setVisible(true);
					return;
				} else {
					lblBorrMsg
							.setText("Successfully loaded user into the data base");
					lblBorrMsg.setForeground(Color.GREEN);
					lblBorrMsg.setVisible(true);
				}

			}
		});

		JButton btnNewButton_1 = new JButton("Clear All");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				phone.setText("");
				state.setText("");
				city.setText("");
				badd2.setText("");
				badd1.setText("");
				blname.setText("");
				bfname.setText("");
				lblBorrMsg.setText("");
			}
		});

		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3
				.setHorizontalGroup(gl_panel_3
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								gl_panel_3
										.createSequentialGroup()
										.addContainerGap(332, Short.MAX_VALUE)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panel_3
																		.createSequentialGroup()
																		.addComponent(
																				lblBorrMsg,
																				GroupLayout.PREFERRED_SIZE,
																				640,
																				GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addGroup(
																Alignment.TRAILING,
																gl_panel_3
																		.createSequentialGroup()
																		.addGroup(
																				gl_panel_3
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addGroup(
																								gl_panel_3
																										.createSequentialGroup()
																										.addGroup(
																												gl_panel_3
																														.createParallelGroup(
																																Alignment.LEADING)
																														.addComponent(
																																lblFirstName)
																														.addComponent(
																																lblLastName)
																														.addComponent(
																																lblAddress)
																														.addComponent(
																																lblAddressLine)
																														.addComponent(
																																lblCity)
																														.addComponent(
																																lblState)
																														.addComponent(
																																lblPhoneNo))
																										.addGap(31)
																										.addGroup(
																												gl_panel_3
																														.createParallelGroup(
																																Alignment.LEADING,
																																false)
																														.addComponent(
																																city)
																														.addComponent(
																																badd2)
																														.addComponent(
																																badd1)
																														.addComponent(
																																blname)
																														.addComponent(
																																bfname,
																																GroupLayout.DEFAULT_SIZE,
																																153,
																																Short.MAX_VALUE)
																														.addGroup(
																																gl_panel_3
																																		.createSequentialGroup()
																																		.addPreferredGap(
																																				ComponentPlacement.RELATED)
																																		.addGroup(
																																				gl_panel_3
																																						.createParallelGroup(
																																								Alignment.TRAILING,
																																								false)
																																						.addComponent(
																																								state,
																																								Alignment.LEADING)
																																						.addComponent(
																																								phone,
																																								Alignment.LEADING))
																																		.addPreferredGap(
																																				ComponentPlacement.RELATED,
																																				67,
																																				Short.MAX_VALUE)))
																										.addGap(31))
																						.addGroup(
																								gl_panel_3
																										.createSequentialGroup()
																										.addComponent(
																												btnNewButton,
																												GroupLayout.DEFAULT_SIZE,
																												276,
																												Short.MAX_VALUE)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)))
																		.addComponent(
																				btnNewButton_1)
																		.addGap(224)))));
		gl_panel_3
				.setVerticalGroup(gl_panel_3
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_3
										.createSequentialGroup()
										.addGap(58)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																bfname,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblFirstName))
										.addGap(30)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																blname,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblLastName))
										.addGap(28)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblAddress)
														.addComponent(
																badd1,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(29)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblAddressLine)
														.addComponent(
																badd2,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(28)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																city,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblCity))
										.addGap(26)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblState)
														.addComponent(
																state,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblPhoneNo)
														.addComponent(
																phone,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(lblBorrMsg)
										.addGap(18)
										.addGroup(
												gl_panel_3
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																btnNewButton_1)
														.addComponent(
																btnNewButton))
										.addContainerGap(196, Short.MAX_VALUE)));
		panel_3.setLayout(gl_panel_3);
	}
}
