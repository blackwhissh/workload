package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.dto.request.PublishGiftRequest;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.service.GiftService;
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
@RequestMapping("/v1/gift")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GiftController {
    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping("/publish")
    @Operation(summary = "Publish gift")
    public ResponseEntity<GiftDTO> publishGift(@RequestBody PublishGiftRequest request,
                                               @RequestParam(name = "receiver") Optional<String> receiverWorkId) {
        String workId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(giftService.publishGift(workId, request.giftDate(), request.start(), request.end(), receiverWorkId));
    }

    @GetMapping("/{workId}")
    @Operation(summary = "Get gifts by work id")
    public ResponseEntity<List<GiftDTO>> getGiftsByWorkId(@PathVariable String workId) {
        return ResponseEntity.ok(giftService.getGiftsByWorkId(workId));
    }

    @GetMapping("/current")
    @Operation(summary = "Get currently logged in user's gifts")
    public ResponseEntity<List<GiftDTO>> getGiftsByCurrentEmployee() {
        return ResponseEntity.ok(giftService.getGiftsByWorkId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    @Operation(summary = "Get all gifts, used by manager")
    public ResponseEntity<List<GiftDTO>> getAllGifts(){
        return ResponseEntity.ok(giftService.getAllGifts());
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/filter")
    @Operation(summary = "Get gifts by status, used by manager")
    public ResponseEntity<?> getAllByStatus(@RequestParam(name = "status") String status) {
        if (Arrays.stream(RequestStatusEnum.values()).anyMatch(statusEnum -> status.equalsIgnoreCase(statusEnum.toString()))) {
            return ResponseEntity.ok(giftService.getAllGiftsByStatus(RequestStatusEnum.valueOf(status.toUpperCase())));
        }
        return ResponseEntity.badRequest().body("Wrong status provided");
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active gifts")
    public ResponseEntity<List<GiftDTO>> getAllActiveGifts() {
        return ResponseEntity.ok(giftService.getAllActiveGifts());
    }

    @DeleteMapping("/delete/{giftId}")
    @Operation(summary = "Delete currently logged in user's gift by gift id")
    public ResponseEntity<?> deleteCurrentUserGift(@PathVariable(name = "giftId") int giftId) {
        if (giftService.deleteCurrentUserGift(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), giftId)) {
            return ResponseEntity.ok("Gift deleted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during gift deletion");
        }
    }

    @PostMapping("/receive/{giftId}")
    @Operation(summary = "Receive gift by currently logged in user")
    public ResponseEntity<?> receiveGift(@PathVariable(name = "giftId") int giftId) {

        if (giftService.receiveGift(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), giftId)) {
            return ResponseEntity.ok("Gift received successfully, waiting for confirmation");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during receiving gift");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/accept/{giftId}")
    @Operation(summary = "Accept gift by manager")
    public ResponseEntity<?> acceptGift(@PathVariable(name = "giftId") int giftId) {
        if (giftService.acceptGift(giftId)) {
            return ResponseEntity.ok("Gift accepted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during accepting gift");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/reject/{giftId}")
    @Operation(summary = "Reject gift by manager")
    public ResponseEntity<?> rejectGift(@PathVariable(name = "giftId") int giftId) {
        try {
            giftService.rejectGift(giftId);
            return ResponseEntity.ok("Gift rejected successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error occurred during rejecting gift");
        }
    }
}
