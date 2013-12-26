package library;


import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;

public class ThirdTabPanel extends JPanel {
	
	   private JTextField jtfBOOK_ID = new JTextField(15);	   
	   private JTextField jtfCARD_NO = new JTextField(30);
	   private JTextField jtfFNAME = new JTextField(15);
	   private JTextField jtfLNAME = new JTextField(15);
	   
	   private JButton jbtSEARCH = new JButton("Search");
	   private JButton jbtCHECK_IN = new JButton("Check in");
	   private JPanel input = new JPanel(new BorderLayout());
	   private JPanel output = new JPanel(new BorderLayout());
	   private JTable table = null;
	   
	   private Connection con = null;
	   Vector<String> columnNames = null;
	   Vector<Vector<Object>> data = null;
	   /** Initialize the applet */
	   public ThirdTabPanel(Connection con) {
		   // Initialize database connection and create a Statement object
		   this.con = con;
		   jbtSEARCH.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   jbtSearch_actionPerformed(e);
			   }
		   });
		   jbtCHECK_IN.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   jbtCHECK_IN_actionPerformed(e);
			   }
		   });
		   setLayout(new BorderLayout());
		   JPanel p1 = new JPanel(new GridLayout(4,2,5,5));
		   p1.add(new JLabel("Book ID (optional)"));
		   p1.add(jtfBOOK_ID);
		   
		   p1.add(new JLabel("Card Number (optional)"));
		   p1.add(jtfCARD_NO);
		   
		   p1.add(new JLabel("First Name (optional)"));
		   p1.add(jtfFNAME);
		   p1.add(new JLabel("Last Name (optional)"));
		   p1.add(jtfLNAME);
		   JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		   //p2.add(p1, BorderLayout.CENTER);
		   p2.add(jbtSEARCH, BorderLayout.EAST);
		   jbtSEARCH.setToolTipText("Please click this button to check out");
		   input.setBorder(new TitledBorder("Input"));
		   input.add(p1, BorderLayout.CENTER);
		   input.add(p2, BorderLayout.SOUTH);
		   input.setPreferredSize(new Dimension(640, 165));
		   output.setBorder(new TitledBorder("Please select which book you want to check in and then click CHECK IN"));
		   add(input, BorderLayout.NORTH);
		   table = new JTable();
		   JScrollPane scrollPane = new JScrollPane( table );
		   JPanel p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		   p3.add(jbtCHECK_IN, BorderLayout.EAST);
		   output.add(scrollPane , BorderLayout.CENTER);
		   output.add(p3 , BorderLayout.SOUTH);
		   output.setPreferredSize(new Dimension(640, 215));
		   add(output, BorderLayout.CENTER);
	   }

	   private void jbtSearch_actionPerformed(ActionEvent e) {
		   
		   String id1 = jtfBOOK_ID.getText();
		   String cardNo = jtfCARD_NO.getText();
		   String fName = jtfFNAME.getText();
		   String lName = jtfLNAME.getText();
		   columnNames = new Vector<String>();
		   data = new Vector<Vector<Object>>();
		   try {
			   String queryString = "select book_id, branch_id, card_no, fname, lname, date_out, due_date from (book_loans natural join borrower) "
			   		+ "where book_id like ? "
			   		+ "and card_no like ? "
			   		+ "and fname like ? "
			   		+ "and lname like ?";
			   PreparedStatement pstmt = con.prepareStatement(queryString);
			   pstmt.setString(1, '%' + id1 + '%');
			   pstmt.setString(2, '%' + cardNo + '%');
			   pstmt.setString(3, '%' + fName + '%');
			   pstmt.setString(4, '%' + lName + '%');
			   ResultSet rset1 = pstmt.executeQuery();
			   ResultSetMetaData rsmd1 = rset1.getMetaData();
			   getData(columnNames, data, rset1, rsmd1);			   
		   }catch (SQLException ex) {
				   ex.printStackTrace();
			   } 
			   DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
			   table.setModel(dtm);
		       table.setFillsViewportHeight(true);
		   }
			
	   private void jbtCHECK_IN_actionPerformed(ActionEvent e) {
		   int[] rows = table.getSelectedRows();
		   if(rows.length ==0) {
			   JOptionPane.showMessageDialog(null, "Please search for books you want to check in first, and then select a book");
		   }
		   else {
			   
			   try {	
				   int result = 0;
				   for(int row: rows) {
					   Vector<Object> tuple = data.get(row);
					   data.remove(row);
					   String book_id = tuple.get(0).toString();
					   String branch_id = tuple.get(1).toString();
					   String card_no = tuple.get(2).toString();
					   result += checkIn(book_id, branch_id, card_no);
					  
				   }
				   if(result<=0){
					   JOptionPane.showMessageDialog(null, "Exception: No books checked out");
				   }
			   }catch (SQLException ex) {
				   ex.printStackTrace();
			   } 
			   DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
			   table.setModel(dtm);
		       table.setFillsViewportHeight(true);
		   }
	   }
	   public int checkIn(String bookid, String branch, String cardNo) throws SQLException{
		   int result = 0;
		   try
		   {
			   String qString = "delete from book_loans "
			   		+ "where book_id = ? "
			   		+ "and branch_id = ? "
			   		+ "and card_no = ?";
			   PreparedStatement pstmt = con.prepareStatement(qString);
			   pstmt.setString(1, bookid);
			   pstmt.setString(2, branch);
			   pstmt.setString(3, cardNo);
			   result = pstmt.executeUpdate();
		   } catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			   JOptionPane.showMessageDialog(null, e.getMessage());
		   }
		   return result;
	   }
	   public void getData(Vector<String> columnNames, Vector<Vector<Object>> data, 
			   ResultSet rset, ResultSetMetaData rsmd) throws SQLException{
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