package library;


import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;

public class FourthTabPanel extends JPanel {
	   private JTextField jtfCARD_NO = new JTextField(30);
	   private JTextField jtfFNAME = new JTextField(25);
	   private JTextField jtfLNAME = new JTextField(25);
	   private JTextField jtfADDR = new JTextField(100);
	   private JTextField jtfCITY = new JTextField(30);
	   private JTextField jtfSTATE = new JTextField(20);
	   private JTextField jtfPHONE = new JTextField(10);
	   
	   private JButton jbtCHECK_OUT = new JButton("Create New Borrower");
	   
	   private JPanel input = new JPanel(new BorderLayout());
	   private JPanel output = new JPanel(new BorderLayout());
	   private JTable table = null;
	   
	   private Connection con = null;
	   /** Initialize the applet */
	   public FourthTabPanel(Connection con) {
		   // Initialize database connection and create a Statement object
		   this.con = con;
		   jbtCHECK_OUT.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   jbtSearch_actionPerformed(e);
			   }
		   });
		   setLayout(new BorderLayout());
		   JPanel p1 = new JPanel(new GridLayout(7,2,5,5));
		   p1.add(new JLabel("Card Number (required)"));
		   p1.add(jtfCARD_NO);
		   //JPanel pName = new JPanel(new GridLayout(1,2,5,5));
		   p1.add(new JLabel("First Name (required)"));
		   p1.add(jtfFNAME);
		   p1.add(new JLabel("Last Name (required)"));
		   p1.add(jtfLNAME);
		   p1.add(new JLabel("Street Address (required)"));
		   p1.add(jtfADDR);
		   p1.add(new JLabel("City (required)"));
		   p1.add(jtfCITY);
		   p1.add(new JLabel("State (required)"));
		   p1.add(jtfSTATE);
		   p1.add(new JLabel("Phone Number (optional)"));
		   p1.add(jtfPHONE);
		   
		   
		   JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		   //p2.add(p1, BorderLayout.CENTER);
		   p2.add(jbtCHECK_OUT, BorderLayout.EAST);
		   jbtCHECK_OUT.setToolTipText("Please click this button to check out");
		   input.setBorder(new TitledBorder("Input"));
		   input.add(p1, BorderLayout.CENTER);
		   input.add(p2, BorderLayout.SOUTH);
		   input.setPreferredSize(new Dimension(640, 250));
		   output.setBorder(new TitledBorder("Updated Information"));
		   add(input, BorderLayout.NORTH);
		   table = new JTable();
		   JScrollPane scrollPane = new JScrollPane( table );
		   
		   output.add(scrollPane);
		   output.setPreferredSize(new Dimension(640, 140));
		   add(output, BorderLayout.CENTER);
	   }

	   private void jbtSearch_actionPerformed(ActionEvent e) {
		   
		   String cardNo = jtfCARD_NO.getText();
		   String fName = jtfFNAME.getText();
		   String lName = jtfLNAME.getText();
		   String address = jtfADDR.getText();
		   String city = jtfCITY.getText();
		   String state = jtfSTATE.getText();
		   String phone = jtfPHONE.getText();
		   if(cardNo.length() ==0) {
			   JOptionPane.showMessageDialog(null, "Card Number is a required field, can't be null");
			   return;
		   }
		   else if(fName.length() == 0) {
			   JOptionPane.showMessageDialog(null, "First name is a required field, can't be null");
			   return;
		   }
		   else if(lName.length() == 0) {
			   JOptionPane.showMessageDialog(null, "Last name is a required field, can't be null");
			   return;
		   }
		   else if(address.length() == 0) {
			   JOptionPane.showMessageDialog(null, "Street Address is a required field, can't be null");
			   return;
		   }
		   else if(city.length() == 0) {
			   JOptionPane.showMessageDialog(null, "City is a required field, can't be null");
			   return;
		   }
		   else if(state.length() == 0) {
			   JOptionPane.showMessageDialog(null, "State is a required field, can't be null");
			   return;
		   }
		   String addr = address + ", " + city + ", " + state;
		   Vector<String> columnNames = new Vector<String>();
		   Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		   try {
			   String queryString = "select * from borrower where fname = ? and lname = ? and address = ?";
			   PreparedStatement pstmt = con.prepareStatement(queryString);
			   pstmt.setString(1, fName);
			   pstmt.setString(2, lName);
			   pstmt.setString(3, addr);
			   ResultSet rset1 = pstmt.executeQuery();
			   if(rset1.next()) {
				   String cardID = rset1.getString("card_no");
				   if(cardID.equals(cardNo)) {
					   JOptionPane.showMessageDialog(null, "This borrower has already been in the system");
					   return;
				   }
				   else {
					   JOptionPane.showMessageDialog(null, "Borrowers are allowed to possess exactly one library card, creating failure");
					   return;
				   }
			   }
			   else {
				   int result = createBorrower(cardNo, fName, lName, addr, phone);
				   if(result >0) {
					   String qQuery = "select * from borrower where card_no =?";
					   PreparedStatement pstmt3 = con.prepareStatement(qQuery);
					   pstmt3.setString(1, cardNo);
					   ResultSet rset3 = pstmt3.executeQuery();
					   ResultSetMetaData rsmd3 = rset3.getMetaData();
					   getData(columnNames,data,rset3, rsmd3);
				   }
				   else {
					   //JOptionPane.showMessageDialog(null, "Exception: No borrower created");
				   }
			   }
			   
		   }catch (SQLException ex) {
				   ex.printStackTrace();
			   } 
			   DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
			   table.setModel(dtm);
		       table.setFillsViewportHeight(true);
		   }
			
	   public int createBorrower(String cardNo, String fName, String lName, String addr, String phone) throws SQLException{
		   int result = 0;
		   try {
			   String qString = "insert into borrower values(?, ?, ?, ?, ?)";
			   PreparedStatement pstmt = con.prepareStatement(qString);
			   pstmt.setString(1, cardNo);
			   pstmt.setString(2, fName);
			   pstmt.setString(3, lName);
			   pstmt.setString(4, addr);
			   pstmt.setString(5, phone);
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