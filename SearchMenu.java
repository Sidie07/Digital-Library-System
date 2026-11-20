package ui;

import dao.BookDAO;
import dao.BorrowingDAO;
import dao.MemberDAO;
import models.Book;
import models.BorrowingRecord;
import models.Member;

import java.util.List;

public class SearchMenu {
    private final BookDAO bookDAO           = new BookDAO();
    private final MemberDAO memberDAO       = new MemberDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== SEARCH RECORDS ==========");
            System.out.println("  1. Search Books");
            System.out.println("  2. Search Members");
            System.out.println("  3. Search Borrowing Records by Member");
            System.out.println("  4. Search Borrowing Records by Book");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=====================================");

            int choice = InputHelper.readIntInRange("  Select option: ", 0, 4);
            switch (choice) {
                case 1 -> searchBooks();
                case 2 -> searchMembers();
                case 3 -> searchBorrowingByMember();
                case 4 -> searchBorrowingByBook();
                case 0 -> running = false;
            }
        }
    }

    private void searchBooks() {
        String keyword = InputHelper.readNonEmpty("\n  Search books by title, author, or ID: ");
        List<Book> results = bookDAO.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("  No books found for '" + keyword + "'.");
        } else {
            System.out.println(Book.tableHeader());
            results.forEach(System.out::println);
            System.out.printf("%n  Found %d result(s).%n", results.size());
        }
        InputHelper.pause();
    }

    private void searchMembers() {
        String keyword = InputHelper.readNonEmpty("\n  Search members by name or ID: ");
        List<Member> results = memberDAO.searchMembers(keyword);
        if (results.isEmpty()) {
            System.out.println("  No members found for '" + keyword + "'.");
        } else {
            System.out.println(Member.tableHeader());
            results.forEach(System.out::println);
            System.out.printf("%n  Found %d result(s).%n", results.size());
        }
        InputHelper.pause();
    }

    private void searchBorrowingByMember() {
        String keyword = InputHelper.readNonEmpty("\n  Enter member name or ID: ");
        List<BorrowingRecord> results = borrowingDAO.searchByMember(keyword);
        if (results.isEmpty()) {
            System.out.println("  No borrowing records found for member '" + keyword + "'.");
        } else {
            System.out.println(BorrowingRecord.tableHeader());
            results.forEach(System.out::println);
            System.out.printf("%n  Found %d result(s).%n", results.size());
        }
        InputHelper.pause();
    }

    private void searchBorrowingByBook() {
        String keyword = InputHelper.readNonEmpty("\n  Enter book title or ID: ");
        List<BorrowingRecord> results = borrowingDAO.searchByBook(keyword);
        if (results.isEmpty()) {
            System.out.println("  No borrowing records found for book '" + keyword + "'.");
        } else {
            System.out.println(BorrowingRecord.tableHeader());
            results.forEach(System.out::println);
            System.out.printf("%n  Found %d result(s).%n", results.size());
        }
        InputHelper.pause();
    }
}
