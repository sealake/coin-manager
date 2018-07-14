package net.sealake.coin.configuration;

import net.sealake.coin.auth.BasicAuthenticationFilter;
import net.sealake.coin.auth.BasicAuthenticationProvider;
import net.sealake.coin.auth.BasicAuthenticationSuccessHandler;
import net.sealake.coin.auth.TokenAuthenticationFilter;
import net.sealake.coin.auth.TokenAuthenticationProvider;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.service.GenericUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  // @Autowired
  // private AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  private GenericUserService userService;

  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  @Autowired
  private BasicAuthenticationProvider basicAuthenticationProvider;

  @Autowired
  private BasicAuthenticationSuccessHandler loginSuccessHandler;

  @Autowired
  private Settings settings;

  @Override
  public void init(final WebSecurity web) throws Exception {
    super.init(web);
    //设置忽略目录
    web.ignoring().antMatchers("/swagger-ui.html",
        "/configuration/ui/**",
        "/configuration/security/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/v2/api-docs/**"
    );
  }

  /**
   * 配置url访问规则.
   */
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .exceptionHandling()
        // .authenticationEntryPoint(authenticationEntryPoint)
        // .and()
        // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests().antMatchers(HttpMethod.POST, ApiConstants.API_V1_LOGIN).permitAll()
        .and()
        .authorizeRequests().antMatchers(ApiConstants.API_V1 + "/**").authenticated()
        .and()
        .addFilterBefore(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.headers().cacheControl().disable();

  }

  @Override
  public void configure(final AuthenticationManagerBuilder auth) throws Exception {
    userService.createAdminIfNotExists();

    auth.authenticationProvider(tokenAuthenticationProvider);
    auth.authenticationProvider(basicAuthenticationProvider);
  }

  private BasicAuthenticationFilter loginAuthenticationFilter() throws Exception {
    AntPathRequestMatcher authMatcher = new AntPathRequestMatcher(ApiConstants.API_V1_LOGIN, "POST");
    BasicAuthenticationFilter filter = new BasicAuthenticationFilter(authMatcher, settings, loginSuccessHandler);
    filter.setAuthenticationManager(this.authenticationManagerBean());
    return filter;
  }

  private TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
    AntPathRequestMatcher authMatcher = new AntPathRequestMatcher(ApiConstants.API_V1 + "/**");
    TokenAuthenticationFilter filter = new TokenAuthenticationFilter(authMatcher);
    filter.setAuthenticationManager(this.authenticationManagerBean());
    return filter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new StandardPasswordEncoder();
  }
}
