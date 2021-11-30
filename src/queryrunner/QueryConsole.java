package queryrunner;
import java.util.Scanner;

/**
 * Query runner console interface application
 * @author Duc Vo
 * @version 1.0.0
 */
public class QueryConsole {
   public QueryConsole() {
      this.qr = new QueryRunner();
      new QueryConsole(qr);
   }
   public QueryConsole(QueryRunner queryrunnerObj) {
      qr = queryrunnerObj;
      int queryChoice, numParams, numQueries;
      String [] paramString;
      Scanner keyboard = new Scanner(System.in);  // Create a Scanner object
      String hostname = "cs100.seattleu.edu";
      String username = "mm_cpsc502101team01";
      String password = "mm_cpsc502101team01Pass-";
      String database = "mm_cpsc502101team01";

      welcome();
      System.out.printf("Press Enter to use default DATABASE credential ");
      if (keyboard.nextLine().length() != 0) {
         System.out.print("Enter hostname: ");
         hostname = keyboard.nextLine();
         System.out.print("Enter username: ");
         username = keyboard.nextLine();
         System.out.print("Enter password: ");
         password = keyboard.nextLine();
         System.out.print("Enter database: ");
         database = keyboard.nextLine();
      }

      if (!connect(hostname, username, password, database))
         return; // quit program if fail to connect to database

      do {
         numQueries = qr.GetTotalQueries();
         System.out.println("QUERY MENU: ");
         for (int i=0; i < numQueries; i++)
            System.out.printf("Query %d - %s...\n" ,
                    (i+1),
                    qr.GetQueryText(i).substring(0,20));

         System.out.print("Enter Query Number: ");
         queryChoice  = keyboard.nextInt() - 1;
         keyboard.nextLine();

         System.out.println("================ BEGIN QUERY ================");
         System.out.println(qr.GetQueryText(queryChoice));
         System.out.println("================= END QUERY =================\n");

         System.out.print("Run this query? (y/n) ");
         if (keyboard.nextLine().toLowerCase().charAt(0) == 'y') {
            numParams = qr.GetParameterAmtForQuery(queryChoice);
            paramString = new String[numParams];
            System.out.println("QUERY PARAMS INPUT");
            for (int i = 0; i < numParams; i++) {
               System.out.printf("Enter %s: ", qr.GetParamText(queryChoice, i));
               paramString[i] = keyboard.nextLine();
            }

            executeQuery(queryChoice, paramString);
         }
         System.out.print("Continues? (y/n) ");
      } while (keyboard.nextLine().toLowerCase().charAt(0) == 'y');

      keyboard.close();
      disconnect();
      goodbye();
   }

   /**
    * This main method run the query console app directly in this class.
    * The query console app could be run in QueryRunner by passing '-console'
    * as argument in main method
    * @param args
    */
   public static void main(String[] args) {
      new QueryConsole();
   }

   /**
    *
    * @param host host name
    * @param user database username
    * @param pass database password
    * @param database default database name
    * @return connection status (boolean)
    */
   private boolean connect(String host, String user, String pass,
                           String database) {
      boolean connectStatus = qr.Connect(host, user, pass, database);
      System.out.print(connectStatus?"Successfully":"Failed" );
      System.out.println(" connected to database");
      System.out.println(qr.GetError());
      if (!connectStatus) System.out.println(qr.GetError());
      return connectStatus;
   }

   /**
    * Disconnect from database
    * @return disconnect status (boolean)
    */
   private boolean disconnect() {
      boolean disconnectStatus = qr.Disconnect();
      System.out.print(disconnectStatus?"Successfully":"Failed");
      System.out.println(" disconnect from database");
      if (!disconnectStatus) System.out.println(qr.GetError());
      return disconnectStatus;
   }

   /**
    * Determine if the query is action or non action, execute and display
    * result data or updated number of rows
    * @param queryChoice chosen query from menu
    * @param parmstring query parameters
    * @return execution status
    */
   private boolean executeQuery(int queryChoice, String [] parmstring) {
      boolean status;
      if (qr.isActionQuery(queryChoice)) {
         status = qr.ExecuteUpdate(queryChoice, parmstring);
         printUpdateQueryData(status);
      } else {
         status = qr.ExecuteQuery(queryChoice, parmstring);
         printExecuteQueryData(status);
      }
      return status;
   }

   /**
    * Print executed query data for non action query
    * @param status execution status
    */
   private void printExecuteQueryData(boolean status) {
      String [] headers;
      String [][] allData;
      if (status) {
         headers = qr.GetQueryHeaders();
         allData = qr.GetQueryData();
         for (String label: headers ) {
            System.out.print(label);
            System.out.print("  |  ");
         }
         System.out.println();
         if(allData.length < 1 ) {
            System.out.println("No Data");
         }
         else
            for (String[] row : allData) {
               for (String col: row) {
                  System.out.print(col!=""?col:"null");
                  System.out.print("  |  ");
               }
               System.out.println();
            }
      } else System.out.println(qr.GetError());
   }

   /**
    * Print update query result, number of updated rows if success
    * @param status execution status
    */
   private void printUpdateQueryData(boolean status) {
      if (status) {
         int numRowUpdated = qr.GetUpdateAmount();
         System.out.printf("%d Row%s updated successfully!\n", numRowUpdated,
                 numRowUpdated > 1 ?"s were":" was");
      } else System.out.println(qr.GetError());
   }

   /**
    * Welcome message
    */
   private void welcome() {
      System.out.printf("WELCOME TO %s\n", qr.GetProjectTeamApplication());
      System.out.println("==========================================");
   }

   /**
    * Goodbye message
    */
   private void goodbye() {
      System.out.println("==========================================");
      System.out.printf("THANK YOU FOR CHOOSING %s\n",
              qr.GetProjectTeamApplication());
   }

   /**
    * Hold querry runner object
    */
   private QueryRunner qr; // hold query runner object
}