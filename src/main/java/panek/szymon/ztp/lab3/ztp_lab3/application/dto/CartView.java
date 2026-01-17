package panek.szymon.ztp.lab3.ztp_lab3.application.dto;

import java.math.BigDecimal;

public record CartView(String cartId, int itemsCount, BigDecimal totalValue) {}
