package net.sealake.coin.repository;

import net.sealake.coin.entity.RoleAuthority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAuthorityRepository extends JpaRepository<RoleAuthority, Long> {
  RoleAuthority findByAuthority(final String authority);
}
