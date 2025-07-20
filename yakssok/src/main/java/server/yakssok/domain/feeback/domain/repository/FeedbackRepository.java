package server.yakssok.domain.feeback.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import server.yakssok.domain.feeback.domain.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
