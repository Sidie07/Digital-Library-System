package gui;

import dao.BookDAO;
import dao.BorrowingDAO;
import models.BorrowingRecord;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingPanel extends JPanel {

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final BookDAO bookDAO           = new BookDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> statusFilter;

    private static final String[] COLUMNS =
            {"Record ID", "Book Title", "Member", "Borrow Date", "Due Date", "Status"};

    public BorrowingPanel() {
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
        searchField.setToolTipText("Search by book title or member name");
        topPanel.add(searchField);

        topPanel.add(Box.createHorizontalStrut(12));
        topPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Borrowed", "Returned", "Overdue"});
        statusFilter.setPreferredSize(new Dimension(110, 26));
        topPanel.add(statusFilter);

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

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Borrowing Records  (click column headers to sort)"));

        // ── Bottom buttons ────────────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton addBtn    = makeButton("New Borrow",    new Color(0x27AE60));
        JButton editBtn   = makeButton("Edit Record",   new Color(0x2980B9));
        JButton deleteBtn = makeButton("Delete Record", new Color(0xE74C3C));
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
        statusFilter.addActionListener(e -> applyFilters());
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> addRecord());
        editBtn.addActionListener(e -> editRecord());
        deleteBtn.addActionListener(e -> deleteRecord());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (BorrowingRecord r : borrowingDAO.getAllRecords()) {
            tableModel.addRow(new Object[]{
                    r.getRecordId(),
                    r.getBookTitle()  != null ? r.getBookTitle()  : "Book #"   + r.getBookId(),
                    r.getMemberName() != null ? r.getMemberName() : "Member #" + r.getMemberId(),
                    r.getBorrowDate(),
                    r.getDueDate(),
                    r.getStatus()
            });
        }
        applyFilters();
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            try { filters.add(RowFilter.regexFilter("(?i)" + text, 0, 1, 2, 3, 4, 5)); }
            catch (java.util.regex.PatternSyntaxException ignored) {}
        }

        String status = (String) statusFilter.getSelectedItem();
        if (status != null && !status.equals("All"))
            filters.add(RowFilter.regexFilter("(?i)^" + status + "$", 5));

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void addRecord() {
        BorrowDialog dialog = new BorrowDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            BorrowingRecord record = dialog.getRecord();
            if (borrowingDAO.addRecord(record)) {
                bookDAO.updateBookStatus(record.getBookId(), "Borrowed");
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Borrow record created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create borrow record.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a record to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int recordId         = (int) table.getValueAt(row, 0);
        BorrowingRecord record = borrowingDAO.getRecordById(recordId);
        if (record == null) { loadData(); return; }

        String oldStatus = record.getStatus();

        BorrowDialog dialog = new BorrowDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), record);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            BorrowingRecord updated = dialog.getRecord();
            if (borrowingDAO.updateRecord(updated)) {
                if (!updated.getStatus().equals(oldStatus)) {
                    String bookStatus = updated.getStatus().equals("Returned") ? "Available" : "Borrowed";
                    bookDAO.updateBookStatus(updated.getBookId(), bookStatus);
                }
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Borrow record updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update borrow record.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a record to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int recordId      = (int)    table.getValueAt(row, 0);
        String bookTitle  = (String) table.getValueAt(row, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete this borrowing record?\n\nBook: \""
                        + bookTitle + "\"",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (borrowingDAO.deleteRecord(recordId)) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Record deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete record.", "Error",
                        JOptionPane.ERROR_MESSAGE);
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
