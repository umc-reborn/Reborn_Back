package spring.reborn.domain.rebornTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.config.RedisConfig;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.rebornTask.model.PostRebornTaskReq;
import spring.reborn.domain.rebornTask.model.PostRebornTaskRes;
import spring.reborn.domain.rebornTask.model.RebornTask;
import spring.reborn.domain.redis.model.Event;
import spring.reborn.domain.redis.model.EventCount;

import java.sql.Time;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RebornTaskRedisService {

    @Autowired
    private final RedisTemplate<Object,Object> redisTemplate;

    private final RebornTaskDao rebornTaskDao;

    //    private static final long FIRST_ELEMENT = 0;
//    private static final long LAST_ELEMENT = -1;
//    private static final long PUBLISH_SIZE = 10;
//    private static final long LAST_INDEX = 1;
//    private EventCount eventCount;
//    private RebornTaskService rebornTaskService;
//
//    public void setEventCount(int rebornIdx, int queue){
//        this.eventCount = new EventCount(
//                "rebornIdx:"+rebornIdx, queue);
//    }
//
    public void addQueue(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        SetOperations<Object, Object> stringStringSetOperations
                = redisTemplate.opsForSet();
        String key = "reborn:" + postRebornTaskReq.getRebornIdx() + " / " + postRebornTaskReq.getUserIdx();
        String userIdxS = String.valueOf(postRebornTaskReq.getUserIdx());

        // Time to Long으로 바꾸어 60 대신 변수 입력
//        Time productLimitTime = rebornTaskDao.getRebornProductLimitTime(postRebornTaskRes.getRebornTaskIdx());

        stringStringSetOperations.add(key,userIdxS);
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);

    }
//
//    public void getOrder(int rebornIdx){
//        final long start = FIRST_ELEMENT;
//        final long end = LAST_ELEMENT;
//
//        Set<Object> queue = redisTemplate.opsForZSet().range(
//                "rebornIdx:"+rebornIdx, start, end);
//
//        for (Object people : queue) {
//            Long rank = redisTemplate.opsForZSet().rank(
//                    "rebornIdx:"+rebornIdx, people);
//            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
//        }
//    }
//
//    public void publish(int rebornIdx, PostRebornTaskReq postRebornTaskReq) throws BaseException {
//        final long start = FIRST_ELEMENT;
//        final long end = PUBLISH_SIZE - LAST_INDEX;
//
//        Set<Object> queue =
//                redisTemplate.opsForZSet().range(
//                        "rebornIdx:"+rebornIdx, start, end);
//        for (Object people : queue) {
//            PostRebornTaskRes postRebornTaskRes = rebornTaskService.createRebornTask(postRebornTaskReq);
//
//            log.info("리본교환번호 발급 완료 - RebornTaskIdx : {}",
//                    postRebornTaskRes.getRebornTaskIdx());
//            redisTemplate.opsForZSet().remove("rebornIdx:"+rebornIdx, people);
//            this.eventCount.decrease();
//        }
//    }
//
//    public boolean validEnd(){
//        return this.eventCount != null && this.eventCount.end();
//    }
//
//    public long getSize(Event event){
//        return redisTemplate.opsForZSet().size(event.toString());
//    }
}
