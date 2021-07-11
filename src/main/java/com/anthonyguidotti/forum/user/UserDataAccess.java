package com.anthonyguidotti.forum.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDataAccess {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(UserModel userModel) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userModel);

        jdbcTemplate.update(
                "INSERT INTO user (sub, given_name, family_name, email) " +
                        "VALUES (:sub, :givenName, :familyName, :email)",
                namedParameters
        );
    }

    public List<UserModel> read() {
        return jdbcTemplate.query("SELECT * FROM user", new UserRowMapper());
    }

    public UserModel read(String sub) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("sub", sub);

        return jdbcTemplate.queryForObject(
                "SELECT * FROM user WHERE sub = :sub LIMIT 1",
                namedParameters,
                new UserRowMapper()
        );
    }

    public static class UserRowMapper implements RowMapper<UserModel> {
        @Override
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserModel userModel = new UserModel();
            userModel.setSub(rs.getString("sub"));
            userModel.setGivenName(rs.getString("given_name"));
            userModel.setFamilyName(rs.getString("family_name"));
            userModel.setEmail(rs.getString("email"));

            return userModel;
        }
    }
}
