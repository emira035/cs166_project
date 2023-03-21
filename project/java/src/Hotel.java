/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;  

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   public static String globalUserID;

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 3: viewRooms(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql); break;
                   case 4: viewRecentBookingsfromCustomer(esql); break;
                   case 5: updateRoomInfo(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewBookingHistoryofHotel(esql); break;
                   case 8: viewRegularCustomers(esql); break;
                   case 9: placeRoomRepairRequests(esql); break;
                   case 10: viewRoomRepairHistory(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         globalUserID = userID;
         
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return userID;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static String getUserID() {
      return globalUserID;
   }

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {}
   public static void viewRooms(Hotel esql) {}
   public static void bookRooms(Hotel esql) {}


   public static void viewRecentBookingsfromCustomer(Hotel esql) {
      try{
         //Gets the customer information from UserID
         String query = String.format("SELECT b.hotelID, b.roomNumber, r.price, b.bookingDate FROM Rooms as r, RoomBookings as b WHERE b.hotelID = r.hotelID AND b.roomNumber = r.roomNumber AND b.customerID = '%s' ORDER BY b.* DESC LIMIT 5", getUserID());         

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
      
   }
   public static void updateRoomInfo(Hotel esql) {}

   public static void viewRecentUpdates(Hotel esql) {}

   public static void viewBookingHistoryofHotel(Hotel esql) {
      //Variables to keep track if user wants to enter a date range or not
      boolean dateRange = false;
      boolean noDateRange = false;
      try{
         //Variables to hold the 2 date inputs
         String firstDate = null;
         String secondDate = null;
         //An option to check if user enters 2 same date i.e. 2/12/2015-2/12/2015
         boolean sameDate = false;
         String query = "";
         String yesCheck = "Yes";
         String yCheck = "Y";
         String noCheck = "No";
         String nCheck = "N";

         //This can be move to where it checks where it is a manager before getting date inputs
         System.out.print("Do you want to enter date ranges?\n Enter Yes or No: ");
         String dateCheck = in.readLine();

         //This calls the global function to get UserID and stores it
         //Need this function in order to determine if it is a customer or manager 
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();

         if(tempString.equals("manager")) {
            //This check if user says yes to enter 2 dates: Yes/Y
            if(yesCheck.equalsIgnoreCase(dateCheck) || yCheck.equalsIgnoreCase(dateCheck)) {
               dateRange = true;
               System.out.print("Date range accepted\n");

               //This checks if user says no: No/n
            } else if (noCheck.equalsIgnoreCase(dateCheck) || nCheck.equalsIgnoreCase(dateCheck)) {
               noDateRange = true;
               System.out.print("No range needed\n");
            } else {
               //If user enters invalid select then it will not ask for date input
               //Such as Hello, 123, etc
               System.out.print("There is an error with date input\n");
            }

            //This checks if user wants to enter 2 valid date 
            if(dateRange == true && noDateRange == false) {

               System.out.print("\tEnter Begin Date (MM/DD/YYYY): ");
               SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
               Date startDob = dateFormat.parse(in.readLine());
               firstDate = dateFormat.format(startDob);

               System.out.print("\tEnter End Date (MM/DD/YYYY): ");
               Date lastDob = dateFormat.parse(in.readLine());
               secondDate = dateFormat.format(lastDob);

               //This checks if user enter an incorrect date
               //such as last date before the first which does not make sense
               //i.e. 2/12/2015 - 1/10/2012
               if(firstDate.compareTo(secondDate) > 0) {
                  System.out.println("Last Date cannot be before the first date");
               }

               //Checks if they enter the same date 
               if(firstDate.compareTo(secondDate) == 0) {
                  sameDate = true;
               }
            }

            //if user does not want to enter any date ranges
            if(noDateRange == true) {
               System.out.print("No date range required\n");
               //grab all booking information that is manage by the manager
               query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID", getUserID());         
            } else {
               //checks if user enter 2 valid dates or 2 same date
               //EX: 2/12/2015 - 3/4/2019 or 2/12/2015 - 2/12/2015
               if(sameDate == false) {
                  query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID AND (b.bookingDate BETWEEN '%s' AND '%s')", getUserID(), firstDate, secondDate);         
               } else {
                  query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID AND b.bookingDate = '%s'", getUserID(), firstDate);         
               }
            }

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
      } else {
         System.out.print("You are not a manager\n");
      }

      }catch(Exception e){
         //System.err.println (e.getMessage() + "?");
         if(dateRange == false) {
            System.out.print("");
         } else {
            //To avoid printing out error message for equalsIgnoreCase
            System.err.println (e.getMessage() + "!");
         }
      }
   }
   public static void viewRegularCustomers(Hotel esql) {
         try{
            //Get UserID
            String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", getUserID());
            List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
            String tempString = tempQuery.get(0).get(0).trim();
            
            if(tempString.equals("manager")) {

               System.out.print("\tEnter Hotel ID: ");
               Integer hotelID = Integer.parseInt(in.readLine());
               //need to check if hotelID belong to that manager
               String checkHotelID = String.format("SELECT hotelID FROM Hotel as h WHERE h.managerUserID = '%s' AND h.hotelID = '%d'", getUserID(), hotelID);
               int userRow = esql.executeQuery(checkHotelID);

               //if the hotelID belongs to the manager
               if(userRow > 0) {

                  //Check regular customer from hotelID 
                  String query = String.format("SELECT u.name, COUNT(u.name) FROM Users as u, RoomBookings as b WHERE b.customerID = u.userID AND b.hotelID = '%s' GROUP BY u.name ORDER BY COUNT(u.name) DESC LIMIT 5", hotelID);         
                  int rowCount = esql.executeQueryAndPrintResult(query);
                  System.out.println ("total row(s): " + rowCount);
               } else {
                  System.out.print("Hote does not belong to you\n");
               }
            } else {
               System.out.print("You are not a manager\n");
            }

         }catch(Exception e){
            System.err.println (e.getMessage());
         } 
   } 
   public static void placeRoomRepairRequests(Hotel esql) {
      try{
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();
                  
         if(tempString.equals("manager")) {
            System.out.print("\tEnter Hotel ID: ");
            Integer hotelID = Integer.parseInt(in.readLine());

            //need to check if hotelID belong to that manager
            String checkHotelID = String.format("SELECT hotelID FROM Hotel as h WHERE h.managerUserID = '%s' AND h.hotelID = '%d'", getUserID(), hotelID);
            int userRow = esql.executeQuery(checkHotelID);

            //if the hotelID belongs to the manager
            if(userRow > 0) {
               System.out.print("\tEnter Room Number: ");
               Integer roomNumber = Integer.parseInt(in.readLine());

               System.out.print("\tEnter Company ID: ");
               Integer companyID = Integer.parseInt(in.readLine());

               SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
               Date date = new Date();
               String currDate = dateFormat.format(date);            
               String curr2DateQuery = String.format("SELECT CURRENT_DATE");

               List<List<String>> tempQuery2 = esql.executeQueryAndReturnResult(curr2DateQuery);
               String tempString2 = tempQuery2.get(0).get(0).trim();

               System.out.print("Curr: " + tempString2 + "\n");

               //counts the amount of queries/data located in roomRepair
               //This helps with knowing the amount of queriest created
               String lastQuery = String.format("SELECT * FROM RoomRepairs");
               Integer queryAmount = esql.executeQuery(lastQuery);
               
               /*
                  This checks if there is an existing data from the given inputs
                  So why did I not use NOT IN, NOT EXISTS, DELETE the data
                  Notes:
                  There was error when inserting due to duplicate key value violates unique constraint pkey
                  There was an error when DELETING as there was no way to alter exist(delete) because of
                     duplicate key value violates unique constraint foreign key
               */
               String deleteQuery = String.format("SELECT * FROM RoomRepairs WHERE repairID = '%d' AND companyID = '%d' AND hotelID = '%d' AND roomNumber = '%d' AND repairDate = '%s'",queryAmount, companyID, hotelID, roomNumber, tempQuery2);
               Integer amount = esql.executeQuery(deleteQuery);
               //System.out.println ("total row(s) for repair queries: " + amount);

               //This checks if there is NO existing data by checking if there a  result
               if(amount == 0) {
                  
                  //after passing all checks, begin adding the data
                  String repairQuery = String.format("INSERT INTO RoomRepairs (repairID, companyID, hotelID, roomNumber, repairDate) VALUES ('%d','%d', '%d', '%d', '%s')", queryAmount+1, companyID, hotelID, roomNumber, tempString2);
                  esql.executeUpdate(repairQuery);
                  
                  //System.out.print("Queries 1 Done: " + queryAmount + "\n");
                  
                  String roomRequestQuery = String.format("INSERT INTO RoomRepairRequests (requestNumber, managerID, repairID) VALUES ('%d','%s', '%d')", queryAmount+1, getUserID(), queryAmount+1);
                  esql.executeUpdate(roomRequestQuery);

                  //System.out.print("Queries 2 Done: " + queryAmount + "\n");

                  int rowCount = esql.executeQueryAndPrintResult(repairQuery);
                  System.out.println ("total row(s) for room repair: " + rowCount);
                  rowCount = esql.executeQueryAndPrintResult(roomRequestQuery);
                  System.out.println ("total row(s) for repair request: " + rowCount);
               } else {
                  System.out.print("This row already exist\n");
               }
            } else {
               System.out.print("Hotel does not belong to you\n");
            }
         } else {
            System.out.print("You are not a manager\n");
         }

      }catch(Exception e){
         System.err.println (e.getMessage());
      }

   }

   public static void viewRoomRepairHistory(Hotel esql) {
      try{
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();
         
         if(tempString.equals("manager")) {
            //String query = String.format("SELECT r.companyID, r.hotelID, r.roomNumber, r.repairDate FROM RoomRepairs as r, RoomRepairRequests as h WHERE h.managerID = '%s' AND r.repairID = h.repairID", getUserID());         
            String query = String.format("SELECT r.* FROM RoomRepairs as r, RoomRepairRequests as h WHERE h.managerID = '%s' AND r.repairID = h.repairID", getUserID());         

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
         } else {
            System.out.print("You are not a manager\n");
         }

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

}//end Hotel
