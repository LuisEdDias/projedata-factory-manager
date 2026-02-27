package lat.luisdias.factory.manager.dto.productmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductMaterialRequest(
        @NotBlank(message = "{validation.product_material.code.required}")
        String rawMaterialCode,

        @NotNull(message = "{validation.product_material.quantity.required}")
        @DecimalMin(value = "0.0001", message = "{validation.product_material.quantity.min}")
        BigDecimal quantityRequired
) {
}
