package server.yakssok.domain.feedback.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import server.yakssok.domain.feedback.domain.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackQueryRepository {
}
