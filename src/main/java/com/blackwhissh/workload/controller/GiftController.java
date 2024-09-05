package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.dto.request.PublishGiftRequest;
import com.blackwhissh.workload.service.GiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/gift")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GiftController {
    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping("/publish")
    public ResponseEntity<GiftDTO> publishGift(@RequestBody PublishGiftRequest request) {
        return ResponseEntity.ok(giftService.publishGift(request));
    }

    @GetMapping("/{workId}")
    public ResponseEntity<List<GiftDTO>> getGiftsByWorkId(@PathVariable String workId) {
        return ResponseEntity.ok(giftService.getGiftsByWorkId(workId));
    }
}
