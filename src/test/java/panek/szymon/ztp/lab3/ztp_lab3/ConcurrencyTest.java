package panek.szymon.ztp.lab3.ztp_lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.AddProductCommand;
import panek.szymon.ztp.lab3.ztp_lab3.application.port.ProductServicePort;
import panek.szymon.ztp.lab3.ztp_lab3.application.service.CartService;
import panek.szymon.ztp.lab3.ztp_lab3.domain.Cart;
import panek.szymon.ztp.lab3.ztp_lab3.domain.CartRepository;
import panek.szymon.ztp.lab3.ztp_lab3.infrastructure.scheduler.CartCleanupScheduler; // Import

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @MockitoBean
    private ProductServicePort productService;

    // --- POPRAWKA 1: Wyłączamy scheduler na czas testu ---
    @MockitoBean
    private CartCleanupScheduler cartCleanupScheduler;

    @BeforeEach
    void setup() {
        cartRepository.deleteAll();
        when(productService.reserveProduct(anyString(), anyInt())).thenReturn(true);
        when(productService.getProductPrice(anyString())).thenReturn(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should process concurrent updates without losing data (Optimistic Locking + Retry)")
    void shouldHandleConcurrentUpdates() throws InterruptedException {
        String userId = "concurrent-user";
        int numberOfThreads = 5;

        // Tworzymy koszyk PRZED testem
        cartRepository.save(new Cart(userId));

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    AddProductCommand command = new AddProductCommand(
                            userId,
                            "product-" + index,
                            1
                    );
                    cartService.handle(command);
                } catch (Exception e) {
                    // W teście chcemy widzieć ewentualne wyjątki, które przebiły się przez @Retryable
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean tasksFinished = latch.await(10, TimeUnit.SECONDS);
        assertTrue(tasksFinished, "Nie wszystkie wątki zakończyły pracę w wyznaczonym czasie!");
        executorService.shutdown();

        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        assertEquals(numberOfThreads, cart.getItems().size(), "Liczba produktów w koszyku powinna być równa liczbie wątków");
    }
}