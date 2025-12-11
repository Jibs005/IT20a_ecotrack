import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class EcoDashboard extends JFrame {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcoDashboard app = new EcoDashboard();
            app.showLoginThenStart();
        });
    }

    public EcoDashboard() {
        setTitle("EcoTrack: Electricity Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(12, 100, 148));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(30, 120, 140)));
        topBar.setPreferredSize(new Dimension(0, 56));

        JLabel title = new JLabel("  EcoTrack: Electricity Usage Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTop.setOpaque(false);
        JLabel icon = new JLabel("\uD83C\uDF31");
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
        leftTop.add(icon);
        leftTop.add(title);
        topBar.add(leftTop, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        rightTop.setOpaque(false);
        loggedInLabel = new JLabel("Logged in as: " + currentUser);
        loggedInLabel.setForeground(Color.WHITE);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> doLogout());
        rightTop.add(loggedInLabel);
        rightTop.add(logout);
        topBar.add(rightTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel leftNav = new JPanel();
        leftNav.setLayout(new BoxLayout(leftNav, BoxLayout.Y_AXIS));
        leftNav.setBackground(new Color(34, 142, 165));
        leftNav.setBorder(new EmptyBorder(14, 12, 14, 12));

        // ❌ Removed: "Settings"
        String[] nav = {"Overview", "Class Rankings", "Detailed Logs"};

        for (String n : nav) {
            JButton btn = new JButton(n);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 46));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(73, 173, 183));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> switchCenter(n));
            leftNav.add(btn);
            leftNav.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        add(leftNav, BorderLayout.WEST);

        centerCards = new CardLayout();
        centerPanel = new JPanel(centerCards);

        centerPanel.add(buildOverviewPanel(), "Overview");
        centerPanel.add(buildClassRankingsPanel(), "Class Rankings");
        centerPanel.add(buildLogsPanel(), "Detailed Logs");

        add(centerPanel, BorderLayout.CENTER);

        generateDataForPeriod("Weekly");
    }

    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "This is an electricity-focused dashboard.\n\n" +
                "- Class Rankings shows electricity per classroom (NO sorting / NO search).\n" +
                "- Detailed Logs contain searching + sorting (Newest/Oldest).\n\n" +
                "Use 'Simulate Update' to increase electricity randomly."
        );
        info.setEditable(false);
        info.setBackground(p.getBackground());
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(info, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildClassRankingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new BorderLayout());
        JLabel t = new JLabel("Classroom Electricity Rankings");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        top.add(t, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        controls.setOpaque(false);
        controls.add(new JLabel("Time Period:"));

        JComboBox<String> periodCombo = new JComboBox<>(new String[]{"Recently", "Weekly", "Monthly"});
        periodCombo.setSelectedItem("Weekly");
        periodCombo.addActionListener(e -> {
            String pSel = (String) periodCombo.getSelectedItem();
            generateDataForPeriod(pSel);
            logAction("Changed time period to " + pSel);
        });
        controls.add(periodCombo);

        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("+"));
        controls.add(smallIconButton("-"));

        top.add(controls, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"#", "Classroom/Room ID (Electricity Only)", "Electricity (kWh)"};
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

        panel.add(new JScrollPane(rankingTable), BorderLayout.CENTER);

        JPanel lower = new JPanel(new BorderLayout());
        lower.add(new JLabel("Top 5 Classrooms by Electricity"), BorderLayout.NORTH);

        ChartPanel chart = new ChartPanel();
        chart.setPreferredSize(new Dimension(760, 180));
        lower.add(chart, BorderLayout.CENTER);
        panel.add(lower, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildLogsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Detailed Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(title, BorderLayout.NORTH);

        JPanel topControls = new JPanel(new BorderLayout());
        JPanel leftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftSearch.setOpaque(false);

        leftSearch.add(new JLabel("Search logs:"));
        logsSearchField = new JTextField(30);
        logsSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyLogsFilter(); }
            public void removeUpdate(DocumentEvent e) { applyLogsFilter(); }
            public void changedUpdate(DocumentEvent e) { applyLogsFilter(); }
        });
        leftSearch.add(logsSearchField);
        topControls.add(leftSearch, BorderLayout.WEST);

        JPanel rightSorts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightSorts.setOpaque(false);

        JButton dateNewest = new JButton("Date Newest");
        dateNewest.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(0, SortOrder.DESCENDING))
        ));

        JButton dateOldest = new JButton("Date Oldest");
        dateOldest.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING))
        ));

        JButton userAsc = new JButton("User A→Z");
        userAsc.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(1, SortOrder.ASCENDING))
        ));

        JButton userDesc = new JButton("User Z→A");
        userDesc.addActionListener(e -> logSorter.setSortKeys(
                Collections.singletonList(new RowSorter.SortKey(1, SortOrder.DESCENDING))
        ));

        rightSorts.add(dateNewest);
        rightSorts.add(dateOldest);
        rightSorts.add(userAsc);
        rightSorts.add(userDesc);

        topControls.add(rightSorts, BorderLayout.EAST);
        p.add(topControls, BorderLayout.NORTH);

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

        p.add(new JScrollPane(logTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sim = new JButton("Simulate Update");
        sim.addActionListener(e -> simulateRandomUpdate());
        bottom.add(sim);

        JButton clear = new JButton("Clear Logs");
        clear.addActionListener(e -> logModel.setRowCount(0));
        bottom.add(clear);

        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private JButton smallIconButton(String text) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(2, 6, 2, 6));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.addActionListener(e -> logAction("Clicked control " + text));
        return b;
    }

    private void switchCenter(String name) {
        centerCards.show(centerPanel, name);
    }

    private void showLoginThenStart() {
        LoginDialog dlg = new LoginDialog(this);
        dlg.setVisible(true);
        if (dlg.isSucceeded()) {
            currentUser = dlg.getUsername();
            loggedInLabel.setText("Logged in as: " + currentUser);
            logAction("User logged in");
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    private void doLogout() {
        logAction("User logged out");
        dispose();
        EcoDashboard fresh = new EcoDashboard();
        fresh.showLoginThenStart();
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

    static class ClassroomData {
        String id;
        int electricity;
        ClassroomData(String id, int e) { this.id = id; this.electricity = e; }
    }

    static class LoginDialog extends JDialog {
        private JTextField userField;
        private boolean succeeded = false;

        LoginDialog(Frame parent) {
            super(parent, "Login", true);
            setLayout(new BorderLayout());

            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6,6,6,6);
            c.gridx = 0; c.gridy = 0;
            p.add(new JLabel("Username:"), c);

            c.gridx = 1;
            userField = new JTextField("Administrator", 18);
            p.add(userField, c);

            add(p, BorderLayout.CENTER);

            JPanel btns = new JPanel();
            JButton ok = new JButton("Login");
            ok.addActionListener(e -> {
                if (!userField.getText().trim().isEmpty()) {
                    succeeded = true;
                    dispose();
                }
            });

            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e -> {
                succeeded = false;
                dispose();
            });

            btns.add(ok);
            btns.add(cancel);
            add(btns, BorderLayout.SOUTH);

            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        String getUsername() { return userField.getText().trim(); }
        boolean isSucceeded() { return succeeded; }
    }

    class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();

            g.setColor(Color.WHITE);
            g.fillRect(0,0,w,h);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(40, 10, w-80, h-60);

            List<Integer> values = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for (int r = 0; r < Math.min(5, rankingModel.getRowCount()); r++) {
                int val = Integer.parseInt(rankingModel.getValueAt(r, 2).toString());
                values.add(val);
                names.add(rankingModel.getValueAt(r, 1).toString());
            }

            if (values.isEmpty()) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("No data to display", 60, h/2);
                return;
            }

            int max = Collections.max(values);
            int areaW = w - 160;
            int barW = Math.max(48, areaW / values.size());
            int baseY = h - 30;

            for (int i = 0; i < values.size(); i++) {
                int barH = (int)((double)values.get(i) / max * (h - 80));
                int x = 70 + i * barW;
                int y = baseY - barH;

                if (values.get(i) == max) g.setColor(new Color(88, 180, 93));
                else g.setColor(new Color(83, 154, 203));

                g.fillRect(x, y, barW - 20, barH);

                g.setColor(Color.DARK_GRAY);
                g.drawString(names.get(i), x, baseY + 14);
                g.drawString(values.get(i) + " kWh", x, y - 4);
            }
        }
    }
}
