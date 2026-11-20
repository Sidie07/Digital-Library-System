package gui;

import models.Book;

import javax.swing.*;
import java.awt.*;

public class BookDialog extends JDialog {

    private boolean confirmed = false;
    private Book result;
    private final Book existing;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField categoryField;
    private JComboBox<String> statusCombo;

    public BookDialog(JFrame parent, Book existing) {
        super(parent, existing == null ? "Add New Book" : "Edit Book", true);
        this.existing = existing;
        setSize(430, 290);
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

        titleField    = new JTextField(25);
        authorField   = new JTextField(25);
        categoryField = new JTextField(25);
        statusCombo   = new JComboBox<>(new String[]{"Available", "Borrowed"});

        String[]     labels = {"Title *", "Author *", "Category *", "Status"};
        JComponent[] fields = {titleField, authorField, categoryField, statusCombo};

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            form.add(new JLabel(labels[i]), gc);
            gc.gridx = 1; gc.weightx = 0.7;
            form.add(fields[i], gc);
        }

        // Required field note
        gc.gridx = 0; gc.gridy = labels.length; gc.gridwidth = 2;
        JLabel note = new JLabel("  * Required fields");
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
        titleField.setText(existing.getTitle());
        authorField.setText(existing.getAuthor());
        categoryField.setText(existing.getCategory());
        statusCombo.setSelectedItem(existing.getAvailabilityStatus());
    }

    private void save() {
        String title    = titleField.getText().trim();
        String author   = authorField.getText().trim();
        String category = categoryField.getText().trim();
        String status   = (String) statusCombo.getSelectedItem();

        String error = Validator.validateBook(title, author, category);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Book(title, author, category, status);
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Book getBook()        { return result; }
}
