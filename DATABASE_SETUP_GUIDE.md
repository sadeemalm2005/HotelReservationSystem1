# Hotel Reservation System - Database Setup Guide

## Prerequisites
- Oracle Database installed (Oracle 11g or higher)
- SQL Developer or SQL*Plus installed
- Database credentials: `system/1234` on `localhost:1522/XE`

---

## Step 1: Connect to Oracle Database

### Option A: Using SQL Developer
1. Open Oracle SQL Developer
2. Click **New Connection** (green plus icon)
3. Enter connection details:
   - **Connection Name**: Hotel Reservation System
   - **Username**: `system`
   - **Password**: `1234`
   - **Hostname**: `localhost`
   - **Port**: `1522`
   - **SID**: `XE`
4. Click **Test** to verify connection
5. Click **Connect**

### Option B: Using SQL*Plus
```bash
sqlplus system/1234@localhost:1522/XE
```

---

## Step 2: Create Database Schema

### Execute the schema creation script:
```sql
@database_schema.sql
```

Or manually copy and paste the contents of `database_schema.sql` into SQL Developer and run it.

### What this does:
✅ Drops existing tables and sequences (if any)
✅ Creates sequences for auto-increment IDs
✅ Creates 8 tables:
   - USERS (stores all user accounts with roles)
   - GUESTS (guest-specific information)
   - ROOM_TYPES (room categories and pricing)
   - ROOMS (individual rooms)
   - RESERVATIONS (booking records)
   - PAYMENTS (payment transactions)
   - SERVICES (additional services)
   - RESERVATION_SERVICES (links reservations to services)

---

## Step 3: Insert Sample Data

### Execute the sample data script:
```sql
@sample_data.sql
```

Or manually copy and paste the contents of `sample_data.sql` into SQL Developer and run it.

### What this inserts:

#### **Users (18 total)**
- **3 Admins**
  - Username: `admin1`, `admin2`, `admin3`
  - Password: `password123`

- **5 Receptionists**
  - Username: `receptionist1`, `receptionist2`, `receptionist3`, `receptionist4`, `receptionist5`
  - Password: `password123`

- **10 Guests**
  - Usernames: `fatima_ahmad`, `aisha_mohamed`, `maryam_abdullah`, `khadija_salem`, `noor_ibrahim`, `sarah_hassan`, `layla_khalid`, `zainab_omar`, `amira_yousef`, `huda_ali`
  - Password: `password123`

#### **Room Types (4 types)**
- Single ($100/night, capacity: 1)
- Double ($150/night, capacity: 2)
- Suite ($250/night, capacity: 4)
- Deluxe ($400/night, capacity: 2)

#### **Rooms (20 rooms)**
- 5 Single rooms (101, 102, 103, 201, 202)
- 7 Double rooms (104, 105, 106, 203, 204, 301, 302)
- 5 Suite rooms (205, 206, 303, 304, 401)
- 3 Deluxe rooms (305, 402, 403)

#### **Services (5 services)**
- WiFi (Free)
- Breakfast ($25)
- Parking ($15)
- Laundry ($30)
- Room Service ($20)

#### **Reservations (15 reservations)**
- 3 CHECKED_OUT (past stays)
- 4 CHECKED_IN (currently staying)
- 3 CONFIRMED (upcoming bookings)
- 3 PENDING (awaiting confirmation)
- 2 CANCELLED

#### **Payments (7 payments)**
- For checked-out and checked-in reservations

---

## Step 4: Verify Data Insertion

### Check table row counts:
```sql
SELECT 'USERS' AS TABLE_NAME, COUNT(*) AS ROW_COUNT FROM USERS
UNION ALL
SELECT 'GUESTS', COUNT(*) FROM GUESTS
UNION ALL
SELECT 'ROOM_TYPES', COUNT(*) FROM ROOM_TYPES
UNION ALL
SELECT 'ROOMS', COUNT(*) FROM ROOMS
UNION ALL
SELECT 'RESERVATIONS', COUNT(*) FROM RESERVATIONS
UNION ALL
SELECT 'PAYMENTS', COUNT(*) FROM PAYMENTS
UNION ALL
SELECT 'SERVICES', COUNT(*) FROM SERVICES
UNION ALL
SELECT 'RESERVATION_SERVICES', COUNT(*) FROM RESERVATION_SERVICES;
```

### Expected output:
```
TABLE_NAME              ROW_COUNT
----------------------- ----------
USERS                   18
GUESTS                  10
ROOM_TYPES              4
ROOMS                   20
RESERVATIONS            15
PAYMENTS                7
SERVICES                5
RESERVATION_SERVICES    9
```

---

## Step 5: Test Login Credentials

### Admin Login:
- **Username**: `admin1`
- **Password**: `password123`
- **Expected**: Redirects to AdminDashboard

### Receptionist Login:
- **Username**: `receptionist1`
- **Password**: `password123`
- **Expected**: Redirects to ReceptionistDashboard

### Guest Login:
- **Username**: `fatima_ahmad`
- **Password**: `password123`
- **Expected**: Redirects to GuestDashboard

---

## Step 6: View Sample Data

### View all users with their roles:
```sql
SELECT USER_ID, USERNAME, EMAIL, ROLE
FROM USERS
ORDER BY ROLE, USER_ID;
```

### View available rooms:
```sql
SELECT r.ROOM_NUMBER, rt.TYPE_NAME, rt.BASE_PRICE, r.STATUS, r.FLOOR
FROM ROOMS r
JOIN ROOM_TYPES rt ON r.TYPE_ID = rt.TYPE_ID
WHERE r.STATUS = 'AVAILABLE'
ORDER BY r.FLOOR, r.ROOM_NUMBER;
```

### View current reservations (checked in):
```sql
SELECT res.RESERVATION_ID, g.FIRST_NAME || ' ' || g.LAST_NAME AS GUEST_NAME,
       r.ROOM_NUMBER, res.CHECK_IN_DATE, res.CHECK_OUT_DATE, res.TOTAL_PRICE, res.STATUS
FROM RESERVATIONS res
JOIN GUESTS g ON res.GUEST_ID = g.GUEST_ID
JOIN ROOMS r ON res.ROOM_ID = r.ROOM_ID
WHERE res.STATUS = 'CHECKED_IN'
ORDER BY res.CHECK_IN_DATE;
```

### View hotel statistics:
```sql
SELECT
    (SELECT COUNT(*) FROM GUESTS) AS TOTAL_GUESTS,
    (SELECT COUNT(*) FROM ROOMS) AS TOTAL_ROOMS,
    (SELECT COUNT(*) FROM ROOMS WHERE STATUS = 'OCCUPIED') AS OCCUPIED_ROOMS,
    (SELECT COUNT(*) FROM RESERVATIONS WHERE STATUS IN ('CONFIRMED', 'CHECKED_IN')) AS ACTIVE_RESERVATIONS,
    (SELECT SUM(AMOUNT) FROM PAYMENTS) AS TOTAL_REVENUE
FROM DUAL;
```

---

## Troubleshooting

### Issue 1: "Table or view does not exist"
**Solution**: Make sure you ran `database_schema.sql` first to create all tables.

### Issue 2: "Sequence does not exist"
**Solution**: The sequences are created in `database_schema.sql`. Re-run the schema creation script.

### Issue 3: "ORA-00001: unique constraint violated"
**Solution**: The sample data has already been inserted. Either:
- Drop all tables and re-run both scripts, OR
- Manually delete existing data before re-inserting

### Issue 4: "Cannot connect to database"
**Solution**:
- Verify Oracle Database service is running
- Check connection details (username, password, port, SID)
- Test connection using SQL Developer or SQL*Plus

### Issue 5: Foreign key constraint error
**Solution**: Make sure you're inserting data in the correct order:
1. USERS
2. GUESTS (depends on USERS)
3. ROOM_TYPES
4. ROOMS (depends on ROOM_TYPES)
5. RESERVATIONS (depends on GUESTS and ROOMS)
6. PAYMENTS (depends on RESERVATIONS)
7. SERVICES
8. RESERVATION_SERVICES (depends on RESERVATIONS and SERVICES)

---

## Next Steps

After completing the database setup:

1. ✅ Verify all tables and data are created correctly
2. ✅ Test login credentials from the application
3. ✅ Run the JavaFX application (`HotelReservationSystem1.java`)
4. ✅ Test each user role (Admin, Receptionist, Guest)
5. ✅ Verify all features work correctly

---

## Database Credentials Summary

**For Application** (`DatabaseConnection.java`):
- URL: `jdbc:oracle:thin:@localhost:1522:XE`
- Username: `system`
- Password: `1234`
- Driver: `oracle.jdbc.driver.OracleDriver`

**Test Login Accounts**:
| Role | Username | Password | Access |
|------|----------|----------|--------|
| Admin | admin1 | password123 | Full system access |
| Receptionist | receptionist1 | password123 | Check-in/out, Room management |
| Guest | fatima_ahmad | password123 | Book rooms, View reservations |

---

## Support

If you encounter any issues:
1. Check the error message in the console
2. Verify database connection settings in `DatabaseConnection.java`
3. Ensure Oracle JDBC driver is added to your project classpath
4. Review SQL scripts for any syntax errors

**Note**: All passwords in the sample data are `password123` for testing purposes. In production, these should be properly hashed and secured.
