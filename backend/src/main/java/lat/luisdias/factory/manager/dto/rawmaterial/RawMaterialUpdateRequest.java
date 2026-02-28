package lat.luisdias.factory.manager.dto.rawmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RawMaterialUpdateRequest(
        @NotBlank(message = "{validation.raw_material.name.required}")
        @Size(max = 255, message = "{validation.raw_material.name.size}")
        String name,

        @NotNull(message = "{validation.raw_material.unit_cost.required}")
        @DecimalMin(value = "0.0", message = "{validation.raw_material.unit_cost.min}")
        BigDecimal unitCost
) {
}
