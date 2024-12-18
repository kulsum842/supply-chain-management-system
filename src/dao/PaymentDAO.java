package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import model.*;

/**
 * Data Access Object (DAO) class for interacting with the Payment database.
 * This class provides methods to perform CRUD (Create, Read, Update, Delete) operations on payments.
 */
public class PaymentDAO {

    /**
     * Adds a new payment to the database and updates the order status to "Paid".
     * This method handles both inserting the payment record and updating the corresponding order status in a transaction.
     * 
     * @param payment The payment object containing the payment details to be added.
     * @throws SQLException If there is an error in database operations, including transaction management.
     */
    public void addPayment(Payment payment) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Start a transaction by setting auto-commit to false
            conn.setAutoCommit(false);

            // SQL query to insert a new payment into the Payment table
            String sql = "INSERT INTO Payment (order_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, payment.getOrderId());           // Set the order ID
            stmt.setDouble(2, payment.getAmount());         // Set the payment amount
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate())); // Set the payment date
            stmt.setString(4, payment.getPaymentMethod());  // Set the payment method
            stmt.executeUpdate();

            // SQL query to update the order status to "Paid"
            String updateOrderStatus = "UPDATE orders SET status = 'Paid' WHERE order_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateOrderStatus);
            updateStmt.setInt(1, payment.getOrderId()); // Set the order ID
            updateStmt.executeUpdate();

            // Commit the transaction
            conn.commit();
        } catch (SQLException ex) {
            // Rollback the transaction in case of error
            conn.rollback();
            throw ex;
        } finally {
            // Set auto-commit back to true and close the connection
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Retrieves all payments from the database.
     * 
     * @return A list of all payments.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Payment> getAllPayments() throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM Payment";
        
        // Create a statement and execute the query
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // Create a list to store payment objects
        List<Payment> payments = new ArrayList<>();
        while (rs.next()) {
            // Retrieve payment details from the result set
            int paymentId = rs.getInt("payment_id");
            int orderId = rs.getInt("order_id");
            double amount = rs.getDouble("amount");
            LocalDate paymentDate = rs.getDate("payment_date").toLocalDate();
            String paymentMethod = rs.getString("payment_method");

            // Add the payment object to the list
            payments.add(new Payment(paymentId, orderId, amount, paymentDate, paymentMethod));
        }
        // Close the connection and return the list of payments
        conn.close();
        return payments;
    }

    /**
     * Updates an existing payment in the database.
     * 
     * @param payment The payment object containing updated payment details.
     * @throws SQLException If there is an error in database operations.
     */
    public void updatePayment(Payment payment) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to update the payment details
        String sql = "UPDATE Payment SET order_id=?, amount=?, payment_date=?, payment_method=? WHERE payment_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the values for the prepared statement based on the payment object
        stmt.setInt(1, payment.getOrderId());           // Set the order ID
        stmt.setDouble(2, payment.getAmount());         // Set the payment amount
        stmt.setDate(3, Date.valueOf(payment.getPaymentDate())); // Set the payment date
        stmt.setString(4, payment.getPaymentMethod());  // Set the payment method
        stmt.setInt(5, payment.getPaymentId());         // Set the payment ID to identify which payment to update
        
        // Execute the update query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Deletes a payment from the database.
     * 
     * @param paymentId The ID of the payment to be deleted.
     * @throws SQLException If there is an error in database operations.
     */
    public void deletePayment(int paymentId) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to delete a payment by its payment ID
        String sql = "DELETE FROM Payment WHERE payment_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the payment ID for the query
        stmt.setInt(1, paymentId);

        // Execute the delete query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Searches for payments by order ID or payment method.
     * 
     * @param query The search term (order ID or payment method) to search for in the payments.
     * @return A list of payments that match the search criteria.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Payment> searchPayments(String query) throws SQLException {
        // Create a list to store payment objects
        List<Payment> payments = new ArrayList<>();
        
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to search for payments by order ID or payment method
        String sql = "SELECT * FROM Payment WHERE order_id LIKE ? OR payment_method LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the query string with wildcards for partial matching
        stmt.setString(1, "%" + query + "%");
        stmt.setString(2, "%" + query + "%");
        
        // Execute the query and get the result set
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Retrieve payment details from the result set
            int paymentId = rs.getInt("payment_id");
            int orderId = rs.getInt("order_id");
            double amount = rs.getDouble("amount");
            LocalDate paymentDate = rs.getDate("payment_date").toLocalDate();
            String paymentMethod = rs.getString("payment_method");
            
            // Add the payment object to the list
            payments.add(new Payment(paymentId, orderId, amount, paymentDate, paymentMethod));
        }
        // Close the connection and return the list of payments
        conn.close();
        return payments;
    }
}
