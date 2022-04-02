package com.anthonyguidotti.forum;

import com.anthonyguidotti.forum.user.UserDataAccess;
import com.anthonyguidotti.forum.user.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

@ContextConfiguration(classes = {
        DatabaseConfig.class,
        UserDataAccess.class
})
@ExtendWith(SpringExtension.class)
public class UserUnitTests {
    private static final String SUB = "1a2a3a4a5a6a7a8a9a0a1a2a3";
    private static final LocalDateTime JOIN_DATE = LocalDateTime.now();

    private final UserDataAccess userDataAccess;

    @Autowired
    public UserUnitTests(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    @BeforeEach
    public void before() {
        UserModel userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setEmail("asdf");
        userModel.setGivenName("first");
        userModel.setFamilyName("last");
        userModel.setSub(SUB);
        userModel.setDisplayName("display");
        userModel.setJoinDate(JOIN_DATE);

        userDataAccess.create(userModel);
    }

    @AfterEach
    public void after() {
        userDataAccess.drop();
    }

    @Test
    public void readUser() {
        UserModel userModel = userDataAccess.read(SUB);
        Assertions.assertNotNull(userModel);
    }
}
