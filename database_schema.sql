-- ============================================
-- Hotel Reservation System - Database Schema
-- Oracle Database
-- ============================================

-- Drop existing tables (in reverse order of dependencies)
DROP TABLE RESERVATION_SERVICES CASCADE CONSTRAINTS;
DROP TABLE PAYMENTS CASCADE CONSTRAINTS;
DROP TABLE RESERVATIONS CASCADE CONSTRAINTS;
DROP TABLE ROOMS CASCADE CONSTRAINTS;
DROP TABLE ROOM_TYPES CASCADE CONSTRAINTS;
DROP TABLE SERVICES CASCADE CONSTRAINTS;
DROP TABLE GUESTS CASCADE CONSTRAINTS;
DROP TABLE USERS CASCADE CONSTRAINTS;

-- Drop sequences
DROP SEQUENCE user_seq;
DROP SEQUENCE guest_seq;
DROP SEQUENCE room_type_seq;
DROP SEQUENCE room_seq;
DROP SEQUENCE reservation_seq;
DROP SEQUENCE payment_seq;
DROP SEQUENCE service_seq;

-- ============================================
-- Create Sequences for Auto-Increment IDs
-- ============================================

CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE guest_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE room_type_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE room_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE reservation_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE payment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE service_seq START WITH 1 INCREMENT BY 1;

-- ============================================
-- Table: USERS
-- ============================================

CREATE TABLE USERS (
    user_id NUMBER PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    phone VARCHAR2(20),
    role VARCHAR2(20) NOT NULL,
    created_date DATE DEFAULT SYSDATE,
    CONSTRAINT chk_role CHECK (role IN ('GUEST', 'RECEPTIONIST', 'ADMIN'))
);

-- ============================================
-- Table: GUESTS
-- ============================================

CREATE TABLE GUESTS (
    guest_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    id_number VARCHAR2(20) UNIQUE,
    address VARCHAR2(200),
    CONSTRAINT fk_guest_user FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ============================================
-- Table: ROOM_TYPES
-- ============================================

CREATE TABLE ROOM_TYPES (
    type_id NUMBER PRIMARY KEY,
    type_name VARCHAR2(50) UNIQUE NOT NULL,
    description VARCHAR2(500),
    base_price NUMBER(10,2) NOT NULL,
    capacity NUMBER NOT NULL,
    amenities VARCHAR2(500),
    CONSTRAINT chk_base_price CHECK (base_price > 0),
    CONSTRAINT chk_capacity CHECK (capacity > 0)
);

-- ============================================
-- Table: ROOMS
-- ============================================

CREATE TABLE ROOMS (
    room_id NUMBER PRIMARY KEY,
    room_number VARCHAR2(10) UNIQUE NOT NULL,
    type_id NUMBER NOT NULL,
    floor NUMBER,
    status VARCHAR2(20) DEFAULT 'AVAILABLE',
    CONSTRAINT fk_room_type FOREIGN KEY (type_id) REFERENCES ROOM_TYPES(type_id) ON DELETE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'CLEANING'))
);

-- ============================================
-- Table: RESERVATIONS
-- ============================================

CREATE TABLE RESERVATIONS (
    reservation_id NUMBER PRIMARY KEY,
    guest_id NUMBER NOT NULL,
    room_id NUMBER NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price NUMBER(10,2) NOT NULL,
    status VARCHAR2(20) DEFAULT 'PENDING',
    booking_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_reservation_guest FOREIGN KEY (guest_id) REFERENCES GUESTS(guest_id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES ROOMS(room_id) ON DELETE CASCADE,
    CONSTRAINT chk_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')),
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date),
    CONSTRAINT chk_total_price CHECK (total_price >= 0)
);

-- ============================================
-- Table: PAYMENTS
-- ============================================

CREATE TABLE PAYMENTS (
    payment_id NUMBER PRIMARY KEY,
    reservation_id NUMBER NOT NULL,
    amount NUMBER(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    payment_method VARCHAR2(20),
    CONSTRAINT fk_payment_reservation FOREIGN KEY (reservation_id) REFERENCES RESERVATIONS(reservation_id) ON DELETE CASCADE,
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'CARD', 'ONLINE'))
);

-- ============================================
-- Table: SERVICES
-- ============================================

CREATE TABLE SERVICES (
    service_id NUMBER PRIMARY KEY,
    service_name VARCHAR2(100) NOT NULL,
    price NUMBER(10,2) NOT NULL,
    description VARCHAR2(500),
    CONSTRAINT chk_service_price CHECK (price >= 0)
);

-- ============================================
-- Table: RESERVATION_SERVICES
-- ============================================

CREATE TABLE RESERVATION_SERVICES (
    reservation_id NUMBER NOT NULL,
    service_id NUMBER NOT NULL,
    quantity NUMBER DEFAULT 1,
    PRIMARY KEY (reservation_id, service_id),
    CONSTRAINT fk_rs_reservation FOREIGN KEY (reservation_id) REFERENCES RESERVATIONS(reservation_id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_service FOREIGN KEY (service_id) REFERENCES SERVICES(service_id) ON DELETE CASCADE,
    CONSTRAINT chk_quantity CHECK (quantity > 0)
);

COMMIT;
