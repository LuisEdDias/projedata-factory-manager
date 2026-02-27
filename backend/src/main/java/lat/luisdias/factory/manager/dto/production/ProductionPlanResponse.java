package lat.luisdias.factory.manager.dto.production;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductionPlanResponse(
        List<ProductionPlanItem> plan,
        BigDecimal estimatedTotalRevenue,
        Map<String, BigDecimal> remainingStock
) {
}
