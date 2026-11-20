package gui;

import database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("St Mary's University Library System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "Are you sure you want to exit the Library System?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    DatabaseManager.getInstance().close();
                    dispose();
                    System.exit(0);
                }
            }
        });

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── Header ───────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x2C3E50));
        header.setPreferredSize(new Dimension(0, 62));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel titleLabel = new JLabel("St Mary's University  —  Library Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel versionLabel = new JLabel("CPS4005  |  Assessment 2");
        versionLabel.setForeground(new Color(0xABB2B9));
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(versionLabel, BorderLayout.EAST);

        // ── Tabbed pane ───────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.addTab("  Books  ",           new BooksPanel());
        tabs.addTab("  Members  ",         new MembersPanel());
        tabs.addTab("  Borrowing Records  ", new BorrowingPanel());

        // ── Status bar ────────────────────────────────────────────────────────
        JLabel statusBar = new JLabel("   Connected  |  library.db  |  St Mary's University Library System");
        statusBar.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        statusBar.setPreferredSize(new Dimension(0, 24));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
}
