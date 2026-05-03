package com.growup.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

    /**
     * Extrae roles del claim 'role' en el JWT.
     */
@Slf4j
@Component
public class JwtAuthenticationConverter
    implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    /** Nombre del claim para el rol en el token JWT. */
    private static final String ROLE_CLAIM = "role";

    /** Prefijo para autoridades. */
    private static final String AUTHORITY_PREFIX = "ROLE_";

    @Override
    public final Mono<AbstractAuthenticationToken> convert(
        final Jwt jwt) {
        List<String> roles = extractRoles(jwt);

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> {
                    String authority = role.startsWith(AUTHORITY_PREFIX)
                        ? role : AUTHORITY_PREFIX + role;
                    log.debug("Adding authority: {}", authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        log.debug("JWT converted with authorities: {}", authorities);

        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(final Jwt jwt) {
        Object roleClaim = jwt.getClaim(ROLE_CLAIM);

        if (roleClaim == null) {
            log.warn("No 'role' claim found in JWT");
            return Collections.emptyList();
        }

        if (roleClaim instanceof String) {
            return Stream.of(((String) roleClaim).split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .collect(Collectors.toList());
        } else if (roleClaim instanceof List) {
            return ((List<String>) roleClaim).stream()
                    .filter(role -> role != null && !role.isEmpty())
                    .collect(Collectors.toList());
        } else {
            log.warn("Unexpected type for 'role' claim: {}",
                roleClaim.getClass());
            return Collections.emptyList();
        }
    }
}
