package server.yakssok.domain.medication_schedule.domain.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

@RequiredArgsConstructor
@Repository
public class MedicationScheduleJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_SQL = """
        INSERT INTO medication_schedule (medicine_name, scheduled_date, scheduled_time, is_taken, medication_id)
        VALUES (?, ?, ?, ?, ?)
    """;

	public void batchInsert(List<MedicationSchedule> schedules) {
		jdbcTemplate.batchUpdate(
			INSERT_SQL,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MedicationSchedule schedule = schedules.get(i);
					ps.setObject(2, schedule.getScheduledDate()); // LocalDate
					ps.setObject(3, schedule.getScheduledTime()); // LocalTime
					ps.setBoolean(4, schedule.isTaken());
					ps.setLong(5, schedule.getMedicationId());
				}

				@Override
				public int getBatchSize() {
					return schedules.size();
				}
			}
		);
	}
}
