# Library Management System Coding Assignment

## Description

You are tasked with building a simple Library Management System using Java Spring Boot. The system should allow librarians to manage books and users.

## Requirements

1. **User Management:**
   - Implement basic CRUD operations for users (Librarians and Members).
   - Users should have at least the following attributes: `id`, `name`, `email`, `role`.

2. **Book Management:**
   - Implement basic CRUD operations for books.
   - Each book should have attributes such as `id`, `title`, `author`, `ISBN`, `genre`, and `quantity`.

3. **Borrowing System:**
   - Users should be able to borrow books.
   - Users should be able to return books.
   - Implement a mechanism to track who borrowed which book and when.

4. **Validation:**
   - Implement appropriate validation to ensure data integrity. For example, ensure that a book cannot have a negative quantity, and users cannot borrow more books than available in stock.

5. **Security:**
   - Implement basic authentication and authorization using Spring Security.
   - Librarians should have access to all functionalities.
   - Members should only have access to borrowing and returning books functionalities.

6. **Documentation:**
   - Include proper documentation for your code (Javadoc comments).
   - Write a README file explaining how to run the application, any assumptions made, and any additional notes.

7. **Testing:**
   - Write unit tests to cover critical parts of your code, especially for validation and business logic.

## Additional Notes

- Use Spring Boot to set up the project.
- Use an in-memory database such as H2 for simplicity.
- Utilize RESTful APIs for handling CRUD operations.
- Keep the project structure clean and organized.
- Pay attention to naming conventions and code readability.
- Bonus points for implementing additional features like search functionality for books, pagination, etc.

## Submission

- fork his poject and make request o mee o a branch of labelled by your "firsname-lastname".
- Include any necessary instructions to run the application.
