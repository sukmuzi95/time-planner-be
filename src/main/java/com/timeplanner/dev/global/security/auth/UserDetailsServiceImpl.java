package com.timeplanner.dev.global.security.auth;

import com.timeplanner.dev.domain.user.entity.User;
import com.timeplanner.dev.domain.user.repository.UserRepository;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("loadUserByUsername() email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());

        return UserDetailsImpl.create(user);
    }
}
