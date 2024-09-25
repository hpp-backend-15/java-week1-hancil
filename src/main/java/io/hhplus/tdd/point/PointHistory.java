package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {


    public static PointHistory create(long userId, long amount, TransactionType type) {
        return new PointHistory(
                System.currentTimeMillis(),  // 고유 ID를 시간으로 임시 생성 (필요에 따라 수정 가능)
                userId,
                amount,
                type,
                System.currentTimeMillis()
        );
    }

}
