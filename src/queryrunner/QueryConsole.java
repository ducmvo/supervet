package queryrunner;
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class QueryConsole {
   public QueryConsole(QueryRunner queryrunnerObj) {
      qr = queryrunnerObj;

      String hostname = "cs100.seattleu.edu";
      String username = "mm_cpsc502101team01";
      String password = "mm_cpsc502101team01Pass-";
      String database = "mm_cpsc502101team01";

      connect(hostname, username, password, database);

      int m_queryChoice = 1;

      System.out.println(qr.GetQueryText(m_queryChoice));
      Scanner keyboard = new Scanner(System.in);  // Create a Scanner object
      System.out.print("Enter Customer ID: ");
      String customerID = keyboard.nextLine();
      System.out.println("customerID is: " + customerID);

      int nAmt = qr.GetParameterAmtForQuery(m_queryChoice);
      String [] parmstring={customerID};
      String [] headers;
      String [][] allData;

      boolean bOK = qr.ExecuteQuery(m_queryChoice, parmstring);
      if (bOK ==true)
      {
         headers = qr.GetQueryHeaders();
         allData = qr.GetQueryData();
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

      disconnect();
   }

   private boolean connect(String host, String user, String pass,
                           String database) {
      boolean connectStatus = qr.Connect(host, user, pass, database);
      System.out.println(connectStatus?"Successfully":"Failed" + "connected to database");
      System.out.println(qr.GetError());
      if (!connectStatus) System.out.println(qr.GetError());
      return connectStatus;
   }

   private boolean disconnect() {
      boolean disconnectStatus = qr.Disconnect();
      System.out.println(disconnectStatus?"Successfully":"Failed" +
              "disconnect from database");
      if (!disconnectStatus) System.out.println(qr.GetError());
      return disconnectStatus;
   }

   private QueryRunner qr; // hold query runner object
}
