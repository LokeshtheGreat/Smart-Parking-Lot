CREATE DATABASE IF NOT EXISTS parkease;

USE parkease;

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    number_plate VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type ENUM('BIKE', 'CAR', 'TRUCK') NOT NULL,
    status ENUM('PARKED', 'EXITED') DEFAULT 'PARKED'
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(50) UNIQUE NOT NULL,
    vehicle_id INT NOT NULL,
    slot_number INT NOT NULL,
    entry_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    exit_time DATETIME NULL,
    status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',

    FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(vehicle_id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL UNIQUE,
    parking_hours DOUBLE NOT NULL,
    chargeable_hours INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    was_capped BOOLEAN NOT NULL,
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ticket_id)
        REFERENCES tickets(ticket_id)
) ENGINE=InnoDB;