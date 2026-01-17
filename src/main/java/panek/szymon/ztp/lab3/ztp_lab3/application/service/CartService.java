package panek.szymon.ztp.lab3.ztp_lab3.application.service;




import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.*;
import panek.szymon.ztp.lab3.ztp_lab3.application.port.ProductServicePort;
import panek.szymon.ztp.lab3.ztp_lab3.domain.Cart;
import panek.szymon.ztp.lab3.ztp_lab3.domain.CartItem;
import panek.szymon.ztp.lab3.ztp_lab3.domain.CartRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductServicePort productService;
    private final ApplicationEventPublisher eventPublisher;



    public Cart getCart(GetCartQuery query) {
        return cartRepository.findByUserId(query.userId())
                .orElseGet(() -> cartRepository.save(new Cart(query.userId())));
    }

    // --- COMMAND HANDLERS  ---

    // Mechanizm Retry zapewnia spójność operacyjną przy konfliktach wersji
    // Zwiększamy maxAttempts do 10 i dodajemy multiplier, żeby rozsunąć próby w czasie
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    @Transactional
    public void handle(AddProductCommand command) {
        // 1. Blokada w serwisie zewnętrznym (analiza inżynierska +0.5) [cite: 33, 36]
        boolean reserved = productService.reserveProduct(command.productId(), command.quantity());
        if (!reserved) {
            throw new RuntimeException("Product temporarily unavailable or locked by another user.");
        }

        // 2. Pobranie ceny (HTTP)
        BigDecimal price = productService.getProductPrice(command.productId());

        // 3. Logika domeny
        Cart cart = cartRepository.findByUserId(command.userId())
                .orElse(new Cart(command.userId()));

        cart.addItem(new CartItem(command.productId(), command.quantity(), price));
        cartRepository.save(cart);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3)
    public void handle(RemoveProductCommand command) {
        Cart cart = cartRepository.findByUserId(command.userId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.removeItem(command.productId());
        cartRepository.save(cart);
    }

    @Transactional
    public String handle(ConfirmCartCommand command) {
        Cart cart = cartRepository.findByUserId(command.userId())
                .orElseThrow(() -> new RuntimeException("Cart empty"));

        // Finalizacja: generowanie zlecenia [cite: 11]
        // Rozdzielenie domen: Tu kończy się rola Koszyka, zaczyna Zamówienia
        String orderId = "ORD-" + System.currentTimeMillis(); // Uproszczenie

        // Publikacja zdarzenia (Powiadomienie +0.25)
        eventPublisher.publishEvent(new OrderPlacedEvent(orderId, command.userId()));

        // Usunięcie koszyka po zamówieniu
        cartRepository.delete(cart);

        return orderId;
    }
}