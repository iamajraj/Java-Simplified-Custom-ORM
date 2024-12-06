package Repos;

import core.Repository;
import models.Student;

import java.sql.Connection;

public class StudentRepo extends Repository<Student> {
    public StudentRepo(Connection connection) {
        super(Student.class, connection);
    }
}
