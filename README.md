# ✈️ Travel Booking System

A **Java-based desktop application** for managing travel bookings, with separate interfaces for users and administrators. Built using Java Swing, MySQL, and various external libraries via JAR files.

---

## 🚀 Features

### 👤 User Features
- User registration & authentication
- Book flights, hotels, and transport
- View booking history
- Profile and password management
- Payment processing integration
- Submit service reviews

### 🛠️ Admin Features
- Manage flights, hotels, and transportation
- View and manage user accounts and bookings
- Generate PDF reports
- Monitor system activity (audit logs)
- View reviews, stats, and logs

---

## 🧰 Technologies & Libraries

| Category        | Tools/Libraries Used                          |
|----------------|-----------------------------------------------|
| **Language**    | Java 17                                       |
| **GUI**         | Swing, AWT                                    |
| **Database**    | MySQL 8.0, JDBC                               |
| **Security**    | jBCrypt (Password hashing)                   |
| **Date Picker** | JCalendar 1.4                                 |
| **PDF Reports** | iText 7.1.14 (via JAR, not Maven)             |
| **Look & Feel** | FlatLaf (Modern UI Theme)                     |
| **Testing**     | JUnit 5 (basic test support)                  |

> ⚠️ All libraries are included via JARs in the `/lib` folder. No Maven setup is required.

---

## 💾 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Kingh66/HexSoftwares_Travel_Booking_System.git
```

### 2. Add JAR Dependencies
Place the following `.jar` files in a `lib/` folder and **add them to your project via NetBeans**:
- `mysql-connector-java-8.0.xx.jar`
- `jcalendar-1.4.jar`
- `flatlaf-<version>.jar`
- `jbcrypt-0.4.jar`
- `kernel.jar`, `layout.jar`, and any required iText 7 JARs

### 3. Database Setup

#### 📌 SQL Schema
Run the following in MySQL:

```sql
-- Create Database
CREATE DATABASE IF NOT EXISTS travel_booking;
USE travel_booking;

-- Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    profile_picture_path VARCHAR(255),
    INDEX idx_role (role)
);

-- Flights Table
CREATE TABLE flights (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    airline VARCHAR(50) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure DATETIME NOT NULL,
    arrival DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    seats_available INT NOT NULL,
    status ENUM('scheduled', 'delayed', 'canceled', 'completed') DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_origin_destination (origin, destination),
    INDEX idx_departure (departure)
);

-- Hotels Table
CREATE TABLE hotels (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description TEXT,
    price_per_night DECIMAL(10,2) NOT NULL,
    rooms_available INT NOT NULL,
    amenities VARCHAR(255),
    rating DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_location (location),
    INDEX idx_price (price_per_night)
);

-- Transportation Table
CREATE TABLE transport (
    transport_id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('car', 'bus', 'train') NOT NULL,
    company VARCHAR(50) NOT NULL,
    pickup_location VARCHAR(100) NOT NULL,
    dropoff_location VARCHAR(100) NOT NULL,
    departure DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_pickup_dropoff (pickup_location, dropoff_location)
);

-- Bookings Table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    service_type ENUM('flight', 'hotel', 'transport') NOT NULL,
    service_id INT NOT NULL,
    booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('confirmed', 'cancelled', 'completed') DEFAULT 'confirmed',
    total_price DECIMAL(10,2) NOT NULL,
    payment_status ENUM('paid', 'pending', 'refunded') DEFAULT 'pending',
    check_in DATE,
    check_out DATE,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_service (service_type, service_id),
    INDEX idx_booking_date (booking_date)
);

-- Payments Table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('credit_card', 'debit_card', 'paypal', 'bank_transfer') NOT NULL,
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('success', 'failed', 'pending') DEFAULT 'pending',
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_transaction (transaction_id)
);

-- Reviews Table
CREATE TABLE reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    service_type ENUM('flight', 'hotel', 'transport') NOT NULL,
    service_id INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_service_reviews (service_type, service_id)
);

-- Reports Table
CREATE TABLE reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    report_type ENUM('bookings', 'users', 'financial', 'services') NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parameters TEXT,
    file_path VARCHAR(255),
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Audit Log Table
CREATE TABLE audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action_type VARCHAR(50) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    INDEX idx_action_type (action_type)
);
```

#### 🔁 Triggers
```sql
DELIMITER //

CREATE TRIGGER after_flight_booking
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.service_type = 'flight' THEN
        UPDATE flights
        SET seats_available = seats_available - 1
        WHERE flight_id = NEW.service_id;
    END IF;
END//

CREATE TRIGGER after_hotel_booking
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.service_type = 'hotel' THEN
        UPDATE hotels
        SET rooms_available = rooms_available - 1
        WHERE hotel_id = NEW.service_id;
    END IF;
END//

CREATE TRIGGER after_transport_booking
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.service_type = 'transport' THEN
        UPDATE transport
        SET available = IF(capacity > 0, true, false),
            capacity = capacity - 1
        WHERE transport_id = NEW.service_id;
    END IF;
END//

DELIMITER ;
```

---

## 🔐 Default Admin Login

```text
Username: admin
Password: Sizwe@21
```

---

## 💡 Usage Highlights

- Register → Book Flights/Hotels/Transport → Pay → Track Bookings  
- Admin can monitor activity, generate reports, manage data

---

## 🤝 Contributing

1. Fork the repo  
2. Create a branch: `git checkout -b feature/your-feature`  
3. Commit your changes: `git commit -m "Add your feature"`  
4. Push: `git push origin feature/your-feature`  
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE)

---


> Add screenshots of the login, booking flow, and dashboards here.

---
