package ui;

import dao.BookDAO;
import dao.BorrowingDAO;
import dao.MemberDAO;
import models.Book;
import models.BorrowingRecord;
import models.Member;

import java.util.List;

public class BorrowingMenu {
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final BookDAO bookDAO           = new BookDAO();
    private final MemberDAO memberDAO       = new MemberDAO();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== MANAGE BORROWING RECORDS ==========");
            System.out.println("  1. Record New Borrowing");
            System.out.println("  2. View All Borrowing Records");
            System.out.println("  3. Search Records by Member");
            System.out.println("  4. Search Records by Book");
            System.out.println("  5. Edit Borrowing Record");
            System.out.println("  6. Delete Borrowing Record");
            System.out.println("  0. Back to Main Menu");
            System.out.println("===============================================");

            int choice = InputHelper.readIntInRange("  Select option: ", 0, 6);
            switch (choice) {
                case 1 -> addRecord();
                case 2 -> viewAllRecords();
                case 3 -> searchByMember();
                case 4 -> searchByBook();
                case 5 -> editRecord();
                case 6 -> deleteRecord();
                case 0 -> running = false;
            }
        }
    }

    private void addRecord() {
        System.out.println("\n--- Record New Borrowing ---");

        int bookId = InputHelper.readInt("  Book ID: ");
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("  No book found with ID " + bookId + ".");
            InputHelper.pause();
            return;
        }
        if (!book.getAvailabilityStatus().equalsIgnoreCase("Available")) {
            System.out.println("  '" + book.getTitle() + "' is currently not available.");
            InputHelper.pause();
            return;
        }

        int memberId = InputHelper.readInt("  Member ID: ");
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            System.out.println("  No member found with ID " + memberId + ".");
            InputHelper.pause();
            return;
        }

        String borrowDate = InputHelper.readDate("  Borrow Date");
        String dueDate    = InputHelper.readDate("  Due Date  ");

        BorrowingRecord record = new BorrowingRecord(bookId, memberId, borrowDate, dueDate, "Borrowed");
        if (borrowingDAO.addRecord(record)) {
            bookDAO.updateBookStatus(bookId, "Borrowed");
            System.out.println("\n  Borrowing recorded successfully! Record ID: " + record.getRecordId());
            System.out.println("  '" + book.getTitle() + "' borrowed by " + member.getName() + ".");
        } else {
            System.out.println("\n  Failed to record borrowing.");
        }
        InputHelper.pause();
    }

    private void viewAllRecords() {
        System.out.println("\n--- All Borrowing Records ---");
        List<BorrowingRecord> records = borrowingDAO.getAllRecords();
        if (records.isEmpty()) {
            System.out.println("  No borrowing records found.");
        } else {
            System.out.println(BorrowingRecord.tableHeader());
            records.forEach(System.out::println);
            System.out.println("\n  Total: " + records.size() + " record(s).");
        }
        InputHelper.pause();
    }

    private void searchByMember() {
        System.out.println("\n--- Search Records by Member ---");
        String keyword = InputHelper.readNonEmpty("  Enter member name or ID: ");
        List<BorrowingRecord> results = borrowingDAO.searchByMember(keyword);
        printResults(results, keyword);
    }

    private void searchByBook() {
        System.out.println("\n--- Search Records by Book ---");
        String keyword = InputHelper.readNonEmpty("  Enter book title or ID: ");
        List<BorrowingRecord> results = borrowingDAO.searchByBook(keyword);
        printResults(results, keyword);
    }

    private void printResults(List<BorrowingRecord> results, String keyword) {
        if (results.isEmpty()) {
            System.out.println("  No records found for '" + keyword + "'.");
        } else {
            System.out.println(BorrowingRecord.tableHeader());
            results.forEach(System.out::println);
            System.out.println("\n  Found: " + results.size() + " record(s).");
        }
        InputHelper.pause();
    }

    private void editRecord() {
        System.out.println("\n--- Edit Borrowing Record ---");
        int id = InputHelper.readInt("  Enter Record ID to edit: ");
        BorrowingRecord record = borrowingDAO.getRecordById(id);
        if (record == null) {
            System.out.println("  No record found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Current: " + record);
        System.out.println("\n  (Press ENTER to keep current value)");

        String borrowDate = InputHelper.readLine("  New Borrow Date [" + record.getBorrowDate() + "] (YYYY-MM-DD): ");
        if (!borrowDate.isEmpty()) record.setBorrowDate(borrowDate);

        String dueDate = InputHelper.readLine("  New Due Date [" + record.getDueDate() + "] (YYYY-MM-DD): ");
        if (!dueDate.isEmpty()) record.setDueDate(dueDate);

        String oldStatus = record.getStatus();
        String newStatus = InputHelper.chooseFromOptions("  New Status [" + oldStatus + "]:",
                new String[]{"Borrowed", "Returned", "Overdue", "(Keep current)"});
        if (!newStatus.equals("(Keep current)")) record.setStatus(newStatus);

        if (borrowingDAO.updateRecord(record)) {
            if (!record.getStatus().equals(oldStatus)) {
                String bookAvailability = record.getStatus().equals("Returned") ? "Available" : "Borrowed";
                bookDAO.updateBookStatus(record.getBookId(), bookAvailability);
                System.out.println("  Book is now marked as " + bookAvailability + ".");
            }
            System.out.println("\n  Record updated successfully.");
        } else {
            System.out.println("\n  Failed to update record.");
        }
        InputHelper.pause();
    }

    private void deleteRecord() {
        System.out.println("\n--- Delete Borrowing Record ---");
        int id = InputHelper.readInt("  Enter Record ID to delete: ");
        BorrowingRecord record = borrowingDAO.getRecordById(id);
        if (record == null) {
            System.out.println("  No record found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Record: Book='" + record.getBookTitle()
                + "', Member='" + record.getMemberName() + "', Status='" + record.getStatus() + "'");

        if (InputHelper.confirm("  Are you sure you want to delete this record?")) {
            if (borrowingDAO.deleteRecord(id)) {
                System.out.println("  Record deleted successfully.");
            } else {
                System.out.println("  Failed to delete record.");
            }
        } else {
            System.out.println("  Deletion cancelled.");
        }
        InputHelper.pause();
    }
}
