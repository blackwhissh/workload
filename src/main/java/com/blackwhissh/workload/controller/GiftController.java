package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.dto.request.PublishGiftRequest;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.service.GiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/gift")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GiftController {
    private final GiftService giftService;
    private final JwtUtils jwtUtils;

    public GiftController(GiftService giftService, JwtUtils jwtUtils) {
        this.giftService = giftService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/publish")
    public ResponseEntity<GiftDTO> publishGift(@RequestHeader(name = "Authorization") String jwt,
                                               @RequestBody List<Integer> hourIdList) {
        jwt = jwt.substring(7);
        String workId = jwtUtils.getWorkIdFromJwtToken(jwt);
        return ResponseEntity.ok(giftService.publishGift(workId, hourIdList));
    }

    @GetMapping("/{workId}")
    public ResponseEntity<List<GiftDTO>> getGiftsByWorkId(@PathVariable String workId) {
        return ResponseEntity.ok(giftService.getGiftsByWorkId(workId));
    }

    @GetMapping("/current")
    public ResponseEntity<List<GiftDTO>> getGiftsByCurrentEmployee(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        return ResponseEntity.ok(giftService.getGiftsByWorkId(jwtUtils.getWorkIdFromJwtToken(jwt)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<GiftDTO>> getAllActiveGifts() {
        return ResponseEntity.ok(giftService.getAllActiveGifts());
    }

    @DeleteMapping("/delete/{giftId}")
    public ResponseEntity<?> deleteCurrentUserGift(@PathVariable(name = "giftId") int giftId,
                                                   @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (giftService.deleteCurrentUserGift(jwtUtils.getWorkIdFromJwtToken(jwt), giftId)) {
            return ResponseEntity.ok("Gift deleted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during gift deletion");
        }
    }

    @PostMapping("/receive/{giftId}")
    public ResponseEntity<?> receiveGift(@RequestHeader("Authorization") String jwt,
                                         @PathVariable(name = "giftId") int giftId) {
        jwt = jwt.substring(7);
        if (giftService.receiveGift(jwtUtils.getWorkIdFromJwtToken(jwt), giftId)) {
            return ResponseEntity.ok("Gift received successfully, waiting for confirmation");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during receiving gift");
        }
    }

    @PostMapping("/accept/{giftId}")
    public ResponseEntity<?> acceptGift(@PathVariable(name = "giftId") int giftId) {
        if (giftService.acceptGift(giftId)) {
            return ResponseEntity.ok("Gift accepted successfully");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during accepting gift");
        }
    }
}
