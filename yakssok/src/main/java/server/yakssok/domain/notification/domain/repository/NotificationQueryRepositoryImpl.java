package server.yakssok.domain.notification.domain.repository;

import static server.yakssok.domain.notification.domain.entity.QNotification.*;
import static server.yakssok.domain.user.domain.entity.QUser.*;

import java.util.List;


import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.entity.Notification;

@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Notification> findMyNotifications(Long userId) {
		return jpaQueryFactory
			.selectFrom(notification)
			.where(
				notification.receiverId.eq(userId)
					.or(notification.senderId.eq(userId))
			)
			.orderBy(notification.id.asc())
			.fetch();
	}
}
