package panek.szymon.ztp.lab3.ztp_lab3.infrastructure.scheduler;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import panek.szymon.ztp.lab3.ztp_lab3.domain.Cart;
import panek.szymon.ztp.lab3.ztp_lab3.domain.CartRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartCleanupScheduler {

    private final CartRepository cartRepository;

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredCarts() {
//        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);
        LocalDateTime expirationTime = LocalDateTime.now().minusSeconds(10);
        List<Cart> expiredCarts = cartRepository.findAllByLastModifiedBefore(expirationTime);

        if (!expiredCarts.isEmpty()) {
            log.info("Removing {} expired carts to release locks ", expiredCarts.size());
            cartRepository.deleteAll(expiredCarts);
        }
    }
}