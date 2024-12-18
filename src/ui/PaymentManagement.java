package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.PaymentDAO;
import model.Payment;

public class PaymentManagement {

    // DAO instance for interacting with the Payment data in the database
    private PaymentDAO paymentDAO = new PaymentDAO();
    
    // UI components for managing the Payment information
    private JTable paymentTable; // Table to display payment records
    private DefaultTableModel tableModel; // Table model to hold payment data
    private JTextField orderIdField, amountField, paymentDateField, paymentMethodField; // Input fields for payment details
    private JTextField searchField = new JTextField(15); // Search field for querying payments
    private JButton searchButton = new JButton("Search"); // Search button to trigger the search
    private JButton resetButton = new JButton("Reset"); // Reset button to clear search field and reload data

    // Method to display the Payment Management UI window
    public void showPaymentManagement() {
        // Create the main frame for the Payment Management window
        JFrame paymentFrame = new JFrame("Payment Management");
        paymentFrame.setSize(800, 600); // Set window size
        paymentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close window when done
        paymentFrame.setLayout(new BorderLayout()); // Set layout manager

        // Title label at the top
        JLabel titleLabel = new JLabel("Manage Payments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font for the title
        paymentFrame.add(titleLabel, BorderLayout.NORTH); // Add title label to the top of the frame

        // Panel to hold input fields for Order ID, Amount, Payment Date, and Payment Method
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Use GridBagLayout to arrange components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Add components for Order ID input
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Order ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; orderIdField = new JTextField(20); inputPanel.add(orderIdField, gbc);

        // Add components for Amount input
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; amountField = new JTextField(20); inputPanel.add(amountField, gbc);

        // Add components for Payment Date input (in yyyy-mm-dd format)
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Payment Date (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; paymentDateField = new JTextField(20); inputPanel.add(paymentDateField, gbc);

        // Add components for Payment Method input
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; paymentMethodField = new JTextField(20); inputPanel.add(paymentMethodField, gbc);

        // Add "Add Payment" button to the input panel
        JButton addButton = new JButton("Add Payment");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Arrange buttons in a flow layout
        buttonPanel.add(addButton); // Add the "Add Payment" button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; inputPanel.add(buttonPanel, gbc);
        paymentFrame.add(inputPanel, BorderLayout.WEST); // Add input panel to the left side of the frame

        // Table to display payment records
        tableModel = new DefaultTableModel(new String[]{"ID", "Order ID", "Amount", "Payment Date", "Payment Method"}, 0);
        paymentTable = new JTable(tableModel); // Create the table
        JScrollPane scrollPane = new JScrollPane(paymentTable); // Add scroll to the table
        paymentFrame.add(scrollPane, BorderLayout.CENTER); // Add the table to the center of the frame

        // Panel for search functionality (Search and Reset buttons)
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton); // Add search button
        searchPanel.add(resetButton); // Add reset button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; inputPanel.add(searchPanel, gbc); // Add search panel below input fields

        // Bottom panel with "Back to Main Menu" button
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Main Menu");
        bottomPanel.add(backButton); // Add the back button to the bottom panel
        paymentFrame.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to the bottom of the frame

        // Action listener to go back to the main menu when the back button is clicked
        backButton.addActionListener(e -> {
            paymentFrame.dispose(); // Close the payment management window
            new Main().showMainMenu(LoginUI.currentUser); // Open the main menu
        });

        // Action listener for the search button to trigger the search functionality
        searchButton.addActionListener(e -> searchPayments());
        
        // Action listener for the reset button to reload all payments and clear search field
        resetButton.addActionListener(e -> loadPayments());

        // Action listener for adding a new payment
        addButton.addActionListener(e -> addPayment());

        // Table row selection listener to populate input fields with the selected row's data
        paymentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && paymentTable.getSelectedRow() != -1) {
                    int selectedRow = paymentTable.getSelectedRow(); // Get selected row index
                    // Populate the input fields with data from the selected row
                    orderIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                    amountField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                    paymentDateField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    paymentMethodField.setText((String) tableModel.getValueAt(selectedRow, 4));
                }
            }
        });

        // Load all payments when the window is first shown
        loadPayments();
        paymentFrame.setVisible(true); // Make the window visible
    }

    // Method to load and display all payments in the table
    private void loadPayments() {
        tableModel.setRowCount(0); // Clear current data in the table
        try {
            // Fetch all payments from the database
            List<Payment> payments = paymentDAO.getAllPayments(); 
            for (Payment payment : payments) {
                // Add each payment to the table
                tableModel.addRow(new Object[]{payment.getPaymentId(), payment.getOrderId(), payment.getAmount(), payment.getPaymentDate(), payment.getPaymentMethod()});
            }
        } catch (SQLException e) {
            // Show error message if there's a problem fetching payments from the database
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage());
        }
    }

    // Method to add a new payment
    private void addPayment() {
        try {
            // Get values from input fields
            int orderId = Integer.parseInt(orderIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            LocalDate paymentDate = LocalDate.parse(paymentDateField.getText()); // Parse payment date
            String paymentMethod = paymentMethodField.getText();

            // Create a new payment object with the input data
            Payment payment = new Payment(0, orderId, amount, paymentDate, paymentMethod);

            // Call DAO to add the new payment to the database
            paymentDAO.addPayment(payment);

            // Show success message
            JOptionPane.showMessageDialog(null, "Payment added successfully! Order status updated to 'Paid'.");
            loadPayments(); // Reload the list of payments
        } catch (SQLException ex) {
            // Show error message if there's a problem adding the payment
            JOptionPane.showMessageDialog(null, "Error adding payment: " + ex.getMessage());
        } catch (Exception ex) {
            // Show error message if there's an invalid input
            JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage());
        }
    }

    // Method to search payments based on a query entered in the search field
    private void searchPayments() {
        tableModel.setRowCount(0); // Clear current data in the table
        String query = searchField.getText(); // Get search query from the text field
        try {
            // Fetch payments that match the search query
            List<Payment> payments = paymentDAO.searchPayments(query); 
            for (Payment payment : payments) {
                // Add each matching payment to the table
                tableModel.addRow(new Object[]{payment.getPaymentId(), payment.getOrderId(), payment.getAmount(), payment.getPaymentDate(), payment.getPaymentMethod()});
            }
        } catch (SQLException e) {
            // Show error message if there's a problem searching for payments
            JOptionPane.showMessageDialog(null, "Error searching payments: " + e.getMessage());
        }
    }

    // Main method to launch the Payment Management UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentManagement().showPaymentManagement());
    }
}
