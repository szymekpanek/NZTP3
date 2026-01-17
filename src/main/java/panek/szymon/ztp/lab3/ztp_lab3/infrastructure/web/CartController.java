package panek.szymon.ztp.lab3.ztp_lab3.infrastructure.web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.AddProductCommand;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.ConfirmCartCommand;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.GetCartQuery;
import panek.szymon.ztp.lab3.ztp_lab3.application.dto.RemoveProductCommand;
import panek.szymon.ztp.lab3.ztp_lab3.application.service.CartService;
import panek.szymon.ztp.lab3.ztp_lab3.domain.Cart;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Utworzenie / Podgląd koszyka [cite: 7, 10]
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(new GetCartQuery(userId)));
    }

    // Dodanie produktu [cite: 8]
    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addProduct(@PathVariable String userId, @RequestBody AddProductCommand cmd) {
        // Nadpisujemy userId z path variable dla bezpieczeństwa
        cartService.handle(new AddProductCommand(userId, cmd.productId(), cmd.quantity()));
        return ResponseEntity.ok().build();
    }

    // Usunięcie produktu [cite: 9]
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeProduct(@PathVariable String userId, @PathVariable String productId) {
        cartService.handle(new RemoveProductCommand(userId, productId));
        return ResponseEntity.ok().build();
    }

    // Zatwierdzenie koszyka [cite: 11]
    @PostMapping("/{userId}/confirm")
    public ResponseEntity<String> confirmCart(@PathVariable String userId) {
        String orderId = cartService.handle(new ConfirmCartCommand(userId));
        return ResponseEntity.ok(orderId);
    }
}