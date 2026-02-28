package lat.luisdias.factory.manager.dto.production;

import java.math.BigDecimal;

public record ProductionPlanItem(
        String productCode,
        String productName,
        Long quantityToProduce,
        BigDecimal totalValue,
        BigDecimal itemProfit
) {
}
