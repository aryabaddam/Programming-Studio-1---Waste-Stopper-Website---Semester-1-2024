package app;

import java.util.ArrayList;
import java.util.Collections;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Change by Food Groups</title>" +
               """
                  <link rel="icon" href="WasteStoppersLogo.png">    
               """;;;

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <div class='topnav'>
                <a href='/'>Homepage</a>
                <a href='mission.html'>Waste Stopper's Mission</a>
                <a href='page2A.html'>Change by Country</a>
                <a href='page2B.html'>Change by Food Groups</a>
                <a href='page3A.html'>Locations with Similar Loss</a>
                <a href='page3B.html'>Loss in Food Commodities</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='header'>
                <h1>Change by Food Groups</h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";
        // ArrayList<String> countryData = getALlCountries();
        ArrayList<Integer> yearsData = getAllYears();
        ArrayList<String> groupData = getAllGroups();

        // Add HTML for the page content
        html = html + """
            <div class="dropdown">
            <div class='data-boxs box4'>
            <form method='get'>
            <label for='group'>Group:</label>
            <select id='group' name='group'>
            <option disabled selected value=''>Select Group</option>
            """;

            //Iterate through the groupData to add each group as an option
        for (String group : groupData) {html = html + "<option value='" + group +"'>" + group + "</option>";}
            html = html + """
        </select>

        <label for='start-year'>Start Year:</label>
        <select id='start-year' name='start-year'>
        <option disabled selected value=''>Select Start Year</option>
        """;
        // Iterate through yearsData to add each year as an option
          for (Integer year : yearsData) {
            html = html + "<option value='" + year + "'>" + year + "</option>";
        }
    
        html = html + """
        </select>

        <label for='end-year'>End Year:</label>
        <select id='end-year' name='end-year'>
        <option disabled selected value=''>Select End Year</option>
        """;

        // Iterate through yearsData to add each year as an option
        for (Integer year : yearsData) {
            html = html + "<option value='" + year + "'>" + year + "</option>";
        }        

        html = html + """
        </select>
        <input type='submit' value='Search'>
        </form>
        """;

         // Add results table
         html = html + "<div class='table-container2'>";
         html = html + "<h1>Results:</h1>";
         html = html + "<table>";
         html = html + "<tr><th>Year</th><th>Commodity</th><th>Loss Percentage</th></tr>";

        // Check if there are query parameters
        String group = context.queryParam("group");
        String startYear = context.queryParam("start-year");
        String endYear = context.queryParam("end-year");

        if (group!= null || startYear!= null || endYear!= null) {
            ArrayList<String[] > results = getResults(group, startYear, endYear);

            if (results.isEmpty()) {html = html + "<tr><td colspan='5'>No data available</td></tr>";
        } else {
        for (String[] row : results) {
        html = html + "<tr>";
        for (String cell : row) {
        html = html + "<td>" + (cell != null ? cell : "-") + "</td>";
        }
        html = html + "</tr>";
        }
        }
        } else {html = html + "<tr><td colspan='5'>Please select the filters and click on search.</td></tr>";}
            

        // Close Content div
        html = html + "</table></div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>Waste Stoppers Incorporation - Arya Baddam (s4054024) and Lachlan Corville (s3897401)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    public ArrayList<Integer> getAllYears() {
    // Create the ArrayList of Integer objects to return
    ArrayList<Integer> years = new ArrayList<>();

    // Setup the variable for the JDBC connection
    Connection connection = null;

    try {
    // Connect to the JDBC database
    connection = DriverManager.getConnection(JDBCConnection.DATABASE);

    // Prepare a new SQL Query & Set a timeout
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);

    // The Query to get all distinct years sorted in ascending order
    String query = "SELECT DISTINCT year FROM Date ORDER BY year ASC";

    // Get Result
    ResultSet results = statement.executeQuery(query);

    // Process all of the results
    while (results.next()) {
    int year = results.getInt("year");

    // Add the year to the array
    years.add(year);
    }

    // Sort the years in ascending order
    Collections.sort(years);

    // Close the statement because we are done with it
    statement.close();
    } catch (SQLException e) {
    // If there is an error, let's just print the error
    System.err.println(e.getMessage());
    // e.printStackTrace();
    } finally {
    // Safety code to cleanup
    try {
    if (connection != null) {
    connection.close();
    }
    } catch (SQLException e) {
    // connection close failed.
    System.err.println(e.getMessage());
    // e.printStackTrace();
    }
    }

    // Finally we return all of the years
    return years;
    }

    public ArrayList<String> getAllGroups() {
        // Create the ArrayList of String objects to return
        ArrayList<String> commodities = new ArrayList<String>();
    
        // Setup the variable for the JDBC connection
        Connection connection = null;
    
        try {
        // Connect to the JDBC database
        connection = DriverManager.getConnection(JDBCConnection.DATABASE);
    
        // Prepare a new SQL Query & Set a timeout
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);
    
        // The Query to get all distinct commodities sorted in ascending order
        String query = "SELECT DISTINCT commodity FROM FoodLoss ORDER BY commodity ASC";
    
        // Get Result
        ResultSet results = statement.executeQuery(query);
    
        // Process all of the results
        while (results.next()) {
        String commodity = results.getString("commodity");
    
        // Add the commodity to the array
        commodities.add(commodity);
                }
    
        // Sort the commodities alphabetically
        Collections.sort(commodities);
    
        // Close the statement because we are done with it
        statement.close();
        } catch (SQLException e) {
        // If there is an error, let's just print the error
        System.err.println(e.getMessage());
        // e.printStackTrace();
        } finally {
        // Safety code to cleanup
        try {
        if (connection != null) {
        connection.close();
        }
        } catch (SQLException e) {
        // connection close failed.
        System.err.println(e.getMessage());
        // e.printStackTrace();
        }
        }
    
        // Finally we return all of the commodities
        return commodities;
        }

        public ArrayList<String[]> getResults(String group, String startYear, String endYear) {
            // Create the ArrayList of String[] objects to return
            ArrayList<String[]> resultsList = new ArrayList<>();
        
            // Setup the variable for the JDBC connection
            Connection connection = null;
        
            try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);
        
            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
        
            // Build the query
            StringBuilder query = new StringBuilder("SELECT * FROM FoodLoss WHERE 1=1");
            if (group != null && !group.isEmpty()) {
            query.append(" AND commodity = '").append(group).append("'");
            }
            if (startYear != null && !startYear.isEmpty()) {
            query.append(" AND year >= ").append(startYear);
            }
            if (endYear != null && !endYear.isEmpty()) {
            query.append(" AND year <= ").append(endYear);
            }
        
            // Execute the query
            ResultSet results = statement.executeQuery(query.toString());
        
            // Process all of the results
            while (results.next()) {
            String[] row = new String[3];
            row[0] = String.valueOf(results.getInt("year"));
            row[1] = results.getString("commodity");
            row[2] = results.getString("loss_percentage");
        
            // Add the row to the results list
            resultsList.add(row);
            }
        
            // Close the statement because we are done with it
            statement.close();
            } catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
            //e.printStackTrace();
            } finally {
            // Safety code to cleanup
            try {
            if (connection != null) {
            connection.close();
            }
            } catch (SQLException e) {
            // connection close failed.
            System.err.println(e.getMessage());
            //e.printStackTrace();
            }
            }
        
            // Finally we return all of the results
            return resultsList;
            }

}
