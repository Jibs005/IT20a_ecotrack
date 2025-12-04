import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ecotracker extends JFrame {

    CardLayout card;
    JPanel mainPanel;

    String savedUser = "admin";
    String savedPass = "1234";

    JTextField loginUser;
    JPasswordField loginPass;

    JTextField signUser;
    JPasswordField signPass;

    public ecotracker() {
        setTitle("EcoTrack - Login / Sign Up");
        setSize(250, 250);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        card = new CardLayout();
        mainPanel = new JPanel(card);

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(signUpPanel(), "signup");

        add(mainPanel);
        card.show(mainPanel, "login");
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        loginUser = new JTextField();
        panel.add(loginUser);

        panel.add(new JLabel("Password:"));
        loginPass = new JPasswordField();
        panel.add(loginPass);

        JButton loginBtn = new JButton("Login");
        JButton goSignUp = new JButton("Sign Up");

        panel.add(loginBtn);
        panel.add(goSignUp);

        loginBtn.addActionListener(e -> login());
        goSignUp.addActionListener(e -> card.show(mainPanel, "signup"));

        return panel;
    }

    private JPanel signUpPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("New Username:"));
        signUser = new JTextField();
        panel.add(signUser);

        panel.add(new JLabel("New Password:"));
        signPass = new JPasswordField();
        panel.add(signPass);

        JButton createBtn = new JButton("Create Account");
        JButton goLogin = new JButton("Back to Login");

        panel.add(createBtn);
        panel.add(goLogin);

        createBtn.addActionListener(e -> createAccount());
        goLogin.addActionListener(e -> card.show(mainPanel, "login"));

        return panel;
    }

    private void login() {
        String user = loginUser.getText();
        String pass = new String(loginPass.getPassword());

        if (user.equals(savedUser) && pass.equals(savedPass)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Username or Password.");
        }
    }

    private void createAccount() {
        String user = signUser.getText();
        String pass = new String(signPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        savedUser = user;
        savedPass = pass;

        JOptionPane.showMessageDialog(this, "Account Created Successfully!");

        signUser.setText("");
        signPass.setText("");

        card.show(mainPanel, "login");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ecotracker frame = new ecotracker();
            frame.setVisible(true);
        });
    }
}
    