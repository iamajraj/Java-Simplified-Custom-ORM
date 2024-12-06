# Simplified ORM using JDBC and Java Reflection (MySQL)

This project is a simplified ORM (Object-Relational Mapping) system designed for **MySQL** using JDBC and Java Reflection. It allows interaction with a MySQL database through annotated model classes and provides basic operations like insert, get all, and count. I built this project for fun and practice while learning more about **Reflection** and **JDBC** in Java.

## Project Structure

```
src/
│
├── annotations/        # Contains annotations for database mapping
│   ├── Column.java     # Defines Column annotation for model fields
│   ├── Id.java         # Defines Id annotation for primary keys
│   └── Table.java      # Defines Table annotation for model classes
│
├── core/               # Core logic for database interaction
│   ├── DBManager.java  # Manages database connection and operations
│   └── Repository.java # Generic repository class with CRUD methods
│
├── models/             # Example model classes
│   ├── Student.java    # Example model for Student
│   └── User.java       # Example model for User
│
├── Repos/              # Repository classes for models
│   ├── StudentRepo.java # Repository for Student model
│   └── UserRepo.java    # Repository for User model
│
└── Main.java           # Entry point of the application
```

## Features

- **Annotations**: Use annotations like `@Table`, `@Column`, and `@Id` to map Java classes to MySQL database tables.
- **Generic Repository**: A generic `Repository` class that provides basic operations:
  - `insert(Object obj)`: Insert a record into the database.
  - `getAll()`: Retrieve all records from the table.
  - `count()`: Count the number of records in the table.
- **Model Classes**: Example models (`Student` and `User`) with basic fields and annotations.
- **Model Repositories**: Specific repositories (`StudentRepo`, `UserRepo`) that extend the generic `Repository` and perform operations for the corresponding model.

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/yourrepo.git
   ```

2. Add the MySQL JDBC driver to your project dependencies.

3. Create and configure the database connection in `DBManager` (located in `src/core`).

4. Define your model classes with appropriate annotations (`@Table`, `@Column`, `@Id`).

5. Use the repositories to interact with the database.

## Example Model (`User.java`)

```java
package models;

import annotations.Column;
import annotations.Id;
import annotations.Table;

@Table(name = "users")
public class User {
    @Column(name = "id")
    @Id
    public int id;
    
    @Column(name = "name")
    public String name;
    
    @Column(name = "age")
    public int age;

    public User(){}

    public User(int id, String name, int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

## Example Usage

1. **DB Manager**: Set up your database and perform migrations with `DBManager`:

   ```java
   DBManager dbManager = new DBManager();
   dbManager.migrateTables(new Class[]{User.class, Student.class}, dropIfExists: true, log: false);
   Connection connection = dbManager.getConnection();
   ```
2. **Repository**: Set up your repository for your model .eg `User`
   ```java
    public class UserRepo extends Repository<User> {
        public UserRepo(Connection connection){
            super(User.class, connection);
        }
    }
   ```
4. Interact with the `User` model through `UserRepo`:

   ```java
   UserRepo userRepo = new UserRepo(connection);
   
   // Insert a new user
   User newUser = new User("John", 101);
   boolean isInserted = userRepo.insert(newUser);
   if (isInserted) {
       System.out.println("models.User inserted!");
   } else {
       System.out.println("models.User not inserted.");
   }

   // Retrieve all users
   ArrayList<User> users = userRepo.getAll();
   for (User user : users) {
       System.out.println("ID: " + user.id + " Name: " + user.name + " Age: " + user.age);
   }

   // Count users
   int count = userRepo.count();
   System.out.println("COUNT: " + count);
   ```

## How It Works

- **Annotations**: 
  - `@Table`: Maps the model class to a specific table.
  - `@Id`: Marks the primary key field.
  - `@Column`: Maps the class fields to table columns.
  
- **Repository**:
  - The `Repository` class provides generic methods like `insert()`, `getAll()`, and `count()` using JDBC.
  - The `UserRepo` and `StudentRepo` classes extend `Repository` and pass the model class and database connection to the superclass.

## Contribution

Feel free to fork the repository and submit pull requests for any improvements or fixes!
