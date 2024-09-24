package io.hhplus.tdd;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class GetPointServiceTest {

    @Mock
    private PointService pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void id가_0보다_작거나_같은_id값은_예외() {

        //given
        long id = -1L;
        given(pointService.getUserPoint(id))
                .willThrow(new IllegalArgumentException("유효하지 않은 id 입니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.getUserPoint(id));

        //then
        assertEquals("유효하지 않은 id 입니다.", exception.getMessage());
    }

    @Test
    void id가_db에_존재하지_않으면_400() {

        //given
        long id = 999L;
        given(pointService.getUserPoint(id))
                .willThrow(new IllegalArgumentException("사용자가 존재하지 않습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.getUserPoint(id));

        //then
        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 포인트_조회에_성공하면_UserPoint_객체_반환() {

        //given
        long id = 1L;
        given(pointService.getUserPoint(id))
                .willReturn(new UserPoint(id, 10, System.currentTimeMillis()));

        //when
        UserPoint userPoint = pointService.getUserPoint(id);

        //then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(10);
    }

}
