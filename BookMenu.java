package ui;

import dao.BookDAO;
import models.Book;

import java.util.List;

public class BookMenu {
    private final BookDAO bookDAO = new BookDAO();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== MANAGE BOOKS ==========");
            System.out.println("  1. Add New Book");
            System.out.println("  2. View All Books");
            System.out.println("  3. Search Books");
            System.out.println("  4. Update Book");
            System.out.println("  5. Delete Book");
            System.out.println("  0. Back to Main Menu");
            System.out.println("==================================");

            int choice = InputHelper.readIntInRange("  Select option: ", 0, 5);
            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewAllBooks();
                case 3 -> searchBooks();
                case 4 -> updateBook();
                case 5 -> deleteBook();
                case 0 -> running = false;
            }
        }
    }

    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        String title    = InputHelper.readNonEmpty("  Title:    ");
        String author   = InputHelper.readNonEmpty("  Author:   ");
        String category = InputHelper.readNonEmpty("  Category: ");
        String status   = InputHelper.chooseFromOptions("  Availability:", new String[]{"Available", "Borrowed"});

        Book book = new Book(title, author, category, status);
        if (bookDAO.addBook(book)) {
            System.out.println("\n  Book added successfully! Assigned ID: " + book.getBookId());
        } else {
            System.out.println("\n  Failed to add book.");
        }
        InputHelper.pause();
    }

    private void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("  No books found in the library.");
        } else {
            System.out.println(Book.tableHeader());
            books.forEach(System.out::println);
            System.out.println("\n  Total: " + books.size() + " book(s).");
        }
        InputHelper.pause();
    }

    private void searchBooks() {
        System.out.println("\n--- Search Books ---");
        String keyword = InputHelper.readNonEmpty("  Search by title, author, or ID: ");
        List<Book> results = bookDAO.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("  No books found matching '" + keyword + "'.");
        } else {
            System.out.println(Book.tableHeader());
            results.forEach(System.out::println);
            System.out.println("\n  Found: " + results.size() + " result(s).");
        }
        InputHelper.pause();
    }

    private void updateBook() {
        System.out.println("\n--- Update Book ---");
        int id = InputHelper.readInt("  Enter Book ID to update: ");
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            System.out.println("  No book found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Current details:");
        System.out.println(Book.tableHeader());
        System.out.println(book);
        System.out.println("\n  (Press ENTER to keep current value)");

        String title = InputHelper.readLine("  New Title [" + book.getTitle() + "]: ");
        if (!title.isEmpty()) book.setTitle(title);

        String author = InputHelper.readLine("  New Author [" + book.getAuthor() + "]: ");
        if (!author.isEmpty()) book.setAuthor(author);

        String category = InputHelper.readLine("  New Category [" + book.getCategory() + "]: ");
        if (!category.isEmpty()) book.setCategory(category);

        String status = InputHelper.chooseFromOptions(
                "  New Status [" + book.getAvailabilityStatus() + "]:",
                new String[]{"Available", "Borrowed", "(Keep current)"});
        if (!status.equals("(Keep current)")) book.setAvailabilityStatus(status);

        if (bookDAO.updateBook(book)) {
            System.out.println("\n  Book updated successfully.");
        } else {
            System.out.println("\n  Failed to update book.");
        }
        InputHelper.pause();
    }

    private void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        int id = InputHelper.readInt("  Enter Book ID to delete: ");
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            System.out.println("  No book found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Book to delete: " + book.getTitle() + " by " + book.getAuthor());
        if (InputHelper.confirm("  Are you sure you want to delete this book?")) {
            if (bookDAO.deleteBook(id)) {
                System.out.println("  Book deleted successfully.");
            } else {
                System.out.println("  Failed to delete book.");
            }
        } else {
            System.out.println("  Deletion cancelled.");
        }
        InputHelper.pause();
    }
}
