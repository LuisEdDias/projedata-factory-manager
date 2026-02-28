package lat.luisdias.factory.manager.dto.rawmaterial;

import jakarta.validation.constraints.*;
import lat.luisdias.factory.manager.model.MeasurementUnit;

import java.math.BigDecimal;

public record RawMaterialCreateRequest(
        @NotBlank(message = "{validation.raw_material.code.required}")
        @Pattern(regexp = "^[A-Za-z0-9_-]{3,50}$", message = "{validation.raw_material.code.invalid_format}")
        String code,

        @NotBlank(message = "{validation.raw_material.name.required}")
        @Size(max = 255, message = "{validation.raw_material.name.size}")
        String name,

        @NotNull(message = "{validation.raw_material.initial_stock.required}")
        @DecimalMin(value = "0.0", message = "{validation.raw_material.initial_stock.min}")
        BigDecimal initialStock,

        @NotNull(message = "{validation.raw_material.unit.required}")
        MeasurementUnit unit,

        @NotNull(message = "{validation.raw_material.unit_cost.required}")
        @DecimalMin(value = "0.0", message = "{validation.raw_material.unit_cost.min}")
        BigDecimal unitCost
) {
}
