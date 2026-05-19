// FILE: src/main/java/edu/cit/medil/libtek/config/UserIdAuthFilter.java
package edu.cit.medil.libtek.config;

import edu.cit.medil.libtek.features.user.User;
import edu.cit.medil.libtek.features.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Temporary auth filter: extracts the user email from the Authorization header
 * value that was stored during login (format: "Bearer <email>").
 *
 * Replace this with a proper JWT filter once you add JWT signing to AuthController.
 */
public class UserIdAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public UserIdAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Token is the user's email — set by AuthController below
            Optional<User> userOpt = userRepository.findByEmail(token);
            userOpt.ifPresent(user -> {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        filterChain.doFilter(request, response);
    }
}