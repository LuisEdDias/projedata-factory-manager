package lat.luisdias.factory.manager.service;

import lat.luisdias.factory.manager.dto.production.ProductionPlanItem;
import lat.luisdias.factory.manager.dto.production.ProductionPlanResponse;
import lat.luisdias.factory.manager.model.Product;
import lat.luisdias.factory.manager.model.ProductMaterial;
import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.repository.ProductRepository;
import lat.luisdias.factory.manager.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductionOptimizationService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionOptimizationService(
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository
    ) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @Transactional(readOnly = true)
    public ProductionPlanResponse calculateOptimalProductionPlan() {
        List<Product> allProducts = productRepository.findAll();

        if (!allProducts.isEmpty()) {
            productRepository.fetchCompositionsForProducts(allProducts);
        }

        List<RawMaterial> allMaterials = rawMaterialRepository.findAll();

        Map<String, BigDecimal> virtualStock = allMaterials.stream()
                .collect(Collectors.toMap(
                        RawMaterial::getCode,
                        RawMaterial::getStockQuantity
                ));

        List<Product> sortedProducts = allProducts.stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .toList();

        List<ProductionPlanItem> planItems = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Product product : sortedProducts) {
            if (product.getComposition().isEmpty()) continue;

            long maxUnitsToProduce = calculateMaxUnitsPossible(product, virtualStock);

            if (maxUnitsToProduce > 0) {
                deductFromVirtualStock(product, maxUnitsToProduce, virtualStock);

                BigDecimal itemRevenue = product.getPrice().multiply(BigDecimal.valueOf(maxUnitsToProduce));
                totalRevenue = totalRevenue.add(itemRevenue);

                planItems.add(new ProductionPlanItem(
                        product.getCode(),
                        product.getName(),
                        maxUnitsToProduce,
                        itemRevenue
                ));
            }
        }

        return new ProductionPlanResponse(planItems, totalRevenue, virtualStock);
    }

    private long calculateMaxUnitsPossible(Product product, Map<String, BigDecimal> currentStock) {
        long maxPossible = Long.MAX_VALUE;

        for (ProductMaterial material : product.getComposition()) {
            String materialCode = material.getRawMaterial().getCode();
            BigDecimal available = currentStock.getOrDefault(materialCode, BigDecimal.ZERO);
            BigDecimal required = material.getQuantityRequired();

            if (required == null || available.compareTo(BigDecimal.ZERO) <= 0 || required.compareTo(BigDecimal.ZERO) <= 0) {
                return 0;
            }

            long possible = available.divide(required, 0, RoundingMode.DOWN).longValue();
            maxPossible = Math.min(maxPossible, possible);

            if (maxPossible == 0) return 0;
        }

        return maxPossible == Long.MAX_VALUE ? 0 : maxPossible;
    }

    private void deductFromVirtualStock(Product product, long quantityProduced, Map<String, BigDecimal> currentStock) {
        BigDecimal units = BigDecimal.valueOf(quantityProduced);

        for (ProductMaterial material : product.getComposition()) {
            String materialCode = material.getRawMaterial().getCode();
            BigDecimal totalRequired = material.getQuantityRequired().multiply(units);

            BigDecimal current = currentStock.getOrDefault(materialCode, BigDecimal.ZERO);
            currentStock.put(materialCode, current.subtract(totalRequired));
        }
    }
}
