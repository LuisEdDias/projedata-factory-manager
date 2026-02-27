package lat.luisdias.factory.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/raw-materials")
@Tag(name = "Raw Materials", description = "Endpoints for managing raw materials and their inventory.")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Register a new raw material", description = "Create a raw material with initial stock and measurement unit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated (e.g., duplicate code)", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
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
    @Operation(summary = "Search for raw material by code", description = "Returns the raw material details and current stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RawMaterialResponse get(@PathVariable @NotBlank String code) {
        return service.getByCode(code);
    }

    @GetMapping
    @Operation(summary = "List raw materials", description = "Returns a paginated list of raw materials.")
    @ApiResponse(responseCode = "200", description = "Success")
    public Page<RawMaterialResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping("/{code}")
    @Operation(
            summary = "Update raw material name",
            description = "Allows you to update the raw material name independently."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RawMaterialResponse updateName(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid RawMaterialUpdateRequest req
    ) {
        return service.updateName(code, req);
    }

    @PostMapping("/{code}/stock/add")
    @Operation(
            summary = "Add stock",
            description = "Increases the available stock for the specified raw material."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data (e.g., negative quantity)", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RawMaterialResponse addStock(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid StockChangeRequest req
    ) {
        return service.addStock(code, req);
    }

    @PostMapping("/{code}/stock/consume")
    @Operation(
            summary = "Consume stock",
            description = "Decreases the available stock for the raw material."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated (e.g., insufficient stock)", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RawMaterialResponse consumeStock(
            @PathVariable @NotBlank String code,
            @RequestBody @Valid StockChangeRequest req
    ) {
        return service.consumeStock(code, req);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a raw material",
            description = "Removes the raw material from the database if it is not being used in any product recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated (e.g., raw material is in use by a product)", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void delete(@PathVariable @NotBlank String code) {
        service.deleteByCode(code);
    }
}
