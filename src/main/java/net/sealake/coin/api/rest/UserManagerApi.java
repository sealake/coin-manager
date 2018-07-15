package net.sealake.coin.api.rest;

import com.jcabi.aspects.Loggable;

import io.swagger.annotations.ApiOperation;

import net.sealake.coin.api.request.UserCreateRequest;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.service.GenericUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Loggable
@RestController
@RequestMapping(ApiConstants.API_V1)
public class UserManagerApi {
  @Autowired
  private GenericUserService userService;

  @PostMapping("/users")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "创建用户")
  public ResponseEntity<GenericUser> createUser(@RequestBody UserCreateRequest request) {

    GenericUser user = userService.createUser(request);
    return new ResponseEntity<GenericUser>(user, HttpStatus.CREATED);
  }

  @GetMapping("/users")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "分页展示所有用户")
  public Page<GenericUser> listUsers(@RequestParam final Integer page,
      @RequestParam final Integer size, @RequestParam final Sort.Direction sort) {

    int pageNumber = (page == null) ? 0 : page;
    int pageSize = (size == null) ? 10 : size;

    return userService.listUsers(pageNumber, pageSize, sort);
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "删除用户")
  public ResponseEntity<String> deleteUser(@PathVariable final Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
