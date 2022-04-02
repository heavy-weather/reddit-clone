package com.anthonyguidotti.forum;

import com.anthonyguidotti.forum.comment.CommentDataAccess;
import com.anthonyguidotti.forum.comment.CommentModel;
import com.anthonyguidotti.forum.community.CommunityDataAccess;
import com.anthonyguidotti.forum.community.CommunityModel;
import com.anthonyguidotti.forum.post.PostDataAccess;
import com.anthonyguidotti.forum.post.PostModel;
import com.anthonyguidotti.forum.user.UserDataAccess;
import com.anthonyguidotti.forum.user.UserModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {
        DatabaseConfig.class,
        UserDataAccess.class,
        CommunityDataAccess.class,
        CommentDataAccess.class,
        PostDataAccess.class
})
@ExtendWith(SpringExtension.class)
public class PostUnitTests {
    private static final UUID POST_1_ID = UUID.randomUUID();
    private static final String TITLE_1 = "title1";
    private static final UUID USER_1_ID = UUID.randomUUID();
    private static final UUID COMMUNITY_1_ID = UUID.randomUUID();
    private static final String TOKEN_1 = "token1";
    private static final UUID COMMENT_1_ID = UUID.randomUUID();

    private final UserDataAccess userDataAccess;
    private final CommunityDataAccess communityDataAccess;
    private final CommentDataAccess commentDataAccess;
    private final PostDataAccess postDataAccess;

    @Autowired
    public PostUnitTests(
            UserDataAccess userDataAccess,
            CommunityDataAccess communityDataAccess,
            CommentDataAccess commentDataAccess,
            PostDataAccess postDataAccess
    ) {
        this.userDataAccess = userDataAccess;
        this.communityDataAccess = communityDataAccess;
        this.commentDataAccess = commentDataAccess;
        this.postDataAccess = postDataAccess;
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

        CommentModel commentModel = new CommentModel();
        commentModel.setId(COMMENT_1_ID);
        commentModel.setUserId(USER_1_ID);
        commentModel.setCreationDate(now);
        commentModel.setUpdatedDate(now);
        commentModel.setContent("post content");
        commentDataAccess.create(commentModel);

        PostModel postModel = new PostModel();
        postModel.setId(POST_1_ID);
        postModel.setUserId(USER_1_ID);
        postModel.setCommunityId(COMMUNITY_1_ID);
        postModel.setTitle(TITLE_1);
        postModel.setCommentId(COMMENT_1_ID);
        postDataAccess.create(postModel);
    }

    @AfterEach
    public void after() {
        postDataAccess.drop();
        commentDataAccess.drop();
        communityDataAccess.drop();
        userDataAccess.drop();
    }

    @Test
    public void readByCommunity() {
        List<PostModel> postModels = postDataAccess.readByCommunity(COMMUNITY_1_ID);
        Assertions.assertEquals(1, postModels.size());
    }
}
