package com.anthonyguidotti.forum.user;

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
public class UserDataAccess {
    private static final String TABLE_NAME = "f_user";
    private static final RowMapper<UserModel> rowMapper = (rs, rowNum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getObject("id", UUID.class));
        userModel.setSub(rs.getString("sub"));
        userModel.setGivenName(rs.getString("given_name"));
        userModel.setFamilyName(rs.getString("family_name"));
        userModel.setEmail(rs.getString("email"));
        userModel.setDisplayName(rs.getString("display_name"));
        userModel.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime());

        return userModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(UserModel userModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userModel);
        namedParameters.registerSqlType("joinDate", Types.TIMESTAMP);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (id, sub, given_name, family_name, email, display_name, join_date) " +
                        "VALUES (:id, :sub, :givenName, :familyName, :email, :displayName, :joinDate)",
                namedParameters
        );
    }

    public List<UserModel> read() {
        return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME, rowMapper);
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public UserModel read(String sub) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("sub", sub);

        return jdbcTemplate.queryForObject(
                "SELECT * FROM " + TABLE_NAME + " WHERE sub = :sub LIMIT 1",
                namedParameters,
                rowMapper
        );
    }
}
