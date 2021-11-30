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

      connect(hostname, username, password, database);

   do {
      numberQueries = qr.GetTotalQueries();
      System.out.println("QUERY MENU: ");
      for (int i=0; i < numberQueries; i++)
         System.out.println("Query " + (i+1));

      System.out.print("Enter Query Number: ");
      queryChoice  = keyboard.nextInt() - 1;
      keyboard.nextLine();

      System.out.println("================ BEGIN QUERY ================");
      System.out.println(qr.GetQueryText(queryChoice));
      System.out.println("================= END QUERY =================\n");

      numParams = qr.GetParameterAmtForQuery(queryChoice);
      paramString = new String[numParams];
      System.out.println("Query Params: ");
      for (int i = 0; i < numParams; i++) {
         System.out.printf("Enter %s: ", qr.GetParamText(queryChoice, i));
         paramString[i] = keyboard.nextLine();
      }

      executeQuery(queryChoice, paramString);

      System.out.print("Continues? ");
   } while (keyboard.nextLine() == "");

   keyboard.close();
   disconnect();
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

   private void executeQuery(int queryChoice, String [] parmstring) {
      String [] headers;
      String [][] allData;
      boolean bOK = qr.ExecuteQuery(queryChoice, parmstring);
      if (bOK ==true)
      {
         headers = qr.GetQueryHeaders();
         allData = qr.GetQueryData();
         for (String label: headers ) {
            System.out.print(label);
            System.out.print("  |  ");
         }
         System.out.println();
         for (String[] row : allData) {
            for (String col: row) {
               System.out.print(col);
               System.out.print("  |  ");
            }
            System.out.println();
         }
      }
      else
      {
         System.out.println(qr.GetError());
      }
   }

   private QueryRunner qr; // hold query runner object
}
