package com.dosion.noisense.common.security.jwt;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final SecretKey secretKey;

  public String generateToken(Authentication authentication, Long expirationMillis) {
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMillis);

    Claims claims = Jwts.claims();
    claims.put("user-id", customUserDetails.getId());
    claims.put("username", customUserDetails.getUsername());

    return Jwts.builder()
      .setSubject(customUserDetails.getUsername())
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .signWith(secretKey, SignatureAlgorithm.HS512)
      .compact();
  }

  public Long getUserIdFromToken(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(secretKey)
      .build()
      .parseClaimsJws(token)
      .getBody()
      .get("user-id", Long.class);
  }

  public Boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      log.error("[validateToken] MalformedJwtException : {}", e.getMessage());
      return false;
    } catch (ExpiredJwtException e) {
      log.error("[validateToken] ExpiredJwtException : {}", e.getMessage());
      return false;
    } catch (UnsupportedJwtException e) {
      log.error("[validateToken] UnsupportedJwtException : {}", e.getMessage());
      return false;
    } catch (IllegalArgumentException e) {
      log.error("[validateToken] IllegalArgumentException : {}", e.getMessage());
      return false;
    } catch (JwtException e) {
      log.error("[validateToken] JwtException : {}", e.getMessage());
      return false;
    }
  }
}
