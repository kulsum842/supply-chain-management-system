package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Data Access Object (DAO) class for interacting with the Shipment database.
 * This class provides methods to perform CRUD (Create, Read, Update, Delete) operations on shipments.
 */
public class ShipmentDAO {

    /**
     * Adds a new shipment record to the database.
     * 
     * @param shipment The Shipment object containing the shipment details to be added.
     * @throws SQLException If there is an error in database operations.
     */
    public void addShipment(Shipment shipment) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to insert a new shipment record into the Shipment table
        String sql = "INSERT INTO Shipment (order_id, shipment_date, delivery_date, status) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the parameters for the prepared statement based on the shipment object
        stmt.setInt(1, shipment.getOrderId());             // Set the order ID
        stmt.setString(2, shipment.getShipmentDate());     // Set the shipment date
        stmt.setString(3, shipment.getDeliveryDate());     // Set the delivery date
        stmt.setString(4, shipment.getStatus());           // Set the shipment status
        
        // Execute the update query to add the shipment record
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Retrieves all shipment records from the database.
     * 
     * @return A list of all shipments in the database.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Shipment> getAllShipments() throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to fetch all shipments from the Shipment table
        String sql = "SELECT * FROM Shipment";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // Create a list to store shipment objects
        List<Shipment> shipments = new ArrayList<>();
        while (rs.next()) {
            // Retrieve shipment details from the result set
            int shipmentId = rs.getInt("shipment_id");
            int orderId = rs.getInt("order_id");
            String shipmentDate = rs.getString("shipment_date");
            String deliveryDate = rs.getString("delivery_date");
            String status = rs.getString("status");
            
            // Add the shipment object to the list
            shipments.add(new Shipment(shipmentId, orderId, shipmentDate, deliveryDate, status));
        }
        
        // Close the connection and return the list of shipments
        conn.close();
        return shipments;
    }

    /**
     * Updates an existing shipment record in the database.
     * 
     * @param shipment The Shipment object containing updated shipment details.
     * @throws SQLException If there is an error in database operations.
     */
    public void updateShipment(Shipment shipment) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to update shipment details in the Shipment table
        String sql = "UPDATE Shipment SET order_id=?, shipment_date=?, delivery_date=?, status=? WHERE shipment_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the parameters for the prepared statement based on the shipment object
        stmt.setInt(1, shipment.getOrderId());             // Set the order ID
        stmt.setString(2, shipment.getShipmentDate());     // Set the shipment date
        stmt.setString(3, shipment.getDeliveryDate());     // Set the delivery date
        stmt.setString(4, shipment.getStatus());           // Set the shipment status
        stmt.setInt(5, shipment.getShipmentId());          // Set the shipment ID to identify the shipment to update
        
        // Execute the update query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Deletes a shipment record from the database.
     * 
     * @param shipmentId The ID of the shipment to be deleted.
     * @throws SQLException If there is an error in database operations.
     */
    public void deleteShipment(int shipmentId) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to delete a shipment by its shipment ID
        String sql = "DELETE FROM Shipment WHERE shipment_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the shipment ID for the query
        stmt.setInt(1, shipmentId);
        
        // Execute the delete query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Searches for shipments by status.
     * 
     * @param query The search term for shipment status.
     * @return A list of shipments that match the search criteria (status).
     * @throws SQLException If there is an error in database operations.
     */
    public List<Shipment> searchShipments(String query) throws SQLException {
        // Create a list to store shipment objects
        List<Shipment> shipments = new ArrayList<>();
        
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to search for shipments by status
        String sql = "SELECT * FROM Shipment WHERE status LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set the search term with wildcards for partial matching
        stmt.setString(1, "%" + query + "%");
        
        // Execute the query and get the result set
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Retrieve shipment details from the result set
            int shipmentId = rs.getInt("shipment_id");
            int orderId = rs.getInt("order_id");
            String shipmentDate = rs.getString("shipment_date");
            String deliveryDate = rs.getString("delivery_date");
            String status = rs.getString("status");
            
            // Add the shipment object to the list
            shipments.add(new Shipment(shipmentId, orderId, shipmentDate, deliveryDate, status));
        }
        
        // Close the connection and return the list of shipments
        conn.close();
        return shipments;
    }
}
