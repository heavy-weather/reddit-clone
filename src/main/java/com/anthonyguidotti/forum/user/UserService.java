package com.anthonyguidotti.forum.user;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {
    private final UserDataAccess userDataAccess;

    public UserService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public UserModel read(String sub) {
        return userDataAccess.read(sub);
    }

    public List<UserModel> get() {
        return userDataAccess.read();
    }
}
