package server.yakssok.global.common.reponse;

import java.util.List;

public record PageResponse<T>(
	Boolean hasNext,
	List<T> content
) {
	public static <T> PageResponse<T> of(List<T> content, boolean hasNext) {
		return new PageResponse<>(
			hasNext,
			content
		);
	}
}