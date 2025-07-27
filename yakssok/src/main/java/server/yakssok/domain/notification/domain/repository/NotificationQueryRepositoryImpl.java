package server.yakssok.domain.notification.domain.repository;

import static server.yakssok.domain.notification.domain.entity.QNotification.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.entity.Notification;

@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Notification> findMyNotifications(Long userId, Long cursorId, int limit) {

		return jpaQueryFactory
			.selectFrom(notification)
			.where(
				notification.receiverId.eq(userId)
					.or(notification.senderId.eq(userId)),
				ltCursorId(cursorId)
			)
			.orderBy(notification.id.desc())
			.limit(limit)
			.fetch();
	}

	private static BooleanExpression ltCursorId(Long cursorId) {
		if (cursorId == null) {
			return null;
		}
		return notification.id.lt(cursorId);
	}
}
