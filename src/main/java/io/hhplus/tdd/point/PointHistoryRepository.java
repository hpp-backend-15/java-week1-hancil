package io.hhplus.tdd.point;

import java.util.List;

public interface PointHistoryRepository {
    List<PointHistory> findById(Long id);
    PointHistory insert(long userId, long amount, TransactionType type, long updateMillis);
}
