package com.heyticket.backend.module.security.jwt;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.ValidationFailureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private final long accessExpirationMillis;

    private final long refreshExpirationMillis;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
        @Value("${jwt.expiration.access}") long accessExpirationMillis,
        @Value("${jwt.expiration.refresh}") long refreshExpirationMillis) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    public TokenInfo generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            .setExpiration(new Date(now + accessExpirationMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshExpirationMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return TokenInfo.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public TokenInfo regenerateToken(String email, String authorities) {
        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
            .setSubject(email)
            .claim("auth", authorities)
            .setExpiration(new Date(now + accessExpirationMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshExpirationMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return TokenInfo.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            log.info("Invalid JWT");
            throw new ValidationFailureException("JWT is invalid.", InternalCode.INVALID_JWT);
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT", e);
            throw new ValidationFailureException("JWT is invalid.", InternalCode.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT", e);
            throw new ValidationFailureException("JWT is expired.", InternalCode.INVALID_JWT);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new ValidationFailureException("JWT is unsupported.", InternalCode.INVALID_JWT);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new ValidationFailureException("JWT claims string is empty.", InternalCode.INVALID_JWT);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
