package gui;

import dao.BookDAO;
import models.Book;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BooksPanel extends JPanel {

    private final BookDAO bookDAO = new BookDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> statusFilter;

    private static final String[] COLUMNS = {"ID", "Title", "Author", "Category", "Status"};

    public BooksPanel() {
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
        searchField.setToolTipText("Search by ID, title, or author");
        topPanel.add(searchField);

        topPanel.add(Box.createHorizontalStrut(12));
        topPanel.add(new JLabel("Category:"));
        categoryFilter = new JComboBox<>(new String[]{"All"});
        categoryFilter.setPreferredSize(new Dimension(140, 26));
        topPanel.add(categoryFilter);

        topPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Available", "Borrowed"});
        statusFilter.setPreferredSize(new Dimension(110, 26));
        topPanel.add(statusFilter);

        JButton refreshBtn = new JButton("Refresh");
        topPanel.add(refreshBtn);

        // ── Table ─────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : String.class; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setGridColor(new Color(0xDDDDDD));
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(190);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Books  (click column headers to sort)"));

        // ── Bottom buttons ────────────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        JButton addBtn    = makeButton("Add Book",    new Color(0x27AE60));
        JButton editBtn   = makeButton("Edit Book",   new Color(0x2980B9));
        JButton deleteBtn = makeButton("Delete Book", new Color(0xE74C3C));

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
        categoryFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> addBook());
        editBtn.addActionListener(e -> editBook());
        deleteBtn.addActionListener(e -> deleteBook());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();

        List<String> categories = new ArrayList<>();
        categories.add("All");
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                    b.getBookId(), b.getTitle(), b.getAuthor(),
                    b.getCategory(), b.getAvailabilityStatus()});
            if (!categories.contains(b.getCategory()))
                categories.add(b.getCategory());
        }

        String selected = (String) categoryFilter.getSelectedItem();
        categoryFilter.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
        if (selected != null && categories.contains(selected))
            categoryFilter.setSelectedItem(selected);

        applyFilters();
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            try { filters.add(RowFilter.regexFilter("(?i)" + text, 0, 1, 2, 3, 4)); }
            catch (java.util.regex.PatternSyntaxException ignored) {}
        }

        String cat = (String) categoryFilter.getSelectedItem();
        if (cat != null && !cat.equals("All"))
            filters.add(RowFilter.regexFilter("(?i)^" +
                    java.util.regex.Pattern.quote(cat) + "$", 3));

        String status = (String) statusFilter.getSelectedItem();
        if (status != null && !status.equals("All"))
            filters.add(RowFilter.regexFilter("(?i)^" + status + "$", 4));

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void addBook() {
        BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (bookDAO.addBook(dialog.getBook())) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Book added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add book. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) table.getValueAt(row, 0);
        Book book  = bookDAO.getBookById(bookId);
        if (book == null) { loadData(); return; }

        BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), book);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Book updated = dialog.getBook();
            updated.setBookId(bookId);
            if (bookDAO.updateBook(updated)) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Book updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId     = (int)    table.getValueAt(row, 0);
        String title   = (String) table.getValueAt(row, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete:\n\n\"" + title + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(bookId)) {
                loadData();
                JOptionPane.showMessageDialog(this,
                        "Book deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete book.\nIt may have linked borrowing records.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 30));
        return btn;
    }
}
