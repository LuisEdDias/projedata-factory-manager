package lat.luisdias.factory.manager.dto.product;

import lat.luisdias.factory.manager.dto.productmaterial.ProductMaterialResponse;
import lat.luisdias.factory.manager.model.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        String code,
        String name,
        BigDecimal price,
        BigDecimal totalMaterialCost,
        BigDecimal unitProfit,
        List<ProductMaterialResponse> materials
) {
    public ProductResponse(Product product) {
        this(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getTotalMaterialCost(),
                product.getUnitProfit(),
                product.getComposition().stream()
                        .map(ProductMaterialResponse::new)
                        .toList()
        );
    }
}
