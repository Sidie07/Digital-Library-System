package ui;

import dao.MemberDAO;
import models.Member;

import java.util.List;

public class MemberMenu {
    private final MemberDAO memberDAO = new MemberDAO();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== MANAGE MEMBERS ==========");
            System.out.println("  1. Register New Member");
            System.out.println("  2. View All Members");
            System.out.println("  3. Search Members");
            System.out.println("  4. Update Member");
            System.out.println("  5. Delete Member");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=====================================");

            int choice = InputHelper.readIntInRange("  Select option: ", 0, 5);
            switch (choice) {
                case 1 -> addMember();
                case 2 -> viewAllMembers();
                case 3 -> searchMembers();
                case 4 -> updateMember();
                case 5 -> deleteMember();
                case 0 -> running = false;
            }
        }
    }

    private void addMember() {
        System.out.println("\n--- Register New Member ---");
        String name  = InputHelper.readNonEmpty("  Full Name: ");
        String email = InputHelper.readNonEmpty("  Email:     ");
        String type  = InputHelper.chooseFromOptions("  Membership Type:",
                new String[]{"Standard", "Premium", "Student", "Staff"});

        Member member = new Member(name, email, type);
        if (memberDAO.addMember(member)) {
            System.out.println("\n  Member registered successfully! Assigned ID: " + member.getMemberId());
        } else {
            System.out.println("\n  Failed to register member.");
        }
        InputHelper.pause();
    }

    private void viewAllMembers() {
        System.out.println("\n--- All Members ---");
        List<Member> members = memberDAO.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("  No members registered.");
        } else {
            System.out.println(Member.tableHeader());
            members.forEach(System.out::println);
            System.out.println("\n  Total: " + members.size() + " member(s).");
        }
        InputHelper.pause();
    }

    private void searchMembers() {
        System.out.println("\n--- Search Members ---");
        String keyword = InputHelper.readNonEmpty("  Search by name or ID: ");
        List<Member> results = memberDAO.searchMembers(keyword);
        if (results.isEmpty()) {
            System.out.println("  No members found matching '" + keyword + "'.");
        } else {
            System.out.println(Member.tableHeader());
            results.forEach(System.out::println);
            System.out.println("\n  Found: " + results.size() + " result(s).");
        }
        InputHelper.pause();
    }

    private void updateMember() {
        System.out.println("\n--- Update Member ---");
        int id = InputHelper.readInt("  Enter Member ID to update: ");
        Member member = memberDAO.getMemberById(id);
        if (member == null) {
            System.out.println("  No member found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Current details:");
        System.out.println(Member.tableHeader());
        System.out.println(member);
        System.out.println("\n  (Press ENTER to keep current value)");

        String name = InputHelper.readLine("  New Name [" + member.getName() + "]: ");
        if (!name.isEmpty()) member.setName(name);

        String email = InputHelper.readLine("  New Email [" + member.getEmail() + "]: ");
        if (!email.isEmpty()) member.setEmail(email);

        String type = InputHelper.chooseFromOptions(
                "  New Membership Type [" + member.getMembershipType() + "]:",
                new String[]{"Standard", "Premium", "Student", "Staff", "(Keep current)"});
        if (!type.equals("(Keep current)")) member.setMembershipType(type);

        if (memberDAO.updateMember(member)) {
            System.out.println("\n  Member updated successfully.");
        } else {
            System.out.println("\n  Failed to update member.");
        }
        InputHelper.pause();
    }

    private void deleteMember() {
        System.out.println("\n--- Delete Member ---");
        int id = InputHelper.readInt("  Enter Member ID to delete: ");
        Member member = memberDAO.getMemberById(id);
        if (member == null) {
            System.out.println("  No member found with ID " + id + ".");
            InputHelper.pause();
            return;
        }

        System.out.println("  Member to delete: " + member.getName() + " (" + member.getEmail() + ")");
        if (InputHelper.confirm("  Are you sure you want to delete this member?")) {
            if (memberDAO.deleteMember(id)) {
                System.out.println("  Member deleted successfully.");
            } else {
                System.out.println("  Failed to delete member. They may have active borrowing records.");
            }
        } else {
            System.out.println("  Deletion cancelled.");
        }
        InputHelper.pause();
    }
}
