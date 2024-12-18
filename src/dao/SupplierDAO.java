package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Data Access Object (DAO) class for interacting with the Supplier table in the database.
 * This class provides methods to add, update, delete, and retrieve supplier information.
 */
public class SupplierDAO {

    /**
     * Adds a new supplier to the Supplier table in the database.
     * 
     * @param supplier The Supplier object containing the details of the supplier to be added.
     * @throws SQLException If there is an error with the database operations.
     */
    public void addSupplier(Supplier supplier) throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to insert a new supplier into the Supplier table
        String sql = "INSERT INTO Supplier (supplier_name, contact_person_name, phone_number, email, address, city) VALUES (?, ?, ?, ?, ?, ?)";
        
        // Preparing the SQL statement
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, supplier.getSupplierName());
        stmt.setString(2, supplier.getContactPersonName());
        stmt.setString(3, supplier.getPhoneNumber());
        stmt.setString(4, supplier.getEmail());
        stmt.setString(5, supplier.getAddress());
        stmt.setString(6, supplier.getCity());
        
        // Executing the insert query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Retrieves all suppliers from the Supplier table.
     * 
     * @return A list of all suppliers in the database.
     * @throws SQLException If there is an error with the database operations.
     */
    public List<Supplier> getAllSuppliers() throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to select all suppliers from the Supplier table
        String sql = "SELECT * FROM Supplier";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        List<Supplier> suppliers = new ArrayList<>();
        
        // Iterating through the result set and adding each supplier to the list
        while (rs.next()) {
            int supplierId = rs.getInt("supplier_id");
            String supplierName = rs.getString("supplier_name");
            String contactPersonName = rs.getString("contact_person_name");
            String phoneNumber = rs.getString("phone_number");
            String email = rs.getString("email");
            String address = rs.getString("address");
            String city = rs.getString("city");
            
            Supplier supplier = new Supplier(supplierId, supplierName, contactPersonName, phoneNumber, email, address, city);
            suppliers.add(supplier);
        }
        conn.close();
        return suppliers;
    }

    /**
     * Updates an existing supplier's information in the Supplier table.
     * 
     * @param supplier The Supplier object containing the updated details of the supplier.
     * @throws SQLException If there is an error with the database operations.
     */
    public void updateSupplier(Supplier supplier) throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to update a supplier's details
        String sql = "UPDATE Supplier SET supplier_name=?, contact_person_name=?, phone_number=?, email=?, address=?, city=? WHERE supplier_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Setting the values for the prepared statement
        stmt.setString(1, supplier.getSupplierName());
        stmt.setString(2, supplier.getContactPersonName());
        stmt.setString(3, supplier.getPhoneNumber());
        stmt.setString(4, supplier.getEmail());
        stmt.setString(5, supplier.getAddress());
        stmt.setString(6, supplier.getCity());
        stmt.setInt(7, supplier.getSupplierId());
        
        // Executing the update query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Deletes a supplier from the Supplier table by its supplier ID.
     * 
     * @param supplierId The ID of the supplier to be deleted.
     * @throws SQLException If there is an error with the database operations.
     */
    public void deleteSupplier(int supplierId) throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to delete a supplier by its ID
        String sql = "DELETE FROM Supplier WHERE supplier_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Setting the supplier ID for the prepared statement
        stmt.setInt(1, supplierId);
        
        // Executing the delete query
        stmt.executeUpdate();
        conn.close();
    }

    /**
     * Searches for suppliers whose names or contact persons match the given query.
     * 
     * @param query The search term to match against supplier names and contact person names.
     * @return A list of suppliers whose name or contact person matches the search term.
     * @throws SQLException If there is an error with the database operations.
     */
    public List<Supplier> searchSuppliers(String query) throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to search suppliers by name or contact person
        String sql = "SELECT * FROM Supplier WHERE supplier_name LIKE ? OR contact_person_name LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Setting the query for the prepared statement (using wildcards for partial matching)
        stmt.setString(1, "%" + query + "%");
        stmt.setString(2, "%" + query + "%");
        
        // Executing the query and iterating over the result set
        ResultSet rs = stmt.executeQuery();
        List<Supplier> suppliers = new ArrayList<>();
        
        while (rs.next()) {
            int supplierId = rs.getInt("supplier_id");
            String supplierName = rs.getString("supplier_name");
            String contactPersonName = rs.getString("contact_person_name");
            String phoneNumber = rs.getString("phone_number");
            String email = rs.getString("email");
            String address = rs.getString("address");
            String city = rs.getString("city");
            
            Supplier supplier = new Supplier(supplierId, supplierName, contactPersonName, phoneNumber, email, address, city);
            suppliers.add(supplier);
        }
        conn.close();
        return suppliers;
    }

    /**
     * Retrieves a list of suppliers with pagination, limiting the number of suppliers returned.
     * 
     * @param limit The number of suppliers to return per page.
     * @param offset The starting point for the records to fetch (used for pagination).
     * @return A list of suppliers for the requested page.
     * @throws SQLException If there is an error with the database operations.
     */
    public List<Supplier> getSuppliersWithPagination(int limit, int offset) throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to select a limited number of suppliers with an offset for pagination
        String sql = "SELECT * FROM Supplier LIMIT ? OFFSET ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Setting the limit and offset for the prepared statement
        stmt.setInt(1, limit);
        stmt.setInt(2, offset);
        
        // Executing the query and iterating over the result set
        ResultSet rs = stmt.executeQuery();
        List<Supplier> suppliers = new ArrayList<>();
        
        while (rs.next()) {
            int supplierId = rs.getInt("supplier_id");
            String supplierName = rs.getString("supplier_name");
            String contactPersonName = rs.getString("contact_person_name");
            String phoneNumber = rs.getString("phone_number");
            String email = rs.getString("email");
            String address = rs.getString("address");
            String city = rs.getString("city");
            
            Supplier supplier = new Supplier(supplierId, supplierName, contactPersonName, phoneNumber, email, address, city);
            suppliers.add(supplier);
        }
        conn.close();
        return suppliers;
    }

    /**
     * Retrieves the total count of suppliers in the database.
     * 
     * @return The total number of suppliers in the Supplier table.
     * @throws SQLException If there is an error with the database operations.
     */
    public int getSupplierCount() throws SQLException {
        // Establishing a connection to the database
        Connection conn = DatabaseConnection.getConnection();
        
        // SQL query to count the total number of suppliers
        String sql = "SELECT COUNT(*) FROM Supplier";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1); // Getting the count from the result set
        }
        conn.close();
        return count; // Returning the total supplier count
    }
}
