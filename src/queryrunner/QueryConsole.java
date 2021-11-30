package queryrunner;
import java.util.Scanner;

/**
 * Query runner console application
 * @author Duc Vo
 */
public class QueryConsole {
   public QueryConsole(QueryRunner queryrunnerObj) {
      qr = queryrunnerObj;
      int queryChoice = 1;
      int numParams;
      int numberQueries;
      String [] paramString;
      Scanner keyboard = new Scanner(System.in);  // Create a Scanner object
      String hostname = "cs100.seattleu.edu";
      String username = "mm_cpsc502101team01";
      String password = "mm_cpsc502101team01Pass-";
      String database = "mm_cpsc502101team01";

      welcome();

      connect(hostname, username, password, database);

      do {
         numberQueries = qr.GetTotalQueries();
         System.out.println("QUERY MENU: ");
         for (int i=0; i < numberQueries; i++)
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

   private boolean connect(String host, String user, String pass,
                           String database) {
      boolean connectStatus = qr.Connect(host, user, pass, database);
      System.out.print(connectStatus?"Successfully":"Failed" );
      System.out.println(" connected to database");
      System.out.println(qr.GetError());
      if (!connectStatus) System.out.println(qr.GetError());
      return connectStatus;
   }

   private boolean disconnect() {
      boolean disconnectStatus = qr.Disconnect();
      System.out.print(disconnectStatus?"Successfully":"Failed");
      System.out.println(" disconnect from database");
      if (!disconnectStatus) System.out.println(qr.GetError());
      return disconnectStatus;
   }

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
   private void printUpdateQueryData(boolean status) {
      if (status) {
         int numRowUpdated = qr.GetUpdateAmount();
         System.out.printf("%d Row%s updated successfully!\n", numRowUpdated,
                 numRowUpdated > 1 ?"s were":" was");
      } else System.out.println(qr.GetError());
   }

   private void welcome() {
      System.out.printf("WELCOME TO %s\n", qr.GetProjectTeamApplication());
      System.out.println("==========================================");
   }

   private void goodbye() {
      System.out.println("==========================================");
      System.out.printf("THANK YOU FOR CHOOSING %s\n",
              qr.GetProjectTeamApplication());
   }

   private QueryRunner qr; // hold query runner object
}

//    You need to determine if it is a parameter query. If it is, then

//    you will need to ask the user to put in the values for the Parameters in your query
//    you will then call ExecuteQuery or ExecuteUpdate (depending on whether it is an action query or regular query)
//    if it is a regular query, you should then get the data by calling GetQueryData. You should then display this
//    output.
//    If it is an action query, you will tell how many row's were affected by it.
//
//    This is Psuedo Code for the task:
//    Connect()
//    n = GetTotalQueries()
//    for (i=0;i < n; i++)
//    {
//       Is it a query that Has Parameters
//       Then
//           amt = find out how many parameters it has
//           Create a paramter array of strings for that amount
//           for (j=0; j< amt; j++)
//              Get The Paramater Label for Query and print it to console. Ask the user to enter a value
//              Take the value you got and put it into your parameter array
//           If it is an Action Query then
//              call ExecuteUpdate to run the Query
//              call GetUpdateAmount to find out how many rows were affected, and print that value
//           else
//               call ExecuteQuery
//               call GetQueryData to get the results back
//               print out all the results
//           end if
//      }
//    Disconnect()


// NOTE - IF THERE ARE ANY ERRORS, please print the Error output
// NOTE - The QueryRunner functions call the various JDBC Functions that are in QueryJDBC. If you would rather code JDBC
// functions directly, you can choose to do that. It will be harder, but that is your option.
// NOTE - You can look at the QueryRunner API calls that are in QueryFrame.java for assistance. You should not have to
//    alter any code in QueryJDBC, QueryData, or QueryFrame to make this work.
//                System.out.println("Please write the non-gui functionality");
