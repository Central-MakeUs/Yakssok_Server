package server.yakssok.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import server.yakssok.domain.user.domain.entity.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
	Optional<UserDevice> findByUserId(Long userId);
	List<UserDevice> findByUserIdAndAlertOnTrue(Long userId);
}
