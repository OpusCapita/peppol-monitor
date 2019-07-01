package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.TransmissionStatisticsDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class StatisticsService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // from and to are dates, ex: 2019-05-30
    public List<TransmissionStatisticsDto> get(String from, String to) {
        String query = String.format("select doc_type, direction, count(*) as files from raw_stats where tstamp >= '%s' and tstamp < '%s' group by direction, doc_type order by direction, files desc, doc_type", from, to);
        return jdbcTemplate.query(query, new StatsRowMapper());
    }

    public class StatsRowMapper implements RowMapper {
        @Override
        public Object mapRow(@NotNull ResultSet resultSet, int rowNum) throws SQLException {
            TransmissionStatisticsDto dto = new TransmissionStatisticsDto();
            dto.setDoc_type(resultSet.getString("doc_type"));
            dto.setDirection(resultSet.getString("direction"));
            dto.setFiles(resultSet.getInt("files"));
            return dto;
        }
    }
}
