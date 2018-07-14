package net.sealake.coin.util;

import net.sealake.coin.entity.RoleAuthority;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthorityUtil {

  public static final List<RoleAuthority> NO_AUTHORITIES = Collections.emptyList();

  public static List<RoleAuthority> commaSeparatedStringToAuthorityList(String authorityString) {
    return createAuthorityList(StringUtils.tokenizeToStringArray(authorityString, ","));
  }

  public static Set<String> authorityListToSet(Collection<? extends RoleAuthority> userAuthorities) {
    Set<String> set = new HashSet<String>(userAuthorities.size());

    for (RoleAuthority authority : userAuthorities) {
      set.add(authority.getAuthority());
    }

    return set;
  }

  public static List<RoleAuthority> createAuthorityList(String... roles) {
    List<RoleAuthority> authorities = new ArrayList<RoleAuthority>(roles.length);

    for (String role : roles) {
      authorities.add(new RoleAuthority(role));
    }

    return authorities;
  }
}
