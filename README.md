# Simple ORM framework <img src="asset/framework.png" alt="drawing" width="27px"/>

### A simple object–relational mapping framework

This is a simple framework that creates a relational database system and 
maps a given classes inside packages into database tables.

#### The mapping of classes to database tables is as follows,

- The @Table annotation must be used over the class name to create the database table. The name of the database table is the class name.
- The @PrimaryKey annotation must be used over the one field in the class to create the table's primary key.
- The column names of the database tables are matched to the field names of the class.
- Database tables can be created with both instance and static variables.

#### Supported data types in the framework,
- String -> VARCHAR(256)
- int -> INT
- Integer -> INT
- double -> DOUBLE(10,2)
- Double -> DOUBLE(10,2)
- BigDecimal -> DECIMAL(10,2)
- Date -> DATE
- Time -> TIME

The goal of this project was to improve the knowledge of java annotations and java reflection API.

## Used Technologies

- Java SE 11
- Apache Maven 3.8.6
- Added dependencies to pom.xml
    - mysql-connector-j 8.0.31
    - junit-jupiter-api 5.8.2
    - junit-jupiter-engine 5.8.2

#### Used Integrated Development Environment
- IntelliJ IDEA

## How to use ?
This project can be used by cloning the
project to your local computer.

#### Clone this repository
1. Clone the project using `git clone https://github.com/PubuduJ/simple-orm-framework.git` terminal command.
2. Open the `pom.xml` file from **IntelliJ IDEA**, make sure to open this as a project.
3. To install this framework to local jar repository execute `mvn install` command.
4. Once the framework is installed in your local JAR repository, it can be used with any Java project.
5. Inorder to use the framework call `InitializeDB.initialize()` method by passing the correct arguments.
6. `InitializeDB.initialize()` method requires host, port, database name, username, password and package list to scan as String arguments.

## Version
v1.0.0

## License
Copyright &copy; 2022 [Pubudu Janith](https://www.linkedin.com/in/pubudujanith94/). All Rights Reserved.<br>
This project is licensed under the [MIT license](LICENSE.txt).