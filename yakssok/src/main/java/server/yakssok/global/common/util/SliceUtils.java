package server.yakssok.global.common.util;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class SliceUtils {

	public static <T> Slice<T> toSlice(List<T> list, int limit) {
		boolean hasNext = list.size() > limit;
		List<T> content = hasNext ? list.subList(0, limit) : list;
		return new SliceImpl<>(content, Pageable.ofSize(limit), hasNext);
	}

	public static int limitForHasNext(int limit) {
		return limit + 1;
	}
}
