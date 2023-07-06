package sk.avo.chatapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import sk.avo.chatapi.domain.service.UserService;
import sk.avo.chatapi.domain.shared.Tuple;
import sk.avo.chatapi.security.shared.UserRoles;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
  private final ApplicationService applicationService;
  private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

  public JwtRequestFilter(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws ServletException, IOException {
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }
    Tuple<Long, String> tokenPayloadTuple;
    final String token = header.substring(7);
    final UserEntity userEntity;
    try {
      tokenPayloadTuple = applicationService.validateTokenAndGetUserIdAndTokenType(token);
      userEntity = applicationService.callDomainService(UserService.class).getUserById(tokenPayloadTuple.getFirst());
    } catch (final InvalidTokenException | UserNotFoundException e) {
      logger.info(e.getMessage());
      chain.doFilter(request, response);
      return;
    }
    if (!tokenPayloadTuple.getSecond().equals("access")) {
      logger.info("token is not access");
      chain.doFilter(request, response);
      return;
    }
    boolean isUserVerified = userEntity.getIsVerified();
    final UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
                userEntity,
            null,
            List.of(
                (GrantedAuthority)
                    () -> isUserVerified ? UserRoles.USER_VERIFIED : UserRoles.USER_UNVERIFIED));
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(request, response);
  }
}
