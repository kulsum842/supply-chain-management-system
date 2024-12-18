package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

import dao.SupplierDAO;
import model.Supplier;

public class SupplierManagement {
    // GUI components
    private JFrame supplierFrame;
    private SupplierDAO supplierDAO = new SupplierDAO(); // DAO object for database interaction
    private JTable supplierTable;
    private DefaultTableModel tableModel; // Table model for displaying supplier data
    private JTextField supplierNameField, contactPersonNameField, phoneNumberField, emailField, addressField, cityField;
    private JTextField searchField = new JTextField(15); // Search field
    private JButton searchButton = new JButton("Search"); // Search button
    private JButton resetButton = new JButton("Reset"); // Reset button

    // Method to display the Supplier Management window
    public void showSupplierManagement() {
        supplierFrame = new JFrame("Supplier Management"); // Create a new JFrame
        supplierFrame.setSize(800, 600); // Set size of the frame
        supplierFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close operation
        supplierFrame.setLayout(new BorderLayout()); // Set layout for the frame

        // Title label at the top of the window
        JLabel titleLabel = new JLabel("Manage Suppliers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font for title
        supplierFrame.add(titleLabel, BorderLayout.NORTH); // Add title to the north of the layout

        // Input panel for adding/updating supplier details
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Adding labels and input fields for supplier details
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Supplier Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; supplierNameField = new JTextField(20); inputPanel.add(supplierNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Contact Person Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; contactPersonNameField = new JTextField(20); inputPanel.add(contactPersonNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; phoneNumberField = new JTextField(20); inputPanel.add(phoneNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; emailField = new JTextField(20); inputPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; addressField = new JTextField(20); inputPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; cityField = new JTextField(20); inputPanel.add(cityField, gbc);

        // Buttons for Add, Update, and Delete actions
        JButton addButton = new JButton("Add Supplier");
        JButton updateButton = new JButton("Update Supplier");
        JButton deleteButton = new JButton("Delete Supplier");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Use FlowLayout for button arrangement
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(buttonPanel, gbc); // Add buttons to input panel
        supplierFrame.add(inputPanel, BorderLayout.WEST); // Add input panel to the left side

        // Set up the table for displaying supplier data
        tableModel = new DefaultTableModel(new String[]{"ID", "Supplier Name", "Contact Person Name", "Phone Number", "Email", "Address", "City"}, 0);
        supplierTable = new JTable(tableModel); // Create table with the model
        JScrollPane scrollPane = new JScrollPane(supplierTable); // Wrap table with scroll pane
        supplierFrame.add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

        // Panel for search functionality
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout()); // Layout for search components
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        // Adding search panel to the input panel
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; inputPanel.add(searchPanel, gbc);

        // Action listener for search button
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            if (!query.isEmpty()) {
                searchSuppliers(query); // Perform search if query is not empty
            }
        });

        // Action listener for reset button (reload suppliers and clear search)
        resetButton.addActionListener(e -> {
            loadSuppliers(); // Reload all suppliers
            searchField.setText(""); // Clear search field
        });

        // Action listeners for Add, Update, Delete buttons
        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());

        // Selection listener for the table (to fill the input fields with selected row data)
        supplierTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && supplierTable.getSelectedRow() != -1) {
                    int selectedRow = supplierTable.getSelectedRow();
                    // Set the fields based on the selected row
                    supplierNameField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    contactPersonNameField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    phoneNumberField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    emailField.setText((String) tableModel.getValueAt(selectedRow, 4));
                    addressField.setText((String) tableModel.getValueAt(selectedRow, 5));
                    cityField.setText((String) tableModel.getValueAt(selectedRow, 6));
                }
            }
        });

        // Button to return to the main menu
        JButton mainMenuButton = new JButton("Return to Main Menu");
        mainMenuButton.addActionListener((ActionEvent e) -> {
            supplierFrame.dispose(); // Close current window
            new Main().showMainMenu(null); // Show the main menu window
        });

        // Panel for the bottom part (return to main menu button)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(mainMenuButton);

        supplierFrame.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to the bottom

        // Load suppliers from the database to display in the table
        loadSuppliers();
        supplierFrame.setVisible(true); // Display the window
    }

    // Method to load all suppliers from the database
    private void loadSuppliers() {
        tableModel.setRowCount(0); // Clear current data in the table
        try {
            List<Supplier> suppliers = supplierDAO.getAllSuppliers(); // Get all suppliers from the database
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPersonName(),
                    supplier.getPhoneNumber(),
                    supplier.getEmail(),
                    supplier.getAddress(),
                    supplier.getCity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading suppliers: " + e.getMessage()); // Show error message
        }
    }

    // Method to add a new supplier
    private void addSupplier() {
        // Get values from input fields
        String supplierName = supplierNameField.getText();
        String contactPersonName = contactPersonNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String city = cityField.getText();

        // Check if any field is empty
        if (supplierName.isEmpty() || contactPersonName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out."); // Show error if empty
            return;
        }

        // Create new Supplier object
        Supplier supplier = new Supplier(0, supplierName, contactPersonName, phoneNumber, email, address, city);
        try {
            supplierDAO.addSupplier(supplier); // Add the supplier to the database
            JOptionPane.showMessageDialog(null, "Supplier added successfully!"); // Success message
            loadSuppliers(); // Reload suppliers
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding supplier: " + ex.getMessage()); // Show error if failed
        }
    }

    // Method to update the selected supplier
    private void updateSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Please select a supplier to update."); // Error if no row selected
            return;
        }

        // Get supplier ID and updated details
        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String supplierName = supplierNameField.getText();
        String contactPersonName = contactPersonNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String city = cityField.getText();

        // Check if any field is empty
        if (supplierName.isEmpty() || contactPersonName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out."); // Show error if empty
            return;
        }

        // Create new Supplier object with updated data
        Supplier supplier = new Supplier(supplierId, supplierName, contactPersonName, phoneNumber, email, address, city);
        try {
            supplierDAO.updateSupplier(supplier); // Update supplier in the database
            JOptionPane.showMessageDialog(null, "Supplier updated successfully!"); // Success message
            loadSuppliers(); // Reload suppliers
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating supplier: " + ex.getMessage()); // Show error if failed
        }
    }

    // Method to delete the selected supplier
    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Please select a supplier to delete."); // Error if no row selected
            return;
        }

        // Get supplier ID and ask for confirmation
        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this supplier?",
                "Delete Supplier",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                supplierDAO.deleteSupplier(supplierId); // Delete supplier from the database
                JOptionPane.showMessageDialog(null, "Supplier deleted successfully!"); // Success message
                loadSuppliers(); // Reload suppliers
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting supplier: " + ex.getMessage()); // Show error if failed
            }
        }
    }

    // Method to search suppliers based on a query
    private void searchSuppliers(String query) {
        tableModel.setRowCount(0); // Clear current table data
        try {
            List<Supplier> suppliers = supplierDAO.searchSuppliers(query); // Get suppliers matching the query
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPersonName(),
                    supplier.getPhoneNumber(),
                    supplier.getEmail(),
                    supplier.getAddress(),
                    supplier.getCity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching suppliers: " + e.getMessage()); // Show error if failed
        }
    }

    // Main method to launch the SupplierManagement UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupplierManagement().showSupplierManagement()); // Create and show UI
    }
}
