package panek.szymon.ztp.lab3.ztp_lab3.infrastructure.http;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import panek.szymon.ztp.lab3.ztp_lab3.application.port.ProductServicePort;

import java.math.BigDecimal;

@Service
public class ProductServiceClient implements ProductServicePort {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PRODUCT_SERVICE_URL = "http://localhost:8081/products";

    @Override
    public boolean reserveProduct(String productId, int quantity) {
        return !"unavailable-item".equals(productId);

    }

    @Override
    public BigDecimal getProductPrice(String productId) {
        return new BigDecimal("99.99");
    }



}