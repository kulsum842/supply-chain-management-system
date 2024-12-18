package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import dao.OrderDAO;
import model.Order;

public class OrderManagement {

    // DAO instance to interact with the Order data in the database
    private OrderDAO orderDAO = new OrderDAO();

    // UI components
    private DefaultTableModel tableModel; // Table model to manage the order data in the JTable
    private JTextField orderDateField, customerNameField, itemIdField, quantityField, shipmentStatusField, paymentStatusField, searchField;
    private JButton searchButton, resetButton, addButton, updateButton, deleteButton;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Date format pattern for order date
    private JTable orderTable; // JTable to display orders

    // Method to show the Order Management window
    public void showOrderManagement() {
        // Create a JFrame to hold the Order Management UI
        JFrame frame = new JFrame("Order Management");
        frame.setSize(800, 600); // Set the frame size
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the window when it's disposed
        frame.setLayout(new BorderLayout()); // Set the layout of the frame to BorderLayout

        // Title label at the top of the window
        JLabel titleLabel = new JLabel("Manage Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set title font style
        frame.add(titleLabel, BorderLayout.NORTH); // Add the title label to the top

        // Panel to hold input fields for order details
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Use GridBagLayout to position input fields
        GridBagConstraints gbc = new GridBagConstraints(); // Create constraints for GridBagLayout
        gbc.fill = GridBagConstraints.HORIZONTAL; // Set the components to fill horizontally
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding around each component

        // Add components (labels and text fields) for Order Date, Customer Name, Item ID, Quantity, Shipment Status, and Payment Status
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Order Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; orderDateField = new JTextField(20); inputPanel.add(orderDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; customerNameField = new JTextField(20); inputPanel.add(customerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Item ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; itemIdField = new JTextField(20); inputPanel.add(itemIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; quantityField = new JTextField(20); inputPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Shipment Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; shipmentStatusField = new JTextField(20); inputPanel.add(shipmentStatusField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; paymentStatusField = new JTextField(20); inputPanel.add(paymentStatusField, gbc);

        // Panel to hold buttons (Add, Update, Delete)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Use FlowLayout for button arrangement
        addButton = new JButton("Add Order");
        updateButton = new JButton("Update Order");
        deleteButton = new JButton("Delete Order");
        buttonPanel.add(addButton); // Add the Add button
        buttonPanel.add(updateButton); // Add the Update button
        buttonPanel.add(deleteButton); // Add the Delete button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(buttonPanel, gbc); // Add button panel to the input panel

        frame.add(inputPanel, BorderLayout.WEST); // Add the input panel to the left side of the frame

        // Table to display orders with the respective columns
        tableModel = new DefaultTableModel(new String[]{"Order ID", "Order Date", "Customer Name", "Item ID", "Quantity", "Shipment Status", "Payment Status"}, 0);
        orderTable = new JTable(tableModel); // Create the JTable
        JScrollPane scrollPane = new JScrollPane(orderTable); // Add scroll functionality to the table
        frame.add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the frame

        // Search and Reset functionality panel
        searchField = new JTextField(15); // Create search text field
        searchButton = new JButton("Search"); // Create search button
        resetButton = new JButton("Reset"); // Create reset button
        JPanel searchPanel = new JPanel(); // Panel to hold search components
        searchPanel.add(new JLabel("Search:")); // Label for search
        searchPanel.add(searchField); // Add the search field
        searchPanel.add(searchButton); // Add the search button
        searchPanel.add(resetButton); // Add the reset button
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; inputPanel.add(searchPanel, gbc); // Add search panel to the input panel

        // Bottom panel with a back button to the main menu
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Main Menu");
        bottomPanel.add(backButton); // Add the back button
        frame.add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel to the frame

        // Action listeners for buttons and components
        backButton.addActionListener(e -> {
            frame.dispose(); // Close the Order Management window
            new Main().showMainMenu(LoginUI.currentUser); // Go back to the main menu
        });

        // Search functionality - filter orders based on the entered query
        searchButton.addActionListener(e -> searchOrders(searchField.getText()));
        
        // Reset search field and reload all orders
        resetButton.addActionListener(e -> {
            searchField.setText(""); // Clear the search field
            loadAllOrders(); // Reload all orders
        });

        // Action listeners for Add, Update, and Delete buttons
        addButton.addActionListener(e -> addOrder());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());

        // Row selection listener to populate the fields when a row is selected
        orderTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && orderTable.getSelectedRow() != -1) {
                    int selectedRow = orderTable.getSelectedRow();
                    orderDateField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                    customerNameField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    itemIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                    quantityField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
                    shipmentStatusField.setText((String) tableModel.getValueAt(selectedRow, 5));
                    paymentStatusField.setText((String) tableModel.getValueAt(selectedRow, 6));
                }
            }
        });

        // Load all orders when the window is displayed
        loadAllOrders(); 
        frame.setVisible(true); // Make the frame visible
    }

    // Method to load all orders from the database and display them in the table
    private void loadAllOrders() {
        try {
            List<Order> allOrders = orderDAO.getAllOrders(); // Get all orders from the database
            tableModel.setRowCount(0); // Clear any existing data in the table

            // Add each order to the table
            for (Order order : allOrders) {
                tableModel.addRow(new Object[]{order.getOrderId(), order.getOrderDate(), order.getCustomerName(), order.getItemId(), order.getQuantity(), order.getShipmentStatus(), order.getPaymentStatus()});
            }
        } catch (SQLException ex) {
            // Show an error message if there's an issue loading the orders
            JOptionPane.showMessageDialog(null, "Error loading orders: " + ex.getMessage());
        }
    }

    // Method to add a new order to the database
    private void addOrder() {
        if (validateInput()) {
            try {
                LocalDate orderDate = LocalDate.parse(orderDateField.getText(), dateFormatter); // Parse order date
                int itemId = Integer.parseInt(itemIdField.getText()); // Get Item ID
                int quantity = Integer.parseInt(quantityField.getText()); // Get Quantity
                String shipmentStatus = shipmentStatusField.getText(); // Get Shipment Status
                String paymentStatus = paymentStatusField.getText(); // Get Payment Status

                // Create a new Order object
                Order order = new Order(0, orderDate, customerNameField.getText(), itemId, quantity, shipmentStatus, paymentStatus);
                orderDAO.addOrder(order); // Add the order to the database
                JOptionPane.showMessageDialog(null, "Order added successfully!"); // Show success message
                loadAllOrders(); // Reload the table
                clearFields(); // Clear input fields
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd."); // Show error message for invalid date format
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Item ID and Quantity must be valid integers."); // Show error message for invalid number input
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error adding order: " + ex.getMessage()); // Show error message for database error
            }
        }
    }

    // Method to update an existing order in the database
    private void updateOrder() {
        int selectedRow = orderTable.getSelectedRow(); // Get selected row in the table
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select an order to update."); // If no order is selected, show a message
            return;
        }

        try {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0); // Get the Order ID from the selected row
            LocalDate orderDate = LocalDate.parse(orderDateField.getText(), dateFormatter); // Parse the order date
            int itemId = Integer.parseInt(itemIdField.getText()); // Get Item ID
            int quantity = Integer.parseInt(quantityField.getText()); // Get Quantity
            String shipmentStatus = shipmentStatusField.getText(); // Get Shipment Status
            String paymentStatus = paymentStatusField.getText(); // Get Payment Status

            // Create an updated Order object
            Order order = new Order(orderId, orderDate, customerNameField.getText(), itemId, quantity, shipmentStatus, paymentStatus);
            orderDAO.updateOrder(order); // Update the order in the database
            JOptionPane.showMessageDialog(null, "Order updated successfully!"); // Show success message
            loadAllOrders(); // Reload the table
            clearFields(); // Clear input fields
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd."); // Show error message for invalid date format
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Item ID and Quantity must be valid integers."); // Show error message for invalid number input
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating order: " + ex.getMessage()); // Show error message for database error
        }
    }

    // Method to delete an existing order from the database
    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow(); // Get selected row in the table
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select an order to delete."); // If no order is selected, show a message
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0); // Get the Order ID from the selected row
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this order?", "Confirm Delete", JOptionPane.YES_NO_OPTION); // Ask for confirmation
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDAO.deleteOrder(orderId); // Delete the order from the database
                JOptionPane.showMessageDialog(null, "Order deleted successfully!"); // Show success message
                loadAllOrders(); // Reload the table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting order: " + ex.getMessage()); // Show error message for database error
            }
        }
    }

    // Method to search orders based on the query string
    private void searchOrders(String query) {
        if (query.isEmpty()) {
            loadAllOrders(); // If the search query is empty, load all orders
            return;
        }

        try {
            List<Order> filteredOrders = orderDAO.searchOrders(query); // Get the filtered orders from the database
            tableModel.setRowCount(0); // Clear the table

            // Add the filtered orders to the table
            for (Order order : filteredOrders) {
                tableModel.addRow(new Object[]{order.getOrderId(), order.getOrderDate(), order.getCustomerName(), order.getItemId(), order.getQuantity(), order.getShipmentStatus(), order.getPaymentStatus()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error searching orders: " + ex.getMessage()); // Show error message for database error
        }
    }

    // Method to validate the input fields before adding or updating an order
    private boolean validateInput() {
        // Check if any of the fields are empty
        if (orderDateField.getText().isEmpty() || customerNameField.getText().isEmpty() || itemIdField.getText().isEmpty() || quantityField.getText().isEmpty() || shipmentStatusField.getText().isEmpty() || paymentStatusField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out!"); // Show error message if any field is empty
            return false;
        }
        return true; // Return true if all fields are valid
    }

    // Method to clear the input fields after an action is completed
    private void clearFields() {
        // Clear all the input fields
        orderDateField.setText("");
        customerNameField.setText("");
        itemIdField.setText("");
        quantityField.setText("");
        shipmentStatusField.setText("");
        paymentStatusField.setText("");
        orderTable.clearSelection(); // Clear the selection in the table
    }
}
