package gui;

import dao.MemberDAO;
import models.Member;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MembersPanel extends JPanel {

    private final MemberDAO memberDAO = new MemberDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> typeFilter;

    private static final String[] COLUMNS = {"ID", "Name", "Email", "Membership Type"};

    public MembersPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // ── Top toolbar ───────────────────────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(22);
        searchField.setToolTipText("Search by ID, name, or email");
        topPanel.add(searchField);

        topPanel.add(Box.createHorizontalStrut(12));
        topPanel.add(new JLabel("Membership Type:"));
        typeFilter = new JComboBox<>(new String[]{"All", "Standard", "Premium", "Student", "Staff"});
        typeFilter.setPreferredSize(new Dimension(120, 26));
        topPanel.add(typeFilter);

        JButton refreshBtn = new JButton("Refresh");
        topPanel.add(refreshBtn);

        // ── Table ─────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return col == 0 ? Integer.class : String.class;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setGridColor(new Color(0xDDDDDD));
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Members  (click column headers to sort)"));

        // ── Bottom buttons ────────────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton addBtn    = makeButton("Add Member",    new Color(0x27AE60));
        JButton editBtn   = makeButton("Edit Member",   new Color(0x2980B9));
        JButton deleteBtn = makeButton("Delete Member", new Color(0xE74C3C));
        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);

        add(topPanel,    BorderLayout.NORTH);
        add(scrollPane,  BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────────────────
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { applyFilters(); }
            public void removeUpdate(DocumentEvent e)  { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        typeFilter.addActionListener(e -> applyFilters());
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> addMember());
        editBtn.addActionListener(e -> editMember());
        deleteBtn.addActionListener(e -> deleteMember());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (Member m : memberDAO.getAllMembers())
            tableModel.addRow(new Object[]{
                    m.getMemberId(), m.getName(), m.getEmail(), m.getMembershipType()});
        applyFilters();
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            try { filters.add(RowFilter.regexFilter("(?i)" + text, 0, 1, 2, 3)); }
            catch (java.util.regex.PatternSyntaxException ignored) {}
        }

        String type = (String) typeFilter.getSelectedItem();
        if (type != null && !type.equals("All"))
            filters.add(RowFilter.regexFilter("(?i)^" + type + "$", 3));

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void addMember() {
        MemberDialog dialog = new MemberDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (memberDAO.addMember(dialog.getMember())) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Member registered successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register member.\nThat email address may already be in use.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId   = (int) table.getValueAt(row, 0);
        Member member  = memberDAO.getMemberById(memberId);
        if (member == null) { loadData(); return; }

        MemberDialog dialog = new MemberDialog((JFrame) SwingUtilities.getWindowAncestor(this), member);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Member updated = dialog.getMember();
            updated.setMemberId(memberId);
            if (memberDAO.updateMember(updated)) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Member updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update member.\nThat email address may already be in use.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId = (int)    table.getValueAt(row, 0);
        String name  = (String) table.getValueAt(row, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete member:\n\n\"" + name + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(memberId)) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Member deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete member.\nThey may have active borrowing records.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 30));
        return btn;
    }
}
