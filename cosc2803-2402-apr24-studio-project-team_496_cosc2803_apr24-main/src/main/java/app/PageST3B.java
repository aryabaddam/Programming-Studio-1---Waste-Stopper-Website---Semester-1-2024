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

public class PageST3B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 3.2</title>"+
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
                <h1>Loss in Food Commodities</h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        ArrayList<String> groupData = getAllGroups();
        ArrayList<String> similarityCriteria = getSimilarityCriteria();



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
        
        """;

        html = html + """
                </select>
                <label for='similarity'>Similarity By:</label>
                <select id='similarity' name='similarity'>
                    <option disabled selected value=''>Select Similarity</option>
        """;

        for (String criteria : similarityCriteria) {
            html = html + "<option value='" + criteria + "'>" + criteria + "</option>";
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
        html = html + "<tr><th>Commodity</th><th>Country</th><th>Region</th><th>cpc_code</th><th>Year</th><th>Loss Percentage</th><th>Activity</th><th>Food Supply Stage</th><th>Cause of Loss</th></tr>";

        
        String group = context.queryParam("group");
        String similarity = context.queryParam("similarity");

        if (group != null && similarity != null) {
            ArrayList<String[]> results = getResults(group, similarity);

            if (results.isEmpty()) {
                html = html + "<tr><td colspan='10'>No data available</td></tr>";
            } else {
                for (String[] row : results) {
                    html = html + "<tr>";
                    for (String cell : row) {
                        html = html + "<td>" + (cell != null ? cell : "-") + "</td>";
                    }
                    html = html + "</tr>";
                }
            }
        } else {
            html = html + "<tr><td colspan='10'>Please select the filters and click on search.</td></tr>";
        }

        html = html + "</table>";
        html = html + "</div>";
        html = html + "</div>";

        // Close Content div
        html = html + "</div>";

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

        public ArrayList<String> getSimilarityCriteria() {
            ArrayList<String> criteria = new ArrayList<>();
            criteria.add("country");
            criteria.add("cpc_code");
            criteria.add("commodity");
            criteria.add("year");
            criteria.add("loss_percentage");
            criteria.add("activity");
            criteria.add("food_supply_stage");
            criteria.add("cause_of_loss");
            return criteria;
        }

        public ArrayList<String[]> getResults(String groups, String similarity) {
            ArrayList<String[]> resultsList = new ArrayList<>();
    
            try (Connection connection = DriverManager.getConnection(JDBCConnection.DATABASE)) {
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
    
                // Query to get the selected country and similar countries based on the similarity criteria
                String query = String.format(
                    "SELECT * FROM FoodLoss WHERE commodity = '%s' OR %s = (SELECT %s FROM FoodLoss WHERE commodity = '%s') ORDER BY %s",
                    groups, similarity.replace(" ", "_"), similarity.replace(" ", "_"), groups, similarity.replace(" ", "_")
                );
    
                ResultSet results = statement.executeQuery(query);
    
                while (results.next()) {
                    String[] row = new String[10];
                    row[0] = results.getString("commodity");
                    row[1] = results.getString("country");
                    row[3] = results.getString("cpc_code");
                    row[4] = results.getString("year");
                    row[5] = results.getString("loss_percentage");
                    row[6] = results.getString("activity");
                    row[7] = results.getString("food_supply_stage");
                    row[8] = results.getString("cause_of_loss");
    
                    resultsList.add(row);
                }
    
                statement.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
    
            return resultsList;
        }

}
