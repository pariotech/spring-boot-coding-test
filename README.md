
# Library Management System

## Description

This is a Library Management System built with Spring Boot. It provides a set of RESTful APIs for managing books, borrows, librarians, and members. The system allows members to borrow and return books, while librarians can manage the books in the library and track borrowed books.

## Features

- **Book Management**: The system provides APIs for managing books in the library. Librarians can add books to the library, get details of a book, list all books, search for books by title, author, or genre, update the details of a book, get a list of books managed by a specific librarian, and remove books from the library.

- **Borrow Management**: Members can borrow and return books through the system. The system keeps track of all borrowed books, including who borrowed them and when they were borrowed. Librarians can view a list of all borrowed books.

- **Librarian Management**: The system provides APIs for managing librarians. You can get the details of a librarian, list all librarians, remove a librarian, and update the details of a librarian.

- **Member Management**: The system provides APIs for managing members. You can get the details of a member, list all members, update the details of a member, and remove a member.

- **Authentication**: The system provides APIs for user registration and sign-in.

## Setup

The application uses an in-memory H2 database for simplicity. The configuration for the database is as follows:

```properties
spring.application.name=LibraryManagementSystem
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:lmstestdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

To start the application, run the following command in the root directory of the project:

```./mvnw spring-boot:run```

This will start the application on port 8080.

 ## Usage
The application provides several RESTful APIs for managing the library. Here are some examples:


## API Documentation

Detailed API documentation is available on Postman. You can access it here: https://documenter.getpostman.com/view/25585012/2sA2xnxph1

# Testing
The application includes unit tests for the service layer. These tests cover various scenarios and edge cases to ensure the application behaves as expected. To run the tests, use the following command:

```./mvnw test```

# Contributing
Contributions are welcome! Please feel free to submit a pull request. If you find any bugs or have suggestions for improvements, please open an issue.

# License
This project is licensed under the terms of the MIT license.