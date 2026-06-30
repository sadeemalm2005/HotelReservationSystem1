-- ============================================
-- Fix Admin and Receptionist Login Issues
-- Run this script to ensure admin1, receptionist1, and receptionist2 can log in
-- ============================================

-- First, let's check if these users exist
SELECT USER_ID, USERNAME, PASSWORD, ROLE
FROM USERS
WHERE USERNAME IN ('admin1', 'receptionist1', 'receptionist2')
ORDER BY USERNAME;

-- If the above query shows the users don't exist, run the INSERT statements below
-- If they exist but have wrong passwords, run the UPDATE statements below

-- ============================================
-- Option 1: INSERT users if they don't exist
-- ============================================
-- Uncomment these lines if the users don't exist:

-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'admin1', 'password123', 'admin1@hotel.com', '0501234567', 'ADMIN', SYSDATE);
-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist1', 'password123', 'recep1@hotel.com', '0501234570', 'RECEPTIONIST', SYSDATE);
-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist2', 'password123', 'recep2@hotel.com', '0501234571', 'RECEPTIONIST', SYSDATE);
-- COMMIT;

-- ============================================
-- Option 2: UPDATE passwords if users exist but can't log in
-- ============================================
-- Uncomment these lines if the users exist but passwords are wrong:

-- UPDATE USERS SET PASSWORD = 'password123' WHERE USERNAME = 'admin1';
-- UPDATE USERS SET PASSWORD = 'password123' WHERE USERNAME = 'receptionist1';
-- UPDATE USERS SET PASSWORD = 'password123' WHERE USERNAME = 'receptionist2';
-- COMMIT;

-- ============================================
-- Option 3: Delete and re-insert (if there are issues)
-- ============================================
-- Uncomment these if you want to start fresh:

-- DELETE FROM USERS WHERE USERNAME IN ('admin1', 'receptionist1', 'receptionist2');
-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'admin1', 'password123', 'admin1@hotel.com', '0501234567', 'ADMIN', SYSDATE);
-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist1', 'password123', 'recep1@hotel.com', '0501234570', 'RECEPTIONIST', SYSDATE);
-- INSERT INTO USERS VALUES (user_seq.NEXTVAL, 'receptionist2', 'password123', 'recep2@hotel.com', '0501234571', 'RECEPTIONIST', SYSDATE);
-- COMMIT;

-- ============================================
-- Verify the fix
-- ============================================
-- Run this to verify all users can now log in:
SELECT USER_ID, USERNAME, PASSWORD, EMAIL, PHONE, ROLE
FROM USERS
WHERE USERNAME IN ('admin1', 'receptionist1', 'receptionist2')
ORDER BY USERNAME;

-- Expected output:
-- admin1 | password123 | admin1@hotel.com | 0501234567 | ADMIN
-- receptionist1 | password123 | recep1@hotel.com | 0501234570 | RECEPTIONIST
-- receptionist2 | password123 | recep2@hotel.com | 0501234571 | RECEPTIONIST
