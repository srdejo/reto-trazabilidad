package co.com.srdejo.trazabilidad.infrastructure.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String SECRET = "a-secret-key-with-at-least-32-bytes!!";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(SECRET);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private String validToken() {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(String.valueOf((Long) 15L))
                .claim("role", "CUSTOMER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(secretKey)
                .compact();
    }

    @Test
    void doFilterInternal_withValidToken_authenticatesWithTheRolePrefixedAsAnAuthority() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken());

        filter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(15L);
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_CUSTOMER");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withoutAuthorizationHeader_continuesUnauthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withMalformedToken_clearsSecurityContextAndContinues() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer not-a-real-token");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withTokenSignedByAnotherKey_clearsSecurityContextAndContinues() throws Exception {
        SecretKey otherKey = Keys.hmacShaKeyFor("a-completely-different-secret-32b!!".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("15")
                .claim("role", "CUSTOMER")
                .signWith(otherKey)
                .compact();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
