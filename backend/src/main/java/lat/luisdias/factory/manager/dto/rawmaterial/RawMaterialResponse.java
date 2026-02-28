package lat.luisdias.factory.manager.dto.rawmaterial;

import lat.luisdias.factory.manager.model.MeasurementUnit;
import lat.luisdias.factory.manager.model.RawMaterial;

import java.math.BigDecimal;

public record RawMaterialResponse(
        String code,
        String name,
        BigDecimal stockQuantity,
        MeasurementUnit unit,
        BigDecimal unitCost
) {
    public RawMaterialResponse(RawMaterial rawMaterial) {
        this(
                rawMaterial.getCode(),
                rawMaterial.getName(),
                rawMaterial.getStockQuantity(),
                rawMaterial.getUnit(),
                rawMaterial.getUnitCost()
        );
    }
}
