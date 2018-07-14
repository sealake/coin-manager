package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.UserCreateRequest;
import net.sealake.coin.configuration.Settings;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.entity.RoleAuthority;
import net.sealake.coin.entity.enums.UserStatusEnum;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.repository.GenericUserRepository;
import net.sealake.coin.repository.RoleAuthorityRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class GenericUserService implements UserDetailsService {

  @Autowired
  private Settings settings;

  @Autowired
  private GenericUserRepository userRepo;

  @Autowired
  private RoleAuthorityRepository authRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * 如果数据库表中不存在admin用户，创建该用户
   * @return 创建或查询出的admin用户信息
   */
  @Transactional
  public GenericUser createAdminIfNotExists() {
    GenericUser user = findByUsername(settings.getAdminUserName());
    if (null == user) {
      user = createUser(settings.getAdminUserName(), settings.getAdminPassword(), Authorizes.ROLE_ADMIN);
    }

    return user;
  }

  /**
   * 创建用户
   * @param request
   * @return
   */
  @Transactional
  public GenericUser createUser(UserCreateRequest request) {
    return createUser(request.getUsername(), request.getPassword(), request.getRole().getCode());
  }

  /**
   * 检查数据库中是否存在username和password匹配的用户
   * @param username
   * @param password
   * @return 如果存在，则返回，否则返回null
   */
  public GenericUser checkUser(final String username, final String password) {
    try {
      GenericUser user = findByUsername(username);
      if (null != user &&
          passwordEncoder.matches(password, user.getPassword())) {
        return user;
      }
    }catch (Exception ex) {
      log.error("login check failed! username: {}, password: {}, errors: {}", username, password, ex);
      throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
    }

    return null;
  }

  public Page<GenericUser> listUsers(int page, int size, Sort.Direction sort) {
    Pageable pageable = new PageRequest(page, size, sort, "id");
    return userRepo.findAll(pageable);
  }

  public GenericUser findByUserId(final long id) {
    return userRepo.findOne(id);
  }

  public GenericUser findByUsername(final String username) {
    return userRepo.findByUsername(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findByUsername(username);
  }

  private GenericUser createUser(final String username, final String password, final String authority) {

    RoleAuthority auth = authRepo.findByAuthority(authority);
    if (null == auth) {
      log.warn("failed get RoleAuthority by authStr: {}, create it now!", authority);
      auth = RoleAuthority.builder().authority(authority).build();
      auth = authRepo.save(auth);
    }

    GenericUser user = new GenericUser();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setUserStatus(UserStatusEnum.ACTIVE);
    user.getAuthorities().add(auth);

    DateTime curTime = DateTime.now();
    user.setCreateTime(curTime);
    user.setLastModifiedTime(curTime);
    return userRepo.save(user);
  }
}
