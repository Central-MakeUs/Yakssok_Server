package server.yakssok.domain.medication.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import lombok.Getter;
import server.yakssok.domain.medication.application.exception.MedicationException;
@Getter
public enum MedicationType {
	CHRONIC("만성 질환 관리"),
	MENTAL("정신 건강 관리"),
	SUPPLEMENT("건강기능식품/영양보충"),
	BEAUTY("미용 관련 관리"),
	HIGHRISK("고위험군 복약"),
	DIET("다이어트/대사 관련"),
	TEMPORARY("통증/감기 등 일시적 치료"),
	OTHER("기타 설정");

	private final String label;

	MedicationType(String label) {
		this.label = label;
	}

	public static MedicationType from(String name) {
		try {
			return MedicationType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new MedicationException(INVALID_INPUT_VALUE);
		}
	}
}
