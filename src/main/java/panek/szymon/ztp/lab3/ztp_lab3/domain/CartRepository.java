package panek.szymon.ztp.lab3.ztp_lab3.domain; // Upewnij się, że pakiet jest Twój

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findByUserId(String userId);
    void delete(Cart cart);

    void deleteAll(Iterable<? extends Cart> entities);
    void deleteAll();

    List<Cart> findAllByLastModifiedBefore(LocalDateTime dateTime);
}