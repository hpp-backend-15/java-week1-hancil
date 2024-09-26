package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private static final long MAX_POINT = 1_000_000_000L;

    public static UserPoint empty(long id) {
        //사용자가 존재하지 않으면 null 반환한다.
        return null;
//        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    //포인트 충전을 UserPoint 에게 책임 전가
    public UserPoint addPoints(long amount) {

        long addPoint = this.point + amount;

        if(amount > MAX_POINT || addPoint > MAX_POINT){
            throw new IllegalArgumentException("충전 금액이 최대 허용 금액을 초과했습니다.");
        }

        if(amount <= 0){
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        return new UserPoint(this.id, addPoint, System.currentTimeMillis());
    }

    //포인트 사용에 대한 기능 구현도 준비해야함
    public UserPoint consumePoints(long amount) {
        long usePoint = this.point - amount;

        if (amount >= MAX_POINT) {
            throw new IllegalArgumentException("사용하려는 포인트가 최대 허용 금액을 초과했습니다.");
        }

        if(amount <= 0){
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        if(usePoint < 0){
            throw new IllegalArgumentException("보유한 포인트보다 많은 금액을 사용할 수 없습니다.");
        }

        return new UserPoint(this.id, usePoint, System.currentTimeMillis());
    }
}
