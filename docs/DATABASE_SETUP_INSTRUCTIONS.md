# Database Setup Instructions

## Prerequisites
1. Oracle Database installed and running
2. Oracle SQL Developer or SQL*Plus
3. Database credentials:
   - Username: system
   - Password: 1234
   - Connection: localhost:LAPTOP-A3N5KC9H:1522/XE

## Step 1: Run Database Schema Script

1. Open Oracle SQL Developer or SQL*Plus
2. Connect using the credentials above
3. Open the file `database_schema.sql`
4. Execute the entire script
5. Verify you see: "Database schema created successfully!"

This will create:
- 8 tables (USERS, GUESTS, ROOM_TYPES, ROOMS, RESERVATIONS, PAYMENTS, SERVICES, RESERVATION_SERVICES)
- 7 sequences for auto-increment IDs
- All necessary constraints and foreign keys

## Step 2: Insert Sample Data

1. Open the file `sample_data.sql`
2. Execute the entire script
3. Verify you see: "Sample data inserted successfully!"

This will insert:
- 3 Admin accounts
- 5 Receptionist accounts
- 10 Guest accounts (Arabic female student names)
- 4 Room types (Single, Double, Suite, Deluxe)
- 20 Rooms
- 5 Services
- 15 Sample reservations
- 7 Payments
- 9 Reservation-service links

## Step 3: Verify Data

Run these queries to verify data insertion:

```sql
SELECT COUNT(*) FROM USERS;          -- Should return 18
SELECT COUNT(*) FROM GUESTS;         -- Should return 10
SELECT COUNT(*) FROM ROOM_TYPES;     -- Should return 4
SELECT COUNT(*) FROM ROOMS;          -- Should return 20
SELECT COUNT(*) FROM SERVICES;       -- Should return 5
SELECT COUNT(*) FROM RESERVATIONS;   -- Should return 15
SELECT COUNT(*) FROM PAYMENTS;       -- Should return 7
```

## Step 4: Test Login Credentials

All users have the password: `password123`

### Admin Accounts:
- admin1 / password123
- admin2 / password123
- admin3 / password123

### Receptionist Accounts:
- receptionist1 / password123
- receptionist2 / password123
- receptionist3 / password123
- receptionist4 / password123
- receptionist5 / password123

### Guest Accounts:
- fatima_ahmad / password123
- aisha_mohamed / password123
- maryam_abdullah / password123
- khadija_salem / password123
- noor_ibrahim / password123
- sarah_hassan / password123
- layla_khalid / password123
- zainab_omar / password123
- amira_yousef / password123
- huda_ali / password123

## Troubleshooting

### If you get "table or view does not exist" error:
- The DROP statements at the beginning will fail on first run - this is normal
- Continue with the script execution

### If you need to reset the database:
1. Run `database_schema.sql` again (it will drop and recreate all tables)
2. Run `sample_data.sql` again to reinsert data

### If sequences are out of sync:
```sql
-- Reset all sequences
DROP SEQUENCE user_seq;
DROP SEQUENCE guest_seq;
DROP SEQUENCE room_type_seq;
DROP SEQUENCE room_seq;
DROP SEQUENCE reservation_seq;
DROP SEQUENCE payment_seq;
DROP SEQUENCE service_seq;

-- Then run database_schema.sql again
```

## Database Schema Diagram

```
USERS (1) ----→ (1) GUESTS (1) ----→ (*) RESERVATIONS
                                           ↓
                                          (1)
                                           ↓
ROOM_TYPES (1) ----→ (*) ROOMS (1) ----→ (*) RESERVATIONS
                                           ↓
                                          (*)
                                           ↓
                                        PAYMENTS

SERVICES (*) ←----→ (*) RESERVATION_SERVICES ←----→ (*) RESERVATIONS
```

## Next Steps

After database setup is complete:
1. Configure Oracle JDBC driver in the Java project
2. Create DatabaseConnection class
3. Test connectivity from Java application
