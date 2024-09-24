package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointRepository pointRepository;

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
}
