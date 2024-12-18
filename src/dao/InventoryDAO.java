package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Data Access Object (DAO) class for interacting with the Inventory database.
 * Provides methods to perform CRUD operations on the inventory items.
 */
public class InventoryDAO {

    /**
     * Adds a new inventory item to the database.
     * 
     * @param item : The inventory item to be added.
     * @throws SQLException : If there is an error in database operations.
     */
    public void addItem(Inventory item) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to insert a new item into the Inventory table
        String sql = "INSERT INTO Inventory (item_name, quantity, supplier_id) VALUES (?, ?, ?)";

        // Create a PreparedStatement to execute the SQL query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, item.getItemName()); // Set item name
        stmt.setInt(2, item.getQuantity());    // Set item quantity
        stmt.setInt(3, item.getSupplierId());  // Set supplier ID

        // Execute the insert operation
        stmt.executeUpdate();

        // Close the connection
        conn.close();
    }

    /**
     * Retrieves the total count of items in the inventory.
     * 
     * @return The count of items in the inventory.
     * @throws SQLException If there is an error in database operations.
     */
    public int getItemCount() throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to get the count of items in the Inventory table
        String sql = "SELECT COUNT(*) FROM inventory";

        // Execute the query and process the result
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Return the count from the result set if available
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0; // Return 0 if no items found
    }

    /**
     * Retrieves all items from the inventory.
     * 
     * @return A list of all inventory items.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Inventory> getAllItems() throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to select all items from the Inventory table
        String sql = "SELECT * FROM Inventory";

        // Create a statement to execute the query
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // List to store all inventory items
        List<Inventory> items = new ArrayList<>();
        
        // Process each row in the result set and create Inventory objects
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            int supplierId = rs.getInt("supplier_id");

            // Add the created Inventory object to the list
            items.add(new Inventory(itemId, itemName, quantity, supplierId));
        }

        // Close the connection
        conn.close();

        // Return the list of items
        return items;
    }

    /**
     * Updates an existing inventory item in the database.
     * 
     * @param item The inventory item with updated details.
     * @throws SQLException If there is an error in database operations.
     */
    public void updateItem(Inventory item) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to update an existing inventory item
        String sql = "UPDATE Inventory SET item_name=?, quantity=?, supplier_id=? WHERE item_id=?";

        // Create a PreparedStatement to execute the update query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, item.getItemName()); // Set item name
        stmt.setInt(2, item.getQuantity());    // Set item quantity
        stmt.setInt(3, item.getSupplierId());  // Set supplier ID
        stmt.setInt(4, item.getItemId());      // Set item ID to identify which item to update

        // Execute the update operation
        stmt.executeUpdate();

        // Close the connection
        conn.close();
    }

    /**
     * Deletes an inventory item from the database by its item ID.
     * 
     * @param itemId The ID of the item to be deleted.
     * @throws SQLException If there is an error in database operations.
     */
    public void deleteItem(int itemId) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to delete an inventory item by its ID
        String sql = "DELETE FROM Inventory WHERE item_id=?";

        // Create a PreparedStatement to execute the delete query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, itemId); // Set the item ID to delete

        // Execute the delete operation
        stmt.executeUpdate();

        // Close the connection
        conn.close();
    }

    /**
     * Searches for inventory items based on a search term (either item name or supplier ID).
     * 
     * @param searchTerm The term to search for in item names and supplier IDs.
     * @return A list of inventory items that match the search criteria.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Inventory> searchItems(String searchTerm) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to search items by item name or supplier ID
        String sql = "SELECT * FROM Inventory WHERE item_name LIKE ? OR supplier_id LIKE ?";

        // Create a PreparedStatement to execute the search query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + searchTerm + "%"); // Search for item name containing the search term
        stmt.setString(2, "%" + searchTerm + "%"); // Search for supplier ID containing the search term

        // Execute the query and store results in a list
        ResultSet rs = stmt.executeQuery();
        List<Inventory> items = new ArrayList<>();

        // Process each row in the result set and create Inventory objects
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            int supplierId = rs.getInt("supplier_id");

            // Add the created Inventory object to the list
            items.add(new Inventory(itemId, itemName, quantity, supplierId));
        }

        // Close the connection
        conn.close();

        // Return the list of matching items
        return items;
    }

    /**
     * Retrieves a paginated list of inventory items.
     * 
     * @param page The page number to retrieve (1-based index).
     * @param itemsPerPage The number of items to return per page.
     * @return A list of inventory items for the specified page.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Inventory> getItemsByPage(int page, int itemsPerPage) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to retrieve a specific page of items
        String sql = "SELECT * FROM Inventory LIMIT ? OFFSET ?";

        // Create a PreparedStatement to execute the query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, itemsPerPage);         // Limit the number of items per page
        stmt.setInt(2, (page - 1) * itemsPerPage); // Calculate the offset based on the page

        // Execute the query and store results in a list
        ResultSet rs = stmt.executeQuery();
        List<Inventory> items = new ArrayList<>();

        // Process each row in the result set and create Inventory objects
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            int supplierId = rs.getInt("supplier_id");

            // Add the created Inventory object to the list
            items.add(new Inventory(itemId, itemName, quantity, supplierId));
        }

        // Close the connection
        conn.close();

        // Return the paginated list of items
        return items;
    }

    /**
     * Retrieves inventory items that have a quantity below a given threshold.
     * 
     * @param threshold The quantity threshold.
     * @return A list of inventory items with quantities less than the threshold.
     * @throws SQLException If there is an error in database operations.
     */
    public List<Inventory> getLowStockItems(int threshold) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to select items with quantities less than the threshold
        String sql = "SELECT * FROM Inventory WHERE quantity < ?";

        // Create a PreparedStatement to execute the query
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, threshold); // Set the quantity threshold for the query

        // Execute the query and store results in a list
        ResultSet rs = stmt.executeQuery();
        List<Inventory> lowStockItems = new ArrayList<>();

        // Process each row in the result set and create Inventory objects
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            int supplierId = rs.getInt("supplier_id");

            // Add the created Inventory object to the list
            lowStockItems.add(new Inventory(itemId, itemName, quantity, supplierId));
        }

        // Close the connection
        conn.close();

        // Return the list of low stock items
        return lowStockItems;
    }

    /**
     * Checks whether a supplier exists in the suppliers table.
     * 
     * @param supplierId The ID of the supplier to check.
     * @return true if the supplier exists, false otherwise.
     * @throws SQLException If there is an error in database operations.
     */
    public boolean doesSupplierExist(int supplierId) throws SQLException {
        // Get a connection to the database
        Connection conn = DatabaseConnection.getConnection();

        // SQL query to check if the supplier exists in the suppliers table
        String query = "SELECT COUNT(*) FROM suppliers WHERE supplier_id = ?";

        // Create a PreparedStatement to execute the query
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, supplierId); // Set the supplier ID in the query

            // Execute the query and check the result
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0; // Return true if supplier exists, else false
        }
    }
}
