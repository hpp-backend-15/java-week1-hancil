package io.hhplus.tdd.point;

public interface UserPointRepository{
    UserPoint findById(Long id);
    UserPoint save(UserPoint userPoint);
}
