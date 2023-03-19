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

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {


   }//end
   public static void viewRooms(Hotel esql) {
      try{
         System.out.print("\tEnter Hotel ID: ");
         Integer hotelID = Integer.parseInt(in.readLine());
         System.out.print("\tEnter Date (MM/DD/YYYY): ");
         SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
         Date dob = dateFormat.parse(in.readLine());
         String strDate = dateFormat.format(dob);
         
         /*
            Unsure if correct:
            
         */
         String query = String.format("SELECT r.roomNumber, r.price FROM Rooms as r, RoomBookings as b WHERE r.hotelID = '%d' AND r.hotelID = b.hotelID AND b.bookingDate = '%s'", hotelID, strDate);         

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   } //end
   public static void bookRooms(Hotel esql) {
      try{
         System.out.print("\tEnter Hotel ID: ");
         Integer hotelID = Integer.parseInt(in.readLine());

         System.out.print("\tEnter Room Number ");
         Integer room_Number = Integer.parseInt(in.readLine());

         System.out.print("\tEnter Date (MM/DD/YYYY): ");
         SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
         SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-mm-dd");

         Date dob = dateFormat.parse(in.readLine());
         String strDate = dateFormat.format(dob);
         
         /*
            Unsure if correct:
            
         */
         //String testQuery = String.format("SELECT b1.bookingDate FROM RoomBookings as b1 WHERE b1.roomNumber = '%d' AND b1.hotelID = '%d'", room_Number, hotelID);
         //String newTest = testQuery;
         //String query = dateFormat.format(oldDateFormat.parse(newTest));

         //String query = String.format("SELECT roomNumber FROM RoomBookings WHERE bookingDate != '%s'", strDate);
         //String query = String.format("SELECT b1.bookingID from RoomBookings as b1 WHERE b1.bookingDate IN ( SELECT b.bookingDate FROM RoomBookings as b WHERE b.hotelID = '%d' and b.roomNumber = '%d' )", hotelID, room_Number);         

         //String query = String.format("SELECT DISTINCT r.price FROM Rooms as r, RoomBookings as b3 WHERE b3.bookingDate NOT IN ( SELECT b1.bookingDate FROM RoomBookings as b1 WHERE b1.hotelID = '%d' AND b1.roomNumber = '%d' AND r.hotelID = '%d' AND r.roomNumber = '%d')", strDate, hotelID, room_Number, hotelID, room_Number, strDate); 

         String query = String.format("SELECT DISTINCT b.bookingID, r.price FROM Rooms as r, RoomBookings as b WHERE b.bookingDate = '%s' AND b.hotelID = '%d' AND b.roomNumber = '%d' AND r.hotelID = b.hotelID AND r.roomNumber = b.roomNumber", strDate, hotelID, room_Number);

         //WHERE b1.roomNumber = r.roomNumber AND b1.hotelID = r.hotelID
         //String query = String.format("SELECT DISTINCT r.price FROM Rooms as r, RoomBookings as b3 WHERE r.hotelID = '%d' AND r.roomNumber = '%d' AND b3.bookingDate NOT IN ( SELECT DISTINCT b1.bookingDate FROM RoomBookings as b1 WHERE b1.roomNumber = r.roomNumber AND b1.hotelID = r.hotelID )", hotelID, room_Number, strDate); 

         //String query = String.format("SELECT DISTINCT r.price FROM Rooms as r, RoomBookings as b3 WHERE r.hotelID = '%d' AND r.roomNumber = '%d' AND b3.bookingDate NOT IN ( SELECT DISTINCT b.bookingDate FROM Roombookings as b WHERE NOT EXISTS ( SELECT DISTINCT b1.bookingDate FROM RoomBookings as b1 WHERE b1.roomNumber = r.roomNumber AND b1.hotelID = r.hotelID ))", hotelID, room_Number, strDate); 
                       

         //String query = String.format("SELECT bookingDate FROM RoomBookings");


         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println ("room book");
         System.err.println (e.getMessage());
      }
   }


   public static void viewRecentBookingsfromCustomer(Hotel esql) {
      /*
      Need User id to make sure that it is a user not manager
      User id would be use to make sure it can not see other user
      See up to 5 last books made
      Get hotelID, room number, billing information, and date of booking
      {} = means not sure if needed

      EDIT: This needs to optimize where it should not ask for the 
      login information again.

      select b.hotelID, b.roomNumber, r.price, b.bookingDate
      from rooms r, roomBooking b
      where {[esql.user] = 'Customer' / esql.getNewUserID("SELECT last_value FROM users_userID_seq"}
      b.hotelID = r.hotelID, b.roomNumber = r.roomNumber 
      limit 5;

      */
      try{
         

         String userValue = LogIn(esql);

         String query = String.format("SELECT b.hotelID, b.roomNumber, r.price, b.bookingDate FROM Rooms as r, RoomBookings as b WHERE b.hotelID = r.hotelID AND b.roomNumber = r.roomNumber AND b.customerID = '%s' ORDER BY b.* DESC LIMIT 5", userValue);         

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
         //System.out.println("\nID" + esql.login(esql) + "\n");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
      
   }
   public static void updateRoomInfo(Hotel esql) {}

   public static void viewRecentUpdates(Hotel esql) {
      System.out.print("\tQuestion 6");
   }

   public static void viewBookingHistoryofHotel(Hotel esql) {
      /*
         Get bookingID, customer Name, hotelID, room number,
            date of booking for each booking

         Option to input range of date and show all booking in that range

         SELECT b.bookingID, u.name, b.hotelID, b.roomNumber
         FROM RoomBookings as b, Users as u
         Where (From_date BETWEEN ['x'] AND ['y'])
      */
      boolean dateResult = false;
      try{
         
         String firstDate = null;
         String secondDate = null;
         boolean sameDate = false;
         String query = "";
         String yesCheck = "Yes";
         String yCheck = "Y";
         String noCheck = "No";
         String nCheck = "N";
         System.out.print("Do you want to enter date ranges?\n Enter Yes or No: ");
         String dateCheck = in.readLine();

         /*
            Error printing with equalsIgnoreCase:
            Needs to print out error message if I do not get yes or no
               such as poop
            Add condition for Y and not just yes

            EDIT: Need to check if it is the hotel that the manager manage

         */

         if(yesCheck.equalsIgnoreCase(dateCheck) || yCheck.equalsIgnoreCase(dateCheck)) {
            dateResult = true;
         } else if (noCheck.equalsIgnoreCase(dateCheck) || nCheck.equalsIgnoreCase(dateCheck)) {
            dateResult = false;
            System.out.print("Exit\n");
         } else {
            System.out.print("There is an error with date input\n");
         }


         if(dateResult == true) {

            System.out.print("\tEnter Begin Date (MM/DD/YYYY): ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
            Date startDob = dateFormat.parse(in.readLine());
            firstDate = dateFormat.format(startDob);

            System.out.print("\tEnter End Date (MM/DD/YYYY): ");
            Date lastDob = dateFormat.parse(in.readLine());
            secondDate = dateFormat.format(lastDob);

            //Error checking
            if(firstDate.compareTo(secondDate) > 0) {
               System.out.println("Last Date cannot be before the first date");
            }
         }

         if(firstDate.compareTo(secondDate) == 0) {
            sameDate = true;
         }

         if(sameDate == false) {
            query = String.format("SELECT b.bookingID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u WHERE b.customerID = u.userID AND (b.bookingDate BETWEEN '%s' AND '%s')", firstDate, secondDate);         
         } else {
            query = String.format("SELECT b.bookingID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u WHERE b.customerID = u.userID AND b.bookingDate = '%s'", firstDate);         
         }

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);

      }catch(Exception e){
         if(dateResult == false) {
            System.out.print("");
         } else {
            //To avoid printing out error message for equalsIgnoreCase
            System.err.println (e.getMessage() + "!");
         }
      }

   }
   public static void viewRegularCustomers(Hotel esql) {
      try{
         System.out.print("\tEnter Hotel ID: ");
         Integer hotelID = Integer.parseInt(in.readLine());

         String query = String.format("SELECT u.name, COUNT(u.name) FROM Users as u, RoomBookings as b WHERE b.customerID = u.userID AND b.hotelID = '%s' GROUP BY u.name ORDER BY COUNT(u.name) DESC", hotelID);         


         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void placeRoomRepairRequests(Hotel esql) {

   }
   public static void viewRoomRepairHistory(Hotel esql) {
      try{
         
         System.out.print("\tEnter Hotel ID: ");
         Integer hotelID = Integer.parseInt(in.readLine());

         String query = String.format("SELECT u.name, COUNT(u.name) FROM Users as u, RoomBookings as b WHERE b.customerID = u.userID AND b.hotelID = '%s' GROUP BY u.name ORDER BY COUNT(u.name) DESC", hotelID);         


         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

}//end Hotel

