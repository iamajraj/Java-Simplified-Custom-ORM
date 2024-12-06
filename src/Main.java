import Repos.StudentRepo;
import Repos.UserRepo;
import core.DBManager;
import models.Student;
import models.User;

import java.sql.Connection;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        dbManager.migrateTables(new Class[]{User.class, Student.class}, true, false);

        Connection connection = dbManager.getConnection();

        testUser(connection);
        testStudent(connection);
    }

    public static void testUser(Connection connection){
        UserRepo userRepo = new UserRepo(connection);
        User newUser = new User("Raj", 101);
        boolean isInserted = userRepo.insert(newUser);
        if(isInserted){
            System.out.println("models.User inserted!");
        }else{
            System.out.println("models.User not inserted.");
        }
        ArrayList<User> users = userRepo.getAll();
        for(User user: users){
            System.out.println("ID: " + user.id + " Name: " + user.name + " Age: " + user.age);
        }
        int count = userRepo.count();
        System.out.println("COUNT: " + count);
    }

    public static void testStudent(Connection connection){
        StudentRepo studentRepo = new StudentRepo(connection);
        Student newStudent = new Student("Raj", "Computer Science (Unofficial)", 101);
        boolean isInserted = studentRepo.insert(newStudent);
        if(isInserted){
            System.out.println("models.Student data inserted!");
        }else{
            System.out.println("models.Student data not inserted.");
        }
        ArrayList<Student> students = studentRepo.getAll();
        for(Student student: students){
            System.out.println("ID: " + student.id + " Name: " + student.studentName + " Class: " + student.studentClass + " Age: " + student.studentAge);
        }
        int count = studentRepo.count();
        System.out.println("COUNT: " + count);
    }
}
