package com.anthonyguidotti.forum.post;

import com.anthonyguidotti.forum.community.CommunityModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class PostDataAccess {
    private static final String TABLE_NAME = "post";
    private static final RowMapper<PostModel> rowMapper = (rs, rowNum) -> {
        PostModel postModel = new PostModel();
        postModel.setId(rs.getObject("id", UUID.class));
        postModel.setParentId(rs.getObject("parent_id", UUID.class));
        postModel.setCommunityId(rs.getObject("community_id", UUID.class));
        postModel.setCommentId(rs.getObject("comment_id", UUID.class));
        postModel.setUserId(rs.getObject("user_id", UUID.class));
        postModel.setTitle(rs.getString("title"));

        return postModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(PostModel postModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(postModel);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (id, parent_id, community_id, comment_id, user_id, title) " +
                        "VALUES (:id, :parentId, :communityId, :commentId, :userId, :title)",
                namedParameters
        );
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public List<PostModel> readByCommunity(UUID communityId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("communityId", communityId);

        return jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME + " WHERE community_id = :communityId",
                namedParameters,
                rowMapper
        );
    }
}
