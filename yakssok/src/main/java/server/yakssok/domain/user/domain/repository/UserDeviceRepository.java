package server.yakssok.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import server.yakssok.domain.user.domain.entity.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
	List<UserDevice> findByUserIdAndAlertOnTrue(Long userId);
	Optional<UserDevice> findByUserIdAndDeviceId(Long userId, String deviceId);
}
