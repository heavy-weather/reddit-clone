package com.anthonyguidotti.forum;

import com.anthonyguidotti.forum.comment.*;
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
import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {
        DatabaseConfig.class,
        UserDataAccess.class,
        CommentDataAccess.class,
        ReactionDataAccess.class
})
@ExtendWith(SpringExtension.class)
public class CommentUnitTests {
    private static final UUID COMMENT_1_ID = UUID.randomUUID();
    private static final UUID COMMENT_2_ID = UUID.randomUUID();
    private static final UUID USER_1_ID = UUID.randomUUID();

    private final CommentDataAccess commentDataAccess;
    private final UserDataAccess userDataAccess;
    private final ReactionDataAccess reactionDataAccess;

    @Autowired
    public CommentUnitTests(
            UserDataAccess userDataAccess,
            CommentDataAccess commentDataAccess,
            ReactionDataAccess reactionDataAccess
    ) {
        this.userDataAccess = userDataAccess;
        this.commentDataAccess = commentDataAccess;
        this.reactionDataAccess = reactionDataAccess;
    }

    @BeforeEach
    public void before() {
        LocalDateTime commentTime = LocalDateTime.now().minusHours(4);

        UserModel userModel = new UserModel();
        userModel.setId(USER_1_ID);
        userModel.setDisplayName("asdf");
        userModel.setSub("asdf");
        userModel.setJoinDate(commentTime);
        userModel.setEmail("asdf");
        userModel.setGivenName("first");
        userModel.setFamilyName("last");
        userDataAccess.create(userModel);

        CommentModel commentModel = new CommentModel();
        commentModel.setId(COMMENT_1_ID);
        commentModel.setUserId(USER_1_ID);
        commentModel.setCreationDate(commentTime);
        commentModel.setUpdatedDate(commentTime);
        commentModel.setContent("post content");
        commentDataAccess.create(commentModel);

        LocalDateTime now = LocalDateTime.now();

        CommentModel commentModel1 = new CommentModel();
        commentModel1.setId(COMMENT_2_ID);
        commentModel1.setUserId(USER_1_ID);
        commentModel1.setCreationDate(now);
        commentModel1.setUpdatedDate(now);
        commentModel1.setParentId(commentModel.getId());
        commentModel1.setContent("response");
        commentDataAccess.create(commentModel1);

        ReactionModel reactionModel = new ReactionModel();
        reactionModel.setUserId(USER_1_ID);
        reactionModel.setCommentId(COMMENT_1_ID);
        reactionModel.setType(ReactionType.UP);
        reactionDataAccess.create(reactionModel);
    }

    @AfterEach
    public void after() {
        reactionDataAccess.drop();
        commentDataAccess.drop();
        userDataAccess.drop();
    }

    @Test
    public void readComment() {
        CommentModel commentModel = commentDataAccess.read(COMMENT_1_ID);
        Assertions.assertEquals(USER_1_ID, commentModel.getUserId());
    }

    @Test
    public void readReactions() {
        List<ReactionModel> reactionModels = reactionDataAccess.readByComment(COMMENT_1_ID);
        Assertions.assertEquals(1, reactionModels.size());
    }
}
