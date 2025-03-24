
package com.ishyiga.service;

import com.ishyiga.entities.RefundCancelled;

import java.util.List;

public interface RefundCancelledService {
    List<RefundCancelled> getAllRefundsCancelled();
    RefundCancelled saveRefundCancelled(RefundCancelled refundCancelled);
    void deleteRefundCancelled(Long id);
}
