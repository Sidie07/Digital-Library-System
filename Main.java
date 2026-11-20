import database.DatabaseManager;
import ui.BookMenu;
import ui.BorrowingMenu;
import ui.InputHelper;
import ui.MemberMenu;
import ui.SearchMenu;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.getInstance();

        printWelcomeBanner();

        BookMenu bookMenu         = new BookMenu();
        MemberMenu memberMenu     = new MemberMenu();
        BorrowingMenu borrowMenu  = new BorrowingMenu();
        SearchMenu searchMenu     = new SearchMenu();

        boolean running = true;
        while (running) {
            System.out.println("\n+==========================================+");
            System.out.println("|   ST MARY'S UNIVERSITY LIBRARY SYSTEM   |");
            System.out.println("+==========================================+");
            System.out.println("|  1. Manage Books                        |");
            System.out.println("|  2. Manage Members                      |");
            System.out.println("|  3. Manage Borrowing Records            |");
            System.out.println("|  4. Search Records                      |");
            System.out.println("|  0. Exit System                         |");
            System.out.println("+==========================================+");

            int choice = InputHelper.readIntInRange("  Select option: ", 0, 4);
            switch (choice) {
                case 1 -> bookMenu.show();
                case 2 -> memberMenu.show();
                case 3 -> borrowMenu.show();
                case 4 -> searchMenu.show();
                case 0 -> running = false;
            }
        }

        DatabaseManager.getInstance().close();
        System.out.println("\nThank you for using St Mary's Library System. Goodbye!");
    }

    private static void printWelcomeBanner() {
        System.out.println("+==========================================+");
        System.out.println("|   ST MARY'S UNIVERSITY LIBRARY SYSTEM   |");
        System.out.println("|        Library Management System        |");
        System.out.println("+==========================================+");
        System.out.println();
    }
}
