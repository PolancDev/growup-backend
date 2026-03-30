package com.growup.auth.infrastructure.security;

import com.growup.auth.domain.port.out.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserPersistencePort userPersistencePort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("GrowUp-Log: UserDetailsServiceImpl - Cargando usuario: {}", username);

        return userPersistencePort.findByEmail(username)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()));

                    return new User(user.getEmail(),
                            user.getPassword(),
                            user.getIsActive(),
                            user.getIsActive(),
                            user.getIsActive(),
                            true,
                            authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}