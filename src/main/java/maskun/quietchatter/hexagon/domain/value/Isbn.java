package maskun.quietchatter.hexagon.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public record Isbn(
        @Column(name = "isbn", nullable = false, length = 13)
        String value
) {

    private static Isbn fromRaw(String rawValue) {
        // 1. 정규화: 하이픈(-)과 공백을 모두 제거하고, 'x'는 대문자 'X'로 변환
        String normalized = rawValue.replaceAll("[\\s-]+", "").toUpperCase();
        return new Isbn(rawValue);

    }

    public Isbn {
        Objects.requireNonNull(value, "ISBN 값은 null일 수 없습니다.");

        if (!isValid(value)) {
            throw new IllegalArgumentException("잘못된 ISBN 형식 또는 체크섬: " + value);
        }
    }

    /**
     * ISBN-10 또는 ISBN-13 유효성을 검사하는 헬퍼 메소드
     */
    private static boolean isValid(String normalized) {
        int length = normalized.length();

        if (length == 10) {
            return isValidIsbn10(normalized);
        }
        if (length == 13) {
            return isValidIsbn13(normalized);
        }

        return false;
    }

    /**
     * ISBN-10 체크섬 검증 (Modulus 11) (10*d1 + 9*d2 + ... + 2*d9 + 1*d10) % 11 == 0
     */
    private static boolean isValidIsbn10(String isbn) {
        // 9자리의 숫자 + 마지막 1자리는 숫자 또는 'X'
        if (!isbn.matches("^\\d{9}[\\dX]$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (10 - i) * Character.getNumericValue(isbn.charAt(i));
        }

        // 마지막 10번째 문자(Check Digit) 처리
        char checkDigitChar = isbn.charAt(9);
        int checkDigit = (checkDigitChar == 'X') ? 10 : Character.getNumericValue(checkDigitChar);

        sum += checkDigit;

        return (sum % 11 == 0);
    }

    /**
     * ISBN-13 체크섬 검증 (Modulus 10) EAN-13 알고리즘과 동일
     */
    private static boolean isValidIsbn13(String isbn) {
        // 13자리의 숫자
        if (!isbn.matches("^\\d{13}$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) { // 마지막 13번째 자리는 체크섬이므로 12자리까지만
            int digit = Character.getNumericValue(isbn.charAt(i));
            // 홀수번째 자리는 1, 짝수번째 자리는 3을 곱함 (인덱스는 0부터 시작)
            sum += (i % 2 == 0) ? digit * 1 : digit * 3;
        }

        // 10으로 나눈 나머지를 10에서 뺀 값이 체크섬
        // (단, 10에서 뺀 값이 10이면 0으로 취급)
        int checkDigit = (10 - (sum % 10)) % 10;

        int lastDigit = Character.getNumericValue(isbn.charAt(12));

        return checkDigit == lastDigit;
    }
}
