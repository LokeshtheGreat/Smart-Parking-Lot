# ParkEase – Smart Parking Lot Management System

ParkEase is a Java-based Smart Parking Lot Management System integrated with MySQL. It manages vehicle entry, parking slot allocation, ticket generation, vehicle exit, billing, and payment records.

## 1. Features

* Supports Bike, Car, and Truck parking.
* Manages a parking capacity of 20 slots.
* Allocates two consecutive slots for trucks.
* Prevents duplicate active vehicle registrations.
* Generates unique parking tickets.
* Calculates parking charges based on vehicle type and parking duration.
* Applies an 8-hour billing limit.
* Stores vehicle, ticket, and payment information in MySQL.
* Restores active parking records when the application restarts.

## 2. Technologies Used

* Java (JDK)
* MySQL Server
* MySQL Connector/J (JDBC)
* VS Code (recommended IDE)

## 3. Project Structure

```text
Smart_Parking_Lot/
│
├── src/
│   ├── Main.java
│   ├── ParkingLot.java
│   ├── ParkingDAO.java
│   ├── DatabaseConnection.java
│   ├── Vehicle.java
│   ├── Bike.java
│   ├── Car.java
│   ├── Truck.java
│   ├── VehicleType.java
│   ├── Ticket.java
│   └── Receipt.java
│
├── lib/
│   └── mysql-connector-j-26.7.0.jar
│
├── database/
│   └── parkease.sql
│
├── scripts/
│   ├── check-requirements.bat
│   └── run.bat
│
├── db.properties.example
├── .gitignore
└── README.md
```

## 4. Prerequisites

Before running ParkEase, ensure that the following are installed:

1. Java Development Kit (JDK).
2. MySQL Server 8.0 or a compatible version.
3. MySQL Workbench (optional, for database management).

The MySQL JDBC Connector is already included in the project's `lib` directory.

Verify Java installation:

```bash
java -version
javac -version
```

## 5. Installation and Setup

### Step 1: Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
```

Navigate into the project directory:

```bash
cd Smart_Parking_Lot
```

### Step 2: Start MySQL Server

Ensure that the MySQL Server service is installed and running.

On Windows, the service can be checked using the Services application.

### Step 3: Create the Database

The project includes an SQL setup script:

```text
database/parkease.sql
```

Using MySQL Workbench:

1. Open MySQL Workbench.
2. Connect to your local MySQL Server.
3. Open `database/parkease.sql`.
4. Execute the complete SQL script.

Alternatively, use the MySQL command line:

```bash
mysql -u root -p < database/parkease.sql
```

This creates the `parkease` database and the required tables:

* `vehicles`
* `tickets`
* `payments`

**Note:** Execute this script only during the initial setup. Do not execute it every time the application is launched.

### Step 4: Configure Database Credentials

The repository contains a template file:

```text
db.properties.example
```

Create a copy of this file and rename the copy to:

```text
db.properties
```

Open `db.properties` and configure your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/parkease
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your own MySQL password.

**Important:**

* Do not upload `db.properties` to GitHub.
* Each user must configure their own database credentials.
* The actual credentials are excluded from version control.

## 6. Running the Application

After completing the database setup and configuration:

1. Navigate to the project directory.
2. Open the `scripts` folder.
3. Double-click:

```text
run.bat
```

The script automatically:

* Checks the required environment.
* Verifies Java and the JDBC driver.
* Checks the database configuration file.
* Compiles the Java source files.
* Launches the ParkEase application.

Alternatively, compile and run manually from the project root:

```bash
javac -cp "lib/*" -d out src/*.java
```

```bash
java -cp "out;lib/*" Main
```

## 7. Database Functionality

ParkEase uses MySQL to maintain persistent application data.

| Operation           | Database action                             |
| ------------------- | ------------------------------------------- |
| Vehicle entry       | Stores vehicle and parking ticket details   |
| Vehicle exit        | Updates ticket and vehicle status           |
| Payment             | Stores parking duration and payment details |
| Application restart | Restores active parking records             |

The database runs locally on each user's machine. Each user maintains their own database instance.

## 8. Troubleshooting

| Problem                 | Possible solution                                         |
| ----------------------- | --------------------------------------------------------- |
| Java not found          | Install a JDK and configure the PATH environment variable |
| MySQL connection failed | Ensure MySQL Server is running                            |
| Access denied for user  | Verify the username and password in `db.properties`       |
| Database not found      | Execute `database/parkease.sql`                           |
| JDBC driver not found   | Verify that the Connector/J JAR exists in `lib`           |
| Compilation failed      | Check the Java source files and JDK installation          |

## 9. Important Notes

* The project is designed for Windows environments and uses batch scripts.
* Each user requires a local MySQL Server installation.
* The SQL setup script is executed only once during initial setup.
* Database credentials must remain local and must not be committed to GitHub.
* The included JDBC connector is used to establish communication between Java and MySQL.

## 10. Project Objective

The objective of ParkEase is to provide a simple parking management solution using Java Object-Oriented Programming concepts and MySQL database integration.

The system demonstrates vehicle management, slot allocation, exception handling, ticket generation, billing, and persistent data storage.
