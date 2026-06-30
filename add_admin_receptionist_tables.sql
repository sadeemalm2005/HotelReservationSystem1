-- ============================================
-- Add sequences for ADMINS and RECEPTIONISTS
-- ============================================

CREATE SEQUENCE admin_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE receptionist_seq START WITH 1 INCREMENT BY 1;

-- ============================================
-- Table: ADMINS
-- ============================================

CREATE TABLE ADMINS (
    admin_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ============================================
-- Table: RECEPTIONISTS
-- ============================================

CREATE TABLE RECEPTIONISTS (
    receptionist_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    CONSTRAINT fk_receptionist_user FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ============================================
-- Migrate existing ADMIN users to ADMINS table
-- Note: You'll need to add first/last names manually or update this script
-- ============================================

-- Example: INSERT INTO ADMINS (ADMIN_ID, USER_ID, FIRST_NAME, LAST_NAME)
-- SELECT admin_seq.NEXTVAL, USER_ID, 'FirstName', 'LastName'
-- FROM USERS WHERE ROLE = 'ADMIN';

-- ============================================
-- Migrate existing RECEPTIONIST users to RECEPTIONISTS table
-- Note: You'll need to add first/last names manually or update this script
-- ============================================

-- Example: INSERT INTO RECEPTIONISTS (RECEPTIONIST_ID, USER_ID, FIRST_NAME, LAST_NAME)
-- SELECT receptionist_seq.NEXTVAL, USER_ID, 'FirstName', 'LastName'
-- FROM USERS WHERE ROLE = 'RECEPTIONIST';