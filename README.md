/** Name: Renkai Ji
* CS6360, Assignment 3
* Section 002
* Due Date: 11:59 pm, 12/08/13
*/


I. Purpose
----------

Create a database application in java that interfaces with a Library database with the following requirements.
1) GUI
2) Determining Book Availability
3) Checking Out Books
4) Checking Out Books
5) Create new borrowers



II. File list
--------------
Main.java 
//set up the MySQL connection and main frames

FirstTabPanel.java 
//Determining Book Availability
//Support substring matching

SecondTabPanel.java
//Checking Out Books
//Implementation with extra feature. If the number of BOOK_LOANS for a given book at a branch already equals the No_of_copies and my application will prompt a message suggesting another branch for you to check out based on the number of available books in each branch of the system.

ThirdTabPanel.java
//Checking Out Books

FourthTabPanel.java
//Create new borrowers


III. Compiling and Executing on command line
---------------------------------------------
on Eclipse,
   First Create a new package named library, import all the java source files. Make sure you have changed the user name and password to MySQL in Main.java to your own. Then click run.
