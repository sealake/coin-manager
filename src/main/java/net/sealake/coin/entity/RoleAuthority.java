package net.sealake.coin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuthority implements GrantedAuthority {
  private static final long serialVersionUID = 2543432119441562908L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "authority", unique = true, length = 30)
  private String authority;

  public RoleAuthority(String authority) {
    this.authority = authority;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RoleAuthority) {
      return StringUtils.equals(authority, ((RoleAuthority) obj).authority);
    }
    return false;
  }

  public int hashCode() {
    return this.authority.hashCode();
  }

  public String toString() {
    return this.authority;
  }

}