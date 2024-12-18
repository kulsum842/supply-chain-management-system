package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

import dao.ShipmentDAO;
import model.Shipment;

public class ShipmentManagement {

    // DAO instance for interacting with the database
    private ShipmentDAO shipmentDAO = new ShipmentDAO();

    // UI components
    private JTable shipmentTable;
    private DefaultTableModel tableModel;
    private JTextField shipmentIdField, orderIdField, shipmentDateField, deliveryDateField;
    private JComboBox<String> statusComboBox; // Dropdown for shipment status
    private JTextField searchField; // Search text field
    private JButton searchButton = new JButton("Search");
    private JButton resetButton = new JButton("Reset");
    private JButton addButton = new JButton("Add Shipment");
    private JButton updateButton = new JButton("Update Shipment");
    private JButton deleteButton = new JButton("Delete Shipment");

    // Method to show the Shipment Management window
    public void showShipmentManagement() {
        // Create the main frame for the window
        JFrame frame = new JFrame("Shipment Management");
        frame.setSize(800, 600); // Set the size of the window
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the frame on dispose
        frame.setLayout(new BorderLayout()); // Use BorderLayout for the main layout

        // Title label at the top of the window
        JLabel titleLabel = new JLabel("Manage Shipments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font for title
        frame.add(titleLabel, BorderLayout.NORTH); // Add title label to the top of the frame

        // Input panel where users can input data to add or edit a shipment
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding around components

        // Adding input fields for Shipment ID, Order ID, Shipment Date, Delivery Date, and Status
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Shipment ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; shipmentIdField = new JTextField(20); inputPanel.add(shipmentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Order ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; orderIdField = new JTextField(20); inputPanel.add(orderIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Shipment Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; shipmentDateField = new JTextField(20); inputPanel.add(shipmentDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Delivery Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; deliveryDateField = new JTextField(20); inputPanel.add(deliveryDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; 
        statusComboBox = new JComboBox<>(new String[]{"Pending", "Shipped", "Delivered"}); // Dropdown for status
        inputPanel.add(statusComboBox, gbc);

        // Panel for action buttons (Add, Update, Delete)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // FlowLayout for button arrangement
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; inputPanel.add(buttonPanel, gbc);
        frame.add(inputPanel, BorderLayout.WEST); // Add input panel to the left side of the frame

        // Create the table for displaying the list of shipments
        tableModel = new DefaultTableModel(new String[]{"Shipment ID", "Order ID", "Shipment Date", "Delivery Date", "Status"}, 0);
        shipmentTable = new JTable(tableModel); // Create table with the model
        JScrollPane scrollPane = new JScrollPane(shipmentTable); // Wrap the table with a JScrollPane
        frame.add(scrollPane, BorderLayout.CENTER); // Add the table to the center of the frame

        // Search functionality UI
        searchField = new JTextField(15); // Create search text field
        JPanel searchPanel = new JPanel(); // Panel to hold the search components
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(searchPanel, gbc);

        // Panel at the bottom for "Back to Main Menu" button
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Main Menu");
        bottomPanel.add(backButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        backButton.addActionListener(e -> {
            frame.dispose(); // Close the shipment management window
            new Main().showMainMenu(LoginUI.currentUser); // Return to the main menu
        });

        // Search button functionality
        searchButton.addActionListener(e -> searchShipments(searchField.getText()));
        resetButton.addActionListener(e -> {
            searchField.setText(""); // Clear search field
            loadAllShipments(); // Reload all shipments
        });

        // Add, Update, and Delete button actions
        addButton.addActionListener(e -> addShipment());
        updateButton.addActionListener(e -> updateShipment());
        deleteButton.addActionListener(e -> deleteShipment());

        // Selection listener for the table (to populate the input fields with the selected shipment data)
        shipmentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && shipmentTable.getSelectedRow() != -1) {
                    int selectedRow = shipmentTable.getSelectedRow();
                    shipmentIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                    orderIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                    shipmentDateField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    deliveryDateField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    statusComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4)); // Set the status combo box
                }
            }
        });

        // Load all shipments when the window is shown
        loadAllShipments();
        frame.setVisible(true); // Make the window visible
    }

    // Method to load all shipments from the database and display them in the table
    private void loadAllShipments() {
        try {
            List<Shipment> allShipments = shipmentDAO.getAllShipments(); // Fetch all shipments from the database
            tableModel.setRowCount(0); // Clear current data in the table

            // Loop through the list of shipments and add each one to the table
            for (Shipment shipment : allShipments) {
                tableModel.addRow(new Object[]{shipment.getShipmentId(), shipment.getOrderId(), shipment.getShipmentDate(), shipment.getDeliveryDate(), shipment.getStatus()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading shipments: " + ex.getMessage()); // Display an error message if there is a problem
        }
    }

    // Method to add a new shipment
    private void addShipment() {
        try {
            // Get input values from the form fields
            int orderId = Integer.parseInt(orderIdField.getText());
            String shipmentDate = shipmentDateField.getText();
            String deliveryDate = deliveryDateField.getText();
            String status = (String) statusComboBox.getSelectedItem(); // Get selected status

            // Create a new shipment object with the provided data
            Shipment shipment = new Shipment(0, orderId, shipmentDate, deliveryDate, status);
            shipmentDAO.addShipment(shipment); // Add the new shipment to the database

            JOptionPane.showMessageDialog(null, "Shipment added successfully!"); // Show success message
            loadAllShipments(); // Reload the list of shipments
            clearFields(); // Clear the input fields
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values."); // Show error if the input is not numeric
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding shipment: " + ex.getMessage()); // Show database error if operation fails
        }
    }

    // Method to update the selected shipment
    private void updateShipment() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select a shipment to update."); // Show error if no row is selected
            return;
        }

        try {
            // Get the shipment ID from the selected row and input values from the fields
            int shipmentId = (int) tableModel.getValueAt(selectedRow, 0);
            int orderId = Integer.parseInt(orderIdField.getText());
            String shipmentDate = shipmentDateField.getText();
            String deliveryDate = deliveryDateField.getText();
            String status = (String) statusComboBox.getSelectedItem();

            // Create a new shipment object with the updated data
            Shipment updatedShipment = new Shipment(shipmentId, orderId, shipmentDate, deliveryDate, status);
            shipmentDAO.updateShipment(updatedShipment); // Update the shipment in the database

            JOptionPane.showMessageDialog(null, "Shipment updated successfully!"); // Show success message
            loadAllShipments(); // Reload the list of shipments
            clearFields(); // Clear the input fields
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values."); // Show error if the input is not numeric
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating shipment: " + ex.getMessage()); // Show database error if operation fails
        }
    }

    // Method to delete the selected shipment
    private void deleteShipment() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select a shipment to delete."); // Show error if no row is selected
            return;
        }

        int shipmentId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this shipment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                shipmentDAO.deleteShipment(shipmentId); // Delete the shipment from the database
                JOptionPane.showMessageDialog(null, "Shipment deleted successfully!"); // Show success message
                loadAllShipments(); // Reload the list of shipments
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting shipment: " + ex.getMessage()); // Show error message if deletion fails
            }
        }
    }

    // Method to search for shipments based on a query
    private void searchShipments(String query) {
        try {
            // Search shipments in the database that match the query
            List<Shipment> searchResults = shipmentDAO.searchShipments(query);
            tableModel.setRowCount(0); // Clear current table data

            // Add each matching shipment to the table
            for (Shipment shipment : searchResults) {
                tableModel.addRow(new Object[]{shipment.getShipmentId(), shipment.getOrderId(), shipment.getShipmentDate(), shipment.getDeliveryDate(), shipment.getStatus()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error searching shipments: " + ex.getMessage()); // Show error if search fails
        }
    }

    // Method to clear all input fields
    private void clearFields() {
        shipmentIdField.setText("");
        orderIdField.setText("");
        shipmentDateField.setText("");
        deliveryDateField.setText("");
        statusComboBox.setSelectedIndex(0); // Reset status combo box to the default option
        shipmentTable.clearSelection(); // Clear table selection
    }
}
