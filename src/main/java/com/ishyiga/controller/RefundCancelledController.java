
package com.ishyiga.controller;

import com.ishyiga.entities.RefundCancelled;
import com.ishyiga.service.RefundCancelledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refunds_cancelled")
class RefundCancelledController {
    @Autowired
    private RefundCancelledService refundCancelledService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public List<RefundCancelled> getAllRefundsCancelled() {
        return refundCancelledService.getAllRefundsCancelled();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public RefundCancelled createRefundCancelled(@RequestBody RefundCancelled refundCancelled) {
        return refundCancelledService.saveRefundCancelled(refundCancelled);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public void deleteRefundCancelled(@PathVariable Long id) {
        refundCancelledService.deleteRefundCancelled(id);
    }
}
