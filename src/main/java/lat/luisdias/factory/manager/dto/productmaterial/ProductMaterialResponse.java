package lat.luisdias.factory.manager.dto.productmaterial;

import lat.luisdias.factory.manager.model.MeasurementUnit;
import lat.luisdias.factory.manager.model.ProductMaterial;

import java.math.BigDecimal;

public record ProductMaterialResponse(
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal quantityRequired,
        MeasurementUnit unit
) {
    public ProductMaterialResponse(ProductMaterial material) {
        this(
                material.getRawMaterial().getCode(),
                material.getRawMaterial().getName(),
                material.getQuantityRequired(),
                material.getRawMaterial().getUnit()
        );
    }
}
