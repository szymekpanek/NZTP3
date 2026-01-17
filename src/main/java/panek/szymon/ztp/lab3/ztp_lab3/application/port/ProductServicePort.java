package panek.szymon.ztp.lab3.ztp_lab3.application.port;


import java.math.BigDecimal;

public interface ProductServicePort {
    boolean reserveProduct(String productId, int quantity);
    BigDecimal getProductPrice(String productId);
}
