package hotelreservationsystem1.models;

/**
 * Admin user class
 * Demonstrates Inheritance - extends User
 * Demonstrates Polymorphism - overrides methods from User
 */
public class Admin extends User {

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String username, String password, String email, String phone) {
        super(username, password, email, phone);
    }

    // Implement abstract method from User
    @Override
    public String getRole() {
        return "ADMIN";
    }

    // Implement abstract method from User (Polymorphism)
    @Override
    public void displayInfo() {
        System.out.println("Administrator Information:");
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Role: " + getRole());
        System.out.println("Registered: " + getCreatedDate());
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
