package library;


import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;

public class SecondTabPanel extends JPanel {
	
	   private JTextField jtfBOOK_ID = new JTextField(15);
	   private JTextField jtfBRANCH_ID = new JTextField(15);
	   private JTextField jtfCARD_NO = new JTextField(30);
	   
	   private JButton jbtCHECK_OUT = new JButton("Check Out");
	   
	   private JPanel input = new JPanel(new BorderLayout());
	   private JPanel output = new JPanel(new BorderLayout());
	   private JTable table = null;
	   
	   private Connection con = null;
	   /** Initialize the applet */
	   public SecondTabPanel(Connection con) {
		   // Initialize database connection and create a Statement object
		   this.con = con;
		   jbtCHECK_OUT.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   jbtSearch_actionPerformed(e);
			   }
		   });
		   setLayout(new BorderLayout());
		   JPanel p1 = new JPanel(new GridLayout(3,2,5,5));
		   p1.add(new JLabel("Book ID (required)"));
		   p1.add(jtfBOOK_ID);
		   p1.add(new JLabel("Branch ID (required)"));
		   p1.add(jtfBRANCH_ID);
		   p1.add(new JLabel("Card Number (required)"));
		   p1.add(jtfCARD_NO);
		   
		   JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		   //p2.add(p1, BorderLayout.CENTER);
		   p2.add(jbtCHECK_OUT, BorderLayout.EAST);
		   jbtCHECK_OUT.setToolTipText("Please click this button to check out");
		   input.setBorder(new TitledBorder("Input"));
		   input.add(p1, BorderLayout.CENTER);
		   input.add(p2, BorderLayout.SOUTH);
		   input.setPreferredSize(new Dimension(640, 140));
		   output.setBorder(new TitledBorder("Check Out Information"));
		   add(input, BorderLayout.NORTH);
		   table = new JTable();
		   JScrollPane scrollPane = new JScrollPane( table );
		   
		   output.add(scrollPane);
		   output.setPreferredSize(new Dimension(640, 250));
		   add(output, BorderLayout.CENTER);
	   }

	   private void jbtSearch_actionPerformed(ActionEvent e) {
		   
		   String id1 = jtfBOOK_ID.getText();
		   String id2 = jtfBRANCH_ID.getText();
		   String cardNo = jtfCARD_NO.getText();
		   if(id1.length() ==0) {
			   JOptionPane.showMessageDialog(null, "Book ID is a required field, can't be null");
			   return;
		   }
		   else if(id2.length() == 0) {
			   JOptionPane.showMessageDialog(null, "Branch ID is a required field, can't be null");
			   return;
		   }
		   else if(cardNo.length() == 0) {
			   JOptionPane.showMessageDialog(null, "Card Number is a required field, can't be null");
			   return;
		   }
		   Vector<String> columnNames = new Vector<String>();
		   Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		   try {
			   String queryString = "select c.book_id, c.branch_id, c.no_of_copies, count(card_no) as num_out, (c.no_of_copies - count(card_no)) as num_avail "
			   		+ "from	"
			   		+ "(select * from book_copies where book_id = ?) as c	"
			   		+ "left join "
			   		+ "(select * from book_loans where book_id = ?) as b	"
			   		+ "on c.branch_id = b.branch_id	"
			   		+ "group by c.branch_id";
			   PreparedStatement pstmt = con.prepareStatement(queryString);
			   pstmt.setString(1, id1);
			   pstmt.setString(2, id1);
			   ResultSet rset1 = pstmt.executeQuery();
			 //  ResultSetMetaData rsmd1 = rset1.getMetaData();
			   HashMap<String, Integer> hm = new HashMap<String, Integer>();
			   String bestBranch = "1";
			   int bestCopies = 0;
			   while(rset1.next()) {
				   String branch_id = rset1.getString("branch_id");
				   int num = rset1.getInt("num_avail");
				   if(num > bestCopies) {
					   bestBranch = branch_id;
					   bestCopies = num;
				   }
				   hm.put(branch_id, num);
			   }
			   if(hm.get(id2)!=null) {
				   int numAvail = hm.get(id2);
				   if(numAvail <= 0) {
					   if(bestCopies != 0) {
						   JOptionPane.showMessageDialog(null, "No more books available in the this branch, but you may want to go to branch "
						   		+ bestBranch + ", since it has " + bestCopies + " copies left");
					   }
					   else
						   JOptionPane.showMessageDialog(null, "No more books available in the library");
				   }
				   else {
					   String qString = "select count(*) as num from book_loans where Card_no = ?";
					   PreparedStatement pstmt2 = con.prepareStatement(qString);
					   pstmt2.setString(1, cardNo);
					   ResultSet rset2 = pstmt2.executeQuery();
					   if(rset2.next()) {
						   int numberBorrowed = rset2.getInt("num");
						   if(numberBorrowed >= 3) {
							   JOptionPane.showMessageDialog(null, "This borrower has already 3 book loans");
						   }
						   else{
							   int result = checkOut(id1, id2, cardNo);
							   if(result >0) {
								   String qQuery = "select * from book_loans where book_id = ? and branch_id = ? and card_no = ?";
								   PreparedStatement pstmt3 = con.prepareStatement(qQuery);
								   pstmt3.setString(1, id1);
								   pstmt3.setString(2, id2);
								   pstmt3.setString(3, cardNo);
								   ResultSet rset3 = pstmt3.executeQuery();
								   ResultSetMetaData rsmd3 = rset3.getMetaData();
								   getData(columnNames,data,rset3, rsmd3);
							   }
							   else {
								   //JOptionPane.showMessageDialog(null, "Exception: No books checked out");
							   }
						   }
					   }
				   }
			   }
			   else {
				   JOptionPane.showMessageDialog(null, "No such book or no such branch exists");
			   }
		   }catch (SQLException ex) {
				   ex.printStackTrace();
			   } 
			   DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
			   table.setModel(dtm);
		       table.setFillsViewportHeight(true);
		   }
			
	   public int checkOut(String id1, String id2, String cardNo) throws SQLException{
		   int result = 0;
		   try
		   {
			   String qString = "insert into book_loans "
		   		+ "values(?, ?, ?, current_date(), addDate(current_date(), 14))";
			   PreparedStatement pstmt = con.prepareStatement(qString);
			   pstmt.setString(1, id1);
			   pstmt.setString(2, id2);
			   pstmt.setString(3, cardNo);
			   result = pstmt.executeUpdate();
		   } catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			   JOptionPane.showMessageDialog(null, e.getMessage());
		   }
		   return result;
	   }
	   public void getData(Vector<String> columnNames, Vector<Vector<Object>> data, ResultSet rset, ResultSetMetaData rsmd) throws SQLException{
		   int columnsNumber = rsmd.getColumnCount();
		   for(int i = 1; i<= columnsNumber; i++) {
			   columnNames.addElement(rsmd.getColumnName(i));
		   }
		  
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