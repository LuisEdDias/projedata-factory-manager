package lat.luisdias.factory.manager.dto.rawmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StockChangeRequest(

        @NotNull(message = "{validation.raw_material.change_stock.required}")
        @DecimalMin(value = "0.1", message = "{validation.raw_material.change_stock.min}")
        BigDecimal quantity
) {
}
