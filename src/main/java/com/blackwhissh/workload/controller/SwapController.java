package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.dto.SwapDTO;
import com.blackwhissh.workload.dto.request.PublishSwapRequest;
import com.blackwhissh.workload.dto.response.PublishSwapResponse;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.service.SwapService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/swap")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SwapController {
    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/publish")
    @Operation(summary = "Publish swap, by employee")
    public ResponseEntity<?> publishSwap(@RequestBody PublishSwapRequest request,
                                         @RequestParam(name = "receiver") Optional<String> receiverWorkId) {

        String workId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            SwapDTO swapDTO = swapService.publishSwap(
                    workId,
                    request.swapDate(),
                    request.start(),
                    request.end(),
                    request.targetDate(),
                    request.targetStart(),
                    request.targetEnd(),
                    receiverWorkId);

            return ResponseEntity.ok(
                    new PublishSwapResponse(
                            swapDTO.swapId(),
                            swapDTO.publisherWorkId(),
                            swapDTO.receiverWorkId(),
                            swapDTO.hourDTO(),
                            swapDTO.swapDate(),
                            swapDTO.publishDate(),
                            swapDTO.status(),
                            swapDTO.targetDate(),
                            swapDTO.targetStart(),
                            swapDTO.targetEnd()
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error encountered during publishing swap");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/{workId}")
    @Operation(summary = "Get swaps by work id, by manager")
    public ResponseEntity<List<SwapDTO>> getSwapsByWorkId(@PathVariable String workId) {
        return ResponseEntity.ok(swapService.getSwapsByWorkId(workId));
    }

    @GetMapping("/current")
    @Operation(summary = "Get currently logged in employee's swaps")
    public ResponseEntity<List<SwapDTO>> getSwapsByCurrentEmployee() {
        return ResponseEntity.ok(swapService.getSwapsByWorkId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    @Operation(summary = "Get all swaps, by manager")
    public ResponseEntity<List<SwapDTO>> getAllSwaps(){
        return ResponseEntity.ok(swapService.getAllSwaps());
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/filter")
    @Operation(summary = "Get all swaps by status, by manager")
    public ResponseEntity<?> getAllByStatus(@RequestParam(name = "status") String status) {
        if (Arrays.stream(RequestStatusEnum.values()).anyMatch(statusEnum -> status.equalsIgnoreCase(statusEnum.toString()))) {
            return ResponseEntity.ok(swapService.getAllSwapsByStatus(RequestStatusEnum.valueOf(status.toUpperCase())));
        }
        return ResponseEntity.badRequest().body("Wrong status provided");
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active swaps")
    public ResponseEntity<List<SwapDTO>> getAllActiveSwaps() {
        return ResponseEntity.ok(swapService.getAllActiveSwaps());
    }
    @DeleteMapping("/delete/{swapId}")
    @Operation(summary = "Delete currently logged in employee's swap")
    public ResponseEntity<?> deleteCurrentUserSwap(@PathVariable(name = "swapId") int swapId) {
        if (swapService.deleteCurrentUserSwap(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), swapId)) {
            return ResponseEntity.ok("Swap deleted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during swap deletion");
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/receive/{swapId}")
    @Operation(summary = "Receive swap by currently logged in user")
    public ResponseEntity<?> receiveSwap(@PathVariable(name = "swapId") int swapId) {
        if (swapService.receiveSwap(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), swapId)) {
            return ResponseEntity.ok("Swap received successfully, waiting for confirmation");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during receiving swap");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/accept/{swapId}")
    @Operation(summary = "Accept swap, by manager")
    public ResponseEntity<?> acceptSwap(@PathVariable(name = "swapId") int swapId) {
        if (swapService.acceptSwap(swapId)) {
            return ResponseEntity.ok("Swap accepted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during accepting swap");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/reject/{swapId}")
    @Operation(summary = "Reject swap, by manager")
    public ResponseEntity<?> rejectSwap(@PathVariable(name = "swapId") int swapId) {
        try {
            swapService.rejectSwap(swapId);
            return ResponseEntity.ok("Swap rejected successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error occurred during rejecting swap");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/filter-non-received")
    @Operation(summary = "Delete non-received swaps")
    public ResponseEntity<?> filterNonReceivedSwaps() {
        swapService.filterNonReceivedSwaps();
        return ResponseEntity.ok("Filtered non-received swaps");
    }
}
