package dao;

import model.DatabaseConnection;
import model.User;
import java.sql.*;

/**
 * Data Access Object (DAO) class for interacting with the Users table in the database.
 * This class provides methods for user authentication and retrieving user data.
 */
public class UserDAO {

    /**
     * Authenticates a user by checking the provided username and password against the Users table.
     * 
     * @param username The username of the user attempting to authenticate.
     * @param password The password of the user attempting to authenticate.
     * @return A User object if authentication is successful, or null if authentication fails.
     * @throws SQLException If there is an error with the database operations.
     */
    public User authenticateUser(String username, String password) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to check if a user exists with the given username and password
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the parameters for the prepared statement
        stmt.setString(1, username);  // Set the username
        stmt.setString(2, password);  // Set the password
        
        // Execute the query and retrieve the result set
        ResultSet rs = stmt.executeQuery();
        
        // Check if the user exists
        if (rs.next()) {
            // If the user is found, retrieve the user details from the result set
            int userId = rs.getInt("user_id");
            String role = rs.getString("role");
            
            // Close the connection and return the user object
            conn.close();
            return new User(userId, username, password, role); // Return authenticated user
        }
        
        // If no user is found, close the connection and return null
        conn.close();
        return null; // Authentication failed, return null
    }
}
