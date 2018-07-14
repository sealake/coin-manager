package net.sealake.coin.repository;

import net.sealake.coin.entity.GenericUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericUserRepository extends JpaRepository<GenericUser, Long> {
  GenericUser findByUsername(final String username);
}