package com.anthonyguidotti.forum;

import com.anthonyguidotti.forum.community.*;
import com.anthonyguidotti.forum.user.UserDataAccess;
import com.anthonyguidotti.forum.user.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest( classes = {
        DatabaseConfig.class,
        UserDataAccess.class,
        CommunityDataAccess.class,
        CommunityMembershipDataAccess.class
})
public class CommunityUnitTests {
    private static final UUID COMMUNITY_1_ID = UUID.randomUUID();
    private static final String TOKEN_1 = "token1";
    private static final UUID USER_1_ID = UUID.randomUUID();

    private final UserDataAccess userDataAccess;
    private final CommunityDataAccess communityDataAccess;
    private final CommunityMembershipDataAccess communityMembershipDataAccess;

    @Autowired
    public CommunityUnitTests(
            UserDataAccess userDataAccess,
            CommunityDataAccess communityDataAccess,
            CommunityMembershipDataAccess communityMembershipDataAccess
    ) {
        this.userDataAccess = userDataAccess;
        this.communityDataAccess = communityDataAccess;
        this.communityMembershipDataAccess = communityMembershipDataAccess;
    }

    @BeforeEach
    public void before() {
        LocalDateTime now = LocalDateTime.now();

        UserModel userModel = new UserModel();
        userModel.setId(USER_1_ID);
        userModel.setDisplayName("asdf");
        userModel.setSub("asdf");
        userModel.setJoinDate(now);
        userModel.setEmail("asdf");
        userModel.setGivenName("first");
        userModel.setFamilyName("last");
        userDataAccess.create(userModel);

        CommunityModel communityModel = new CommunityModel();
        communityModel.setId(COMMUNITY_1_ID);
        communityModel.setCreationDate(now);
        communityModel.setName("name");
        communityModel.setToken(TOKEN_1);
        communityModel.setDescription("description");
        communityDataAccess.create(communityModel);

        CommunityMembershipModel communityMembershipModel = new CommunityMembershipModel();
        communityMembershipModel.setUserId(USER_1_ID);
        communityMembershipModel.setCommunityId(COMMUNITY_1_ID);
        communityMembershipModel.setJoinDate(now);
        communityMembershipModel.setType(MembershipType.USER);
        communityMembershipDataAccess.create(communityMembershipModel);
    }

    @AfterEach
    public void after() {
        communityMembershipDataAccess.drop();
        communityDataAccess.drop();
        userDataAccess.drop();
    }

    @Test
    public void readByToken() {
        CommunityModel communityModel = communityDataAccess.readByToken(TOKEN_1);
        Assertions.assertNotNull(communityModel);
    }
}
