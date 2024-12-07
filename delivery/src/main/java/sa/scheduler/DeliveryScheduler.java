package sa.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Component
public class DeliveryScheduler {
    private final TaskScheduler scheduler;
    private final ConcurrentHashMap<Long, ScheduledFuture> deliveryTask = new ConcurrentHashMap<>();

    public void reserve(Long deliveryId, Runnable runnable){
        Duration duration = getDurationFor1Min();
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(runnable, duration);
        deliveryTask.put(deliveryId, future);
    }

    public void cancel(Long deliveryId) {
        ScheduledFuture future = deliveryTask.get(deliveryId);
        if (future != null) {
            future.cancel(true);
            deliveryTask.remove(deliveryId);
        }
    }

    private Duration getDurationFor1Min(){
        return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
    }
}
