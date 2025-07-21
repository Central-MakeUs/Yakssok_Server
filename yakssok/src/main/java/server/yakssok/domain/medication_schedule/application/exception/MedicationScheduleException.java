package server.yakssok.domain.medication_schedule.application.exception;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class MedicationScheduleException extends GlobalException {

	public MedicationScheduleException(ResponseCode responseCode) {
		super(responseCode);
	}
}
