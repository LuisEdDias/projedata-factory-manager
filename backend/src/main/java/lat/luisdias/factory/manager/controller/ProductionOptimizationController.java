package lat.luisdias.factory.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lat.luisdias.factory.manager.dto.production.ProductionPlanResponse;
import lat.luisdias.factory.manager.service.ProductionOptimizationService;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production/plan")
@Tag(name = "Production Optimization", description = "Endpoints for generating optimal production plans using a greedy algorithm.")
public class ProductionOptimizationController {

    private final ProductionOptimizationService optimizationService;

    public ProductionOptimizationController(ProductionOptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @GetMapping("/revenue")
    @Operation(
            summary = "Calculate the optimal production plan (Max Revenue)",
            description = "Analyzes current raw material stock and product recipes to maximize total revenue using a greedy algorithm. This is a read-only simulation and does not deduct actual stock."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error during calculation", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductionPlanResponse calculatePlanByRevenue() {
        return optimizationService.getPlanByRevenue();
    }


    @GetMapping("/profit")
    @Operation(
            summary = "Calculate the optimal production plan (Max Profit)",
            description = "Analyzes current raw material stock, product recipes, and raw material costs to maximize net profit using a greedy algorithm. This is a read-only simulation and does not deduct actual stock."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error during calculation", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductionPlanResponse calculatePlanByProfit() {
        return optimizationService.getPlanByProfit();
    }
}
