package hotelreservationsystem1.models;

/**
 * Guest user class
 * Demonstrates Inheritance - extends User
 */
public class Guest extends User {
    private int guestId;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String address;

    // Constructors
    public Guest() {
        super();
    }

    public Guest(String username, String password, String email, String phone,
                 String firstName, String lastName, String idNumber, String address) {
        super(username, password, email, phone);
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.address = address;
    }

    // Implement abstract method from User
    @Override
    public String getRole() {
        return "GUEST";
    }

    // Implement abstract method from User
    @Override
    public void displayInfo() {
        System.out.println("Guest Information:");
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("ID Number: " + idNumber);
        System.out.println("Address: " + address);
    }

    // Get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId=" + guestId +
                ", name='" + getFullName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
