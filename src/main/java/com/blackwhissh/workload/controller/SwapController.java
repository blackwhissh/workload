//package com.blackwhissh.workload.controller;
//
//import com.blackwhissh.workload.dto.SwapDTO;
//import com.blackwhissh.workload.dto.request.PublishSwapRequest;
//import com.blackwhissh.workload.dto.response.PublishSwapResponse;
//import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
//import com.blackwhissh.workload.service.SwapService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping(value = "/v1/swap")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class SwapController {
//    private final SwapService swapService;
//
//    public SwapController(SwapService swapService) {
//        this.swapService = swapService;
//    }
//
//    @PostMapping("/publish")
//    public ResponseEntity<PublishSwapResponse> publishSwap(@RequestBody PublishSwapRequest request) {
//        return ResponseEntity.ok(swapService.publishSwap(request));
//    }
//
//    @GetMapping("/by")
//    public ResponseEntity<List<SwapDTO>> listSwapsByStatus(@RequestParam(name = "status") RequestStatusEnum statusEnum) {
//        return ResponseEntity.ok(swapService.listSwapsByStatus(statusEnum));
//    }
//
//    @GetMapping()
//    public ResponseEntity<List<SwapDTO>> listAllSwaps() {
//        return ResponseEntity.of(Optional.ofNullable(swapService.listAllSwaps()));
//    }
//}
