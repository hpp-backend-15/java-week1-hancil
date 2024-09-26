package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

    public UserPoint getUserPoint(long id){

        if(id <= 0){
            throw new IllegalArgumentException("유효하지 않은 id 입니다.");
        }

        UserPoint userPoint = pointRepository.findById(id);
        if(userPoint == null){
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        return userPoint;
    }

    public UserPoint chargeUserPoint(long id, long amount) {

        if(id <= 0){
            throw new IllegalArgumentException("유효하지 않은 id 입니다.");
        }

        Lock lock = getUserLock(id);
        lock.lock();
        UserPoint result = null;

        try{
            UserPoint userPoint = pointRepository.findById(id);
            if(userPoint == null){
                throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
            }

            UserPoint modifiedUserPoint = userPoint.addPoints(amount);
            //업데이트된 포인트 저장
            result = pointRepository.save(modifiedUserPoint);


            // 포인트 저장 후 히스토리 저장
            PointHistory insert = pointHistoryRepository.insert(
                    id, amount, TransactionType.CHARGE, System.currentTimeMillis()
            );
        }finally {
            lock.unlock();
            cleanupLock(id, lock);
        }

        return result;
    }

    public UserPoint useChargePoint(long id, long amount) {

        if(id <= 0){
            throw new IllegalArgumentException("유효하지 않은 id 입니다.");
        }
        Lock lock = getUserLock(id);
        lock.lock();
        UserPoint result = null;
        try{
            UserPoint userPoint = pointRepository.findById(id);
            if(userPoint == null){
                throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
            }

            UserPoint modifiedUserPoint = userPoint.consumePoints(amount);
            //업데이트된 포인트 저장
            result = pointRepository.save(modifiedUserPoint);

            // 포인트 저장 후 히스토리 저장
            PointHistory insert = pointHistoryRepository.insert(
                    id, amount, TransactionType.USE, System.currentTimeMillis()
            );
        }finally {
            lock.unlock();
            cleanupLock(id, lock);
        }

        return result;
    }

    public List<PointHistory> getPointHistories(long id) {

        if(id <= 0){
            throw new IllegalArgumentException("유효하지 않은 id 입니다.");
        }

        UserPoint userPoint = pointRepository.findById(id);
        if(userPoint == null){
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        return pointHistoryRepository.findById(id);
    }


    /**
     * Lock 획득 및 초기화
     */

    private void cleanupLock(long id, Lock lock) {
        if(lock.tryLock()){
            try{
                userLocks.remove(id);
            }finally {
                lock.unlock();
            }
        }
    }

    private Lock getUserLock(long userKey) {
        return userLocks.computeIfAbsent(userKey, k -> new ReentrantLock());
    }

}
