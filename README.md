# Travel Booking System

A Java-based travel booking system that allows users to book flights, hotels, and transfers with an intuitive user interface and secure payment options.

## Features
- **User Authentication**: Secure login and registration.
- **Booking Management**: Book flights, hotels, and transfer services in one place.
- **Payment Handling**: Integrated payment processing for each booking.
- **User-Friendly Interface**: Built with JavaFX for smooth navigation.
- **Data Persistence**: Utilizes Hibernate ORM and MySQL for robust data storage.

## Technologies
- Java, JavaFX, Hibernate, MySQL, Jakarta Persistence API

## Setup
1. Clone the repository: 
   ```bash
   git clone https://github.com/BryBrys/tbs.git
    ```

2. Configure the database (see src/main/resources/hibernate.cfg.xml).
Build and run with Maven:
    ```bash
    mvn clean install
    mvn javafx:run
    ```

## Usage
1. Log in or register as a user.
2. Search and book flights, hotels, and transfers.
3. Complete booking with integrated payment options.
