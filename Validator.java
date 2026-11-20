package gui;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DATE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public static String validateBook(String title, String author, String category) {
        if (title.trim().isEmpty())    return "Title cannot be empty.";
        if (author.trim().isEmpty())   return "Author cannot be empty.";
        if (category.trim().isEmpty()) return "Category cannot be empty.";
        return null;
    }

    public static String validateMember(String name, String email) {
        if (name.trim().isEmpty())  return "Name cannot be empty.";
        if (email.trim().isEmpty()) return "Email cannot be empty.";
        if (!EMAIL.matcher(email.trim()).matches())
            return "Email format is invalid (e.g. user@example.com).";
        return null;
    }

    public static String validateDates(String borrowDate, String dueDate) {
        if (!DATE.matcher(borrowDate.trim()).matches())
            return "Borrow date must be in YYYY-MM-DD format (e.g. 2025-06-01).";
        if (!DATE.matcher(dueDate.trim()).matches())
            return "Due date must be in YYYY-MM-DD format (e.g. 2025-06-15).";
        if (dueDate.compareTo(borrowDate) < 0)
            return "Due date cannot be earlier than the borrow date.";
        return null;
    }
}
