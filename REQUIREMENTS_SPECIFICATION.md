# Hotel Reservation System - Requirements Specification

## Project Information
- **Course:** CS313 Advanced Programming Language
- **Term:** 1, 2025-2026
- **Project:** Hotel Reservation System (Project B)
- **Database:** Oracle Database
- **UI Framework:** JavaFX with FXML
- **Database Credentials:**
  - Username: system
  - Password: 1234
  - Connection: localhost:LAPTOP-A3N5KC9H:1522/XE

---

## 1. Project Overview

A Hotel Reservation System with role-based access control for three user types: Guests, Receptionists, and Administrators. The system manages hotel rooms, reservations, user accounts, and generates reports using JavaFX UI connected to Oracle database.

---

## 2. System Users and Roles

### 2.1 Guest
Regular hotel customers who can browse and book rooms.

### 2.2 Receptionist
Hotel staff who manage reservations and guest check-ins/check-outs.

### 2.3 Administrator
System administrators who oversee all operations and manage system data.

---

## 3. Functional Requirements

### 3.1 Guest Features
1. **User Registration**
   - Register new guest account with personal information
   - Validate unique username and email

2. **Authentication**
   - Login with username and password
   - Logout

3. **Room Browsing**
   - View all available rooms
   - Filter rooms by type (Single, Double, Suite, Deluxe)
   - Filter rooms by price range
   - Sort rooms by price or room number
   - View room details (type, capacity, price, amenities)

4. **Reservation Management**
   - Create new reservation with check-in/check-out dates
   - View validation for date conflicts
   - View all personal reservations (upcoming and past)
   - View reservation details (room, dates, price, status)
   - Cancel pending or confirmed reservations
   - View reservation history

### 3.2 Receptionist Features
1. **Authentication**
   - Login with receptionist credentials
   - Logout

2. **Reservation Management**
   - View all reservations with filters (pending, confirmed, checked-in, completed, cancelled)
   - Sort reservations by date or guest name
   - Confirm pending reservations
   - View reservation details
   - Search reservations by guest name or reservation ID

3. **Check-In/Check-Out**
   - Check-in confirmed reservations
   - Check-out active guests
   - Update room status automatically

4. **Room Availability**
   - View current room availability
   - Update room status (Available, Occupied, Maintenance, Cleaning)
   - View room occupancy statistics

5. **Guest Search**
   - Search guests by name
   - Search guests by ID number
   - View guest reservation history

### 3.3 Administrator Features
1. **Authentication**
   - Login with admin credentials
   - Logout

2. **User Management**
   - View all users (guests, receptionists, admins)
   - Add new users (receptionist, admin accounts)
   - Delete user accounts
   - View user details and activity

3. **Room Management**
   - View all rooms
   - Add new rooms
   - Edit room information
   - Delete rooms
   - Manage room types (add, edit, delete)

4. **Reservation Oversight**
   - View all reservations across all statuses
   - View reservation statistics (total, pending, confirmed, etc.)
   - Override or cancel any reservation

5. **Service Management**
   - Manage hotel services (add, edit, delete)
   - Set service prices

6. **Revenue Reports**
   - Generate revenue reports by date range
   - View revenue by room type
   - View total bookings statistics
   - Export report data

7. **System Monitoring**
   - View system activity logs
   - View occupancy rates
   - View popular room types

---

## 4. User Interface Screens

### 4.1 Common Screens
1. **Login.fxml** - Login interface (GridPane)
   - Username field
   - Password field
   - Login button
   - Register link (for guests)
   - Role-based redirection

2. **GuestRegistration.fxml** - Guest signup (GridPane)
   - Personal information form
   - Username/password creation
   - Submit and cancel buttons

### 4.2 Guest Screens
3. **GuestDashboard.fxml** - Main menu (BorderPane with VBox)
   - Welcome message
   - Navigation buttons (Browse Rooms, My Reservations, Logout)

4. **BrowseRooms.fxml** - Room catalog (BorderPane)
   - Filter panel (VBox) - by type and price
   - Room list (TableView)
   - Room details display
   - "Make Reservation" button

5. **MakeReservation.fxml** - Booking form (GridPane)
   - Room information display
   - Check-in date picker
   - Check-out date picker
   - Total price calculation
   - Confirm and cancel buttons

6. **ViewMyReservations.fxml** - Guest bookings (BorderPane)
   - Reservation list (TableView)
   - Filter by status
   - View details button
   - Cancel reservation button

### 4.3 Receptionist Screens
7. **ReceptionistDashboard.fxml** - Main menu (BorderPane)
   - Quick statistics display
   - Navigation menu (VBox)

8. **ManageReservations.fxml** - Reservation management (BorderPane)
   - Reservation list (TableView)
   - Status filters
   - Confirm/cancel buttons
   - Search functionality

9. **CheckInOut.fxml** - Check-in/out interface (GridPane)
   - Search reservation by ID
   - Guest information display
   - Check-in button
   - Check-out button
   - Payment processing

### 4.4 Administrator Screens
10. **AdminDashboard.fxml** - Main menu (BorderPane)
    - System statistics overview
    - Navigation menu (VBox)

11. **ManageUsers.fxml** - User administration (BorderPane)
    - User list (TableView)
    - Add user button
    - Delete user button
    - User details panel

12. **ManageRooms.fxml** - Room administration (BorderPane)
    - Room list (TableView)
    - Add/edit/delete buttons
    - Room type management section

13. **RevenueReports.fxml** - Reports interface (BorderPane)
    - Date range selection
    - Report type selection
    - Statistics display area
    - Chart visualization (simple bar chart)

---

## 5. Database Schema (Oracle)

### 5.1 Tables

#### USERS
```sql
- user_id (NUMBER, PRIMARY KEY)
- username (VARCHAR2(50), UNIQUE, NOT NULL)
- password (VARCHAR2(100), NOT NULL)
- email (VARCHAR2(100), UNIQUE, NOT NULL)
- phone (VARCHAR2(20))
- role (VARCHAR2(20), NOT NULL) -- 'GUEST', 'RECEPTIONIST', 'ADMIN'
- created_date (DATE, DEFAULT SYSDATE)
```

#### GUESTS
```sql
- guest_id (NUMBER, PRIMARY KEY)
- user_id (NUMBER, FOREIGN KEY → USERS)
- first_name (VARCHAR2(50), NOT NULL)
- last_name (VARCHAR2(50), NOT NULL)
- id_number (VARCHAR2(20), UNIQUE)
- address (VARCHAR2(200))
```

#### ROOM_TYPES
```sql
- type_id (NUMBER, PRIMARY KEY)
- type_name (VARCHAR2(50), UNIQUE, NOT NULL) -- 'Single', 'Double', 'Suite', 'Deluxe'
- description (VARCHAR2(500))
- base_price (NUMBER(10,2), NOT NULL)
- capacity (NUMBER, NOT NULL)
- amenities (VARCHAR2(500))
```

#### ROOMS
```sql
- room_id (NUMBER, PRIMARY KEY)
- room_number (VARCHAR2(10), UNIQUE, NOT NULL)
- type_id (NUMBER, FOREIGN KEY → ROOM_TYPES)
- floor (NUMBER)
- status (VARCHAR2(20), DEFAULT 'AVAILABLE') -- 'AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'CLEANING'
```

#### RESERVATIONS
```sql
- reservation_id (NUMBER, PRIMARY KEY)
- guest_id (NUMBER, FOREIGN KEY → GUESTS)
- room_id (NUMBER, FOREIGN KEY → ROOMS)
- check_in_date (DATE, NOT NULL)
- check_out_date (DATE, NOT NULL)
- total_price (NUMBER(10,2), NOT NULL)
- status (VARCHAR2(20), DEFAULT 'PENDING') -- 'PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'
- booking_date (TIMESTAMP, DEFAULT SYSTIMESTAMP)
```

#### PAYMENTS
```sql
- payment_id (NUMBER, PRIMARY KEY)
- reservation_id (NUMBER, FOREIGN KEY → RESERVATIONS)
- amount (NUMBER(10,2), NOT NULL)
- payment_date (TIMESTAMP, DEFAULT SYSTIMESTAMP)
- payment_method (VARCHAR2(20)) -- 'CASH', 'CARD', 'ONLINE'
```

#### SERVICES
```sql
- service_id (NUMBER, PRIMARY KEY)
- service_name (VARCHAR2(100), NOT NULL)
- price (NUMBER(10,2), NOT NULL)
- description (VARCHAR2(500))
```

#### RESERVATION_SERVICES
```sql
- reservation_id (NUMBER, FOREIGN KEY → RESERVATIONS)
- service_id (NUMBER, FOREIGN KEY → SERVICES)
- quantity (NUMBER, DEFAULT 1)
- PRIMARY KEY (reservation_id, service_id)
```

---

## 6. Technical Requirements

### 6.1 Java Concepts to Include

#### Generics
- Generic Repository class: `Repository<T>`
- Generic collection handlers
- Generic method for sorting: `<T extends Comparable<T>>`

#### Collections Framework
- **ArrayList**: Room lists, user lists
- **LinkedList**: Reservation processing queue
- **HashMap**: User session management, quick lookups
- **TreeSet**: Sorted room types by price
- **Queue**: Pending reservations queue
- **Set**: Unique room numbers validation
- **Map**: Statistics aggregation

#### Iterators
- Custom iterators for reservation collections
- Iterator for report generation
- Enhanced for-each loops with iterators

#### Comparator Interface
- Compare rooms by price
- Compare reservations by date
- Compare guests by name
- Multiple sorting strategies

#### List Interface & ListIterator
- Bidirectional traversal of reservations
- Modify lists during iteration

#### Object-Oriented Concepts
- **Inheritance**:
  - Abstract `User` class
  - `Guest`, `Receptionist`, `Admin` extend `User`
  - `Room` types hierarchy (if needed)

- **Interfaces**:
  - `Comparable<T>` for natural ordering (Room, RoomType classes)
  - Simple custom interface for demonstration (e.g., `Searchable` interface)

- **Composition**:
  - `Reservation` contains `Guest` and `Room` objects
  - `Payment` contains `Reservation` reference

- **Exception Handling**:
  - Custom exceptions: `ReservationConflictException`, `InvalidDateException`, `DatabaseException`
  - Try-catch blocks for DB operations
  - Proper error messages to users

### 6.2 Project Structure

```
HotelReservationSystem1/
├── src/
│   └── hotelreservationsystem/
│       ├── models/              # Entity classes
│       │   ├── User.java (abstract)
│       │   ├── Guest.java
│       │   ├── Receptionist.java
│       │   ├── Admin.java
│       │   ├── Room.java
│       │   ├── RoomType.java
│       │   ├── Reservation.java
│       │   ├── Payment.java
│       │   └── Service.java
│       │
│       ├── database/            # Database Operations
│       │   ├── DatabaseConnection.java
│       │   ├── UserDatabase.java
│       │   ├── GuestDatabase.java
│       │   ├── RoomDatabase.java
│       │   ├── ReservationDatabase.java
│       │   ├── PaymentDatabase.java
│       │   └── ServiceDatabase.java
│       │
│       ├── controllers/         # FXML Controllers
│       │   ├── LoginController.java
│       │   ├── GuestRegistrationController.java
│       │   ├── GuestDashboardController.java
│       │   ├── BrowseRoomsController.java
│       │   ├── MakeReservationController.java
│       │   ├── ViewMyReservationsController.java
│       │   ├── ReceptionistDashboardController.java
│       │   ├── ManageReservationsController.java
│       │   ├── CheckInOutController.java
│       │   ├── AdminDashboardController.java
│       │   ├── ManageUsersController.java
│       │   ├── ManageRoomsController.java
│       │   └── RevenueReportsController.java
│       │
│       ├── utils/               # Utility Classes
│       │   ├── Repository.java (generic)
│       │   ├── RoomPriceComparator.java
│       │   ├── ReservationDateComparator.java
│       │   ├── ValidationUtils.java
│       │   ├── DateUtils.java
│       │   └── SessionManager.java
│       │
│       ├── exceptions/          # Custom Exceptions
│       │   ├── ReservationConflictException.java
│       │   ├── InvalidDateException.java
│       │   ├── DatabaseException.java
│       │   └── AuthenticationException.java
│       │
│       └── views/               # FXML Files
│           ├── common/
│           │   ├── Login.fxml
│           │   └── GuestRegistration.fxml
│           ├── guest/
│           │   ├── GuestDashboard.fxml
│           │   ├── BrowseRooms.fxml
│           │   ├── MakeReservation.fxml
│           │   └── ViewMyReservations.fxml
│           ├── receptionist/
│           │   ├── ReceptionistDashboard.fxml
│           │   ├── ManageReservations.fxml
│           │   └── CheckInOut.fxml
│           └── admin/
│               ├── AdminDashboard.fxml
│               ├── ManageUsers.fxml
│               ├── ManageRooms.fxml
│               └── RevenueReports.fxml
│
├── lib/                         # External libraries (Oracle JDBC driver)
├── REQUIREMENTS_SPECIFICATION.md
├── PROGRESS_TRACKING.md
└── README.md
```

---

## 7. Non-Functional Requirements

### 7.1 Usability
- Simple, clean UI suitable for academic project
- Clear navigation between screens
- Intuitive form layouts using GridPane
- Appropriate error messages

### 7.2 Performance
- Response time < 3 seconds for database queries
- Efficient use of collections and generics

### 7.3 Security
- Password storage (basic hashing - SHA-256)
- Session management for logged-in users
- Role-based access control

### 7.4 Data Validation
- Required field validation
- Date range validation (check-out > check-in)
- Email format validation
- Phone number format validation
- Duplicate username/email prevention
- Room availability conflict detection

---

## 8. Sample Data Requirements

### 8.1 Initial Data
- 3 Admin accounts
- 5 Receptionist accounts
- 10 Guest accounts
- 4 Room types (Single, Double, Suite, Deluxe)
- 20 Rooms (distributed across types)
- 5 Services (WiFi, Breakfast, Parking, Laundry, Room Service)
- 15 Sample reservations (various statuses)

---

## 9. Assumptions and Constraints

### 9.1 Assumptions
- One reservation per room per time period
- Check-in time: 14:00, Check-out time: 12:00
- Price calculated per night
- Guest can have multiple active reservations
- Receptionist confirms reservations before check-in
- Payment processing is simplified (no actual payment gateway)

### 9.2 Constraints
- Must use Oracle database (not MySQL as in original spec)
- Must use FXML for all UI screens
- Must demonstrate all required Java concepts from textbook
- Academic-level implementation (not production-grade)
- Windows environment compatibility

---

## 10. Deliverables

1. **Source Code**
   - All Java classes properly organized
   - All FXML files
   - Database schema SQL scripts

2. **Documentation**
   - Requirements Specification (this document)
   - Progress Tracking document
   - Project Report including:
     - Introduction
     - System hierarchy diagram
     - UML class diagram
     - Database schema diagram
     - Execution snapshots
     - Code snapshots

3. **Database**
   - Oracle database creation script
   - Sample data insertion script

---

## 11. Timeline

- **Week 12**: Project submission
- **Week 13**: Project discussion/presentation

---

## 12. References

- **Textbook**: "Intro to Java Programming And Data Structures, Comprehensive Version (10th Edition)" by Y. Daniel Liang
- **JavaFX Documentation**: Oracle JavaFX documentation
- **Oracle Database Documentation**: Oracle SQL and JDBC documentation
