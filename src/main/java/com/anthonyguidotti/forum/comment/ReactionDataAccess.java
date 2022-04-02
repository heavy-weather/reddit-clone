package com.anthonyguidotti.forum.comment;

import com.anthonyguidotti.forum.community.CommunityMembershipModel;
import com.anthonyguidotti.forum.community.CommunityModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class ReactionDataAccess {
    private static final String TABLE_NAME = "reaction";
    private static final RowMapper<ReactionModel> rowMapper = (rs, rowNum) -> {
        ReactionModel reactionModel = new ReactionModel();
        reactionModel.setUserId(rs.getObject("user_id", UUID.class));
        reactionModel.setCommentId(rs.getObject("comment_id", UUID.class));
        reactionModel.setType(ReactionType.valueOf(rs.getString("type")));

        return reactionModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReactionDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(ReactionModel reactionModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(reactionModel);
        namedParameters.registerSqlType("type", Types.DISTINCT);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (user_id, comment_id, type) " +
                        "VALUES (:userId, :commentId, :type)",
                namedParameters
        );
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public List<ReactionModel> readByComment(UUID commentId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("commentId", commentId);

        return jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME + " WHERE comment_id = :commentId",
                namedParameters,
                rowMapper
        );
    }
}
