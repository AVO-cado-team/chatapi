package sk.avo.chatapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

import sk.avo.chatapi.domain.security.JwtTokenService;
import sk.avo.chatapi.domain.security.dto.Tuple;
import sk.avo.chatapi.domain.security.exceptions.InvalidToken;
import sk.avo.chatapi.domain.user.UserService;
import sk.avo.chatapi.domain.user.exceptions.UserNotFoundException;
import sk.avo.chatapi.domain.user.models.UserModel;
import sk.avo.chatapi.security.model.UserRoles;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    public JwtRequestFilter(JwtTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        logger.info("doFilterInternal");
        logger.info(request.getRequestURI());
        logger.info(request.getMethod());
        logger.info(request.getHeader(HttpHeaders.AUTHORIZATION));
        logger.info(request.getHeader(HttpHeaders.CONTENT_TYPE));

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        Tuple<Long, String> tokenPayloadTuple;
        final String token = header.substring(7);
        final UserModel userModel;
        try {
            tokenPayloadTuple = jwtTokenService.validateTokenAndGetUserIdAndTokenType(token);
            userModel = userService.getUserById(tokenPayloadTuple.getFirst());
        } catch (final InvalidToken | UserNotFoundException e) {
            logger.info(e.getMessage());
            chain.doFilter(request, response);
            return;
        }
        if (!tokenPayloadTuple.getSecond().equals("access")) {
            logger.info("token is not access");
            chain.doFilter(request, response);
            return;
        }
        boolean isUserVerified = userModel.getIsVerified();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userModel,
                null,
                List.of((GrantedAuthority) () -> isUserVerified ? UserRoles.USER_VERIFIED : UserRoles.USER_UNVERIFIED)
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

}