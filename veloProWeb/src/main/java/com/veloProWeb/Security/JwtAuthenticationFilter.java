package com.veloProWeb.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Filtro de autenticación JWT que intercepta las solicitudes HTTP,
 * verifica la presencia y validez del token JWT en el encabezado Authorization,
 * y establece la autenticación en el contexto de seguridad de Spring.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwUtil jwtUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    /**
     * Intercepta las solicitudes HTTP y realiza la autenticación basada en JWT.
     * @param request     La solicitud HTTP.
     * @param response    La respuesta HTTP.
     * @param filterChain La cadena de filtros.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException      Si ocurre un error de E/S.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull  FilterChain filterChain) throws ServletException, IOException {
        // Obtiene el encabezado Authorization de la solicitud.
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        String role = null;

        // Verifica si el encabezado Authorization existe y comienza con "Bearer ".
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        // Verifica si el nombre de usuario se extrajo correctamente y no hay autenticación en el contexto de seguridad.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carga los detalles del usuario desde UserDetailsService.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Válida el token JWT.
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Crea un objeto UsernamePasswordAuthenticationToken con los detalles del usuario y el rol.
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                // Establece los detalles de la solicitud en el objeto de autenticación.
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Establece la autenticación en el contexto de seguridad.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Continúa con la cadena de filtros.
        filterChain.doFilter(request, response);
    }
}
