package net.sealake.coin.repository;

import net.sealake.coin.entity.CoinOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinOrderRepository extends JpaRepository<CoinOrder, Long> {

  CoinOrder findByTaskId(Long taskId);
}