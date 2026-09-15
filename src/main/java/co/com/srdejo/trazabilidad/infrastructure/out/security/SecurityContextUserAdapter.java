package co.com.srdejo.trazabilidad.infrastructure.out.security;

import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityContextUserAdapter implements IAuthenticatedUserPort {
    @Override
    public Long getAuthenticatedUserId() {
        return (Long) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
