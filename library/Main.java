package library;
 
/*
 * TabDemo.java
 */
 
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.*;
 
public class Main extends JApplet{
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/library";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "mm091088";
	// Statement for executing queries
	
	private Connection con = null;
	
    final static String TAG1 = "Search for Availability";
    final static String TAG2 = "Check Out Books";
    final static String TAG3 = "Check In Books";
    final static String TAG4 = "Create Borrowers";
    final static int extraWindowWidth = 100;
    
    private void initializeDB() {
		   try {
			   // Load the JDBC driver
			   Class.forName(JDBC_DRIVER);

			   // Establish a connection
			   con = DriverManager.getConnection(DB_URL,USER,PASS);
			   
		   }
		   catch (Exception ex) {
			   ex.printStackTrace();
		   }
	   }
    public void init() {
    	initializeDB();
    	setLayout(new BorderLayout());
    	JTabbedPane tabbedPane = new JTabbedPane();
    	JPanel card1Search = new JPanel();
    	JPanel firstPanel = new FirstTabPanel(con);
    	card1Search.add(firstPanel, BorderLayout.CENTER);
    	
    	JPanel card2CheckOut = new JPanel();
        card2CheckOut.add(new SecondTabPanel(con), BorderLayout.CENTER);
        
        JPanel card3CheckIn = new JPanel();
        card3CheckIn.add(new ThirdTabPanel(con), BorderLayout.CENTER);   
        
        JPanel card4CreateBorrower = new JPanel();
        card4CreateBorrower.add(new FourthTabPanel(con), BorderLayout.CENTER);
 
        tabbedPane.addTab(TAG1, card1Search);
        tabbedPane.addTab(TAG2, card2CheckOut);
        tabbedPane.addTab(TAG3, card3CheckIn);
        tabbedPane.addTab(TAG4, card4CreateBorrower);
        add(tabbedPane, BorderLayout.CENTER);
    }
   
    /** Main method */
    public static void main(String[] args) {
    	Main applet = new Main();
    	JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Library Management System");
		frame.add(applet, BorderLayout.CENTER);
		applet.init();
		applet.start();
		frame.setSize(680, 500);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.pack();
		frame.setVisible(true);
	}			       

}