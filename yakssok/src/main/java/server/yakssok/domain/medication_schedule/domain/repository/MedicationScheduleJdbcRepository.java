package server.yakssok.domain.medication_schedule.domain.repository;

import static server.yakssok.global.exception.ErrorCode.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.exception.MedicationScheduleException;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

@RequiredArgsConstructor
@Repository
public class MedicationScheduleJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_SQL = """
        INSERT INTO medication_schedule (scheduled_date, scheduled_time, is_taken, medication_id, user_id)
        VALUES (?, ?, ?, ?, ?)
    """;

	public void batchInsert(List<MedicationSchedule> schedules) {
		jdbcTemplate.batchUpdate(
			INSERT_SQL,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MedicationSchedule schedule = schedules.get(i);
					ps.setObject(1, schedule.getScheduledDate());
					ps.setObject(2, schedule.getScheduledTime());
					ps.setBoolean(3, schedule.isTaken());
					ps.setLong(4, schedule.getMedicationId());
					ps.setLong(5, schedule.getUserId());
				}

				@Override
				public int getBatchSize() {
					return schedules.size();
				}
			}
		);
	}

	public void batchInsert(List<MedicationSchedule> schedules, int chunkSize) {
		if (schedules == null || schedules.isEmpty()) return;
		if (chunkSize <= 0) throw new MedicationScheduleException(INTERNAL_SERVER_ERROR);

		for (int from = 0; from < schedules.size(); from += chunkSize) {
			int to = Math.min(from + chunkSize, schedules.size());
			List<MedicationSchedule> chunk = schedules.subList(from, to);
			batchInsert(chunk);
		}
	}
}
