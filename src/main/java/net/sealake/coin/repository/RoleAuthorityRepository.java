package net.sealake.coin.repository;

import net.sealake.coin.entity.RoleAuthority;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleAuthorityRepository extends JpaRepository<RoleAuthority, Long> {
  RoleAuthority findByAuthority(final String authority);
}
