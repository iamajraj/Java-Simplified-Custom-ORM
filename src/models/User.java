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
