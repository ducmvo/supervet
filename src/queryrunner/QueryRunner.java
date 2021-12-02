/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import javax.management.Query;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 *
 * LIST OF IMPROVEMENTS:
 * 14 new queries are added to the application to facilitate different user activity.
 * This includes queries that use like, aggregates, parameters, and insert or
 * update statements.
 * Query labels were added
 * Console application was implemented using QueryConsole class
 *
 */

public class QueryRunner {

    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
        int max_queries=20;

        this.m_projectTeamApplication="SUPERVET";
        m_querynames = new String[max_queries];

        // query 1: display the customer table
        String query1 = "SELECT * FROM Customer;";
        m_queryArray.add(new QueryData(query1, new String [] {}, new boolean [] {},
                false, false));
        m_querynames[0] = "Display all customer info";

        // query 2: Find pets by owner
        String query2 = "SELECT Pet.petID, Pet.name, Pet.breed, " +
                "Customer.customerID, Customer.first_name, " +
                "Customer.last_name, Customer.email\n" +
                "FROM Pet\n" +
                "JOIN Customer on Pet.customerID = Customer.customerID\n" +
                "WHERE Customer.customerID = ?;";
        m_queryArray.add(new QueryData(query2, new String[] {"Customer ID"},
                new boolean[] {false}, false, true));
        m_querynames[1] = "Find pets by owner";

        // query 3: display the product table
        String query3 = "SELECT * FROM Product;";
        m_queryArray.add(new QueryData(query3, new String [] {}, new boolean [] {},
                false, false));

        m_querynames[2] = "Display product info ";

        // query 4: retrieve a pet's medication history by email as input
        String query4 = "SELECT PrescriptionID, " +
                "Pet.petID,\n" +
                "Customer.email,\n" +
                "Pet.name AS `pet_name`, \n" +
                "Product.productID,\n" +
                "Product.name AS `drug_name`,\n" +
                "PrescriptionProduct.quantity,\n" +
                "PrescriptionProduct.unit, \n" +
                "PrescriptionProduct.description,\n" +
                "Prescription.createdAt AS `prescribed_date`\n" +
                "FROM Prescription\n" +
                "JOIN PrescriptionProduct USING (prescriptionID)\n" +
                "JOIN Product USING (productID)\n" +
                "JOIN Pet USING (petID)\n" +
                "JOIN Customer USING (customerID)\n" +
                "WHERE Customer.email like ? \n" +
                "ORDER BY Pet.name;";

        m_queryArray.add(new QueryData(query4, new String [] {
                "customer's email"}, new boolean [] {true},
                false, true));
        m_querynames[3] = "Pull pet meds by email";

        // query 5: list customer's purchase history for prescription.
        // takes in customer ID as input
        String query5 = "SELECT Customer.customerID AS CustomerID, \n" +
                "Pet.name AS PetName, Customer.email AS CustomerEmail, \n" +
                "Appointment.date AS AppointmentDate, \n" +
                "Vet.vetID AS VetID, \n" +
                "ROUND((PrescriptionProduct.quantity * Product.price), 2) AS " +
                "TotalBill, \n" +
                "Payment.status AS PaymentStatus\n" +
                "FROM Customer\n" +
                "JOIN Appointment USING (customerID)\n" +
                "JOIN Pet USING (customerID)\n" +
                "JOIN Vet USING (vetID)\n" +
                "JOIN Prescription USING (vetID)\n" +
                "JOIN PrescriptionProduct USING (prescriptionID)\n" +
                "JOIN Product USING (productID)\n" +
                "JOIN Payment USING (prescriptionID)\n" +
                "WHERE Customer.customerID = ?;";
        m_queryArray.add(new QueryData(query5, new String [] {
                "customer's ID"}, new boolean [] {false},
                false, true));
        m_querynames[4] = "List customer med history";

        // query 6: search products by name. takes in drug name as user input.
        String query6 = "SELECT * FROM Product\n" +
                "WHERE name LIKE ?;";
        m_queryArray.add(new QueryData(query6, new String [] {
                "Product's name"}, new boolean [] {true},
                false, true));
        m_querynames[5] = "Find prod by name";

        // query 7: search vet by name. takes in vet's first name as user input
        String query7 = "SELECT * FROM Vet " +
                "WHERE first_name LIKE ?;";
        m_queryArray.add(new QueryData(query7, new String [] {
                "Vet's name"}, new boolean [] {true},
                false, true));
        m_querynames[6] = "Find vet by name";

        // query 8: search pet by name. takes in pet's name as user input
        String query8 = "SELECT * FROM Pet " +
                "JOIN Customer USING (customerID)\n" +
                "WHERE Pet.name LIKE ?;";
        m_queryArray.add(new QueryData(query8, new String [] {
                "Pet's name"}, new boolean [] {true},
                false, true));
        m_querynames[7] = "Find pet by name";

        // query 9: insert new customer
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDateTime = formatter.format(date);
        String query9 = "INSERT INTO Customer " +
                "(createdAt, address, city, state, zip_code, password, \n" +
                "email, first_name, last_name) " +
                "VALUES ('" +
                currentDateTime +
                "', ?, ?, ?, ?, ?, ?, ?, ?);";
        m_queryArray.add(new QueryData(query9, new String [] {
                "address", "city", "state", "zipcode", "password",
                "email", "first name", "last name"},
                new boolean [] {false, false, false, false, false, false, false, false, false},
                true, true));
        m_querynames[8] = "Create Customer";

        // query 10: create new pet to pet table.
        String query10 = "INSERT INTO Pet " +
                "(createdAt, customerID, name, breed) " +
                "VALUES ('" +
                currentDateTime +
                "', ?, ?, ?);";
        m_queryArray.add(new QueryData(query10,
                new String [] {"owner's ID", "name", "breed"},
                new boolean [] {false, false, false},
                true, true));
        m_querynames[9] = "Create Pet";

        // query 11: insert a new prescription to prescription table. Takes
        // in prescriptionID, petID, status and vetID as user input.
        String query11 = "INSERT INTO Prescription \n" +
                "(petID, status, createdAt, vetID) " +
                "VALUES (?, ?, '" +
                currentDateTime +
                "', ?);";
        m_queryArray.add(new QueryData(query11, new String [] {
                "pet's ID", "status", "vetID"},
                new boolean [] {false, false, false},
                true, true));
        m_querynames[10] = "Create Prescription";

        // query 12: insert a new PrescriptionProduct to PrescriptionProduct table.
        // takes in prescriptionID, productID, quantity, unit, description as user input.
        String query12 = "INSERT INTO PrescriptionProduct \n" +
                "(prescriptionID, productID, quantity, unit, description, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, '" +
                currentDateTime +
                "');";
        m_queryArray.add(new QueryData(query12,
                new String [] { "prescription's ID", "product's ID",
                        "quantity", "unit", "description"},
                new boolean [] {false, false, false, false, false},
                true, true));
        m_querynames[11] = "Create Medication";

        // query 13: insert a new sale to sale table.
        // takes in saleID, description, and employeeID as user input
        String query13 = "INSERT INTO Sale " +
                "(saleID, description, createdAt, updatedAt, employeeID) \n" +
                "VALUES (?, ?, '" +
                currentDateTime +
                "', '" +
                currentDateTime +
                "', ?);";
        m_queryArray.add(new QueryData(query13, new String [] {
                "sale's ID", "description", "employeeID"},
                new boolean [] {false, false, false},
                true, true));
        m_querynames[12] = "Create sale";

        // query 14: insert a new SaleProduct to SaleProduct table.
        // takes in saleID, description, and employeeID as user input
        String query14 = "INSERT INTO SaleProduct " +
                "(saleID, productID, quantity, description, createdAt) \n" +
                "VALUES (?, ?, ?, ?, '" +
                currentDateTime +
                "');";
        m_queryArray.add(new QueryData(query14, new String [] {
                "sale's ID", "product's ID", "quantity", "description"},
                new boolean [] {false, false, false, false},
                true, true));
        m_querynames[13] = "Create sale log";

        // query 15: update a product's price.
        // takes in product's ID and new price as user input
        String query15 = "UPDATE Product " +
                "SET price = ?\n" +
                "WHERE productID = ?;";
        m_queryArray.add(new QueryData(query15, new String [] {
                "new price", "product's ID"}, new boolean [] {false, false},
                true, true));
        m_querynames[14] = "Change prod price";

        // Get most recent prescriptionID
        String query16 = "SELECT prescriptionID FROM Prescription ORDER BY " +
                "prescriptionID DESC LIMIT 1;";
        m_queryArray.add(new QueryData(query16,
                new String [] {}, new boolean [] {},
                false, false));
        m_querynames[15] = "Retrieve most recent prescriptionID";

        // List all items for a prescription
        // takes in prescriptionID as input
        String query17 = "SELECT * FROM PrescriptionProduct WHERE " +
                "prescriptionID = ?;";
        m_queryArray.add(new QueryData(query17,
                new String [] {"prescriptionID"}, new boolean [] {false},
                false, true));
        m_querynames[16] = "List prescription items";
    }
       

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String  GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return bConnect;
    }
    
    public String GetError()
    {
        return m_error;
    }

    public String getQueryName(int queryChoice) {
        return m_querynames[queryChoice];
    }

    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
    String[] m_querynames;
            
    /**
     * Run main method with '-console' argument for console interface
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        final QueryRunner queryrunner = new QueryRunner();
        if (args.length == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        } else {
            if (args[0].equals ("-console")) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new QueryConsole(queryrunner);
                    }
                });

            }
        }
    }    
}
