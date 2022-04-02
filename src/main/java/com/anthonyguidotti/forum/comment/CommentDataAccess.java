package com.anthonyguidotti.forum.comment;

import com.anthonyguidotti.forum.user.UserModel;
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
public class CommentDataAccess {
    private static final String TABLE_NAME = "comment";
    private static final RowMapper<CommentModel> rowMapper = (rs, rowNum) -> {
        CommentModel commentModel = new CommentModel();
        commentModel.setId(rs.getObject("id", UUID.class));
        commentModel.setParentId(rs.getObject("parent_id", UUID.class));
        commentModel.setUserId(rs.getObject("user_id", UUID.class));
        commentModel.setContent(rs.getString("content"));
        commentModel.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        commentModel.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());

        return commentModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(CommentModel commentModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(commentModel);
        namedParameters.registerSqlType("creationDate", Types.TIMESTAMP);
        namedParameters.registerSqlType("updatedDate", Types.TIMESTAMP);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (id, parent_id, user_id, content, creation_date, updated_date) " +
                        "VALUES (:id, :parentId, :userId, :content, :creationDate, :updatedDate)",
                namedParameters
        );
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public CommentModel read(UUID id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);

        return jdbcTemplate.queryForObject(
                "SELECT * FROM " + TABLE_NAME + " WHERE id = :id LIMIT 1",
                namedParameters,
                rowMapper
        );
    }
}
