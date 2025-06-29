package server.yakssok.domain.medication;

public enum MedicationType {
	CHRONIC("만성 질환 관리"),
	MENTAL("정신 건강 관리"),
	SUPPLEMENT("건강기능식품/영양보충"),
	BEAUTY("미용 관련 관리"),
	TEMPORARY("통증/감기 등 일시적 치료"),
	HIGHRISK("고위험군 복약"),
	DIET("다이어트/대사 관련"),
	OTHER("기타 설정");
	private final String label;

	MedicationType(String label) {
		this.label = label;
	}
}
