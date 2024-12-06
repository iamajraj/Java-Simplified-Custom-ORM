package models;

import annotations.Column;
import annotations.Id;
import annotations.Table;

@Table(name = "students")
public class Student {
    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "student_class")
    public String studentClass;

    @Column(name = "student_name")
    public String studentName;

    @Column(name = "student_age")
    public int studentAge;

    public Student(){}

    public Student(int id, String studentClass, String studentName, int studentAge) {
        this.id = id;
        this.studentClass = studentClass;
        this.studentName = studentName;
        this.studentAge = studentAge;
    }

    public Student(String studentClass, String studentName, int studentAge) {
        this.studentClass = studentClass;
        this.studentName = studentName;
        this.studentAge = studentAge;
    }
}
