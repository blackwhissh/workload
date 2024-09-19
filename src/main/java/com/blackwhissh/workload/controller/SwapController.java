package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.SwapDTO;
import com.blackwhissh.workload.dto.request.PublishSwapRequest;
import com.blackwhissh.workload.dto.response.PublishSwapResponse;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.service.SwapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/swap")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SwapController {
    private final SwapService swapService;
    private final JwtUtils jwtUtils;

    public SwapController(SwapService swapService, JwtUtils jwtUtils) {
        this.swapService = swapService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishSwap(@RequestBody PublishSwapRequest request) {
        try {
            SwapDTO swapDTO = swapService.publishSwap(
                    request.publisherWorkId(),
                    request.hourIdList(),
                    request.targetDate(),
                    request.targetStart(),
                    request.targetEnd());

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

    @PostMapping("/receive/{swapId}")
    public ResponseEntity<?> receiveGift(@RequestHeader("Authorization") String jwt,
                                         @PathVariable(name = "swapId") int swapId) {
        jwt = jwt.substring(7);
        if (swapService.receiveSwap(jwtUtils.getWorkIdFromJwtToken(jwt), swapId)) {
            return ResponseEntity.ok("Swap received successfully, waiting for confirmation");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during receiving swap");
        }
    }

    @GetMapping("/by")
    public ResponseEntity<List<SwapDTO>> listSwapsByStatus(@RequestParam(name = "status") RequestStatusEnum statusEnum) {
        return ResponseEntity.ok(swapService.listSwapsByStatus(statusEnum));
    }

    @GetMapping()
    public ResponseEntity<List<SwapDTO>> listAllSwaps() {
        return ResponseEntity.of(Optional.ofNullable(swapService.listAllSwaps()));
    }
}
