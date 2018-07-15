package net.sealake.coin.repository;

import net.sealake.coin.entity.CoinSellStrategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinSellStrategyRepository extends JpaRepository<CoinSellStrategy, Long> {
}