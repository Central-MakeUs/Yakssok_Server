package server.yakssok.domain.user.domain.entity;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.global.exception.ErrorCode;

@Embeddable
@Getter
@NoArgsConstructor
public class InviteCode {
	private String value;

	private static final String FORMAT_REGEX = "^[A-Za-z]{6}\\d{1}[A-Za-z]{2}$";
	private static final int LETTERS1_COUNT = 6;
	private static final int LETTERS2_COUNT = 2;

	public InviteCode(String value) {
		validate(value);
		this.value = value;
	}

	private static void validate(String value) {
		if (value == null || !value.matches(FORMAT_REGEX)) {
			throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public static InviteCode generate() {
		return new InviteCode(
			randomLetters(LETTERS1_COUNT) +
				randomDigit() +
				randomLetters(LETTERS2_COUNT)
		);
	}

	private static String randomLetters(int count) {
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			boolean upper = ThreadLocalRandom.current().nextBoolean();
			char base = upper ? 'A' : 'a';
			char c = (char) (base + ThreadLocalRandom.current().nextInt(26));
			sb.append(c);
		}
		return sb.toString();
	}

	private static String randomDigit() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(10));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		InviteCode that = (InviteCode)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
