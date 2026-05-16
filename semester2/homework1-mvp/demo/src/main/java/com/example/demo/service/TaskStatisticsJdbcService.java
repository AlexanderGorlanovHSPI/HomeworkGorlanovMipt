package com.example.demo.service;

import com.example.demo.dto.PriorityStatsDto;
import com.example.demo.model.Priority;

import io.micrometer.core.instrument.MultiGauge.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsJdbcService {
    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityStatsDto> getTaskCountByPriority() {
        String sql = """
                select priority, count(*) as cnt
                from tasks
                group by priority
                order by priority
                 """;
        RowMapper<PriorityStatsDto> rowMapper = new RowMapper<>() {
            @Override
            public PriorityStatsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Priority priority = Priority.valueOf(rs.getString("priority"));
                long count = rs.getLong("cnt");
                return new PriorityStatsDto(priority, count);
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
