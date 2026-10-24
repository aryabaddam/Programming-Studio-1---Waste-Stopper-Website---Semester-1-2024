package app;

import java.util.ArrayList;

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

public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Waste Stopper's Mission</title>";
        
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
                <h1>Waste Stoppers' Mission</h1>
            </div>
        """;

       // Add Div for page Content
        html = html + """
        <div class="grid-container mission">
        <div class='item0'> 
        <p>
            Our company's mission is to establish a webpage that empowers all individuals to learn and address the challenge of food loss and wastage. By utilising techniques to promote data analytics, we, at Waste Stoppers, strive to present educational information about how we can spread the word to help individuals make better choices to help reduce the negative impacts of food loss and waste to the world.
        </p> 
        </div>
        """;;

        // Add HTML for the page content
        html = html + """
            <div class="item1">
                <h1>Some of our wonderful users!!!!</h1>
            </div>

            <div class="item2">
                <div class="container">
                    <img src="Johnathan.JPG" alt="Johnathan" class="img grid">
                    <div class="overlay">
                        My Name is Johnathan
                    </div>
                </div>
            </div>

            <div class="item3">
                <h2>Johnathan "Johnnie" Whitaker</h2>
                <p>Johnnie Whitaker is a 40-year-old food safety officer working for the Australian Government. Whitaker needs to access comprehensive data on food safety regulations and statistics. Additionally, he would like to access ways to monitor and report incidents of food loss and wastage. Whitaker wants to reduce food loss and wastage by promoting good healthy practices and spread public awareness through the Australian Government. He also wants to provide Australians with more practical insights and data-driven recommendations to improve food safety outcomes. Whitaker has over 10 years of experience in the food safety industry. Whitaker has expert knowledge of food safety laws and technology used in preventing food loss and waste. However, Whitaker finds it difficult to access real-time data and analytics on food loss and waste, which makes it hard for him to make decisions easily.</p>
            </div>
            
            <div class="item4">
                <div class="container">
                    <img src="Jacqueline.JPG" alt="Jacqueline" class="imag grid">
                    <div class="overlay">
                        My Name is Jacqueline
                    </div>
                </div>
            </div>
            <div class="item5">
                <h2>Jacqueline "Jackie" Jackson</h2>
                <p>Jackie Jackson is a 31-year-old environmental advocate working for a non-profit focused on sustainable agriculture. Aside from that, Jackie is interested in using technology to help reduce the environmental impacts of food wastage. Jackie really wants to access data regarding food waste so she can further reinforce her beliefs of reducing negative environment impacts. Jackie wants significantly reduce food waste, starting from her local community in West Melbourne. Jackie wants to also ensure that her community is educated about effective strategies to reduce waste. Jackie is very proficient in data analysis and interpretation and is also understands her rights in the Australian legal system.</p>
            </div>

            <div class="item6">
                <div class="container">
                    <img src="Nancy.JPG" alt="nancy" class="image grid">
                    <div class="overlay">
                        My Name is Nancy
                    </div>
                </div>
            </div>
            <div class="item7">
                <h2>Nancy Smitherson</h2>
                <p>Nancy is in her late 50's and as she is getting older wants to make smarter and more eco-conscious decisions as she has just become a grandmother. Nancy needs information on how to better use all the food she and her family are eating each week and how to better use the food scraps. Nancy primary goal is to learn more about food waste and how to reduce it as much as she can with her family. Nancy is familiar with using her computer as it is a regular aspect of her job as a teachers aid, and she originally studied a degree in mathematics majoring in statistics and is familiar.</p>
            </div>
        """;   

        // This example uses JDBC to lookup the countries
        JDBCConnection jdbc = new JDBCConnection();

        // Next we will ask this *class* for the Countries
        ArrayList<Country> countries = jdbc.getAllCountries();

        // Add HTML for the countries list
        html = html + """
        <div class="item8">
            <h1>All Countries in our expansive food loss database!</h1><ul>
        """;

        // Finally we can print out all of the Countries
        for (Country country : countries) {
            html = html + "<li>" + country.getM49Code()
                        + " - " + country.getName() + "</li>";
        }

        // Finish the List HTML
        html = html + "</ul></div>";


        // Close Content div
        html = html + "</div></div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p> Waste Stoppers Incorporation - Arya Baddam (s4054024) and Lachlan Corville (s3897401)
                </p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

}
