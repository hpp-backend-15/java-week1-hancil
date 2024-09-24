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
}
