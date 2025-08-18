package server.yakssok.global.dummy;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.medication.application.service.MedicationService;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class MedicationInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final MedicationService medicationService;
	private final MedicationRepository medicationRepository;

	@Override
	public void run(ApplicationArguments args) {
		User inwoo = userRepository.findUserByProviderId(UserInitializer.OAUTH_TYPE_INWOO, UserInitializer.PROVIDER_ID_INWOO).orElseThrow();
		User ria = userRepository.findUserByProviderId(UserInitializer.OAUTH_TYPE_RIA, UserInitializer.PROVIDER_ID_RIA).orElseThrow();
		boolean inwooHasMedication = medicationRepository.existsByUserId(inwoo.getId());
		boolean riaHasMedication = medicationRepository.existsByUserId(ria.getId());

		if (inwooHasMedication) {
			log.info(">>> 인우의 복약 루틴 더미 데이터 이미 존재");
		} else {
			medicationService.createMedication(inwoo.getId(), MedicationDataFactory.inuBloodPressure());
			medicationService.createMedication(inwoo.getId(), MedicationDataFactory.inuVitaminD());
			medicationService.createMedication(inwoo.getId(), MedicationDataFactory.inuColdMedicine());
			log.info(">>> 인우의 복약 루틴 더미 데이터 생성 완료");
		}

		if (riaHasMedication) {
			log.info(">>> 리아의 복약 루틴 더미 데이터 이미 존재");
		} else {
			medicationService.createMedication(ria.getId(), MedicationDataFactory.riaCollagen());
			medicationService.createMedication(ria.getId(), MedicationDataFactory.riaLactobacillus());
			log.info(">>> 리아의 복약 루틴 더미 데이터 생성 완료");
		}
	}
}