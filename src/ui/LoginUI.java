package ui;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginUI {

    public static User currentUser; 

    private UserDAO userDAO = new UserDAO();

    public void showLogin() {
        JFrame frame = new JFrame("Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; frame.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; frame.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; frame.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; frame.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; frame.add(loginButton, gbc);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                User user = userDAO.authenticateUser(username, password);
                if (user != null) {
                    currentUser = user;
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    new Main().showMainMenu(user); 
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error during login: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}
