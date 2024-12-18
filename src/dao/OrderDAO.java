package dao;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for interacting with the Orders database.
 * Provides methods to perform CRUD (Create, Read, Update, Delete) operations on orders.
 */
public class OrderDAO {

    /**
     * Adds a new order to the database.
     * 
     * @param order The order object containing details of the order to be added.
     * @throws SQLException If there is an error in database operations.
     */
    public void addOrder(Order order) throws SQLException {
        // SQL query to insert a new order into the Orders table
        String sql = "INSERT INTO Orders (order_date, customer_name, item_id, quantity, shipment_status, payment_status) VALUES (?, ?, ?, ?, ?, ?)";

        // Using try-with-resources to automatically close the connection and statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Setting values for the PreparedStatement based on the order object
            stmt.setDate(1, Date.valueOf(order.getOrderDate())); // Set the order date
            stmt.setString(2, order.getCustomerName());          // Set the customer name
            stmt.setInt(3, order.getItemId());                   // Set the item ID
            stmt.setInt(4, order.getQuantity());                 // Set the quantity
            stmt.setString(5, order.getShipmentStatus());       // Set shipment status
            stmt.setString(6, order.getPaymentStatus());        // Set payment status

            // Execute the SQL query to insert the order into the database
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all orders from the database.
     * 
     * @return A list of all orders.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        // SQL query to retrieve all orders from the Orders table
        String sql = "SELECT * FROM Orders";

        // Using try-with-resources to automatically close the connection, statement, and result set
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Iterate over the result set and create Order objects
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                LocalDate orderDate = rs.getDate("order_date").toLocalDate(); // Convert Date to LocalDate
                String customerName = rs.getString("customer_name");
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("quantity");
                String shipmentStatus = rs.getString("shipment_status");
                String paymentStatus = rs.getString("payment_status");

                // Add the created Order object to the list
                orders.add(new Order(orderId, orderDate, customerName, itemId, quantity, shipmentStatus, paymentStatus));
            }
        }
        // Return the list of orders
        return orders;
    }

    /**
     * Updates an existing order in the database.
     * 
     * @param order The order object with updated details.
     * @throws SQLException If there is an error in database operations.
     */
    public void updateOrder(Order order) throws SQLException {
        // SQL query to update an existing order in the Orders table
        String sql = "UPDATE Orders SET order_date=?, customer_name=?, item_id=?, quantity=?, shipment_status=?, payment_status=? WHERE order_id=?";

        // Using try-with-resources to automatically close the connection and statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Setting values for the PreparedStatement based on the order object
            stmt.setDate(1, Date.valueOf(order.getOrderDate())); // Set the order date
            stmt.setString(2, order.getCustomerName());          // Set the customer name
            stmt.setInt(3, order.getItemId());                   // Set the item ID
            stmt.setInt(4, order.getQuantity());                 // Set the quantity
            stmt.setString(5, order.getShipmentStatus());       // Set shipment status
            stmt.setString(6, order.getPaymentStatus());        // Set payment status
            stmt.setInt(7, order.getOrderId());                  // Set the order ID to identify which order to update

            // Execute the update query
            stmt.executeUpdate();
        }
    }

    /**
     * Searches for orders by customer name. Returns orders that match the search query.
     * 
     * @param query The search term (customer name) to search for in the orders.
     * @return A list of orders that match the search criteria.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Order> searchOrders(String query) throws SQLException {
        List<Order> orders = new ArrayList<>();
        // SQL query to search for orders where customer name contains the search term
        String sql = "SELECT * FROM Orders WHERE customer_name LIKE ?";

        // Using try-with-resources to automatically close the connection and statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Setting the search term with wildcards for partial matching
            stmt.setString(1, "%" + query + "%");

            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();

            // Iterate over the result set and create Order objects
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                LocalDate orderDate = rs.getDate("order_date").toLocalDate(); // Convert Date to LocalDate
                String customerName = rs.getString("customer_name");
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("quantity");
                String shipmentStatus = rs.getString("shipment_status");
                String paymentStatus = rs.getString("payment_status");

                // Add the created Order object to the list
                orders.add(new Order(orderId, orderDate, customerName, itemId, quantity, shipmentStatus, paymentStatus));
            }
        }
        // Return the list of orders that match the search criteria
        return orders;
    }

    /**
     * Deletes an order from the database based on the provided order ID.
     * 
     * @param orderId The ID of the order to be deleted.
     * @throws SQLException If there is an error in database operations.
     */
    public void deleteOrder(int orderId) throws SQLException {
        // SQL query to delete an order by its order ID
        String sql = "DELETE FROM Orders WHERE order_id=?";

        // Using try-with-resources to automatically close the connection and statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the order ID for the query
            stmt.setInt(1, orderId);

            // Execute the delete operation
            stmt.executeUpdate();
        }
    }
}
