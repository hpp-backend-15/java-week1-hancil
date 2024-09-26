package io.hhplus.tdd.point;

public record PointHistory(
        Long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {


    public static PointHistory create(long userId, long amount, TransactionType type) {
        return new PointHistory(
                null,  // 고유 ID는 db에서 저장할 때 생성이 되므로 primitive -> Wrapper 로 변환하여 null로 객체를 생성한다.
                userId,
                amount,
                type,
                System.currentTimeMillis()
        );
    }

}
