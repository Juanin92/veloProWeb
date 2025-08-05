package com.veloproweb.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Extrae el nombre de usuario del token JWT.
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * @param token El token JWT.
     * @return La fecha de expiración.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae una reclamación (claim) específica del token JWT.
     * @param token - El token JWT.
     * @param claimsResolver - La función para resolver la reclamación.
     * @param <T> - El tipo de la reclamación.
     * @return - La reclamación extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todas las reclamaciones del token JWT.
     * @param token - El token JWT.
     * @return - Las reclamaciones del token.
     */
    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            System.out.println("Error extracting claims from token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si el token JWT ha expirado.
     * @param token - El token JWT.
     * @return - true si el token ha expirado, false de lo contrario.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para el usuario, incluyendo su rol.
     * @param userDetails - Los detalles del usuario.
     * @return - El token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        claims.put("role", role);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea un token JWT con las reclamaciones y el sujeto especificados.
     * Tiempo expiración del token es cada 18 hr
     * @param claims - Las reclamaciones del token.
     * @param subject - El sujeto del token.
     * @return - El token JWT creado.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 18 * 60 * 60 * 1000))
                .signWith(getSigningKey()).compact();
    }

    /**
     * Válida el token JWT para el usuario.
     * @param token - El token JWT.
     * @param userDetails - Los detalles del usuario.
     * @return - true si el token es válido, false de lo contrario.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Obtiene la clave de firma para el token JWT.
     * @return - La clave de firma.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el rol de un token JWT.
     * @param token - El token JWT del usuario.
     * @return - El rol del usuario en formato String.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

}