package Repos;

import core.Repository;
import models.User;

import java.sql.*;

public class UserRepo extends Repository<User> {
    public UserRepo(Connection connection){
        super(User.class, connection);
    }
}
