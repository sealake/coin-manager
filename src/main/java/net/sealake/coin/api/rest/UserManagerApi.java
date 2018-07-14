package net.sealake.coin.api.rest;

import net.sealake.coin.api.request.UserCreateRequest;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.service.GenericUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.API_V1)
public class UserManagerApi {
  @Autowired
  private GenericUserService userService;

  @PostMapping("/users")
  @PreAuthorize(Authorizes.ADMIN)
  public GenericUser createUser(@RequestBody UserCreateRequest request) {

    return userService.createUser(request);
  }

  @GetMapping("/users")
  public Page<GenericUser> listUsers(@RequestParam final Integer page,
      @RequestParam final Integer size, @RequestParam final Sort.Direction sort) {

    int pageNumber = (page == null) ? 0 : page;
    int pageSize = (size == null) ? 10 : size;

    return userService.listUsers(pageNumber, pageSize, sort);
  }
}