package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.TransmissionStatisticsDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public File get() {
        Map<String, String> result = datesForPreviousMonth();
        List<TransmissionStatisticsDto> stats = get(result.get("from"), result.get("to"));

        String period = result.get("period");
        String path = String.format("/tmp/PEPPOL-OpusCapitaAP-%s-Statistics.csv", period);

        try (PrintWriter writer = new PrintWriter(new File(path))) {

            StringBuilder sb = new StringBuilder();
            sb.append("period,doc_type,direction,files\n");
            for (TransmissionStatisticsDto stat : stats) {
                sb.append(period);
                sb.append(',');
                sb.append(stat.getDoc_type());
                sb.append(',');
                sb.append(stat.getDirection());
                sb.append(',');
                sb.append(stat.getFiles());
                sb.append('\n');
            }
            writer.write(sb.toString());

        } catch (FileNotFoundException e) {
            logger.error("Statistics service exception", e);
        }

        return new File(path);
    }

    private Map<String, String> datesForPreviousMonth() {
        Calendar calendar = Calendar.getInstance();
        Integer currentYear = calendar.get(Calendar.YEAR);
        Integer currentMonth = calendar.get(Calendar.MONTH) + 1;

        String endYear = currentYear.toString();
        String endMonth = currentMonth < 10 ? String.format("0%s", currentMonth) : currentMonth.toString();
        String startYear = currentMonth == 1 ? String.valueOf(currentYear - 1) : currentYear.toString();
        String startMonth = currentMonth == 1 ? "12" : ((currentMonth - 1) < 10 ? String.format("0%s", currentMonth - 1) : String.valueOf(currentMonth - 1));

        Map<String, String> result = new HashMap<>();
        result.put("to", String.format("%s-%s-01", endYear, endMonth));
        result.put("from", String.format("%s-%s-01", startYear, startMonth));
        result.put("period", String.format("%s-%s", startYear, startMonth));
        return result;
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
