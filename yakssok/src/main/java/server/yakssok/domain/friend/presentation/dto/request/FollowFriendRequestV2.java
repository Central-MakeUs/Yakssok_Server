package server.yakssok.domain.friend.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.user.domain.entity.User;

@Schema(description = "지인 팔로우")
public record FollowFriendRequestV2(
	@Schema(description = "팔로우 할 사람의 초대 코드", example = "AWsaZP8cq")
	@NotNull
	String inviteCode
) {
	public Friend toFriend(User user, User following) {
		return Friend.create(user, following);
	}
}
