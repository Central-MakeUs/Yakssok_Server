package server.yakssok.domain.friend.applcation.service;

public record NotTakenMedicationInfo(
	String medicationType,
	String medicineName,
	String scheduleTime
) {}