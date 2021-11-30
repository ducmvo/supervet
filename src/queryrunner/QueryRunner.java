/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

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
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
    
        
        // TODO - You will need to change the queries below to match your queries.
        
        // You will need to put your Project Application in the below variable
        
        this.m_projectTeamApplication="SUPERVET";    // THIS NEEDS TO CHANGE
        // FOR
        // YOUR APPLICATION
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)

        // query 1: insert new customer to customer table. takes in customerID, address, city, state, zipcode, email, first name, and last name as inputs.
        // some parameters are left blank since exceeding 8 parameters would fail running the program.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDateTime = formatter.format(date);
        String query1 = "INSERT INTO Customer " +
                "(createdAt, address, city, state, zip_code, password, email, first_name, last_name) " +
                "VALUES ('" +
                currentDateTime +
                "', ?, ?, ?, ?, ?, ?, ?, ?);";
        m_queryArray.add(new QueryData(query1, new String [] {
                "address", "city", "state", "zipcode", "password", "email", "first name", "last name"}, new boolean [] {false, false, false, false, false, false, false, false, false},
                true, true));

        // query 2: create new pet to pet table.
        String query2 = "INSERT INTO Pet " +
                "(createdAt, customerID, name, breed) " +
                "VALUES ('" +
                currentDateTime +
                "', ?, ?, ?);";
        m_queryArray.add(new QueryData(query2, new String [] {
                "owner's ID", "name", "breed"}, new boolean [] {false, false, false},
                true, true));

        // query 3: insert a new prescription to prescription table. takes in prescriptionID, petID, status and vetID as user input.
        String query3 = "INSERT INTO Prescription " +
                "(prescriptionID, petID, status, createdAt, vetID) " +
                "VALUES (?, ?, ?, '" +
                currentDateTime +
                "', ?);";
        m_queryArray.add(new QueryData(query3, new String [] {
                "prescription's ID", "pet's ID", "status", "vetID"}, new boolean [] {false, false, false, false},
                true, true));

        // query 4: insert a new PrescriptionProduct to PrescriptionProduct table. takes in prescriptionID, productID, quantity, unit, description as user input.
        String query4 = "INSERT INTO PrescriptionProduct " +
                "(prescriptionID, productID, quantity, unit, description, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, '" +
                currentDateTime +
                "');";
        m_queryArray.add(new QueryData(query4, new String [] {
                "prescription's ID", "product's ID", "quantity", "unit", "description"}, new boolean [] {false, false, false, false, false},
                true, true));

        // query 5: retrieve a pet's medication history by email as input
        String query5 = "SELECT PrescriptionID, \n" +
            "Pet.petID,\n" +
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
            "WHERE Customer.email = ? \n" +
            "ORDER BY Pet.name;";

        m_queryArray.add(new QueryData(query5, new String [] {
                "customer's email"}, new boolean [] {false},
                false, true));

        // query 6: insert a new sale to sale table. takes in saleID, description, and employeeID as user input
        String query6 = "INSERT INTO Sale " +
                "(saleID, description, createdAt, updatedAt, employeeID) " +
                "VALUES (?, ?, '" +
                currentDateTime +
                "', '" +
                currentDateTime +
                "', ?);";
        m_queryArray.add(new QueryData(query6, new String [] {
                "sale's ID", "description", "employeeID"}, new boolean [] {false, false, false},
                true, true));

        // query 7: insert a new SaleProduct to SaleProduct table. takes in saleID, description, and employeeID as user input
        String query7 = "INSERT INTO SaleProduct " +
                "(saleID, productID, quantity, description, createdAt) " +
                "VALUES (?, ?, ?, ?, '" +
                currentDateTime +
                "');";
        m_queryArray.add(new QueryData(query7, new String [] {
                "sale's ID", "product's ID", "quantity", "description"}, new boolean [] {false, false, false, false},
                true, true));

        // query 8: list customer's purchase history for prescription. takes in customer ID as input
        String query8 = "SELECT Customer.customerID AS CustomerID, \n" +
                "Pet.name AS PetName, Customer.email AS CustomerEmail, \n" +
                "Appointment.date AS AppointmentDate, \n" +
                "Vet.vetID AS VetID, \n" +
                "ROUND((PrescriptionProduct.quantity * Product.price), 2) AS TotalBill, \n" +
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
        m_queryArray.add(new QueryData(query8, new String [] {
                "customer's ID"}, new boolean [] {false},
                false, true));

        // query 9: search products by name. takes in drug name as user input.
        String query9 = "SELECT * FROM Product\n" +
                "WHERE name LIKE ?;";
        m_queryArray.add(new QueryData(query9, new String [] {
                "Product's name"}, new boolean [] {true},
                false, true));

        // query 10: update a product's price. takes in product's ID and new price as user input
        String query10 = "UPDATE Product\n" +
                "SET price = ?\n" +
                "WHERE productID = ?;";
        m_queryArray.add(new QueryData(query10, new String [] {
                "new price", "product's ID"}, new boolean [] {false, false},
                true, true));

        // query 11: search vet by name. takes in vet's first name as user input
        String query11 = "SELECT * FROM Vet\n" +
                "WHERE first_name LIKE ?;";
        m_queryArray.add(new QueryData(query11, new String [] {
                "Vet's name"}, new boolean [] {true},
                false, true));

        /* 2. Calculate Total Prescription Cost for each pet of a customer, provided customerID */
        /*String query2 = "SELECT petID,\n" +
            "Pet.name AS `pet_name`,\n" +
            "ROUND(SUM(PrescriptionProduct.quantity * Product.price),2) AS `Total Prescription Cost`\n" +
            "FROM Prescription\n" +
            "JOIN PrescriptionProduct USING (prescriptionID)\n" +
            "JOIN Product USING (productID)\n" +
            "JOIN Pet USING (petID)\n" +
            "JOIN Customer USING (customerID)\n" +
            "WHERE customerID = ?\n" +
            "GROUP BY petID\n" +
            "ORDER BY Pet.name;";
        // test customerID: 1781

        m_queryArray.add(new QueryData(query2, new String [] {
                "customerID"}, new boolean [] {false},
                false, true));*/


        /* 3. Find out average prescription cost for a pet of customers who live in Washington state */
        /*String query3 = "SELECT ROUND(AVG(prescription_cost), 2) " +
            "AS `Average State Prescription Cost` FROM (\n" +
            "SELECT SUM(Product.price * PrescriptionProduct.quantity) " +
            "AS prescription_cost \n" +
            "FROM Customer \n" +
            "JOIN Pet USING (customerID)\n" +
            "JOIN Prescription USING (petID)\n" +
            "JOIN PrescriptionProduct USING (prescriptionID)\n" +
            "JOIN Product USING (productID)\n" +
            "WHERE state = ?\n" +
            "GROUP BY prescriptionID\n" +
            ") AS PrescriptionCost;";
        m_queryArray.add(new QueryData(query3, new String [] {
                "State"}, new boolean [] {false},
                false, true));*/



//        String query4 =  "";
//        m_queryArray.add(new QueryData("insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"}, new boolean [] {false, false, false}, true, true));// THIS NEEDS TO CHANGE FOR YOUR APPLICATION


        /*m_queryArray.add(new QueryData("Select * from Vet where vetID = ?;",
                new String [] {"vetID"}, new boolean [] {false},  false,
                true));*/
//        m_queryArray.add(new QueryData("Select * from contact where contact_name like ?", new String [] {"CONTACT_NAME"}, new boolean [] {true}, false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION

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
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        // TODO code application logic here

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals ("-console"))
            {
            	System.out.println("Nothing has been implemented yet. Please implement the necessary code");
               // TODO 
                // You should code the following functionality:

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
                
            }
        }
 
    }    
}
