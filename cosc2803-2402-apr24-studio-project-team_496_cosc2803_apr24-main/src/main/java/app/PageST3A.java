package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * PageST3A HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
    // Create a simple HTML webpage in a String
    String html = "<html>";

    // Add some Head information
    html = html + "<head>" + 
    "<title>Locations with Similar Loss</title>"+
    """
    <link rel="icon" href="WasteStoppersLogo.png">    
    """;

    // Add some CSS (external file)
    html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
    html = html + "</head>";

    // Add the body
    html = html + "<body>";

    // Add the topnav
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
    <h1>Locations with Similar Loss</h1>
    </div>
    """;

    // Add Div for page Content
    html = html + "<div class='content'>";

    // Add form for selecting filters
    ArrayList<String> countryData = getAllCountries();
    ArrayList<String> similarityCriteria = getSimilarityCriteria();

    html = html + """
    <form method='get'>
    <label for='country'>Country/Region:</label>
    <select id='country' name='country'>
    <option disabled selected value=''>Select Country/Region</option>
    """;
    
    for (String country : countryData) {html = html + "<option value='" + country + "'>" + country + "</option>";}
    
    html = html + """
    </select>
    <label for='similarity'>Similarity By:</label>
    <select id='similarity' name='similarity'>
    <option disabled selected value=''>Select Similarity</option>
    """;
        
    for (String criteria : similarityCriteria) {html = html + "<option value='" + criteria + "'>" + criteria + "</option>";}
    
    html = html + """
    </select>
    <input type='submit' value='Search'>
    </form>
    """;

    // Add results table
    html = html + "<div class='table-container2'>";
    html = html + "<h1>Results:</h1>";
    html = html + "<table>";
    html = html + "<tr><th>m49code</th><th>Country</th><th>Region</th><th>cpc_code</th><th>Commodity</th><th>Year</th><th>Loss Percentage</th><th>Activity</th><th>Food Supply Stage</th><th>Cause of Loss</th></tr>";

    String country = context.queryParam("country");
    String similarity = context.queryParam("similarity");

    if (country != null && similarity != null) {
    ArrayList<String[]> results = getResults(country, similarity);

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

    // Footer
    html = html + """
    <div class='footer'>
    <p>Waste Stoppers Incorporation - Arya Baddam (s4054024) and Lachlan Corville (s3897401)</p>
    </div>
    """;

    // Finish the HTML webpage
    html = html + "</body>" + "</html>";

    // Makes Javalin render the webpage
    context.html(html);
    }

    public ArrayList<String> getAllCountries() {
    ArrayList<String> countries = new ArrayList<>();

    try (Connection connection = DriverManager.getConnection(JDBCConnection.DATABASE)) {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);

    String query = "SELECT DISTINCT country FROM FoodLoss ORDER BY country ASC";
    ResultSet results = statement.executeQuery(query);

    while (results.next()) {countries.add(results.getString("country"));}

    statement.close();
    } catch (SQLException e) {
    System.err.println(e.getMessage());
    }

    Collections.sort(countries);
    return countries;
    }

    public ArrayList<String> getSimilarityCriteria() {
    ArrayList<String> criteria = new ArrayList<>();
    criteria.add("m49code");
    criteria.add("country");
    criteria.add("region");
    criteria.add("cpc_code");
    criteria.add("commodity");
    criteria.add("year");
    criteria.add("loss_percentage");
    criteria.add("activity");
    criteria.add("food_supply_stage");
    criteria.add("cause_of_loss");
    return criteria;
    }

    public ArrayList<String[]> getResults(String country, String similarity) {
    ArrayList<String[]> resultsList = new ArrayList<>();

    try (Connection connection = DriverManager.getConnection(JDBCConnection.DATABASE)) {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);

    // Query to get the selected country and similar countries based on the similarity criteria
    String query = String.format(
    "SELECT * FROM FoodLoss WHERE country = '%s' OR %s = (SELECT %s FROM FoodLoss WHERE country = '%s') ORDER BY %s",
    country, similarity.replace(" ", "_"), similarity.replace(" ", "_"), country, similarity.replace(" ", "_")
    );

    ResultSet results = statement.executeQuery(query);

    while (results.next()) {
    String[] row = new String[10];
    row[0] = results.getString("m49code");
    row[1] = results.getString("country");
    row[2] = results.getString("region");
    row[3] = results.getString("cpc_code");
    row[4] = results.getString("commodity");
    row[5] = results.getString("year");
    row[6] = results.getString("loss_percentage");
    row[7] = results.getString("activity");
    row[8] = results.getString("food_supply_stage");
    row[9] = results.getString("cause_of_loss");

    resultsList.add(row);
    }

    statement.close();
    } catch (SQLException e) {
    System.err.println(e.getMessage());
    }

    return resultsList;
    }
}