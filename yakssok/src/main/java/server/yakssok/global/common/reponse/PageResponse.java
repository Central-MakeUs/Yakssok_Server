package server.yakssok.global.common.reponse;

import java.util.List;

public record PageResponse<T>(
	List<T> content
) {
	public static <T> PageResponse<T> of(List<T> content) {
		return new PageResponse<>(content);
	}
}