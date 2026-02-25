package lat.luisdias.factory.manager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialCreateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialResponse;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialUpdateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.StockChangeRequest;
import lat.luisdias.factory.manager.service.RawMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RawMaterialResponse> create(@RequestBody @Valid RawMaterialCreateRequest req) {
        RawMaterialResponse response = service.create(req);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(response.code())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{code}")
    public RawMaterialResponse get(@PathVariable @NotBlank String code) {
        return service.getByCode(code);
    }

    @GetMapping
    public Page<RawMaterialResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping("/{code}")
    public RawMaterialResponse updateName(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid RawMaterialUpdateRequest req
    ) {
        return service.updateName(code, req);
    }

    @PostMapping("/{code}/stock/add")
    public RawMaterialResponse addStock(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid StockChangeRequest req
    ) {
        return service.addStock(code, req);
    }

    @PostMapping("/{code}/stock/consume")
    public RawMaterialResponse consumeStock(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid StockChangeRequest req
    ) {
        return service.consumeStock(code, req);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotBlank String code) {
        service.deleteByCode(code);
    }
}
