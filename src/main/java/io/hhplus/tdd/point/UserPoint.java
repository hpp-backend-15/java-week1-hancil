package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        //사용자가 존재하지 않으면 null 반환한다.
        return null;
//        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    //포인트 충전을 UserPoint 에게 책임 전가
    public UserPoint addPoints(long amount) {
        long newPoint = this.point + amount;
        if (newPoint < 0) {
            throw new IllegalArgumentException("포인트 합계가 잘못되었습니다. 비정상적인 금액을 충전하려고 합니다.");
        }
        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }

    //포인트 사용에 대한 기능 구현도 준비해야함

}
