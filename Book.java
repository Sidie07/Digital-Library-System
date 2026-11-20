package models;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private String availabilityStatus; // "Available", "Borrowed"

    public Book(int bookId, String title, String author, String category, String availabilityStatus) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = availabilityStatus;
    }

    public Book(String title, String author, String category, String availabilityStatus) {
        this(-1, title, author, category, availabilityStatus);
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-35s | %-25s | %-20s | %-12s |",
                bookId, title, author, category, availabilityStatus);
    }

    public static String tableHeader() {
        return String.format("| %-5s | %-35s | %-25s | %-20s | %-12s |",
                "ID", "Title", "Author", "Category", "Status") + "\n" +
                "-".repeat(113);
    }
}
