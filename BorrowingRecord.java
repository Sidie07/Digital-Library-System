package models;

public class BorrowingRecord {
    private int recordId;
    private int bookId;
    private int memberId;
    private String borrowDate;
    private String dueDate;
    private String status; // "Borrowed", "Returned", "Overdue"

    // For display purposes
    private String bookTitle;
    private String memberName;

    public BorrowingRecord(int recordId, int bookId, int memberId,
                           String borrowDate, String dueDate, String status) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public BorrowingRecord(int bookId, int memberId,
                           String borrowDate, String dueDate, String status) {
        this(-1, bookId, memberId, borrowDate, dueDate, status);
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public String toString() {
        String book = (bookTitle != null) ? bookTitle : String.valueOf(bookId);
        String member = (memberName != null) ? memberName : String.valueOf(memberId);
        return String.format("| %-5d | %-25s | %-20s | %-12s | %-12s | %-10s |",
                recordId, book, member, borrowDate, dueDate, status);
    }

    public static String tableHeader() {
        return String.format("| %-5s | %-25s | %-20s | %-12s | %-12s | %-10s |",
                "ID", "Book", "Member", "Borrow Date", "Due Date", "Status") + "\n" +
                "-".repeat(101);
    }
}
