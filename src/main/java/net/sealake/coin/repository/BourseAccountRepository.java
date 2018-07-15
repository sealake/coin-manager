package net.sealake.coin.repository;

import net.sealake.coin.entity.BourseAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BourseAccountRepository extends JpaRepository<BourseAccount, Long> {
}