
package com.ishyiga.repo;

import com.ishyiga.entities.RefundCancelled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundCancelledRepository extends JpaRepository<RefundCancelled, Long> {}
