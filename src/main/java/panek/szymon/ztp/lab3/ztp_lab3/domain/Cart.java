package panek.szymon.ztp.lab3.ztp_lab3.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "carts")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    private String id;

    // Unikalny identyfikator użytkownika [cite: 29]
    private String userId;

    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime lastModified;

    // Wersja do obsługi współbieżności
    @Version
    private Long version;

    public Cart(String userId) {
        this.userId = userId;
        this.lastModified = LocalDateTime.now();
    }

    public void addItem(CartItem item) {
        this.items.add(item);
        this.lastModified = LocalDateTime.now();
    }

    public void removeItem(String productId) {
        this.items.removeIf(i -> i.getProductId().equals(productId));
        this.lastModified = LocalDateTime.now();
    }

    public BigDecimal getTotalValue() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}