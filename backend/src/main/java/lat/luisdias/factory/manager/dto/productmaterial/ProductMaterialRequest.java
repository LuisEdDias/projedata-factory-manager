package lat.luisdias.factory.manager.dto.productmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ProductMaterialRequest(
        @NotBlank(message = "{validation.product_material.code.required}")
        @Pattern(regexp = "^[A-Za-z0-9_-]{3,50}$", message = "{validation.product_material.code.invalid_format}")
        String rawMaterialCode,

        @NotNull(message = "{validation.product_material.quantity.required}")
        @DecimalMin(value = "0.0001", message = "{validation.product_material.quantity.min}")
        BigDecimal quantityRequired
) {
}
