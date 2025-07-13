package server.yakssok.domain.medication.domain.entity;

import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.global.exception.ErrorCode;

public enum AlarmSound {
	YAKSSUK("약쑥! 하고 울리는 소리"),
	BOUNCY("통통 튀는 소리" ),
	SURPRISE("깜짝 놀라게 해주는 소리"),
	SHAKE("약통 흔드는 소리"),
	CHATTY("쫑알쫑알 재촉하는 소리");

	private final String displayName;

	AlarmSound(String displayName) {
		this.displayName = displayName;
	}

	public static AlarmSound from(String name) {
		try {
			return AlarmSound.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new MedicationException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}
}
