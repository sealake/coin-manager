package net.sealake.coin.repository;

import net.sealake.coin.entity.CoinAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinAccountRepository extends JpaRepository<CoinAccount, Long> {
}