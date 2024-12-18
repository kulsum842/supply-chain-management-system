package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import dao.InventoryDAO;
import model.Inventory;

public class InventoryManagement {

    // DAO instance to interact with the Inventory data in the database
    private InventoryDAO inventoryDAO = new InventoryDAO();

    // UI components
    private JTable inventoryTable; // JTable to display inventory items
    private DefaultTableModel tableModel; // Table model to manage inventory data in JTable
    private JTextField itemNameField, quantityField, supplierIdField, searchField = new JTextField(15); // Input fields for item details and search
    private JButton searchButton = new JButton("Search"); // Search button
    private JButton resetButton = new JButton("Reset"); // Reset button to clear search field
    private JButton addButton = new JButton("Add Item"); // Add button to add new item
    private JButton updateButton = new JButton("Update Item"); // Update button to update existing item
    private JButton deleteButton = new JButton("Delete Item"); // Delete button to remove an item
    private static final int LOW_STOCK_THRESHOLD = 50; // Define a threshold for low stock items

    // Method to display the Inventory Management window
    public void showInventoryManagement() {
        JFrame frame = new JFrame("Inventory Management"); // Create a new JFrame for inventory management
        frame.setSize(800, 600); // Set window size
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose the frame when closed
        frame.setLayout(new BorderLayout()); // Use BorderLayout for the layout of the frame

        // Title label
        JLabel titleLabel = new JLabel("Manage Inventory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font style for the title
        frame.add(titleLabel, BorderLayout.NORTH); // Add title label to the north part of the frame

        // Panel for input fields (Item Name, Quantity, Supplier ID)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for positioning components
        GridBagConstraints gbc = new GridBagConstraints(); // Create constraints for GridBagLayout
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding around components

        // Adding labels and input fields for Item Name, Quantity, and Supplier ID
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; itemNameField = new JTextField(20); inputPanel.add(itemNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; quantityField = new JTextField(20); inputPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Supplier ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; supplierIdField = new JTextField(20); inputPanel.add(supplierIdField, gbc);

        // Panel for buttons (Add, Update, Delete)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Use FlowLayout for buttons
        buttonPanel.add(addButton); // Add the "Add Item" button
        buttonPanel.add(updateButton); // Add the "Update Item" button
        buttonPanel.add(deleteButton); // Add the "Delete Item" button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; inputPanel.add(buttonPanel, gbc); // Add the button panel to the input panel

        // Add the input panel to the left side of the frame
        frame.add(inputPanel, BorderLayout.WEST);

        // Set up the table model and JTable to display inventory items
        tableModel = new DefaultTableModel(new String[]{"Item ID", "Item Name", "Quantity", "Supplier ID"}, 0); // Define columns for the table
        inventoryTable = new JTable(tableModel) {
            // Override prepareRenderer to change row colors based on stock levels
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int quantity = (int) getValueAt(row, 2); // Get the quantity of the item
                if (quantity < LOW_STOCK_THRESHOLD) { // Check if stock is below threshold
                    c.setBackground(Color.RED); // Set background to red for low stock
                    c.setForeground(Color.WHITE); // Set text color to white
                } else {
                    c.setBackground(Color.WHITE); // Set background to white
                    c.setForeground(Color.BLACK); // Set text color to black
                }
                return c; // Return the component with the updated background and foreground
            }
        };
        JScrollPane scrollPane = new JScrollPane(inventoryTable); // Add scrolling functionality to the table
        frame.add(scrollPane, BorderLayout.CENTER); // Add the scrollable table to the center of the frame

        // Search panel to handle search input and actions
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:")); // Label for search field
        searchPanel.add(searchField); // Add the search field
        searchPanel.add(searchButton); // Add the search button
        searchPanel.add(resetButton); // Add the reset button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; inputPanel.add(searchPanel, gbc); // Add search panel to the input panel

        // Bottom panel with a back button to go back to the main menu
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Main Menu"); // Back button
        bottomPanel.add(backButton); // Add the back button
        frame.add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel to the frame

        // Action listeners for various buttons
        backButton.addActionListener(e -> {
            frame.dispose(); // Close the current window
            new Main().showMainMenu(LoginUI.currentUser); // Open the main menu
        });

        // Action listener for search functionality
        searchButton.addActionListener(e -> searchItems(searchField.getText()));

        // Action listener for reset functionality
        resetButton.addActionListener(e -> {
            searchField.setText(""); // Clear the search field
            loadAllItems(); // Reload all items into the table
        });

        // Action listeners for Add, Update, and Delete buttons
        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());

        // Row selection listener to populate fields when a row is selected
        inventoryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && inventoryTable.getSelectedRow() != -1) {
                    int selectedRow = inventoryTable.getSelectedRow(); // Get the selected row index
                    itemNameField.setText((String) tableModel.getValueAt(selectedRow, 1)); // Set the item name field
                    quantityField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2))); // Set the quantity field
                    supplierIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3))); // Set the supplier ID field
                }
            }
        });

        // Load all inventory items when the window is displayed
        loadAllItems();
        frame.setVisible(true); // Make the frame visible
    }

    // Method to load all items from the database and display them in the table
    private void loadAllItems() {
        try {
            List<Inventory> allItems = inventoryDAO.getAllItems(); // Get all inventory items from the database
            tableModel.setRowCount(0); // Clear any existing data in the table
            for (Inventory item : allItems) {
                // Add each item to the table
                tableModel.addRow(new Object[]{item.getItemId(), item.getItemName(), item.getQuantity(), item.getSupplierId()});
            }
            checkLowStock(); // Check for low stock items and alert if necessary
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading inventory: " + ex.getMessage()); // Handle error if loading fails
        }
    }

    // Method to check if any items are below the low stock threshold
    private void checkLowStock() {
        try {
            List<Inventory> lowStockItems = inventoryDAO.getLowStockItems(LOW_STOCK_THRESHOLD); // Get low stock items
            if (!lowStockItems.isEmpty()) {
                StringBuilder message = new StringBuilder("Low Stock Alerts:\n");
                for (Inventory item : lowStockItems) {
                    message.append(item.getItemName())
                            .append(" (ID: ")
                            .append(item.getItemId())
                            .append(") - Quantity: ")
                            .append(item.getQuantity())
                            .append("\n");
                }
                JOptionPane.showMessageDialog(null, message.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE); // Show low stock alert
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error checking low stock: " + ex.getMessage()); // Handle error if checking fails
        }
    }

    // Method to add a new item to the inventory
    private void addItem() {
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText()); // Get the supplier ID
            if (!inventoryDAO.doesSupplierExist(supplierId)) {
                JOptionPane.showMessageDialog(null, "Supplier ID does not exist."); // Check if supplier ID is valid
                return;
            }
            String itemName = itemNameField.getText(); // Get the item name
            int quantity = Integer.parseInt(quantityField.getText()); // Get the item quantity
            Inventory item = new Inventory(0, itemName, quantity, supplierId); // Create a new Inventory object
            inventoryDAO.addItem(item); // Add the new item to the database
            JOptionPane.showMessageDialog(null, "Item added successfully!"); // Show success message
            loadAllItems(); // Reload the inventory list
            checkLowStock(); // Check for low stock items
            clearFields(); // Clear the input fields
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values."); // Handle invalid input
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding item: " + ex.getMessage()); // Handle database error
        }
    }

    // Method to update an existing inventory item
    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow(); // Get the selected row in the table
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select an item to update."); // If no row is selected, show error message
            return;
        }

        try {
            int itemId = (int) tableModel.getValueAt(selectedRow, 0); // Get the item ID from the selected row
            int supplierId = Integer.parseInt(supplierIdField.getText()); // Get the supplier ID
            if (!inventoryDAO.doesSupplierExist(supplierId)) {
                JOptionPane.showMessageDialog(null, "Supplier ID does not exist."); // Check if supplier ID is valid
                return;
            }
            String itemName = itemNameField.getText(); // Get the item name
            int quantity = Integer.parseInt(quantityField.getText()); // Get the item quantity
            Inventory updatedItem = new Inventory(itemId, itemName, quantity, supplierId); // Create an updated Inventory object
            inventoryDAO.updateItem(updatedItem); // Update the item in the database
            JOptionPane.showMessageDialog(null, "Item updated successfully!"); // Show success message
            loadAllItems(); // Reload the inventory list
            checkLowStock(); // Check for low stock items
            clearFields(); // Clear the input fields
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values."); // Handle invalid input
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating item: " + ex.getMessage()); // Handle database error
        }
    }

    // Method to delete an inventory item
    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow(); // Get the selected row in the table
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select an item to delete."); // If no row is selected, show error message
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0); // Get the item ID from the selected row
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION); // Confirm deletion
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventoryDAO.deleteItem(itemId); // Delete the item from the database
                JOptionPane.showMessageDialog(null, "Item deleted successfully!"); // Show success message
                loadAllItems(); // Reload the inventory list
                checkLowStock(); // Check for low stock items
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting item: " + ex.getMessage()); // Handle database error
            }
        }
    }

    // Method to search for items in the inventory based on a query
    private void searchItems(String query) {
        try {
            List<Inventory> searchResults = inventoryDAO.searchItems(query); // Get search results from the database
            tableModel.setRowCount(0); // Clear the existing table rows
            for (Inventory item : searchResults) {
                // Add each search result to the table
                tableModel.addRow(new Object[]{item.getItemId(), item.getItemName(), item.getQuantity(), item.getSupplierId()});
            }
            checkLowStock(); // Check for low stock items after searching
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error searching items: " + ex.getMessage()); // Handle database error
        }
    }

    // Method to clear all input fields after an action
    private void clearFields() {
        itemNameField.setText(""); // Clear the item name field
        quantityField.setText(""); // Clear the quantity field
        supplierIdField.setText(""); // Clear the supplier ID field
        inventoryTable.clearSelection(); // Clear the table selection
    }
}
