package net.sealake.coin.repository;

import net.sealake.coin.entity.CoinAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface CoinAccountRepository extends JpaRepository<CoinAccount, Long> {

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @Query(value = "select t from CoinAccount t where t.id = :id")
  CoinAccount findByIdForUpdate(@Param("id") Long id);
}