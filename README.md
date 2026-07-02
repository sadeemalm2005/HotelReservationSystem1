<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:3E2723,50:6D4C41,100:C9A227&height=230&section=header&text=Hotel%20Reservation%20System&fontSize=42&fontColor=ffffff&animation=fadeIn&fontAlignY=38&desc=Java%20•%20JavaFX%20•%20Oracle%20Database%20•%20MVC%20Architecture&descAlignY=58"/>

# 🏨 Hotel Reservation System

### Role-Based Hotel Reservation & Management System

A desktop application developed using **Java**, **JavaFX**, and **Oracle Database** to simplify hotel operations through an intuitive reservation and management system.

Designed following the **Model–View–Controller (MVC)** architecture, the system provides dedicated interfaces for **Administrators**, **Receptionists**, and **Guests**, ensuring an efficient, secure, and user-friendly hotel management experience.

</div>

---

# 📑 Table of Contents

- [📖 Project Overview](#-project-overview)
- [✨ System Highlights](#-system-highlights)
- [🚀 Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [🏗️ System Architecture](#️-system-architecture)
- [📐 UML Class Diagram](#-uml-class-diagram)
- [📷 User Interface Preview](#-user-interface-preview)
- [🗄️ Database](#️-database)
- [🚀 Getting Started](#-getting-started)
- [📂 Project Structure](#-project-structure)
- [💡 Object-Oriented Programming Concepts](#-object-oriented-programming-concepts)
- [🎯 Future Improvements](#-future-improvements)
- [👥 Team Project](#-team-project)
- [👩‍💻 Author](#-author)

---

# 📖 Project Overview

The **Hotel Reservation System** is a desktop-based application developed as part of a **Software Engineering** course.

The system simplifies hotel operations by allowing administrators, receptionists, and guests to interact through dedicated dashboards while maintaining centralized data management using **Oracle Database**.

Developed with **JavaFX**, the application delivers a modern graphical user interface while following the **MVC (Model–View–Controller)** architectural pattern to achieve clean code organization, maintainability, and scalability.

---

# ✨ System Highlights

- 🔐 Secure Role-Based Authentication
- 🏨 Hotel Room Management
- 📅 Reservation Management
- 👥 User Management
- 👤 Guest Reservation Portal
- 🛎️ Receptionist Dashboard
- 👨‍💼 Administrator Dashboard
- 📊 Reports & Statistics
- 🛏️ Room Availability Tracking
- 🗄️ Oracle Database Integration
- 🧩 MVC Architecture
- 🎨 Modern JavaFX User Interface

---

# 🚀 Features

## 👨‍💼 Administrator

- Secure authentication
- Manage users
- Manage room types
- Manage hotel rooms
- Manage hotel services
- Generate reports
- Monitor reservations
- Access administrative dashboard

---

## 👨‍💻 Receptionist

- Guest Check-In
- Guest Check-Out
- Reservation management
- Room availability monitoring
- Daily hotel operations
- Customer assistance

---

## 👤 Guest

- Search available rooms
- Book hotel rooms
- View reservation history
- Manage reservations
- Cancel reservations
- Personal dashboard

---

# 🛠️ Tech Stack

| Technology | Purpose |
|------------|----------|
| ☕ Java | Core application development |
| 🎨 JavaFX | Desktop graphical user interface |
| 📄 FXML | User interface layout |
| 🗄️ Oracle Database 21c XE | Database management |
| 🔌 JDBC | Database connectivity |
| 🧩 MVC | Software architecture |
| 🛠️ Apache Ant | Build automation |
| 💻 NetBeans IDE | Development environment |
| 📐 draw.io | UML design |

---

# 🏗️ System Architecture

The application follows the **MVC (Model–View–Controller)** architecture, separating the user interface, business logic, and database operations.

```text
                User
                  │
                  ▼
         JavaFX User Interface
                  │
                  ▼
             Controllers
                  │
        ┌─────────┴─────────┐
        ▼                   ▼
   Business Logic       Validation
                  │
                  ▼
                 JDBC
                  │
                  ▼
         Oracle Database
```

### Architecture Benefits

- Clean separation of concerns
- Better maintainability
- Easier scalability
- Improved code organization
- Reusable components

---
# 📐 UML Class Diagram

The system was designed following **Object-Oriented Programming (OOP)** principles.

The UML Class Diagram illustrates the relationships between the system classes and reflects the overall software architecture designed during the analysis and implementation phases.

<p align="center">
<img src="docs/UML-Class-Diagram.png" width="950"/>
</p>

---

# 📷 User Interface Preview

The following screenshots demonstrate the main interfaces of the application.

---

## 🏠 Home Page

<p align="center">
<img src="screenshots/01-Home-Page.jpg" width="900"/>
</p>

> The application's landing page provides a welcoming interface and allows users to navigate directly to the login screen.

---

## 🔐 Login

<p align="center">
<img src="screenshots/02-Login.jpg" width="900"/>
</p>

> Secure authentication system supporting three different user roles:
>
> - Administrator
> - Receptionist
> - Guest

---

## 👨‍💼 Administrator Dashboard

<p align="center">
<img src="screenshots/03-Admin-Dashboard.jpg" width="900"/>
</p>

> Provides administrators with an overview of hotel operations including reservations, guests, rooms, and system statistics.

---

## 👥 User Management

<p align="center">
<img src="screenshots/04-User-Management.jpg" width="900"/>
</p>

> Enables administrators to create, update, search, and manage system users through full CRUD operations.

---

## 👤 Guest Dashboard

<p align="center">
<img src="screenshots/05-Guest-Dashboard.jpg" width="900"/>
</p>

> Guests can access their personal dashboard to search rooms, create reservations, and manage existing bookings.

---

## 🛏️ Room Search

<p align="center">
<img src="screenshots/06-Room-Search.jpg" width="900"/>
</p>

> Displays available rooms based on user preferences while providing pricing and room details before booking.

---

## 📅 My Reservations

<p align="center">
<img src="screenshots/07-My-Reservations.jpg" width="900"/>
</p>

> Allows guests to review reservation history, view booking details, and cancel reservations when applicable.

---

## 👨‍💻 Receptionist Dashboard

<p align="center">
<img src="screenshots/08-Receptionist-Dashboard.jpg" width="900"/>
</p>

> Receptionists can efficiently manage daily hotel operations including reservations, guest services, and room availability.

---

## 🔑 Check-In / Check-Out

<p align="center">
<img src="screenshots/09-CheckIn-CheckOut.jpg" width="900"/>
</p>

> Streamlines the guest check-in and check-out process while automatically updating reservation and room status.

---
# 🗄️ Database

The application uses **Oracle Database 21c XE** as its relational database management system.

The database is designed to ensure data integrity, maintain relationships between entities, and efficiently manage hotel operations.

---

## Main Database Entities

| Entity | Description |
|---------|-------------|
| USERS | Stores login credentials and user roles |
| GUESTS | Guest personal information |
| ADMINS | Administrator information |
| RECEPTIONISTS | Receptionist information |
| ROOM_TYPES | Room categories and pricing |
| ROOMS | Hotel room details |
| RESERVATIONS | Reservation records |
| PAYMENTS | Payment transactions |
| SERVICES | Hotel services |
| RESERVATION_SERVICES | Relationship between reservations and services |

---

## Database Features

- Relational database design
- Primary & Foreign Key constraints
- Data integrity enforcement
- Normalized database schema
- Oracle Sequences for ID generation
- JDBC connectivity
- Sample dataset for testing

---

# 🚀 Getting Started

Follow the steps below to run the project locally.

---

## Prerequisites

Before running the application, ensure the following software is installed:

- Java JDK 17 (or later)
- JavaFX SDK
- Oracle Database 21c XE
- NetBeans IDE
- Apache Ant

---

## Clone the Repository

```bash
git clone https://github.com/sadeemalm2005/HotelReservationSystem.git
```

---

## Database Setup

Run the following SQL scripts in Oracle Database:

```text
database_schema.sql
sample_data.sql
```

These scripts will:

- Create all database tables
- Create sequences
- Create relationships
- Insert sample data for testing

---

## Configure Database Connection

Update the database credentials inside:

```text
src/database/DatabaseConnection.java
```

Example:

```java
private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
private static final String USERNAME = "YOUR_USERNAME";
private static final String PASSWORD = "YOUR_PASSWORD";
```

---

## Run the Application

Open the project in **NetBeans IDE** and run:

```text
HotelReservationSystem1.java
```

Alternatively, build the project using Apache Ant.

---

# 📂 Project Structure

```text
HotelReservationSystem
│
├── docs/
│   ├── UML-Class-Diagram.drawio
│   ├── UML-Class-Diagram.png
│   ├── DATABASE_SETUP_GUIDE.md
│   ├── QUICK_START_GUIDE.md
│   └── REQUIREMENTS_SPECIFICATION.md
│
├── screenshots/
│   ├── 01-Home-Page.jpg
│   ├── 02-Login.jpg
│   ├── 03-Admin-Dashboard.jpg
│   ├── 04-User-Management.jpg
│   ├── 05-Guest-Dashboard.jpg
│   ├── 06-Room-Search.jpg
│   ├── 07-My-Reservations.jpg
│   ├── 08-Receptionist-Dashboard.jpg
│   └── 09-CheckIn-CheckOut.jpg
│
├── src/
│   ├── controllers/
│   ├── database/
│   ├── hotelreservationsystem1/
│   │   ├── exceptions/
│   │   └── models/
│   └── views/
│
├── database_schema.sql
├── sample_data.sql
├── build.xml
├── manifest.mf
├── run.bat
└── README.md
```

---

# 💡 Object-Oriented Programming Concepts

This project applies several Object-Oriented Programming principles, including:

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Exception Handling
- Class Relationships
- Separation of Concerns (MVC)

These concepts contribute to creating a scalable, maintainable, and modular software architecture.

---

# 🎯 Future Improvements

Potential enhancements for future versions include:

- Online payment gateway integration
- Email notifications
- Password encryption using BCrypt
- QR Code reservation confirmation
- Customer reviews and ratings
- Room image gallery
- Dashboard analytics
- Multi-language support
- Dark Mode
- Mobile application
- Cloud database deployment
- REST API integration

---
# 👥 Team Project

This project was collaboratively developed as a university team project.

---

<div align="center">

### ⭐ Thank You for Visiting This Repository!

If you found this project helpful or interesting, consider giving it a ⭐ on GitHub.

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:C9A227,50:8D6E63,100:C9A227&height=120&section=footer"/>

</div>
