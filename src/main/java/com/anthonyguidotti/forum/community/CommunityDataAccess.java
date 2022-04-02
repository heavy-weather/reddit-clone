package com.anthonyguidotti.forum.community;

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
public class CommunityDataAccess {
    private static final String TABLE_NAME = "community";
    private static final RowMapper<CommunityModel> rowMapper = (rs, rowNum) -> {
        CommunityModel communityModel = new CommunityModel();
        communityModel.setId(rs.getObject("id", UUID.class));
        communityModel.setName(rs.getString("name"));
        communityModel.setToken(rs.getString("token"));
        communityModel.setDescription(rs.getString("description"));
        communityModel.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());

        return communityModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommunityDataAccess(
            NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(CommunityModel communityModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(communityModel);
        namedParameters.registerSqlType("creationDate", Types.TIMESTAMP);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (id, name, token, description, creation_date) " +
                        "VALUES (:id, :name, :token, :description, :creationDate)",
                namedParameters
        );
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public CommunityModel readByToken(String token) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("token", token);

        return jdbcTemplate.queryForObject(
                "SELECT * FROM " + TABLE_NAME + " WHERE token = :token",
                namedParameters,
                rowMapper
        );
    }
}
