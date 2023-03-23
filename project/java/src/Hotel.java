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
import java.lang.Math;
import java.util.Date;
import java.util.Calendar;
/*import java.sql.Date;*/
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */



public class Hotel {



public class AuthorizedUser{
   private int userId;
   private String username;
   private String usertype;
   private boolean _authenticated;

   public AuthorizedUser(){
      _authenticated = false;
   }

   public boolean isAuthenticated(){
      return _authenticated;
   }

   public int getUserID(){
      return userId;
   }

   public String getUserName(){
      return username;
   }
   public String getUserType(){
      return usertype;
   }
   public void SetInfo(int id,String user,String utype){
      username = user;
      userId = id;
      usertype = utype;
      _authenticated = true;
   }
    /* bs method i have to implemented because its a static class that i cant pass around varibles*/
   public void UnAuthenticate(){
       _authenticated = false;
   }

   public boolean HasElevatedRights(){

       if(usertype.equals("admin")|| usertype.equals("manager")){
         return true;
       }
       return false;
   }
   
}



   // reference to physical database connection.

   private static AuthorizedUser authenticatedUser=null;
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
      authenticatedUser = new AuthorizedUser();
      
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
            System.out.print("\033\143"); // clears console
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. New User Registration!");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT\n");

            switch (readChoice()){
               case 1: CreateNewCustomer(esql); break;
               case 2:  Login(esql);

                if (authenticatedUser.isAuthenticated()) {
              boolean usermenu = true;
              while(usermenu) {

               if(!authenticatedUser.HasElevatedRights()){
                   usermenu =ShowCustomerMode(esql,usermenu);
               }
               else{
                  
                  if(authenticatedUser.getUserType().equals("admin")){
                      usermenu = ShowAdminMode(esql,usermenu);
                  }
                  else{
                      usermenu = ShowManagerMode(esql,usermenu);
                  }
               }

              }
            }
            else
            {
               //System.out.print("\033\143"); // clears console
               System.out.println("\n -- Invalid Username and/or password. --\n");
               System.out.println("Returning to Menu, press Enter to continue...");
               System.in.read();
            }
            break;


               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch

           


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


   public static boolean ShowAdminMode(Hotel esql, boolean usermenu){
      System.out.print("\033\143"); // clears console

      System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("MAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Hotels within 30 units");
      System.out.println("2. View Rooms");
      System.out.println("3. Book a Room");
      System.out.println("4. View recent booking history");
      
      System.out.println("\nManagement");
      System.out.println(".........................\n");
       //the following functionalities basically used by managers
      System.out.println("5. Update Room Information");
      System.out.println("6. View Room Updates Logs ");
      System.out.println(".........................");
      System.out.println("20. Log out");

      switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql); break;
                   case 4: viewRecentBookingsfromCustomer(esql); break;
                   case 5: updateRoomInfo(esql); break;
                   case 6: viewRoomUpdatesLog(esql);break;
                   case 20: 
                     authenticatedUser.UnAuthenticate();
                     usermenu = false; 
                     break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
         return usermenu;
   }


   public static boolean ShowManagerMode(Hotel esql, boolean usermenu){
      System.out.print("\033\143"); // clears console
      System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("MAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Hotels within 30 units");
      System.out.println("2. View Rooms");
      System.out.println("3. Book a Room");
      System.out.println("4. View recent booking history");
      System.out.println(".........................\n");
      
      System.out.println("\nManagement");
      System.out.println(".........................\n");
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
                   case 10:viewRoomRepairHistory(esql); break;
                   case 20: 
                     authenticatedUser.UnAuthenticate();
                     usermenu = false;  
                     break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
         return usermenu;


   }

   public static boolean ShowCustomerMode(Hotel esql,boolean usermenu){

      System.out.print("\033\143"); // clears console
         System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("MAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Hotels within 30 units");
      System.out.println("2. View Rooms");
      System.out.println("3. Book a Room");
      System.out.println("4. View recent booking history");
      System.out.println(".........................");
      System.out.println("20. Log out");

      switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql); break;
                   case 4: viewRecentBookingsfromCustomer(esql); break;
                   case 20: 
                     authenticatedUser.UnAuthenticate();
                     usermenu = false; 
                     break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
                return usermenu;
   }

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

   public static void CreateNewCustomer(Hotel esql){
     
      String usernameInput;
      String passwordInput;
      String passwordInput2;
      boolean keeptrying = true;
      System.out.print("\033\143"); // clears console
      System.out.println("   New User Registration ");
      System.out.println("---------------------------------------- \n");
      System.out.println("Please enter the following information. \n");
      System.out.println(" - Password length must be greater than 5-20 characters \n");
      System.out.println(" - Password must not contain any spaces\n");
      System.out.println("---------------------------------------- \n");

while (keeptrying){
      try{

      System.out.print("Username: ");
      usernameInput = in.readLine();

 
      char[] inputs = System.console().readPassword("Password: ");
      passwordInput = String.valueOf(inputs);

      char[] inputs2 = System.console().readPassword("Confirm Password: ");
      passwordInput2 = String.valueOf(inputs2);

      //valid candidate for becoming a user, now we need to check if the user already exists!
      if(ValidatePassword(passwordInput,passwordInput2)){
         
         String query = String.format("SELECT * FROM USERS WHERE name='%s' ",usernameInput);
          int userNum = esql.executeQuery(query);
         
         //user already exists
         if(userNum > 0){
            keeptrying = true;
              System.out.println("\n A user with that name already exists! \n");
         }
         // we can go ahead and create the user
         else{
              keeptrying =false;
               String createQuery = String.format("INSERT INTO Users (name,password,userType) VALUES('%s','%s','%s')",usernameInput,passwordInput,"customer");
               esql.executeUpdate(createQuery);
               System.out.println(" User sucessfully created with userId = "+ esql.getNewUserID("SELECT last_value FROM users_userID_seq") + " and Name = "+usernameInput);
               return;
             }
      }
         System.out.println("\n1. Try again.");
         System.out.println("2. < Back to Main Menu \n\n");

         int choice = readChoice();

            switch(choice){
            case 1: keeptrying = true; break;
            case 2: keeptrying= false; break;
            default: keeptrying = false; break;
            }
          
      

      }

      catch(Exception e){
        System.out.println(e.getMessage());
      }
   }
   }//end CreateUser


   public static boolean ValidatePassword(String pass,String confirmpass){

      boolean validpass = true;
      if(!pass.equals(confirmpass)){
           System.out.println("\n- 'Password' and 'Confirm Password' are not same! -");
          validpass = false;
      }

       if(pass.contains(" ")){
           System.out.println("\n- Password must not contain Spaces! -");
           validpass = false;
      }

      if((pass.length()<5) || (pass.length() >20)){
           System.out.println("\n- Password must be between 5 and 20 characters!  -");
          validpass = false;
      }
         return validpass;
   }

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static void Login(Hotel esql){
      
      String usernameInput="";
      String passwordInput="";

      authenticatedUser.UnAuthenticate(); 
     
      System.out.print("\033\143"); //clear console
      System.out.println("----------------------------------------");
      System.out.println("   USER LOGIN  ");
      System.out.println("----------------------------------------");
      System.out.println("\nPlease enter your username and password. \n");
        try{
        System.out.print("Username: ");
        usernameInput = in.readLine();

      
        char[] inputs = System.console().readPassword("Password:");
         passwordInput = String.valueOf(inputs);

         String query = String.format("SELECT * FROM USERS WHERE name='%s' AND password = '%s' ",usernameInput,passwordInput );
        
     
        List<List<String>> results = esql.executeQueryAndReturnResult(query);

         // we found a User!
        if(results.size() > 0){

           int userId = Integer.parseInt(results.get(0).get(0));
           String name = results.get(0).get(1);
           String usertype = results.get(0).get(3).trim();
           authenticatedUser.SetInfo(userId,name,usertype);
        }
         
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {

      System.out.print("\033\143"); // clears console
    
      System.out.println("\n-------------------------------------------------------");
      System.out.println(" BROWSE HOTELS");
      System.out.println("-------------------------------------------------------\n");
        System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("\n Please input the following information.\n\n"); 

      try{
         System.out.print("Latitude: ");
         Double  latitude=  Double.parseDouble(in.readLine());
         
         System.out.print("Longitude: ");
         Double longitude = Double.parseDouble(in.readLine());


         String query = String.format("SELECT * FROM HOTEL h WHERE calculate_distance('%s','%s',h.latitude,h.longitude) <= 30",latitude,longitude);

         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         if(results.size()>0){
         
         System.out.println("\nHere are some Hotels within 30 units of distance.\n");
         //for each hotel in   
         System.out.println("\n-------------------------------------------------------");
         System.out.println( String.format("%-15s %-20s %-5s","HOTEL ID","Hotel Name","date established"));
         System.out.println("-------------------------------------------------------");
         for(List<String> hotel : results){
         
           String HotelID = hotel.get(0).trim();
           String HotelName = hotel.get(1).trim();
           String Established= hotel.get(4).trim();
           
            System.out.println( String.format("%-20s %-15s %-15s","     " +HotelID,HotelName,"     "+Established));
         }
          System.out.println("-------------------------------------------------------");

         }
         else{
             System.out.println("\n No nearby results found.");
         }

         in.readLine();
      }
      catch(Exception e){
           System.out.println(e.getMessage());
      }

     

   } 

   public static void viewRooms(Hotel esql) {

      System.out.print("\033\143"); // clears console
      System.out.println("\n---------------------------------");
      System.out.println("Browse for Hotel Rooms");
      System.out.println("---------------------------------\n");
      
      
      System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));

      System.out.println("\nPlease input the following information.\n\n");

      try{
         
         System.out.print("Hotel ID: ");
         String hotelId = in.readLine();
         System.out.print("DATE (MM/DD/YYYY): ");
          
         SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
         String datestring = in.readLine();
         Date date = dateFormat.parse(datestring);

           datestring = dateFormat.format(date);
          
          String query =String.format("SELECT r.roomNumber,r.price FROM Rooms r WHERE r.hotelID='%s' AND r.roomNumber NOT IN (SELECT b.roomNumber FROM RoomBookings b WHERE b.hotelID=r.hotelID AND b.bookingDate='%s')",hotelId,datestring);
          List<List<String>> results = esql.executeQueryAndReturnResult(query);

           if(results.size()>0){
            
            System.out.println("\nThe following rooms are available for the following date.");
            System.out.println("\n---------------------------");
            System.out.println( String.format("%-10s      %-10s","Room Number"," Price"));
            System.out.println("---------------------------");

            for(List<String> room : results){
         
             String roomNumber = room.get(0);
            String price = room.get(1);      
             System.out.println( String.format("   %-10s    $ %-10s","  " +roomNumber,price));
            }
            System.out.println("---------------------------");
             in.readLine();
          
         }
         

         else{
             System.out.println("\n No available rooms found.");
             in.readLine();
         }
         
      }
   
      catch(Exception e){
         System.out.println(e.getMessage());
         System.out.println("SOMETHING WENT WRONG!");
      }

   }//mine

   public static void bookRooms(Hotel esql) {

      System.out.print("\033\143"); // clears console
      System.out.println("\n---------------------------------");
      System.out.println("Book A Room");
      System.out.println("---------------------------------\n");
      System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("\nPlease input the following information.\n\n");
   try{

      System.out.print("Hotel ID: ");
      String hotelId = in.readLine();

      System.out.print("Room Number: ");
      String roomNumber = in.readLine();

      System.out.print("DATE (MM/DD/YYYY): ");
          
      SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
      String datestring = in.readLine();
      Date date = dateFormat.parse(datestring);

      datestring = dateFormat.format(date);
      
      //checks if the room even exists, no point in booking a room if there doesnt even exist that room
      String roomExistsQuery = String.format("SELECT * FROM rooms r WHERE r.roomNumber ='%s' AND r.HotelID ='%s' ",roomNumber,hotelId);      

      int num = esql.executeQuery(roomExistsQuery);

      // sure  there is a room that exists within the specified hotel, but can we book it on that date? lets seee
      if(num >0){

      // now i need to check  if the room is booked
      String query = String.format("SELECT r.roomnumber,r.price FROM Rooms r WHERE r.roomNumber IN ( SELECT b.roomNumber FROM RoomBookings b WHERE b.hotelID='%s' AND b.roomNumber='%s' AND b.bookingDate='%s')",hotelId,roomNumber,datestring);
      num = esql.executeQuery(query);

      //room is not available
      if(num>0){
        System.out.println("\n Unfortunately, the room is not available for this day.");
        in.readLine();
      }
      //room is available
      else{
        System.out.println("\nThe room is available to stay for this day.\n");
        String query2 = String.format("SELECT r.price FROM Rooms r WHERE r.roomNumber='%s' AND r.hotelID='%s'",roomNumber,hotelId);
          
        List<List<String>> results = esql.executeQueryAndReturnResult(query2);
        System.out.println("Price Per Night: $"+results.get(0).get(0));
       
       System.out.println("\n1. Book Room");
       System.out.println("2. Cancel \n");
       
       int choice = readChoice();

       switch(choice){
         case 1:
               //we book room
                String customerID = String.valueOf(authenticatedUser.getUserID());
                 String bookingID= String.valueOf(esql.getNewUserID("SELECT nextval('RoomBookings_bookingID_seq')"));
                String query3 = String.format("INSERT INTO RoomBookings(bookingID,customerID,hotelID,roomNumber,bookingDate) VALUES('%s','%s','%s','%s','%s')",bookingID,customerID,hotelId,roomNumber,datestring);
                
                esql.executeUpdate(query3);
               System.out.println("\nThe room has now been booked.");
               in.readLine();
               break;
         default:
               System.out.println("\nThe room booking has been cancelled.");
                in.readLine();
               break;
            }
         }
      }
       else{
            System.out.println("\n No Hotel/Room exists with those values!");
            in.readLine();
         }
      }// end of try block
   catch(Exception e){
      System.out.println(e.getMessage());
   }

   //mine
}
   public static void viewRecentBookingsfromCustomer(Hotel esql) {
       try{
          System.out.print("\033\143"); // clears console
         //Gets the customer information from UserID

      System.out.println("\n-----------------------------------------------------");
      System.out.println("Recent bookings");
      System.out.println("-----------------------------------------------------\n");
      System.out.println(String.format("Welcome, %-5s\n",authenticatedUser.getUserName()));
      System.out.println("Here is a list of your recent bookings.\n");
         String query = String.format("SELECT b.hotelID, b.roomNumber, r.price, b.bookingDate FROM Rooms as r, RoomBookings as b WHERE b.hotelID = r.hotelID AND b.roomNumber = r.roomNumber AND b.customerID = '%s' ORDER BY b.* DESC LIMIT 5", authenticatedUser.getUserID());         

         //int rowCount = esql.executeQueryAndPrintResult(query);
         List<List<String>> results = esql.executeQueryAndReturnResult(query);
         System.out.println("-----------------------------------------------------");
         System.out.println(String.format("%-10s   %-10s    %-10s  %-10s","Hotel ID","Room Number","Price","Booking Date"));
         System.out.println("-----------------------------------------------------");
         for(List<String> booking :results){

            String hotelID = booking.get(0);
            String roomNumber = booking.get(1);
            String price = booking.get(2);
            String bookingDate = booking.get(3);

            System.out.println(String.format("   %-10s   %-10s  %-10s  %-10s",hotelID,roomNumber,price,bookingDate));
         }
         System.out.println("-----------------------------------------------------");
         System.out.println ("\ntotal results(s): " + results.size());
          in.readLine();
      }catch(Exception e){
         System.err.println (e.getMessage());

      }
   }


   public static void updateRoomInfo(Hotel esql) {
     try{
      System.out.print("\033\143"); // clears console
      System.out.println("\n--------------------------------------------------------");
      System.out.println(" UPDATE ROOM INFORMATION");
       System.out.println("--------------------------------------------------------\n");


      System.out.println(String.format("Welcome, %-5s",authenticatedUser.getUserName()));


      // we still have to check for admin for later updates
       int managerID = authenticatedUser.getUserID();
      System.out.println("\n You manage the following hotels currently.\n");


      String listHotelsAdminQuery =String.format("SELECT hotelID,hotelName, dateEstablished FROM hotel");
      String listHotelsManagerQuery = String.format("SELECT hotelID,hotelName, dateEstablished FROM hotel WHERE managerUserID ='%s'",managerID);

      //returns all the hotels managed by manager id
       List<List<String>> results;

      if(authenticatedUser.getUserType().equals("admin")){
         results = esql.executeQueryAndReturnResult(listHotelsAdminQuery);
      }
      //manager
      else{
         results = esql.executeQueryAndReturnResult(listHotelsManagerQuery);
      }

     System.out.println("--------------------------------------------------------");
     System.out.println( String.format(" %-15s %-20s %-5s","HOTEL ID","Hotel Name","date established"));
     System.out.println("--------------------------------------------------------");
         for(List<String> hotel : results){
         
           String HotelID = hotel.get(0).trim();
           String HotelName = hotel.get(1).trim();
           String Established= hotel.get(2).trim();
           
            System.out.println( String.format("%-20s %-15s %-15s","     " +HotelID,HotelName,"     "+Established));
         }
      System.out.println("--------------------------------------------------------");

      

      System.out.println("\n\nPlease input the following information.\n");


      System.out.print("HOTEL ID: ");
      String hotelId = in.readLine();
      System.out.print("Room Number: ");
      String roomNumber = in.readLine();

      
      
      //need to check if the manager manages this hotel,
      //String managesQuery = String.format("SELECT * FROM hotels WHERE managerUserID='%s' AND hotelID='%s'",managerID,hotelId);

      String AdminQuery = String.format("SELECT * FROM rooms r WHERE r.roomNumber='%s' AND r.hotelID ='%s'",roomNumber,hotelId);
      String ManagerQuery = String.format("SELECT * FROM rooms r WHERE r.roomNumber='%s' AND r.hotelID IN (SELECT h.hotelID FROM hotel h WHERE h.managerUserID='%s' AND h.hotelID='%s')",roomNumber,managerID,hotelId);
     
     if(authenticatedUser.getUserType().equals("admin")){
            results = esql.executeQueryAndReturnResult(AdminQuery);
     }
     else{
            results = esql.executeQueryAndReturnResult(ManagerQuery);
     }
   

      // I manage the selected hotel and room exists!
      if(results.size() >0){

          int roomprice = Integer.parseInt(results.get(0).get(2));
          int newRoomPrice = roomprice;
          String roomURL = results.get(0).get(3);
          String newURL = roomURL;
          boolean ChangesMade = false;
          boolean keepGoing = true;

         while(keepGoing){
         
         System.out.print("\033\143"); // clears console
          System.out.println("----------------------------------------------------------------------- ");
         System.out.println(String.format(" UPDATING INFORMATION FOR    Hotel ID: %s    Room Number: %s",hotelId,roomNumber));
         System.out.println("-----------------------------------------------------------------------\n");


         System.out.println("1) Update Price");
         System.out.println("2) Update Image URL");

         if(ChangesMade){
            System.out.println("3) Save Changes");
            System.out.println("4) Exit\n");
         }
         else{
            System.out.println("3) Exit\n");
         }
         System.out.println(String.format("Current Room Price: $ %s",roomprice));
          System.out.println(String.format("Image URL: %s \n",roomURL));
         int choice = readChoice();

       

         switch(choice)
         {
            case 1:
             System.out.print("\033\143"); 
             System.out.println("\n---------------------------------");
             System.out.println("  Update Price ");
            System.out.println("---------------------------------\n");
            System.out.println(String.format("Current Price: $ %s",roomprice));
             System.out.print("New Price: $ ");
             newRoomPrice = Integer.parseInt(in.readLine());

             System.out.println("\n1) Confirm");
             System.out.println("2) Cancel\n");

            int pricechoice = readChoice();
            
            if(pricechoice == 1){
               System.out.println(String.format("Hotel ID: %s  Room Number: %s Room price will be changed to $%s",hotelId,roomNumber,newRoomPrice));
               roomprice = newRoomPrice;
               ChangesMade= true;
               
            }
            break;

            case 2:
             System.out.print("\033\143"); 
             System.out.println("\n---------------------------------");
             System.out.println("  Update URL ");
             System.out.println("---------------------------------\n");
             System.out.println(String.format("Current Url: %s",roomURL));
             System.out.print("New URL: ");
             newURL= in.readLine();

             System.out.println("\n1) Confirm");
             System.out.println("2) Cancel");

            int urlchoice = readChoice();
            
            if(urlchoice == 1){
               System.out.println(String.format("Hotel ID: %s  Room Number: %s Image URL will be changed to: %s",hotelId,roomNumber,newURL));
               roomURL = newURL;
               ChangesMade= true;
            }
            break;

            case 3:
            //update here
            if(ChangesMade){

               Timestamp currenttime= Timestamp.from(Instant.now());
               String updateQuery = String.format("UPDATE rooms SET price='%s',imageURL='%s' WHERE hotelID='%s' AND roomNumber='%s'", roomprice,roomURL,hotelId,roomNumber);
               int RoomLogID= esql.getNewUserID("SELECT nextval('roomUpdatesLog_UpdateNumber_seq')");

               String updateRoomsLogQuery = String.format("INSERT INTO RoomUpdatesLog(updateNumber,managerID,hotelID,roomNumber,updatedOn) VALUES('%s','%s','%s','%s','%s')",RoomLogID,managerID,hotelId,roomNumber,currenttime);
               esql.executeUpdate(updateQuery);
               esql.executeUpdate(updateRoomsLogQuery);

               System.out.println("\n Room Information has been updated.");
              
            }
            keepGoing = false;
            break;
            default:
            keepGoing = false;
            break;
         }

         }


      }
      // i do not manage the hotel.
      else{
          System.out.println("Invalid Hotel ID and/or Room ID, or you do not  Manage this Hotel.");
      }

      in.readLine();
     }
     catch(Exception e){
        System.out.println(e.getMessage());
     }
   }


   public static void viewRoomUpdatesLog(Hotel esql){
      System.out.print("\033\143"); // clears console
       System.out.println("\n---------------------------------");
       System.out.print(" View Room Updates Log ");
       System.out.println("\n---------------------------------\n");

      System.out.println("Enter the following information.\n");

      try{
      System.out.print("Manager ID: ");
      String ManagerID = in.readLine();
      
      String query = String.format("SELECT * FROM RoomUpdatesLog WHERE managerID ='%s'",ManagerID);

      List<List<String>> results = esql.executeQueryAndReturnResult(query);

      if(results.size()> 0){
         
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println( String.format("%-10s  %-10s   %-10s %-10s          %-20s","Update No.","Manager ID","HotelID","Room Number","Updated on"));
        System.out.println("----------------------------------------------------------------------------");
         for(List<String> roomUpdate : results){
            
              String updateNumber = roomUpdate.get(0).trim();
              String hotelID = roomUpdate.get(2).trim();
              String roomNumber = roomUpdate.get(3).trim();
              String updatedOn = roomUpdate.get(4).trim();

              System.out.println( String.format("\n    %-10s  %-10s   %-10s   %-10s %-20s",updateNumber,ManagerID,hotelID,roomNumber,updatedOn));
         }
          System.out.println("----------------------------------------------------------------------------");
         in.readLine();
      }
      else{
         System.out.println("\n No updates found for the requested manager id.");
         in.readLine();
      }
      }
      catch(Exception e){
         System.out.println(e.getMessage());
       
      }

   }

   //this function needs to fixed
   public static void viewRecentUpdates(Hotel esql) {

      try{
         System.out.print("\033\143"); // clears console
         System.out.println("\n----------------------------------------------------------------------------");
         System.out.println(" View Last 5 Recent Room Updates");
         System.out.println("----------------------------------------------------------------------------\n");

          System.out.println(" Here are the last 5 recent updates made to rooms.\n");
         //Get UserID
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", authenticatedUser.getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();
         
         if(authenticatedUser.getUserType().equals("admin") || authenticatedUser.getUserType().equals("manager")) {

               String query = String.format("SELECT l.* FROM RoomUpdatesLog as l WHERE l.managerID = '%d' ORDER BY updatedOn DESC LIMIT 5",authenticatedUser.getUserID());         
               List<List<String> >results = esql.executeQueryAndReturnResult(query);
               
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println(String.format("%-10s   %-10s %-10s          %-20s","Update No.","HotelID","Room Number","Updated on"));
        System.out.println("----------------------------------------------------------------------------");
         for(List<String> roomUpdate : results){
            

              String updateNumber = roomUpdate.get(0).trim();
              String hotelID = roomUpdate.get(2).trim();
              String roomNumber = roomUpdate.get(3).trim();
              String updatedOn = roomUpdate.get(4).trim();

              System.out.println( String.format("\n    %-10s  %-10s   %-10s %-20s",updateNumber,hotelID,roomNumber,updatedOn));
         }
         System.out.println("----------------------------------------------------------------------------");
        
               System.out.println ("total row(s): " + results.size());
               in.readLine();

            
         } else {
            System.out.print("You are not a manager\n");
         }

      }catch(Exception e){
         System.out.println(e.getMessage());
      }
   }


   public static void viewBookingHistoryofHotel(Hotel esql) {

      System.out.print("\033\143"); // clears console
      System.out.println("\n--------------------------------------------------------------------------------");
      System.out.println("View Hotel Booking History");
      System.out.println("--------------------------------------------------------------------------------");

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
         System.out.print("Do you want to enter date ranges?\n \nEnter Yes or No: ");
         String dateCheck = in.readLine();

         //This calls the global function to get UserID and stores it
         //Need this function in order to determine if it is a customer or manager 
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", authenticatedUser.getUserID());
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
               System.out.print("\nNo range needed\n");
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
               System.out.print("\nNo date range required\n");
               //grab all booking information that is manage by the manager
               query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID", authenticatedUser.getUserID());         
            } else {
               //checks if user enter 2 valid dates or 2 same date
               //EX: 2/12/2015 - 3/4/2019 or 2/12/2015 - 2/12/2015
               if(sameDate == false) {
                  query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID AND (b.bookingDate BETWEEN '%s' AND '%s')", authenticatedUser.getUserID(), firstDate, secondDate);         
               } else {
                  query = String.format("SELECT DISTINCT b.bookingID, b.customerID, u.name, b.hotelID, b.roomNumber, b.bookingDate FROM RoomBookings as b, Users as u, Hotel as h WHERE h.managerUserID = '%s' AND b.hotelID = h.hotelID AND b.customerID = u.userID AND b.bookingDate = '%s'", authenticatedUser.getUserID(), firstDate);         
               }
            }
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println(String.format(" %-10s %-10s %-17s %-10s %-10s  %-10s","Booking ID ","Customer ID"," Customer Name","Hotel ID","Room Number","Booking Date"));
            System.out.println("--------------------------------------------------------------------------------");
            List<List<String>> results = esql.executeQueryAndReturnResult(query);

            for(List<String> booking : results){
                String bookingID = booking.get(0).trim();
                String customerID =booking.get(1).trim();
                String customerName = booking.get(2).trim();
                String hotelID = booking.get(3).trim();
                String roomnumber = booking.get(4).trim();
                String bookingDate = booking.get(5).trim();
               System.out.println(String.format("\n      %-10s %-8s %-17s  %-10s %-10s  %-10s",bookingID,customerID,customerName,hotelID,roomnumber,bookingDate));
            }
            System.out.println("--------------------------------------------------------------------------------");

            System.out.println ("total row(s): " + results.size());
            in.readLine();
      } else {
         System.out.print("You are not a manager\n");
         in.readLine();
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
            System.out.print("\033\143"); // clears console
            System.out.println("--------------------------------------------------------");
            System.out.println(" Regular Customers ");
            System.out.println("--------------------------------------------------------\n");
            //Get UserID
            String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", authenticatedUser.getUserID());
            List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
            String tempString = tempQuery.get(0).get(0).trim();
            
            if(tempString.equals("manager")) {
               
               String managerID = String.valueOf(authenticatedUser.getUserID());
               String listHotelsManagerQuery = String.format("SELECT hotelID,hotelName, dateEstablished FROM hotel WHERE managerUserID ='%s'",managerID);
                System.out.println("\n You manage the following hotels currently.\n");

                List<List<String>> results = esql.executeQueryAndReturnResult(listHotelsManagerQuery);
   
               System.out.println("--------------------------------------------------------");
               System.out.println( String.format(" %-15s %-20s %-5s","HOTEL ID","Hotel Name","date established"));
               System.out.println("--------------------------------------------------------");

               for(List<String> hotel : results){
         
               String HotelID = hotel.get(0).trim();
               String HotelName = hotel.get(1).trim();
               String Established= hotel.get(2).trim();
           
               System.out.println( String.format("%-20s %-15s %-15s","     " +HotelID,HotelName,"     "+Established));
               }

               System.out.println("--------------------------------------------------------\n");


               System.out.print("Enter Hotel ID: ");
               Integer hotelID = Integer.parseInt(in.readLine());
               //need to check if hotelID belong to that manager
               String checkHotelID = String.format("SELECT hotelID FROM Hotel as h WHERE h.managerUserID = '%s' AND h.hotelID = '%d'", authenticatedUser.getUserID(), hotelID);
               int userRow = esql.executeQuery(checkHotelID);

               System.out.println("\nHere are the top customers for that hotel.");
               //if the hotelID belongs to the manager
               if(userRow > 0) {
                  
                  //Check regular customer from hotelID 
                  String query = String.format("SELECT u.name, COUNT(u.name) FROM Users as u, RoomBookings as b WHERE b.customerID = u.userID AND b.hotelID = '%s' GROUP BY u.name ORDER BY COUNT(u.name) DESC LIMIT 5", hotelID);         
                  
                  results = esql.executeQueryAndReturnResult(query);

                  System.out.println("\n-----------------------------");
                  System.out.println(String.format("%-17s  %-10s","Customer Name","Bookings"));
                  System.out.println("-----------------------------");

                  for(List<String> customer: results) {
                     String customerName = customer.get(0).trim();
                     String bookings = customer.get(1).trim();
                     System.out.println(String.format("%-17s     %-13s",customerName,bookings));
                  }
                  System.out.println("-----------------------------");
                  in.readLine();
               } else {
                  System.out.print("Hotel does not belong to you\n");
                  in.readLine();
               }
            } else {
               System.out.print("You are not a manager\n");
               in.readLine();
            }

         }catch(Exception e){
            System.err.println (e.getMessage());
         }
   }
   public static void placeRoomRepairRequests(Hotel esql) {

      try{
         System.out.print("\033\143"); // clears console
         System.out.println("\n--------------------------------------------------------");
         System.out.println(" Place a Room Repair Request ");
          System.out.println("--------------------------------------------------------\n");
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'", authenticatedUser.getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();
                  
         if(tempString.equals("manager")) {

            String managerID = String.valueOf(authenticatedUser.getUserID());
            String listHotelsManagerQuery = String.format("SELECT hotelID,hotelName, dateEstablished FROM hotel WHERE managerUserID ='%s'",managerID);
            System.out.println("\n You manage the following hotels currently.\n");

                List<List<String>> results = esql.executeQueryAndReturnResult(listHotelsManagerQuery);
   
               System.out.println("--------------------------------------------------------");
               System.out.println( String.format(" %-15s %-20s %-5s","HOTEL ID","Hotel Name","date established"));
               System.out.println("--------------------------------------------------------");

               for(List<String> hotel : results){
         
               String HotelID = hotel.get(0).trim();
               String HotelName = hotel.get(1).trim();
               String Established= hotel.get(2).trim();
           
               System.out.println( String.format("%-20s %-15s %-15s","     " +HotelID,HotelName,"     "+Established));
               }

               System.out.println("--------------------------------------------------------\n");


               String listCompaniesQuery = String.format("SELECT * FROM MaintenanceCompany");
               System.out.println("\nHere are all the Maintenance companies we have on file.\n");

               results = esql.executeQueryAndReturnResult(listCompaniesQuery);
               System.out.println("--------------------------------------------------------");
               System.out.println( String.format("%-20s %-15s","Company ID","Company Name"));
               System.out.println("--------------------------------------------------------");

               for(List<String> company : results){
                  String companyID = company.get(0).trim();
                  String companyName = company.get(1).trim();
                   System.out.println( String.format("    %-20s %-15s",companyID,companyName));
               }
          System.out.println("--------------------------------------------------------\n");
            System.out.print("Enter Hotel ID: ");
            Integer hotelID = Integer.parseInt(in.readLine());

            //need to check if hotelID belong to that manager
            String checkHotelID = String.format("SELECT hotelID FROM Hotel as h WHERE h.managerUserID = '%s' AND h.hotelID = '%d'", authenticatedUser.getUserID(), hotelID);
            int userRow = esql.executeQuery(checkHotelID);

            //if the hotelID belongs to the manager
            if(userRow > 0) {
               System.out.print("\nEnter Room Number: ");
               Integer roomNumber = Integer.parseInt(in.readLine());

               System.out.print("\n Enter Company ID: ");
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
                  
                  String roomRequestQuery = String.format("INSERT INTO RoomRepairRequests (requestNumber, managerID, repairID) VALUES ('%d','%s', '%d')", queryAmount+1, authenticatedUser.getUserID(), queryAmount+1);
                  esql.executeUpdate(roomRequestQuery);

                  //System.out.print("Queries 2 Done: " + queryAmount + "\n");

                  int rowCount = esql.executeQueryAndPrintResult(repairQuery);
                  System.out.println ("total row(s) for room repair: " + rowCount);
                  rowCount = esql.executeQueryAndPrintResult(roomRequestQuery);
                  System.out.println ("total row(s) for repair request: " + rowCount);
                


               } else {
                  System.out.print("This row already exist\n");
                    in.readLine();
               }
            } else {
               System.out.print("Hotel does not belong to you\n");
                 in.readLine();
            }
         } else {
            System.out.print("You are not a manager\n");
              in.readLine();
         }

      }catch(Exception e){
         System.err.println (e.getMessage());
      }


   }
   public static void viewRoomRepairHistory(Hotel esql) {
       try{
         System.out.print("\033\143"); // clears console
         System.out.println("-----------------------------------------------------------------");
         System.out.println(" View Room Repair History");
        System.out.println("------------------------------------------------------------------");
         String exampleQuery = String.format("SELECT UserType FROM Users WHERE userID = '%s'",authenticatedUser.getUserID());
         List<List<String>> tempQuery = esql.executeQueryAndReturnResult(exampleQuery);
         String tempString = tempQuery.get(0).get(0).trim();
         
         if(tempString.equals("manager")) {
            //String query = String.format("SELECT r.companyID, r.hotelID, r.roomNumber, r.repairDate FROM RoomRepairs as r, RoomRepairRequests as h WHERE h.managerID = '%s' AND r.repairID = h.repairID", getUserID());         
            String query = String.format("SELECT r.* FROM RoomRepairs as r, RoomRepairRequests as h WHERE h.managerID = '%s' AND r.repairID = h.repairID", authenticatedUser.getUserID());         

            List<List<String>> results = esql.executeQueryAndReturnResult(query);
            
            System.out.println("\nHere is the repair history for your hotels.\n");
            System.out.println("------------------------------------------------------------------");
            System.out.println(String.format("%-10s %-10s %-10s %-10s %-10s","Repair ID","Company ID","Hotel ID","Room Number","Repair Date"));
            System.out.println("------------------------------------------------------------------");
            for(List<String> repairRequest: results){
               String repairID = repairRequest.get(0).trim();
               String companyID =repairRequest.get(1).trim();
               String HotelID = repairRequest.get(2).trim();
               String roomnumber = repairRequest.get(3).trim();
               String repairDate = repairRequest.get(4).trim();
               System.out.println(String.format("   %-10s %-10s %-10s %-10s %-10s",repairID,companyID,HotelID,roomnumber,repairDate));
            }


            System.out.println ("total row(s): " + results.size());
            in.readLine();
         } else {
            System.out.print("You are not a manager\n");
            in.readLine();
         }

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
}


}//end Hotel

