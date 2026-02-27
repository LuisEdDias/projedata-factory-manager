package lat.luisdias.factory.manager.dto.product;

import lat.luisdias.factory.manager.dto.productmaterial.ProductMaterialResponse;
import lat.luisdias.factory.manager.model.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        String code,
        String name,
        BigDecimal price,
        List<ProductMaterialResponse> materials
) {
    public ProductResponse(Product product) {
        this(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getComposition().stream()
                        .map(ProductMaterialResponse::new)
                        .toList()
        );
    }
}
