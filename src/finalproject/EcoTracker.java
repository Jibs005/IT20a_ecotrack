/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package finalproject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;







public class EcoTracker {
    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public static HashMap<String, String> accounts = new HashMap<>();

    public LoginFrame() {
        setTitle("EcoTracker Login");
        setSize(380, 450);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.white);

        JLabel title = new JLabel("EcoTracker Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(90, 20, 250, 40);
        panel.add(title);

        JLabel uLabel = new JLabel("Username:");
        uLabel.setBounds(50, 100, 300, 20);
        panel.add(uLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 125, 260, 35);
        panel.add(usernameField);

        JLabel pLabel = new JLabel("Password:");
        pLabel.setBounds(50, 175, 300, 20);
        panel.add(pLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 200, 260, 35);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(50, 260, 260, 40);
        loginBtn.addActionListener(e -> validateLogin());
        panel.add(loginBtn);

        JButton signupBtn = new JButton("Create Account");
        signupBtn.setBounds(50, 310, 260, 40);
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupFrame();
        });
        panel.add(signupBtn);

        add(panel);
        setVisible(true);
    }

    private void validateLogin() {
        String user = usernameField.getText();
        String pass = String.valueOf(passwordField.getPassword());

        if (!accounts.containsKey(user)) {
            JOptionPane.showMessageDialog(this, "Account does not exist.");
            return;
        }

        if (!accounts.get(user).equals(pass)) {
            JOptionPane.showMessageDialog(this, "Wrong password.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Login successful!");
        dispose();
        new DashboardFrame(user);
    }
}

public class SignupFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField confirmField;

    public SignupFrame() {
        setTitle("Create Account");
        setSize(380, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(90, 20, 300, 40);
        panel.add(title);

        JLabel u = new JLabel("Username:");
        u.setBounds(50, 90, 260, 20);
        panel.add(u);

        userField = new JTextField();
        userField.setBounds(50, 115, 260, 35);
        panel.add(userField);

        JLabel p = new JLabel("Password:");
        p.setBounds(50, 165, 260, 20);
        panel.add(p);

        passField = new JPasswordField();
        passField.setBounds(50, 190, 260, 35);
        panel.add(passField);

        JLabel c = new JLabel("Confirm Password:");
        c.setBounds(50, 240, 260, 20);
        panel.add(c);

        confirmField = new JPasswordField();
        confirmField.setBounds(50, 265, 260, 35);
        panel.add(confirmField);

        JButton createBtn = new JButton("Create Account");
        createBtn.setBounds(50, 330, 260, 40);
        createBtn.addActionListener(e -> handleSignup());
        panel.add(createBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(50, 380, 260, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        panel.add(backBtn);

        add(panel);
        setVisible(true);
    }

    private void handleSignup() {
        String u = userField.getText();
        String p = String.valueOf(passField.getPassword());
        String cp = String.valueOf(confirmField.getPassword());

        if (u.isEmpty() || p.isEmpty() || cp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        if (LoginFrame.accounts.containsKey(u)) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
            return;
        }

        if (!p.equals(cp)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        LoginFrame.accounts.put(u, p);
        JOptionPane.showMessageDialog(this, "Account created!");

        dispose();
        new LoginFrame();
    }
}

public class DashboardFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private OverviewPanel overviewPanel;
    private RankingsPanel rankingsPanel;
    private LogsPanel logsPanel;

    public static HashMap<String, ClassroomData> currentData = new HashMap<>();

    public DashboardFrame(String loggedInUser) {
        setTitle("EcoTracker Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // LEFT NAVIGATION
        JPanel nav = new JPanel();
        nav.setLayout(new GridLayout(6, 1));
        nav.setBackground(new Color(40, 40, 40));
        nav.setPreferredSize(new Dimension(180, 0));

        JButton btnOverview = navButton("Overview");
        JButton btnRankings = navButton("Rankings");
        JButton btnLogs = navButton("Logs");
        JButton btnLogout = navButton("Logout");

        nav.add(btnOverview);
        nav.add(btnRankings);
        nav.add(btnLogs);
        nav.add(new JLabel()); // spacer
        nav.add(btnLogout);

        add(nav, BorderLayout.WEST);

        // CENTER CARD PANEL
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        overviewPanel = new OverviewPanel();
        rankingsPanel = new RankingsPanel();
        logsPanel = new LogsPanel();

        cardPanel.add(overviewPanel, "overview");
        cardPanel.add(rankingsPanel, "rankings");
        cardPanel.add(logsPanel, "logs");

        add(cardPanel, BorderLayout.CENTER);

        // ACTIONS
        btnOverview.addActionListener(e -> cardLayout.show(cardPanel, "overview"));
        btnRankings.addActionListener(e -> cardLayout.show(cardPanel, "rankings"));
        btnLogs.addActionListener(e -> cardLayout.show(cardPanel, "logs"));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(60, 60, 60));
        b.setForeground(Color.white);
        b.setBorderPainted(false);
        return b;
    }
}

public class OverviewPanel extends JPanel {

    public OverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        JLabel title = new JLabel("Overview", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 20));
        cards.setBorder(new EmptyBorder(40, 40, 40, 40));
        cards.setBackground(Color.white);

        cards.add(summaryCard("Total Entries", "0"));
        cards.add(summaryCard("Highest Score", "0"));
        cards.add(summaryCard("Lowest Score", "0"));
        cards.add(summaryCard("Average", "0"));

        add(cards, BorderLayout.CENTER);
    }

    private JPanel summaryCard(String label, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 240, 240));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(new Font("Arial", Font.BOLD, 30));

        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}

public class RankingsPanel extends JPanel {

    private JTable rankTable;

    public RankingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        JLabel title = new JLabel("Classroom Rankings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Room", "Score"};
        rankTable = new JTable(new DefaultTableModel(cols, 0));
        rankTable.setRowHeight(28);

        add(new JScrollPane(rankTable), BorderLayout.CENTER);
    }
}

public class LogsPanel extends JPanel {

    private JTable logsTable;
    private JTextField searchField;

    public LogsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        JLabel title = new JLabel("Logs", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"Room", "Score", "Timestamp"};
        logsTable = new JTable(new DefaultTableModel(cols, 0));
        logsTable.setRowHeight(28);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(logsTable.getModel());
        logsTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) {}
            void filter() {
                sorter.setRowFilter(RowFilter.regexFilter(searchField.getText()));
            }
        });

        add(new JScrollPane(logsTable), BorderLayout.CENTER);
    }
}

public class ClassroomData {
    public String room;
    public int score;
    public String timestamp;

    public ClassroomData(String room, int score, String timestamp) {
        this.room = room;
        this.score = score;
        this.timestamp = timestamp;
    }
}

public class RoundedPanel extends JPanel {

    private int radius;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d = getSize();
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, d.width, d.height, radius, radius);
    }
}




