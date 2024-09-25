package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

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

        if(amount <= 0){
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        UserPoint userPoint = pointRepository.findById(id);
        if(userPoint == null){
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        UserPoint modifiedUserPoint = userPoint.addPoints(amount);
        //업데이트된 포인트 저장
        UserPoint result = pointRepository.save(modifiedUserPoint);


        // 포인트 저장 후 히스토리 저장
        PointHistory insert = pointHistoryRepository.insert(
                id, amount, TransactionType.CHARGE, System.currentTimeMillis()
        );

        return result;
    }

    public UserPoint useChargePoint(long id, long amount) {

        if(id <= 0){
            throw new IllegalArgumentException("유효하지 않은 id 입니다.");
        }

        if(amount <= 0){
            throw new IllegalArgumentException("사용할 금액은 0보다 커야 합니다.");
        }

        UserPoint userPoint = pointRepository.findById(id);
        if(userPoint == null){
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        UserPoint modifiedUserPoint = userPoint.consumePoints(amount);
        //업데이트된 포인트 저장
        UserPoint result = pointRepository.save(modifiedUserPoint);


        // 포인트 저장 후 히스토리 저장
        PointHistory insert = pointHistoryRepository.insert(
                id, amount, TransactionType.USE, System.currentTimeMillis()
        );

        return result;

    }
}
