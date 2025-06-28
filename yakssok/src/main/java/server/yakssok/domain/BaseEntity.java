package server.yakssok.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
