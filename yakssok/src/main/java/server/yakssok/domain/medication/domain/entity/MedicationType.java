package server.yakssok.domain.medication.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import lombok.Getter;
import server.yakssok.domain.medication.application.exception.MedicationException;
@Getter
public enum MedicationType {
	CHRONIC("만성 질환 관리", 1),
	MENTAL("정신 건강 관리", 2),
	SUPPLEMENT("건강기능식품/영양보충", 3),
	BEAUTY("미용 관련 관리", 4),
	HIGHRISK("고위험군 복약", 5),
	DIET("다이어트/대사 관련", 6),
	TEMPORARY("통증/감기 등 일시적 치료", 7),
	OTHER("기타 설정", 8);

	private final String label;
	private final int priority;

	MedicationType(String label, int priority) {
		this.label = label;
		this.priority = priority;
	}

	public static MedicationType from(String name) {
		try {
			return MedicationType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new MedicationException(INVALID_INPUT_VALUE);
		}
	}
}
