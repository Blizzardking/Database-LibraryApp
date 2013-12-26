package library;


import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;

public class FirstTabPanel extends JPanel {
	
	   private JTextField jtfID = new JTextField(15);
	   private JTextField jtfTitle = new JTextField(30);
	   private JTextField jtfAuthor = new JTextField(30);
	   
	   private JButton jbtSearch = new JButton("Search");
	   
	   private JPanel input = new JPanel(new BorderLayout());
	   private JPanel output = new JPanel(new BorderLayout());
	   private JTable table = null;
	   
	   private Connection con = null;
	   /** Initialize the applet */
	   public FirstTabPanel(Connection con) {
		   // Initialize database connection and create a Statement object
		   this.con = con;
		   jbtSearch.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   jbtSearch_actionPerformed(e);
			   }
		   });
		   setLayout(new BorderLayout());
		   JPanel p1 = new JPanel(new GridLayout(3,2,5,5));
		   p1.add(new JLabel("Book ID (optional)"));
		   p1.add(jtfID);
		   p1.add(new JLabel("Book Title (optional)"));
		   p1.add(jtfTitle);
		   p1.add(new JLabel("Book Author (optional)"));
		   p1.add(jtfAuthor);
		   
		   JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		   //p2.add(p1, BorderLayout.CENTER);
		   p2.add(jbtSearch, BorderLayout.EAST);
		   jbtSearch.setToolTipText("Please click this button to search the library database");
		   input.setBorder(new TitledBorder("Input"));
		   input.add(p1, BorderLayout.CENTER);
		   input.add(p2, BorderLayout.SOUTH);
		   input.setPreferredSize(new Dimension(640, 140));
		   output.setBorder(new TitledBorder("Search Result"));
		   add(input, BorderLayout.NORTH);
		   table = new JTable();
		   JScrollPane scrollPane = new JScrollPane( table );
		   
		   output.add(scrollPane);
		   output.setPreferredSize(new Dimension(640, 250));
		   add(output, BorderLayout.CENTER);
	   }

	   private void jbtSearch_actionPerformed(ActionEvent e) {
		   
		   String id = jtfID.getText();
		   String title = jtfTitle.getText();
		   String name = jtfAuthor.getText();
		   Vector<String> columnNames = new Vector<String>();
		   Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		   try {
			   String queryString = "select * from book natural join book_authors "
				   		+ "where book.book_id like ? "
				   		+ "and book.title like ? "
				   		+ "and book_authors.author_name like ?";
			   PreparedStatement pstmt = con.prepareStatement(queryString);
			   pstmt.setString(1, '%' + id + '%');
			   pstmt.setString(2, '%' + title + '%');
			   pstmt.setString(3, '%' + name + '%');
			   ResultSet rset1 = pstmt.executeQuery();
			   ResultSetMetaData rsmd1 = rset1.getMetaData();
			   ArrayList<String> book_ids = new ArrayList<String>();
			   while (rset1.next())
			   {
				   String book_id = rset1.getString("book_id");
			       if(!book_ids.contains(book_id)) 
			    	   book_ids.add(book_id);
			   }
			   //System.out.println(book_ids);
			   if(book_ids.size() ==0) {
				   JOptionPane.showMessageDialog(null, "No books in the libray meeting your search criteria");
			   }
			   else {
				   boolean isFirst = true;
				   for(String book_id: book_ids) {
					   String qString = "select a.book_id, r.branch_id, a.no_of_copies, a.num_avail "
					       		+ "from library_branch as r, "
					       		+ "(select c.book_id, c.branch_id, c.no_of_copies, (c.no_of_copies - count(card_no)) as num_avail "
					       		+ "from "
					       		+ "(select * from book natural join book_copies where book_id = ?) as c "
					       		+ "left join "
					       		+ "(select * from book natural join book_loans where book_id = ?) as b "
					       		+ "on c.branch_id = b.branch_id "
					       		+ "group by c.branch_id) as a "
					       		+ "where r.branch_id = a.branch_id";
					       pstmt = con.prepareStatement(qString);
					       pstmt.setString(1, book_id);
					       pstmt.setString(2, book_id);
					       
					       ResultSet rset2 = pstmt.executeQuery();
					       ResultSetMetaData rsmd2 = rset2.getMetaData();
					       if(isFirst) {
					    	   int columnsNumber = rsmd2.getColumnCount();
					    	   for (int i = 1; i <= columnsNumber; i++)
							   {
								   String columnName = rsmd2.getColumnName(i);
								   columnNames.addElement(columnName);
					           }
					       }
					       getData(data, rset2, rsmd2);
					       isFirst = false;
					 
				   }
			       
			   }
			  /* if (rset.next()) {
				   String first = rset.getString(1);
				   String second = rset.getString(2);
				   String third = rset.getString(3);
				   // Display result in a dialog box
				   JOptionPane.showMessageDialog(null, first + " " + second +
						   " " + third);
			   } else {
				   // Display result in a dialog box
				   
			   }*/
		   }
		   catch (SQLException ex) {
			   ex.printStackTrace();
		   } 
		   DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
		   table.setModel(dtm);
	       table.setFillsViewportHeight(true);
	   }
	   public void getData(Vector<Vector<Object>> data, ResultSet rset, ResultSetMetaData rsmd) throws SQLException{
		   int columnsNumber = rsmd.getColumnCount();

		  
		   while (rset.next())
		   {
               Vector<Object> row = new Vector<Object>(columnsNumber);
		       for (int i = 1; i <= columnsNumber; i++)
		       {
		    	   row.addElement( rset.getObject(i) );
		    	 //  System.out.print(rset.getObject(i));
		       }
		       data.addElement( row );
		      // System.out.println();
		   }
	   }

 
	}