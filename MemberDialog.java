package gui;

import models.Member;

import javax.swing.*;
import java.awt.*;

public class MemberDialog extends JDialog {

    private boolean confirmed = false;
    private Member result;
    private final Member existing;

    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> typeCombo;

    public MemberDialog(JFrame parent, Member existing) {
        super(parent, existing == null ? "Register New Member" : "Edit Member", true);
        this.existing = existing;
        setSize(430, 260);
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

        nameField  = new JTextField(25);
        emailField = new JTextField(25);
        emailField.setToolTipText("e.g. student@stmarys.ac.uk");
        typeCombo  = new JComboBox<>(new String[]{"Standard", "Premium", "Student", "Staff"});

        String[]     labels = {"Full Name *", "Email *", "Membership Type"};
        JComponent[] fields = {nameField, emailField, typeCombo};

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            form.add(new JLabel(labels[i]), gc);
            gc.gridx = 1; gc.weightx = 0.7;
            form.add(fields[i], gc);
        }

        gc.gridx = 0; gc.gridy = labels.length; gc.gridwidth = 2;
        JLabel note = new JLabel("  * Required fields  |  Email must be a valid address");
        note.setFont(note.getFont().deriveFont(Font.ITALIC, 11f));
        note.setForeground(Color.GRAY);
        form.add(note, gc);

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
        nameField.setText(existing.getName());
        emailField.setText(existing.getEmail());
        typeCombo.setSelectedItem(existing.getMembershipType());
    }

    private void save() {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String type  = (String) typeCombo.getSelectedItem();

        String error = Validator.validateMember(name, email);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Member(name, email, type);
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Member getMember()    { return result; }
}
