package server.yakssok.domain.friend.application.service.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
@Component
public class MedicationStatusMapper {
	public List<FollowingMedicationStatusResponse> toMedicationStatusResponses(
		List<Friend> friends,
		Map<Long, Integer> notTakenMap,
		Set<Long> praisedToday
	) {
		List<FollowingMedicationStatusResponse> result = new ArrayList<>();
		for (Friend friend : friends) {
			Long followingId = friend.getFollowing().getId();
			int notTaken = notTakenMap.getOrDefault(followingId, 0);

			if (notTaken > 0) {
				result.add(FollowingMedicationStatusResponse.of(friend, notTaken));
			} else if (praisedToday.contains(followingId)) {
				result.add(FollowingMedicationStatusResponse.of(friend, 0));
			}
		}
		return result;
	}

	public void sortByNotTakenCount(List<FollowingMedicationStatusResponse> statusList) {
		statusList.sort(Comparator.comparingInt(FollowingMedicationStatusResponse::notTakenCount).reversed());
	}

}
