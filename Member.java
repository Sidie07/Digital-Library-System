package models;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String membershipType; // "Standard", "Premium", "Student", "Staff"

    public Member(int memberId, String name, String email, String membershipType) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.membershipType = membershipType;
    }

    public Member(String name, String email, String membershipType) {
        this(-1, name, email, membershipType);
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-30s | %-35s | %-12s |",
                memberId, name, email, membershipType);
    }

    public static String tableHeader() {
        return String.format("| %-5s | %-30s | %-35s | %-12s |",
                "ID", "Name", "Email", "Type") + "\n" +
                "-".repeat(95);
    }
}
