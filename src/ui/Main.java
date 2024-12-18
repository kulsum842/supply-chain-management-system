package ui;

import model.User;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().showLogin());
    }

    public void showMainMenu(User user) {
        JFrame frame = new JFrame("Supply Chain Management System - Welcome " + user.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JButton supplierButton = new JButton("Manage Suppliers");
        JButton inventoryButton = new JButton("Manage Inventory");
        JButton orderButton = new JButton("Manage Orders");
        JButton shipmentButton = new JButton("Track Shipments");
        JButton paymentButton = new JButton("Process Payments");

        frame.setLayout(new GridLayout(5, 1, 10, 10));

        frame.add(supplierButton);
        frame.add(inventoryButton);
        frame.add(orderButton);
        frame.add(shipmentButton);
        frame.add(paymentButton);

        supplierButton.addActionListener(e -> new SupplierManagement().showSupplierManagement());
        inventoryButton.addActionListener(e -> new InventoryManagement().showInventoryManagement());
        orderButton.addActionListener(e -> new OrderManagement().showOrderManagement());
        shipmentButton.addActionListener(e -> new ShipmentManagement().showShipmentManagement());
        paymentButton.addActionListener(e -> new PaymentManagement().showPaymentManagement());

        frame.setVisible(true);
    }
}
