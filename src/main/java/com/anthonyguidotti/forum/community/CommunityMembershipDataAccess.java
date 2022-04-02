package com.anthonyguidotti.forum.community;

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
public class CommunityMembershipDataAccess {
    private static final String TABLE_NAME = "community_membership";
    private static final RowMapper<CommunityMembershipModel> rowMapper = (rs, rowNum) -> {
        CommunityMembershipModel membershipModel = new CommunityMembershipModel();
        membershipModel.setUserId(rs.getObject("user_id", UUID.class));
        membershipModel.setCommunityId(rs.getObject("community_id", UUID.class));
        membershipModel.setType(MembershipType.valueOf(rs.getString("type")));
        membershipModel.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime());

        return membershipModel;
    };
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommunityMembershipDataAccess(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(CommunityMembershipModel communityMembershipModel) {
        BeanPropertySqlParameterSource namedParameters = new BeanPropertySqlParameterSource(communityMembershipModel);
        namedParameters.registerSqlType("type", Types.DISTINCT);
        namedParameters.registerSqlType("joinDate", Types.TIMESTAMP);

        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (user_id, community_id, type, join_date) " +
                        "VALUES (:userId, :communityId, :type, :joinDate)",
                namedParameters
        );
    }

    public void drop() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME, new HashMap<>());
    }

    public List<CommunityMembershipModel> readByUser(UUID userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        return jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME + " WHERE user_id = :userId",
                namedParameters,
                rowMapper
        );
    }
}
