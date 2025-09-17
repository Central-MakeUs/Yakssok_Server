package server.yakssok.domain.friend.application.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Component
public class MedicationStatusMapper {
	public List<FollowingMedicationStatusResponse> toMedicationStatusResponses(
		List<Friend> friends,
		Map<Long, List<MedicationScheduleDto>> nag,
		Map<Long, List<MedicationScheduleDto>> praise
	) {
		List<FollowingMedicationStatusResponse> result = new ArrayList<>(friends.size());
		for (Friend friend : friends) {
			var following = friend.getFollowing();
			Long followingId = following.getId();

			// 1) 미복용이 있으면 NAG 우선
			List<MedicationScheduleDto> nagList = nag.get(followingId);
			if (nagList != null && !nagList.isEmpty()) {
				result.add(FollowingMedicationStatusResponse.ofNag(
					following,
					nagList
				));
				continue;
			}

			// 2) NAG가 없고, 칭찬 대상이면 PRAISE
			List<MedicationScheduleDto> praiseList = praise.get(followingId);
			if (praiseList != null && !praiseList.isEmpty()) {
				result.add(FollowingMedicationStatusResponse.ofPraise(
					following, praiseList)
				);
			}
		}
		return result;
	}
	public void sortByNotTakenCount(List<FollowingMedicationStatusResponse> statusList) {
		statusList.sort((a, b) -> {
			// 잔소리 우선
			if (a.feedbackType().equals("NAG") && b.feedbackType().equals("PRAISE")) return -1;
			if (a.feedbackType().equals("PRAISE") && b.feedbackType().equals("NAG")) return 1;

			// 잔소리의 경우 안먹은 약 개수 내림 차순
			return Integer.compare(b.medicationCount(), a.medicationCount());
		});
	}
}
