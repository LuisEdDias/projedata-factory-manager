package lat.luisdias.factory.manager.dto.rawmaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RawMaterialUpdateRequest(
        @NotBlank(message = "{validation.raw_material.name.required}")
        @Size(max = 150, message = "{validation.raw_material.name.size}")
        String name
) {
}
