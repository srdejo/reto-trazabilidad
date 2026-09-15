package co.com.srdejo.trazabilidad.infrastructure.input.rest;

import co.com.srdejo.trazabilidad.application.dto.request.TraceabilityRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;
import co.com.srdejo.trazabilidad.application.handler.ITraceabilityHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/traceability")
@RequiredArgsConstructor
public class TraceabilityRestController {

    private final ITraceabilityHandler traceabilityHandler;

    @Operation(
            summary = "Register an order status change",
            description = "Creates a traceability record every time an order changes status. " +
                    "Invoked by the order service (reto-plazoleta) on each status transition."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Traceability record created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraceabilityResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Role not authorized to register status changes", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TraceabilityResponseDto> registerStatusChange(
            @Valid @RequestBody TraceabilityRequestDto traceabilityRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traceabilityHandler.registerStatusChange(traceabilityRequestDto));
    }

    @Operation(
            summary = "Get the authenticated customer's traceability history",
            description = "Returns the status-change history for the authenticated customer's orders. " +
                    "The customer id is taken from the JWT token, so a customer can only ever see their own history. " +
                    "Pass 'orderId' to filter the history down to a single order; the order is still scoped to the " +
                    "authenticated customer, so requesting an order that belongs to someone else returns an empty list."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Traceability history returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TraceabilityResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Role not authorized to view the history", content = @Content)
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<TraceabilityResponseDto>> getHistoryByCustomer(
            @Parameter(description = "Order id to filter the history by", example = "1")
            @RequestParam(required = false) Long orderId) {
        return ResponseEntity.ok(traceabilityHandler.getHistoryByCustomer(orderId));
    }
}
