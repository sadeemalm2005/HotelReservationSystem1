-- ============================================
-- QUICK FIX FOR ADMIN AND RECEPTIONIST LOGINS
-- ============================================
-- Copy and run these commands in SQL Developer or your Oracle client
-- ============================================

-- Step 1: Check if users exist
SELECT USERNAME, PASSWORD, ROLE FROM USERS WHERE USERNAME IN ('admin1', 'receptionist1', 'receptionist2');

-- Step 2: If they don't exist or have wrong passwords, run this:
-- This will delete them if they exist, then re-insert them with correct credentials

BEGIN
    -- Delete existing users if any
    DELETE FROM USERS WHERE USERNAME = 'admin1';
    DELETE FROM USERS WHERE USERNAME = 'receptionist1';
    DELETE FROM USERS WHERE USERNAME = 'receptionist2';

    -- Insert admin1
    INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, EMAIL, PHONE, ROLE, CREATED_DATE)
    VALUES (USER_SEQ.NEXTVAL, 'admin1', 'password123', 'admin1@hotel.com', '0501234567', 'ADMIN', SYSDATE);

    -- Insert receptionist1
    INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, EMAIL, PHONE, ROLE, CREATED_DATE)
    VALUES (USER_SEQ.NEXTVAL, 'receptionist1', 'password123', 'recep1@hotel.com', '0501234570', 'RECEPTIONIST', SYSDATE);

    -- Insert receptionist2
    INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, EMAIL, PHONE, ROLE, CREATED_DATE)
    VALUES (USER_SEQ.NEXTVAL, 'receptionist2', 'password123', 'recep2@hotel.com', '0501234571', 'RECEPTIONIST', SYSDATE);

    COMMIT;
END;
/

-- Step 3: Verify they were created
SELECT USER_ID, USERNAME, PASSWORD, EMAIL, ROLE FROM USERS
WHERE USERNAME IN ('admin1', 'receptionist1', 'receptionist2')
ORDER BY USERNAME;

-- ============================================
-- TEST THE LOGINS
-- ============================================
-- Try these credentials in your application:
-- Username: admin1          Password: password123
-- Username: receptionist1   Password: password123
-- Username: receptionist2   Password: password123
-- ============================================
