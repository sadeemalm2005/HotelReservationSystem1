-- ============================================
-- Hotel Reservation System - Sample Data
-- Oracle Database
-- ============================================

-- ============================================
-- Insert Users (3 Admins, 5 Receptionists, 10 Guests)
-- Password is 'password123' for all (will be hashed in application)
-- ============================================

-- Admins
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'admin1', 'password123', 'admin1@hotel.com', '0501234567', 'ADMIN', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'admin2', 'password123', 'admin2@hotel.com', '0501234568', 'ADMIN', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'admin3', 'password123', 'admin3@hotel.com', '0501234569', 'ADMIN', SYSDATE);

-- Receptionists
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist1', 'password123', 'recep1@hotel.com', '0501234570', 'RECEPTIONIST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist2', 'password123', 'recep2@hotel.com', '0501234571', 'RECEPTIONIST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist3', 'password123', 'recep3@hotel.com', '0501234572', 'RECEPTIONIST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist4', 'password123', 'recep4@hotel.com', '0501234573', 'RECEPTIONIST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist5', 'password123', 'recep5@hotel.com', '0501234574', 'RECEPTIONIST', SYSDATE);

-- Guests (Arabic female student names)
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'fatima_ahmad', 'password123', 'fatima.ahmad@student.edu.sa', '0509876543', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'aisha_mohamed', 'password123', 'aisha.mohamed@student.edu.sa', '0509876544', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'maryam_abdullah', 'password123', 'maryam.abdullah@student.edu.sa', '0509876545', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'khadija_salem', 'password123', 'khadija.salem@student.edu.sa', '0509876546', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'noor_ibrahim', 'password123', 'noor.ibrahim@student.edu.sa', '0509876547', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'sarah_hassan', 'password123', 'sarah.hassan@student.edu.sa', '0509876548', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'layla_khalid', 'password123', 'layla.khalid@student.edu.sa', '0509876549', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'zainab_omar', 'password123', 'zainab.omar@student.edu.sa', '0509876550', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'amira_yousef', 'password123', 'amira.yousef@student.edu.sa', '0509876551', 'GUEST', SYSDATE);
INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'huda_ali', 'password123', 'huda.ali@student.edu.sa', '0509876552', 'GUEST', SYSDATE);

-- ============================================
-- Insert Guest Details (for the 10 guest users)
-- ============================================

INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 9, 'Fatima', 'Ahmad', 'ID001', 'Riyadh, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 10, 'Aisha', 'Mohamed', 'ID002', 'Jeddah, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 11, 'Maryam', 'Abdullah', 'ID003', 'Dammam, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 12, 'Khadija', 'Salem', 'ID004', 'Mecca, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 13, 'Noor', 'Ibrahim', 'ID005', 'Medina, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 14, 'Sarah', 'Hassan', 'ID006', 'Riyadh, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 15, 'Layla', 'Khalid', 'ID007', 'Jeddah, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 16, 'Zainab', 'Omar', 'ID008', 'Dammam, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 17, 'Amira', 'Yousef', 'ID009', 'Tabuk, Saudi Arabia');
INSERT INTO GUESTS VALUES (guest_seq.NEXTVAL, 18, 'Huda', 'Ali', 'ID010', 'Abha, Saudi Arabia');

-- ============================================
-- Insert Room Types (4 types)
-- ============================================

INSERT INTO ROOM_TYPES VALUES (room_type_seq.NEXTVAL, 'Single', 'Cozy room with single bed', 100.00, 1, 'WiFi, TV, Mini-fridge');
INSERT INTO ROOM_TYPES VALUES (room_type_seq.NEXTVAL, 'Double', 'Comfortable room with double bed', 150.00, 2, 'WiFi, TV, Mini-fridge, Coffee maker');
INSERT INTO ROOM_TYPES VALUES (room_type_seq.NEXTVAL, 'Suite', 'Spacious suite with living area', 250.00, 4, 'WiFi, TV, Mini-bar, Coffee maker, Balcony');
INSERT INTO ROOM_TYPES VALUES (room_type_seq.NEXTVAL, 'Deluxe', 'Luxurious room with premium amenities', 400.00, 2, 'WiFi, Smart TV, Mini-bar, Coffee maker, Jacuzzi, Balcony');

-- ============================================
-- Insert Rooms (20 rooms distributed across types)
-- ============================================

-- Single rooms (5 rooms)
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '101', 1, 1, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '102', 1, 1, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '103', 1, 1, 'OCCUPIED');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '201', 1, 2, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '202', 1, 2, 'CLEANING');

-- Double rooms (7 rooms)
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '104', 2, 1, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '105', 2, 1, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '106', 2, 1, 'OCCUPIED');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '203', 2, 2, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '204', 2, 2, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '301', 2, 3, 'OCCUPIED');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '302', 2, 3, 'AVAILABLE');

-- Suite rooms (5 rooms)
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '205', 3, 2, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '206', 3, 2, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '303', 3, 3, 'OCCUPIED');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '304', 3, 3, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '401', 3, 4, 'MAINTENANCE');

-- Deluxe rooms (3 rooms)
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '305', 4, 3, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '402', 4, 4, 'AVAILABLE');
INSERT INTO ROOMS VALUES (room_seq.NEXTVAL, '403', 4, 4, 'OCCUPIED');

-- ============================================
-- Insert Services (5 services)
-- ============================================

INSERT INTO SERVICES VALUES (service_seq.NEXTVAL, 'WiFi', 0.00, 'Free high-speed internet');
INSERT INTO SERVICES VALUES (service_seq.NEXTVAL, 'Breakfast', 25.00, 'Continental breakfast buffet');
INSERT INTO SERVICES VALUES (service_seq.NEXTVAL, 'Parking', 15.00, 'Secure parking space per day');
INSERT INTO SERVICES VALUES (service_seq.NEXTVAL, 'Laundry', 30.00, 'Same-day laundry service');
INSERT INTO SERVICES VALUES (service_seq.NEXTVAL, 'Room Service', 20.00, '24/7 room service');

-- ============================================
-- Insert Reservations (15 reservations with various statuses)
-- ============================================

-- CHECKED_OUT (past reservations)
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 1, 3, TO_DATE('2024-10-01', 'YYYY-MM-DD'), TO_DATE('2024-10-05', 'YYYY-MM-DD'), 400.00, 'CHECKED_OUT', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 2, 8, TO_DATE('2024-10-10', 'YYYY-MM-DD'), TO_DATE('2024-10-12', 'YYYY-MM-DD'), 300.00, 'CHECKED_OUT', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 3, 15, TO_DATE('2024-10-15', 'YYYY-MM-DD'), TO_DATE('2024-10-20', 'YYYY-MM-DD'), 1250.00, 'CHECKED_OUT', SYSTIMESTAMP);

-- CHECKED_IN (currently staying)
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 4, 3, TO_DATE('2025-11-01', 'YYYY-MM-DD'), TO_DATE('2025-11-08', 'YYYY-MM-DD'), 700.00, 'CHECKED_IN', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 5, 8, TO_DATE('2025-11-02', 'YYYY-MM-DD'), TO_DATE('2025-11-06', 'YYYY-MM-DD'), 600.00, 'CHECKED_IN', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 6, 14, TO_DATE('2025-11-03', 'YYYY-MM-DD'), TO_DATE('2025-11-10', 'YYYY-MM-DD'), 1750.00, 'CHECKED_IN', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 7, 20, TO_DATE('2025-11-01', 'YYYY-MM-DD'), TO_DATE('2025-11-07', 'YYYY-MM-DD'), 2400.00, 'CHECKED_IN', SYSTIMESTAMP);

-- CONFIRMED (upcoming reservations)
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 8, 1, TO_DATE('2025-11-10', 'YYYY-MM-DD'), TO_DATE('2025-11-15', 'YYYY-MM-DD'), 500.00, 'CONFIRMED', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 9, 6, TO_DATE('2025-11-12', 'YYYY-MM-DD'), TO_DATE('2025-11-14', 'YYYY-MM-DD'), 300.00, 'CONFIRMED', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 10, 13, TO_DATE('2025-11-15', 'YYYY-MM-DD'), TO_DATE('2025-11-20', 'YYYY-MM-DD'), 1250.00, 'CONFIRMED', SYSTIMESTAMP);

-- PENDING (awaiting confirmation)
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 1, 2, TO_DATE('2025-11-20', 'YYYY-MM-DD'), TO_DATE('2025-11-25', 'YYYY-MM-DD'), 500.00, 'PENDING', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 3, 9, TO_DATE('2025-11-22', 'YYYY-MM-DD'), TO_DATE('2025-11-24', 'YYYY-MM-DD'), 300.00, 'PENDING', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 5, 18, TO_DATE('2025-11-25', 'YYYY-MM-DD'), TO_DATE('2025-11-30', 'YYYY-MM-DD'), 2000.00, 'PENDING', SYSTIMESTAMP);

-- CANCELLED
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 2, 7, TO_DATE('2025-11-08', 'YYYY-MM-DD'), TO_DATE('2025-11-10', 'YYYY-MM-DD'), 300.00, 'CANCELLED', SYSTIMESTAMP);
INSERT INTO RESERVATIONS VALUES (reservation_seq.NEXTVAL, 4, 12, TO_DATE('2025-11-18', 'YYYY-MM-DD'), TO_DATE('2025-11-22', 'YYYY-MM-DD'), 600.00, 'CANCELLED', SYSTIMESTAMP);

-- ============================================
-- Insert Payments (for checked out and checked in reservations)
-- ============================================

INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 1, 400.00, SYSTIMESTAMP, 'CARD');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 2, 300.00, SYSTIMESTAMP, 'CASH');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 3, 1250.00, SYSTIMESTAMP, 'CARD');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 4, 700.00, SYSTIMESTAMP, 'ONLINE');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 5, 600.00, SYSTIMESTAMP, 'CARD');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 6, 1750.00, SYSTIMESTAMP, 'CARD');
INSERT INTO PAYMENTS VALUES (payment_seq.NEXTVAL, 7, 2400.00, SYSTIMESTAMP, 'CASH');

-- ============================================
-- Insert Reservation Services (some reservations have additional services)
-- ============================================

INSERT INTO RESERVATION_SERVICES VALUES (1, 2, 5);  -- Reservation 1: Breakfast for 5 days
INSERT INTO RESERVATION_SERVICES VALUES (1, 3, 5);  -- Reservation 1: Parking for 5 days
INSERT INTO RESERVATION_SERVICES VALUES (3, 2, 5);  -- Reservation 3: Breakfast for 5 days
INSERT INTO RESERVATION_SERVICES VALUES (3, 5, 2);  -- Reservation 3: Room Service (2 times)
INSERT INTO RESERVATION_SERVICES VALUES (4, 2, 7);  -- Reservation 4: Breakfast for 7 days
INSERT INTO RESERVATION_SERVICES VALUES (4, 3, 7);  -- Reservation 4: Parking for 7 days
INSERT INTO RESERVATION_SERVICES VALUES (6, 2, 7);  -- Reservation 6: Breakfast for 7 days
INSERT INTO RESERVATION_SERVICES VALUES (6, 4, 1);  -- Reservation 6: Laundry service
INSERT INTO RESERVATION_SERVICES VALUES (7, 5, 3);  -- Reservation 7: Room Service (3 times)

COMMIT;

SELECT 'Sample data inserted successfully!' AS STATUS FROM DUAL;
