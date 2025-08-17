package server.yakssok.domain.friend.application.service.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Component
public class MedicationStatusMapper {
	public List<FollowingMedicationStatusResponse> toMedicationStatusResponses(
		List<Friend> friends,
		Map<Long, List<MedicationSchedule>> nag,
		Map<Long, List<MedicationSchedule>> praise
	) {
		List<FollowingMedicationStatusResponse> result = new ArrayList<>(friends.size());
		for (Friend friend : friends) {
			var following = friend.getFollowing();
			Long uid = following.getId();

			// 1) 미복용이 있으면 NAG 우선
			List<MedicationSchedule> nagList = nag.get(uid);
			if (nagList != null && !nagList.isEmpty()) {
				result.add(FollowingMedicationStatusResponse.ofNag(
					friend,
					nagList
				));
				continue;
			}

			// 2) NAG가 없고, 칭찬 대상이면 PRAISE
			List<MedicationSchedule> praiseList = praise.get(uid);
			if (praiseList != null && !praiseList.isEmpty()) {
				result.add(FollowingMedicationStatusResponse.ofPraise(
					friend, praiseList)
				);
			}
		}
		return result;
	}
	public void sortByNotTakenCount(List<FollowingMedicationStatusResponse> statusList) {
	}
}
