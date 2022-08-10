package ahiri;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This is an utility class for getting database connection.
 * @author Alve
 */
public class DatabaseConnection {
    public Connection databaseLink;
    
    /**
     * @return a database connection
     */
    public Connection getConnection(){
        String databaseName="javaproject_db";
        String databaseUser="root";
        String databasePassword="AlverahmaN";
        String url="jdbc:mysql://localhost/" + databaseName;
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url,databaseUser,databasePassword);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return databaseLink;
    }
}
