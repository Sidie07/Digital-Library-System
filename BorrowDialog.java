package gui;

import dao.BookDAO;
import dao.MemberDAO;
import models.Book;
import models.BorrowingRecord;
import models.Member;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BorrowDialog extends JDialog {

    private boolean confirmed = false;
    private BorrowingRecord result;
    private final BorrowingRecord existing;

    private final BookDAO bookDAO     = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();

    // Add-mode controls
    private JComboBox<String> bookCombo;
    private JComboBox<String> memberCombo;
    private List<Book> availableBooks;
    private List<Member> allMembers;

    // Edit-mode controls
    private JComboBox<String> statusCombo;

    // Shared controls
    private JTextField borrowDateField;
    private JTextField dueDateField;

    public BorrowDialog(JFrame parent, BorrowingRecord existing) {
        super(parent, existing == null ? "New Borrowing Record" : "Edit Borrowing Record", true);
        this.existing = existing;
        setSize(480, existing == null ? 330 : 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        if (existing != null) populateFields();
    }

    private void initComponents() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 5, 7, 5);

        borrowDateField = new JTextField(15);
        borrowDateField.setToolTipText("YYYY-MM-DD  e.g. 2025-06-01");
        dueDateField = new JTextField(15);
        dueDateField.setToolTipText("YYYY-MM-DD  e.g. 2025-06-15");

        int row = 0;

        if (existing == null) {
            // ── Add mode: dropdown selectors ──────────────────────────────────
            availableBooks = bookDAO.getAllBooks().stream()
                    .filter(b -> b.getAvailabilityStatus().equalsIgnoreCase("Available"))
                    .toList();
            allMembers = memberDAO.getAllMembers();

            bookCombo = new JComboBox<>();
            if (availableBooks.isEmpty()) {
                bookCombo.addItem("-- No available books --");
            } else {
                for (Book b : availableBooks)
                    bookCombo.addItem(b.getBookId() + "  |  " + b.getTitle());
            }

            memberCombo = new JComboBox<>();
            if (allMembers.isEmpty()) {
                memberCombo.addItem("-- No members registered --");
            } else {
                for (Member m : allMembers)
                    memberCombo.addItem(m.getMemberId() + "  |  " + m.getName());
            }

            addRow(form, gc, row++, "Book *",   bookCombo);
            addRow(form, gc, row++, "Member *", memberCombo);

        } else {
            // ── Edit mode: fixed labels ───────────────────────────────────────
            JLabel bookLabel = bold(existing.getBookTitle() != null
                    ? existing.getBookTitle() : "Book #" + existing.getBookId());
            JLabel memberLabel = bold(existing.getMemberName() != null
                    ? existing.getMemberName() : "Member #" + existing.getMemberId());

            addRow(form, gc, row++, "Book",   bookLabel);
            addRow(form, gc, row++, "Member", memberLabel);

            statusCombo = new JComboBox<>(new String[]{"Borrowed", "Returned", "Overdue"});
            addRow(form, gc, row++, "Status", statusCombo);
        }

        addRow(form, gc, row++, "Borrow Date *", borrowDateField);
        addRow(form, gc, row++, "Due Date *",    dueDateField);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        JLabel note = new JLabel("  * Dates must be in YYYY-MM-DD format");
        note.setFont(note.getFont().deriveFont(Font.ITALIC, 11f));
        note.setForeground(Color.GRAY);
        form.add(note, gc);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton saveBtn   = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setPreferredSize(new Dimension(90, 30));
        cancelBtn.setPreferredSize(new Dimension(90, 30));
        saveBtn.setBackground(new Color(0x2980B9));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        setLayout(new BorderLayout());
        add(form,    BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(saveBtn);
    }

    private void populateFields() {
        borrowDateField.setText(existing.getBorrowDate());
        dueDateField.setText(existing.getDueDate());
        if (statusCombo != null) statusCombo.setSelectedItem(existing.getStatus());
    }

    private void save() {
        String borrowDate = borrowDateField.getText().trim();
        String dueDate    = dueDateField.getText().trim();

        String dateError = Validator.validateDates(borrowDate, dueDate);
        if (dateError != null) {
            JOptionPane.showMessageDialog(this, dateError, "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existing == null) {
            // Add mode
            if (availableBooks.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "There are no available books to borrow.\nReturn a book first.",
                        "Cannot Save", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (allMembers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "There are no registered members.\nAdd a member first.",
                        "Cannot Save", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookId   = availableBooks.get(bookCombo.getSelectedIndex()).getBookId();
            int memberId = allMembers.get(memberCombo.getSelectedIndex()).getMemberId();
            result = new BorrowingRecord(bookId, memberId, borrowDate, dueDate, "Borrowed");

        } else {
            // Edit mode
            String status = statusCombo != null
                    ? (String) statusCombo.getSelectedItem()
                    : existing.getStatus();
            result = new BorrowingRecord(
                    existing.getRecordId(), existing.getBookId(), existing.getMemberId(),
                    borrowDate, dueDate, status);
            result.setBookTitle(existing.getBookTitle());
            result.setMemberName(existing.getMemberName());
        }

        confirmed = true;
        dispose();
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row,
                        String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.3; gc.gridwidth = 1;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        panel.add(field, gc);
    }

    private JLabel bold(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        return lbl;
    }

    public boolean isConfirmed()       { return confirmed; }
    public BorrowingRecord getRecord() { return result; }
}
