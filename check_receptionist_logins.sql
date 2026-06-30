-- Check Receptionist accounts
SELECT USER_ID, USERNAME, PASSWORD, EMAIL, ROLE
FROM USERS
WHERE ROLE = 'RECEPTIONIST'
ORDER BY USERNAME;

-- If receptionists don't exist or passwords are wrong, run these UPDATE statements:
-- UPDATE USERS SET PASSWORD = 'password123' WHERE USERNAME = 'receptionist1';
-- UPDATE USERS SET PASSWORD = 'password123' WHERE USERNAME = 'receptionist2';
-- COMMIT;
