package server.yakssok.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.user.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {
}
