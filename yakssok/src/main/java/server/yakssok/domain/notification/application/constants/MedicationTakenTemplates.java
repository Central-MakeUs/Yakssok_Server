package server.yakssok.domain.notification.application.constants;

public class MedicationTakenTemplates {
	public static final String TITLE_TEMPLATE = "%s 챙길 시간이에요!";
	public static final String BODY_TEMPLATE = "약 먹고, 뿌듯하게 섭취 완료 버튼 누르기!";

	public static String getTitle(String medicationName) {
		return String.format(TITLE_TEMPLATE, medicationName);
	}

	public static String getBody() {
		return BODY_TEMPLATE;
	}
}
