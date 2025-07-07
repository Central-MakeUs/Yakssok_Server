package server.yakssok.domain.medication.application.exception;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class MedicationException extends GlobalException {
	public MedicationException(ResponseCode responseCode) {
		super(responseCode);
	}
}
