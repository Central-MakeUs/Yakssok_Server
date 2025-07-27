package server.yakssok.domain.notification.domain.repository;

import static server.yakssok.domain.notification.domain.entity.QNotification.*;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.global.common.util.SliceUtils;

@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<Notification> findMyNotifications(Long userId, Long cursorId, int limit) {
		List<Notification> notifications = jpaQueryFactory
			.selectFrom(notification)
			.where(
				notification.receiverId.eq(userId)
					.or(notification.senderId.eq(userId)),
				ltCursorId(cursorId)
			)
			.orderBy(notification.id.desc())
			.limit(SliceUtils.limitForHasNext(limit))
			.fetch();
		return SliceUtils.toSlice(notifications, limit);
	}

	private static BooleanExpression ltCursorId(Long cursorId) {
		if (cursorId == null) {
			return null;
		}
		return notification.id.lt(cursorId);
	}
}
