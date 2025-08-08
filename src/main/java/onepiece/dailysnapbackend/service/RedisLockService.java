package onepiece.dailysnapbackend.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {

  private static final long WAIT_TIMEOUT_SECONDS = 1L;  // 락 대기 시간 (1초)
  private static final long LEASE_TIMEOUT_SECONDS = 5L; // 락 임대 시간 (5초)
  private final RedissonClient redissonClient;

  // 락을 사용한 작업 실행
  public <T> T executeWithLock(String lockKey, Supplier<T> task) {
    RLock lock = redissonClient.getLock(lockKey);
    try {
      if (!lock.tryLock(WAIT_TIMEOUT_SECONDS, LEASE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        log.error("락 획득 실패: lockKey={}, 다른 요청이 먼저 처리 중", lockKey);
        throw new RuntimeException("락 획득 실패: lockKey=" + lockKey);
      }
      log.info("락 획득 성공: lockKey={}", lockKey);

      try {
        return task.get();
      } finally {
        if (lock.isHeldByCurrentThread()) {
          lock.unlock();
          log.info("락 해제: lockKey={}", lockKey);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("락 대기 중 인터럽트 발생: lockKey=" + lockKey, e);
    }
  }
}