import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
public class Ecotracker {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

class LoginFrame extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    static HashMap<String, String> accounts = new HashMap<>();

    public LoginFrame() {

        // Preload a default account (optional)
        if (!accounts.containsKey("Administrator")) {
            accounts.put("Administrator", "admin");
        }

        setTitle("EcoTrack — Login");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Root panel with soft background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        add(root);

        // Container card (rounded)
        RoundedPanel card = new RoundedPanel(16, new Color(255,255,255));
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(480, 340));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(card);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel logo = new JLabel("EcoTrack");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(new Color(10, 90, 140));

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(110, 120, 130));

        JPanel leftHeader = new JPanel(new GridLayout(2,1));
        leftHeader.setOpaque(false);
        leftHeader.add(logo);
        leftHeader.add(sub);

        header.add(leftHeader, BorderLayout.WEST);

        // small right label
        JLabel ver = new JLabel("v1.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(160,160,160));
        header.add(ver, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        // Center form
        JPanel form = new RoundedPanel(12, new Color(250,250,250));
        form.setLayout(null);
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.setPreferredSize(new Dimension(440, 220));

        // Labels and fields (positioning similar to original but prettier)
        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblU.setBounds(40, 20, 200, 18);
        form.add(lblU);

        usernameField = styledTextField();
        usernameField.setBounds(40, 40, 360, 36);
        form.add(usernameField);

        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblP.setBounds(40, 86, 200, 18);
        form.add(lblP);

        passwordField = styledPasswordField();
        passwordField.setBounds(40, 106, 360, 36);
        form.add(passwordField);

        JCheckBox rememberBox = new JCheckBox("Remember me");
        rememberBox.setOpaque(false);
        rememberBox.setBounds(40, 152, 140, 20);
        rememberBox.setForeground(new Color(90,100,110));
        form.add(rememberBox);

        JButton loginBtn = styledPrimaryButton("CONTINUE");
        loginBtn.setBounds(206, 146, 194, 36);
        form.add(loginBtn);

        // Create account link
        JLabel createLink = new JLabel("<HTML><U>Create account</U></HTML>");
        createLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createLink.setBounds(40, 176, 200, 18);
        createLink.setForeground(new Color(12,100,180));
        form.add(createLink);

        card.add(form, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Code 201 • Released: 5/10/25 11:11 PM", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(150,150,150));
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(footer, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (!accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(this, "Account does not exist!");
                return;
            }

            if (!accounts.get(user).equals(pass)) {
                JOptionPane.showMessageDialog(this, "Incorrect password!");
                return;
            }

            JOptionPane.showMessageDialog(this, "Login Successful!");
            new EcoDashboard(user);
            dispose();
        });

        createLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new SignupFrame();
                dispose();
            }
        });

        setVisible(true);
    }
    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return pf;
    }

    private JButton styledPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(12, 100, 180));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8,14,8,14));
        return b;
    }
}
class SignupFrame extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JPasswordField confirmField;

    public SignupFrame() {

        setTitle("EcoTrack — Create Account");
        setSize(560, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        add(root);

        RoundedPanel card = new RoundedPanel(16, new Color(255,255,255));
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 380));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(card);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(10,90,140));
        header.add(title, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);

        // Form area
        JPanel form = new JPanel(null);
        form.setOpaque(false);

        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblU.setBounds(40, 12, 200, 18);
        form.add(lblU);

        usernameField = new JTextField();
        usernameField.setBounds(40, 34, 420, 36);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(usernameField);

        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblP.setBounds(40, 82, 200, 18);
        form.add(lblP);

        passwordField = new JPasswordField();
        passwordField.setBounds(40, 104, 420, 36);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(passwordField);

        JLabel lblC = new JLabel("Confirm Password");
        lblC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblC.setBounds(40, 152, 200, 18);
        form.add(lblC);

        confirmField = new JPasswordField();
        confirmField.setBounds(40, 174, 420, 36);
        confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(confirmField);

        JButton signupBtn = new JButton("CREATE ACCOUNT");
        signupBtn.setBounds(40, 222, 420, 40);
        signupBtn.setBackground(new Color(10,130,200));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupBtn.setFocusPainted(false);
        signupBtn.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        form.add(signupBtn);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setBounds(40, 272, 420, 34);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(12,100,180));
        backBtn.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        form.add(backBtn);

        card.add(form, BorderLayout.CENTER);

        // Actions
        signupBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do NOT match!");
                return;
            }

            if (LoginFrame.accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(this, "Username already taken!");
                return;
            }

            LoginFrame.accounts.put(user, pass);
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");

            new LoginFrame();
            dispose();
        });

        backBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}
class EcoDashboard extends JFrame {
    private String currentUser = "Administrator";
    private JLabel loggedInLabel;
    private CardLayout centerCards;
    private JPanel centerPanel;

    private JTable rankingTable;
    private DefaultTableModel rankingModel;

    private JTable logTable;
    private DefaultTableModel logModel;
    private TableRowSorter<DefaultTableModel> logSorter;
    private JTextField logsSearchField;

    private Map<String, ClassroomData> currentData = new LinkedHashMap<>();
    private final String[] CLASS_IDS = {"Room 3A", "Room 5B", "Room 1C", "Room 2E", "Room 34", "Room 2", "Room 4"};
    private final SimpleDateFormat LOG_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   
    public EcoDashboard(String username) {
        this.currentUser = username == null || username.isEmpty() ? "Anonymous" : username;

        setTitle("EcoTrack — Electricity Dashboard");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        // initial data
        generateDataForPeriod("Weekly");
        // log that user has logged in
        logAction("User logged in");
        setVisible(true);
    }

    private void initUI() {
        // Top bar
        JPanel topBar = new RoundedPanel(0, new Color(12, 100, 148));
        topBar.setLayout(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 72));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(30,120,140)));

        JLabel title = new JLabel("   EcoTrack: Electricity Usage");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 18));
        leftTop.setOpaque(false);
        JLabel icon = new JLabel("\u26A1"); // lightning
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
        icon.setForeground(Color.WHITE);
        leftTop.add(icon);
        leftTop.add(title);
        topBar.add(leftTop, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        rightTop.setOpaque(false);
        loggedInLabel = new JLabel("Logged in: " + currentUser);
        loggedInLabel.setForeground(Color.WHITE);
        loggedInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton logout = smallFlatButton("Logout");
        logout.addActionListener(e -> doLogout());
        rightTop.add(loggedInLabel);
        rightTop.add(logout);
        topBar.add(rightTop, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Main content area: left nav + center cards
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245,247,250));
        add(content, BorderLayout.CENTER);

        // Left navigation - vertical cards
        JPanel leftNavWrap = new JPanel(new BorderLayout());
        leftNavWrap.setOpaque(false);
        leftNavWrap.setPreferredSize(new Dimension(220, 0));

        JPanel leftNav = new JPanel();
        leftNav.setLayout(new BoxLayout(leftNav, BoxLayout.Y_AXIS));
        leftNav.setBackground(new Color(245,247,250));
        leftNav.setBorder(new EmptyBorder(18, 12, 18, 12));

        // App card on top
        RoundedPanel navCard = new RoundedPanel(12, new Color(255,255,255));
        navCard.setLayout(new BorderLayout());
        navCard.setBorder(new EmptyBorder(12,12,12,12));
        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        navTitle.setForeground(new Color(20,70,100));
        navCard.add(navTitle, BorderLayout.NORTH);

        JPanel btns = new JPanel();
        btns.setOpaque(false);
        btns.setLayout(new BoxLayout(btns, BoxLayout.Y_AXIS));
        String[] nav = {"Overview", "Class Rankings", "Detailed Logs"};
        for (String n : nav) {
            JButton b = navButton(n);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            b.addActionListener(e -> switchCenter(n));
            btns.add(b);
            btns.add(Box.createRigidArea(new Dimension(0,8)));
        }
        navCard.add(btns, BorderLayout.CENTER);
        leftNav.add(navCard);
        leftNav.add(Box.createRigidArea(new Dimension(0,12)));

        // quick info card
        RoundedPanel infoCard = new RoundedPanel(12, new Color(255,255,255));
        infoCard.setLayout(new GridLayout(2,1,6,6));
        infoCard.setBorder(new EmptyBorder(10,10,10,10));
        JLabel info1 = new JLabel("<html><b>Current user</b><br/>" + currentUser + "</html>");
        info1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel info2 = new JLabel("<html><b>Mode</b><br/>Electricity only</html>");
        info2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoCard.add(info1);
        infoCard.add(info2);
        leftNav.add(infoCard);

        leftNavWrap.add(leftNav, BorderLayout.NORTH);
        content.add(leftNavWrap, BorderLayout.WEST);

        // Center area: cards controlled by CardLayout
        centerCards = new CardLayout();
        centerPanel = new JPanel(centerCards);
        centerPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        centerPanel.setBackground(new Color(245,247,250));

        centerPanel.add(buildOverviewPanel(), "Overview");
        centerPanel.add(buildClassRankingsPanel(), "Class Rankings");
        centerPanel.add(buildLogsPanel(), "Detailed Logs");

        content.add(centerPanel, BorderLayout.CENTER);
    }

 private JPanel buildOverviewPanel() {

    JPanel p = new JPanel(new BorderLayout());
    p.setOpaque(false);

    RoundedPanel summaryCard = new RoundedPanel(12, Color.WHITE);
    summaryCard.setLayout(new GridLayout(5, 1, 5, 5));
    summaryCard.setBorder(new EmptyBorder(16, 16, 16, 16));

    JLabel sumHeader = new JLabel("Electricity Summary");
    sumHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
    sumHeader.setForeground(new Color(20, 70, 120));
    summaryCard.add(sumHeader);

    int total = 0;
    int highest = Integer.MIN_VALUE;
    int lowest = Integer.MAX_VALUE;

    String highestRoom = "—";
    String lowestRoom = "—";

    for (String room : currentData.keySet()) {
        int kwh = currentData.get(room).electricity;
        total += kwh;

        if (kwh > highest) {
            highest = kwh;
            highestRoom = room;
        }
        if (kwh < lowest) {
            lowest = kwh;
            lowestRoom = room;
        }
    }

    int avg = currentData.size() > 0 ? total / currentData.size() : 0;

    summaryCard.add(new JLabel("• Total Electricity Used: " + total + " kWh"));
    summaryCard.add(new JLabel("• Highest Usage: " + highestRoom + " — " + highest + " kWh"));
    summaryCard.add(new JLabel("• Lowest Usage: " + lowestRoom + " — " + lowest + " kWh"));
    summaryCard.add(new JLabel("• Average per Room: " + avg + " kWh"));
    JPanel wrapper = new JPanel();
    wrapper.setOpaque(false);
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
    wrapper.add(Box.createVerticalStrut(10));
    wrapper.add(summaryCard);
    p.add(wrapper, BorderLayout.NORTH);
    JPanel overview = new JPanel();
    overview.setOpaque(false);
    overview.add(new JLabel("Overview of the project"));
    p.add(overview, BorderLayout.CENTER);

    return p;
}


    private JPanel buildClassRankingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        RoundedPanel topCard = new RoundedPanel(12, Color.WHITE);
        topCard.setLayout(new BorderLayout());
        topCard.setBorder(new EmptyBorder(12,12,12,12));
        JLabel t = new JLabel("Classroom Electricity Rankings");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topCard.add(t, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        controls.add(new JLabel("Time Period:"));

        JComboBox<String> periodCombo = new JComboBox<>(new String[]{"Recently", "Weekly", "Monthly"});
        periodCombo.setSelectedItem("Weekly");
        periodCombo.addActionListener(e -> {
            String pSel = (String) periodCombo.getSelectedItem();
            generateDataForPeriod(pSel);
            logAction("Changed time period to " + pSel);
        });
        styleCombo(periodCombo);
        controls.add(periodCombo);

        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("-"));

        topCard.add(controls, BorderLayout.EAST);
        panel.add(topCard, BorderLayout.NORTH);

        String[] cols = {"#", "Classroom/Room ID (Electricity)", "Electricity (kWh)"};
        rankingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        rankingTable = new JTable(rankingModel);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setRowHeight(28);

        rankingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && rankingTable.getSelectedRow() != -1) {
                int r = rankingTable.convertRowIndexToModel(rankingTable.getSelectedRow());
                String room = (String) rankingModel.getValueAt(r, 1);
                ClassroomData cd = currentData.get(room);
                if (cd != null) {
                    JOptionPane.showMessageDialog(this,
                            "Details for " + room + ":\n- Electricity: " + cd.electricity + " kWh",
                            "Class Details", JOptionPane.INFORMATION_MESSAGE);
                    logAction("Viewed electricity details for " + room);
                }
            }
        });

        RoundedPanel mid = new RoundedPanel(12, Color.WHITE);
        mid.setLayout(new BorderLayout());
        mid.setBorder(new EmptyBorder(12,12,12,12));
        mid.add(new JScrollPane(rankingTable), BorderLayout.CENTER);

        // Lower chart card
        RoundedPanel lower = new RoundedPanel(12, Color.WHITE);
        lower.setLayout(new BorderLayout());
        lower.setBorder(new EmptyBorder(12,12,12,12));
        lower.add(new JLabel("Top 5 Classrooms by Electricity"), BorderLayout.NORTH);

        ChartPanel chart = new ChartPanel();
        chart.setPreferredSize(new Dimension(760, 220));
        lower.add(chart, BorderLayout.CENTER);

        panel.add(mid, BorderLayout.CENTER);
        panel.add(lower, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildLogsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        RoundedPanel header = new RoundedPanel(12, Color.WHITE);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Detailed Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);

        p.add(header, BorderLayout.NORTH);

        RoundedPanel body = new RoundedPanel(12, Color.WHITE);
        body.setLayout(new BorderLayout());
        body.setBorder(new EmptyBorder(12,12,12,12));

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setOpaque(false);

        JPanel leftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftSearch.setOpaque(false);
        leftSearch.add(new JLabel("Search logs:"));
        logsSearchField = new JTextField(28);
        logsSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyLogsFilter(); }
            public void removeUpdate(DocumentEvent e) { applyLogsFilter(); }
            public void changedUpdate(DocumentEvent e) { applyLogsFilter(); }
        });
        logsSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        leftSearch.add(logsSearchField);
        topControls.add(leftSearch, BorderLayout.WEST);

        JPanel rightSorts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightSorts.setOpaque(false);

        JButton dateNewest = smallFlatButton("Date Newest");
        dateNewest.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(0, SortOrder.DESCENDING))
        ));

        JButton dateOldest = smallFlatButton("Date Oldest");
        dateOldest.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING))
        ));

        JButton userAsc = smallFlatButton("User A→Z");
        userAsc.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(1, SortOrder.ASCENDING))
        ));

        JButton userDesc = smallFlatButton("User Z→A");
        userDesc.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(1, SortOrder.DESCENDING))
        ));

        rightSorts.add(userAsc);
        rightSorts.add(userDesc);
        rightSorts.add(dateNewest);
        rightSorts.add(dateOldest);
       

        topControls.add(rightSorts, BorderLayout.EAST);
        body.add(topControls, BorderLayout.NORTH);

        String[] cols = {"Time", "User", "Action", "Type"};
        logModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        logTable = new JTable(logModel);
        logSorter = new TableRowSorter<>(logModel);

        logSorter.setComparator(0, (o1, o2) -> {
            try {
                Date d1 = LOG_TIME.parse(o1.toString());
                Date d2 = LOG_TIME.parse(o2.toString());
                return d1.compareTo(d2);
            } catch (Exception ex) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        logTable.setRowSorter(logSorter);

        body.add(new JScrollPane(logTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton sim = smallPrimaryButton("Simulate Update");
        sim.addActionListener(e -> simulateRandomUpdate());
        bottom.add(sim);

        JButton clear = smallFlatButton("Clear Logs");
        clear.addActionListener(e -> logModel.setRowCount(0));
        bottom.add(clear);

        body.add(bottom, BorderLayout.SOUTH);
        p.add(body, BorderLayout.CENTER);

        return p;
    }
    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(245,247,250));
        b.setForeground(new Color(20,70,100));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        return b;
    }
    private void styleCombo(JComboBox<String> combo) {
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(4,6,4,6)
        ));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
    
    private JButton smallIconButton(String text) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(2, 6, 2, 6));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(new Color(235,235,235));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        b.addActionListener(e -> logAction("Clicked control " + text));
        return b;
    }
    private JButton smallFlatButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0,0,0,0));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        return b;
    }
    private JButton smallPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(10,130,200));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        return b;
    }

    private JButton styledControl(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(230, 240, 245));
        b.setForeground(new Color(20,70,100));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        return b;
    }

    private void switchCenter(String name) {
        centerCards.show(centerPanel, name);
    }

    private void doLogout() {
        logAction("User logged out");
        dispose();
        new LoginFrame();
    }

    private void generateDataForPeriod(String period) {
        currentData.clear();
        Random rnd = new Random(System.currentTimeMillis());

        for (String id : CLASS_IDS) {
            int base = switch (period) {
                case "Monthly" -> 220;
                case "Recently" -> 30;
                default -> 80;
            };
            int elec = Math.max(10, base + rnd.nextInt(Math.max(1, base/2)) - Math.max(1, base/4));
            currentData.put(id, new ClassroomData(id, elec));
        }
        refreshRankingTable();
    }

    private void refreshRankingTable() {
        rankingModel.setRowCount(0);

        List<ClassroomData> list = new ArrayList<>(currentData.values());
        list.sort((a, b) -> Integer.compare(b.electricity, a.electricity));

        int idx = 1;
        for (ClassroomData cd : list) {
            rankingModel.addRow(new Object[]{idx, cd.id, cd.electricity});
            idx++;
        }
    }

    private void logAction(String action) {
        String time = LOG_TIME.format(new Date());
        if (logModel != null)
            logModel.addRow(new Object[]{time, currentUser, action, "INFO"});
    }

    private void simulateRandomUpdate() {
        if (currentData.isEmpty()) return;
        List<ClassroomData> list = new ArrayList<>(currentData.values());
        Random rnd = new Random();

        ClassroomData cd = list.get(rnd.nextInt(list.size()));
        int inc = 5 + rnd.nextInt(26);
        cd.electricity += inc;

        refreshRankingTable();
        logAction("Simulated electricity increase for " + cd.id + " (+" + inc + " kWh)");
    }

    private void applyLogsFilter() {
        if (logsSearchField == null || logSorter == null) return;
        String text = logsSearchField.getText().trim();
        if (text.isEmpty()) {
            logSorter.setRowFilter(null);
        } else {
            List<RowFilter<Object,Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
            RowFilter<Object,Object> or = RowFilter.orFilter(filters);
            logSorter.setRowFilter(or);
        }
    }
    // Classroom data Area
    static class ClassroomData {
        String id;
        int electricity;
        ClassroomData(String id, int e) { this.id = id; this.electricity = e; }
    }
    class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();

            g.setColor(new Color(245,247,250));
            g.fillRect(0,0,w,h);
            g.setColor(new Color(250,250,250));
            g.fillRoundRect(20, 8, w-40, h-20, 10, 10);

            List<Integer> values = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for (int r = 0; r < Math.min(5, rankingModel.getRowCount()); r++) {
                int val = Integer.parseInt(rankingModel.getValueAt(r, 2).toString());
                values.add(val);
                names.add(rankingModel.getValueAt(r, 1).toString());
            }

            if (values.isEmpty()) {
                g.setColor(new Color(120,120,120));
                g.drawString("No data to display", 60, h/2);
                return;
            }

            int max = Collections.max(values);
            int areaW = w - 180;
            int barW = Math.max(48, areaW / values.size());
            int baseY = h - 30;

            for (int i = 0; i < values.size(); i++) {
                int barH = (int)((double)values.get(i) / max * (h - 80));
                int x = 60 + i * barW;
                int y = baseY - barH;

                if (values.get(i) == max) g.setColor(new Color(88, 180, 93));
                else g.setColor(new Color(83, 154, 203));

                g.fillRoundRect(x, y, barW - 24, barH, 6, 6);

                g.setColor(new Color(80,80,80));
                g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g.drawString(names.get(i), x, baseY + 16);
                g.drawString(values.get(i) + " kWh", x, y - 6);
            }
        }
    }
}

class RoundedPanel extends JPanel {
    private int radius;
    private Color bg;

    public RoundedPanel(int radius, Color bg) {
        super();
        this.radius = radius;
        this.bg = bg;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // subtle shadow
        g2.setColor(new Color(0,0,0,10));
        g2.fillRoundRect(4, 6, getWidth()-10, getHeight()-8, radius+6, radius+6);

        // background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, radius, radius);
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Insets getInsets() {
        return new Insets(8,8,8,8);
    }
}
