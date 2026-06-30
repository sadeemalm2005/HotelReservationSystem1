# Hotel Reservation System - Quick Start Guide

## 🚀 Quick Setup (5 Steps)

### Step 1: Start Oracle Database
Ensure Oracle Database service is running on your machine.

### Step 2: Run Database Scripts
```sql
-- In SQL Developer or SQL*Plus
@database_schema.sql
@sample_data.sql
```

### Step 3: Verify Setup
```sql
SELECT COUNT(*) FROM USERS;  -- Should return 18
```

### Step 4: Run Application
Run `HotelReservationSystem1.java` main method

### Step 5: Test Login
Use any of the credentials below

---

## 🔑 Test Credentials

### Admin Accounts (Full Access)
```
Username: admin1
Password: password123
Access: AdminDashboard → User Management, Reports, Room Types, Services
```

### Receptionist Accounts
```
Username: receptionist1
Password: password123
Access: ReceptionistDashboard → Check-In/Out, Room Management
```

### Guest Accounts
```
Username: fatima_ahmad
Password: password123
Access: GuestDashboard → Search Rooms, My Reservations
```

---

## 📋 Testing Checklist

### Test as Admin (admin1/password123):
- [ ] Login successful → AdminDashboard appears
- [ ] View statistics (Total Guests, Rooms, Revenue)
- [ ] Click "User Management" → View all users
- [ ] Click "Reports" → Generate report
- [ ] Click "Room Types" → View room types
- [ ] Click "Services" → View services
- [ ] Click "Logout" → Return to Login

### Test as Receptionist (receptionist1/password123):
- [ ] Login successful → ReceptionistDashboard appears
- [ ] View today's check-ins/check-outs
- [ ] Click "Check-In/Out" → Search reservation
- [ ] Click "Room Management" → View/update room status
- [ ] Click "Logout" → Return to Login

### Test as Guest (fatima_ahmad/password123):
- [ ] Login successful → GuestDashboard appears
- [ ] View recent reservations
- [ ] Click "Search Rooms" → Search available rooms
- [ ] Click "My Reservations" → View all reservations
- [ ] Click "Logout" → Return to Login

---

## 📊 Sample Data Summary

### Users
- **18 total**: 3 Admins, 5 Receptionists, 10 Guests
- All passwords: `password123`

### Rooms
- **20 total**: 5 Single, 7 Double, 5 Suite, 3 Deluxe
- **Statuses**: Available, Occupied, Cleaning, Maintenance

### Reservations
- **15 total**:
  - 3 CHECKED_OUT (completed stays)
  - 4 CHECKED_IN (currently staying)
  - 3 CONFIRMED (upcoming)
  - 3 PENDING (awaiting confirmation)
  - 2 CANCELLED

### Room Types & Pricing
| Type | Price/Night | Capacity |
|------|-------------|----------|
| Single | $100 | 1 person |
| Double | $150 | 2 people |
| Suite | $250 | 4 people |
| Deluxe | $400 | 2 people |

### Services
- WiFi (Free)
- Breakfast ($25)
- Parking ($15)
- Laundry ($30)
- Room Service ($20)

---

## 🔍 Useful SQL Queries for Testing

### View All Available Rooms
```sql
SELECT r.ROOM_NUMBER, rt.TYPE_NAME, rt.BASE_PRICE, r.FLOOR
FROM ROOMS r
JOIN ROOM_TYPES rt ON r.TYPE_ID = rt.TYPE_ID
WHERE r.STATUS = 'AVAILABLE'
ORDER BY r.FLOOR, r.ROOM_NUMBER;
```

### View Current Check-Ins
```sql
SELECT res.RESERVATION_ID, g.FIRST_NAME || ' ' || g.LAST_NAME AS GUEST_NAME, r.ROOM_NUMBER
FROM RESERVATIONS res
JOIN GUESTS g ON res.GUEST_ID = g.GUEST_ID
JOIN ROOMS r ON res.ROOM_ID = r.ROOM_ID
WHERE res.STATUS = 'CHECKED_IN';
```

### View Total Revenue
```sql
SELECT SUM(AMOUNT) AS TOTAL_REVENUE FROM PAYMENTS;
```

### Check User Role
```sql
SELECT USERNAME, ROLE FROM USERS WHERE USERNAME = 'admin1';
```

---

## ⚠️ Common Issues & Solutions

### ❌ Cannot connect to database
**Fix**:
- Check Oracle service is running
- Verify credentials in `DatabaseConnection.java`
- Test connection: `jdbc:oracle:thin:@localhost:1522:XE`

### ❌ Login shows "Invalid username or password"
**Fix**:
- Verify sample data was inserted: `SELECT COUNT(*) FROM USERS;`
- Check username/password: `admin1` / `password123`
- Ensure USER_ROLE column has values

### ❌ Dashboard is empty (no data showing)
**Fix**:
- Verify reservations exist: `SELECT COUNT(*) FROM RESERVATIONS;`
- Check room status: `SELECT COUNT(*) FROM ROOMS WHERE STATUS='AVAILABLE';`
- Ensure foreign keys are correct

### ❌ "ClassNotFoundException: oracle.jdbc.driver.OracleDriver"
**Fix**:
- Add Oracle JDBC driver (ojdbc8.jar or ojdbc11.jar) to project libraries
- In NetBeans: Right-click project → Properties → Libraries → Add JAR/Folder

---

## 🎯 Feature Testing

### For Guests:
1. **Search & Book Rooms**
   - Select check-in/out dates
   - Choose room type
   - Click "Search"
   - Select room and click "Book Selected Room"

2. **View Reservations**
   - See all your reservations
   - Check status (Pending, Confirmed, Checked In, etc.)
   - View services attached to reservation

### For Receptionists:
1. **Check-In**
   - Search reservation by ID
   - Verify guest details
   - Click "Check In"

2. **Check-Out**
   - Search reservation by ID
   - Process payment
   - Click "Check Out"

3. **Room Management**
   - View all rooms
   - Filter by status
   - Update room status (Available, Cleaning, Maintenance, etc.)

### For Admins:
1. **User Management**
   - View all users
   - Add new user (specify role)
   - Update user details
   - Delete user

2. **Generate Reports**
   - Select report type
   - Choose date range
   - Click "Generate Report"
   - View statistics and details

3. **Manage Room Types**
   - Add new room type
   - Set pricing and capacity
   - Update existing types

4. **Manage Services**
   - Add new service
   - Set service price
   - Update existing services

---

## 📝 Project Structure

```
HotelReservationSystem1/
├── src/
│   ├── hotelreservationsystem1/
│   │   ├── models/          # User, Guest, Room, Reservation, etc.
│   │   ├── exceptions/      # Custom exceptions
│   │   └── HotelReservationSystem1.java  # Main class
│   ├── controllers/         # 14 controller classes
│   ├── views/               # 14 FXML files
│   └── database/
│       └── DatabaseConnection.java
├── database_schema.sql      # Schema creation
├── sample_data.sql          # Test data
├── DATABASE_SETUP_GUIDE.md  # Detailed setup guide
└── QUICK_START_GUIDE.md     # This file
```

---

## ✅ Success Checklist

Before considering the project complete:

- [ ] Database created and populated
- [ ] All 18 users can login
- [ ] Admin can access all admin features
- [ ] Receptionist can process check-in/out
- [ ] Guests can search and book rooms
- [ ] All UI screens display correctly
- [ ] Navigation works between screens
- [ ] Logout returns to homepage
- [ ] No console errors during operation

---

## 🎓 Project Requirements Demonstrated

This project demonstrates the following CS313 concepts:

✅ **Chapter 4**: Generics, Collections
- ArrayList (guest reservations)
- LinkedList (service lists)
- HashMap (user lookup)
- TreeSet with Comparator (room sorting)
- Queue (pending tasks)
- Iterator & ListIterator

✅ **Chapter 5**: Database Programming
- JDBC connectivity
- PreparedStatement (SQL injection prevention)
- ResultSet processing
- Transaction management

✅ **OOP Concepts**:
- Inheritance (User → Guest/Receptionist/Admin)
- Polymorphism (getRole() method)
- Composition (Room HAS-A RoomType)
- Interfaces (Comparable)
- Exception Handling (Custom exceptions)

✅ **JavaFX**:
- FXML-based UI design
- Scene navigation
- Event handling
- Controller classes
- TableView, ListView components

---

## 📞 Support

If you need help:
1. Check console output for error messages
2. Review `DATABASE_SETUP_GUIDE.md` for detailed instructions
3. Verify all files are in correct locations
4. Ensure Oracle JDBC driver is in classpath

**Remember**: All test passwords are `password123`
