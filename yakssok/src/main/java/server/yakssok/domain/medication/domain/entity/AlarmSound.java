package server.yakssok.domain.medication.domain.entity;

import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.global.exception.ErrorCode;

public enum AlarmSound {
	FEEL_GOOD("기분 좋아지는 소리"),
	PILL_SHAKE("약통 흔드는 소리"),
	SCOLD("잔소리 해주는 소리"),
	CALL("전화온 듯한 소리"),
	VIBRATION("진동 울리는 소리");

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
