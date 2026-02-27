package lat.luisdias.factory.manager.dto.rawmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lat.luisdias.factory.manager.model.MeasurementUnit;

import java.math.BigDecimal;

public record RawMaterialCreateRequest(
        @NotBlank(message = "{validation.raw_material.code.required}")
        @Size(max = 50, message = "{validation.raw_material.code.size}")
        String code,

        @NotBlank(message = "{validation.raw_material.name.required}")
        @Size(max = 255, message = "{validation.raw_material.name.size}")
        String name,

        @NotNull(message = "{validation.raw_material.initial_stock.required}")
        @DecimalMin(value = "0.0", message = "{validation.raw_material.initial_stock.min}")
        BigDecimal initialStock,

        @NotNull(message = "{validation.raw_material.unit.required}")
        MeasurementUnit unit
) {
}
