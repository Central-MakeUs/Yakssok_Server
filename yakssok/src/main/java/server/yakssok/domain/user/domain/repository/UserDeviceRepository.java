package server.yakssok.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import server.yakssok.domain.user.domain.entity.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
	List<UserDevice> findByUserIdAndAlertOnTrue(Long userId);
	void deleteByUserIdAndDeviceId(Long userId, String deviceId);

	@Modifying
	@Query("DELETE FROM UserDevice ud WHERE ud.user.id = :userId")
	void deleteAllByUserId(Long userId);

	Optional<UserDevice> findByFcmToken(String fcmToken);
	Optional<UserDevice> findByDeviceId(String deviceId);
}
