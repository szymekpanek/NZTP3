package panek.szymon.ztp.lab3.ztp_lab3.infrastructure.notifications;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.OrderPlacedEvent;

@Slf4j
@Component
public class NotificationService {

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("NOTIFICATION: Order {} for user {} started processing. ",
                event.orderId(), event.userId());
    }
}
