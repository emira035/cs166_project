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
           // System.out.print("\033\143"); // clears console
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
                  usermenu = ShowManagerMode(esql,usermenu);
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


   public static boolean ShowManagerMode(Hotel esql, boolean usermenu){
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
                   case 20: 
                     authenticatedUser.UnAuthenticate();
                     usermenu = false; 
                     break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
         return usermenu;


   }

   public static boolean ShowCustomerMode(Hotel esql,boolean usermenu){

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
                   case 5: updateRoomInfo(esql); break;
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
               System.out.println(" User sucessfully created with userId= "+ esql.getNewUserID("SELECT last_value FROM users_userID_seq") + " and Name = "+usernameInput);
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
     


      //System.out.print("\033\143"); //clear console
      System.out.println("   USER LOGIN  ");
      System.out.println("---------------------------------------- \n");
      System.out.println("Please enter your username and password. \n");
      System.out.println("---------------------------------------- \n");
        try{
        System.out.print("User: ");
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
      
      System.out.println("\n---------------------------------\n");
      System.out.println("BROWSE HOTELS");
      System.out.println("\n---------------------------------\n");

      System.out.println("\nPlease input the following information.\n\n");

      try
      {
         
         System.out.print("Hotel ID: ");
         String hotelId = in.readLine();
         System.out.print("DATE: ");
          String Date = in.readLine();

          //LiST<List<String>>
      }
      catch(Exception e){
         System.out.println(e.getMessage());
      }




   } 

   public static void viewRooms(Hotel esql) {}//mine

   public static void bookRooms(Hotel esql) {}//mine

   public static void viewRecentBookingsfromCustomer(Hotel esql) {}
   public static void updateRoomInfo(Hotel esql) {} //mine
   public static void viewRecentUpdates(Hotel esql) {}
   public static void viewBookingHistoryofHotel(Hotel esql) {}
   public static void viewRegularCustomers(Hotel esql) {}
   public static void placeRoomRepairRequests(Hotel esql) {}
   public static void viewRoomRepairHistory(Hotel esql) {}


}//end Hotel

