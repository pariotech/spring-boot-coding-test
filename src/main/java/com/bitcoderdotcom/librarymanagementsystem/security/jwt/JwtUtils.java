package com.bitcoderdotcom.librarymanagementsystem.security.jwt;

import com.bitcoderdotcom.librarymanagementsystem.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
//@DependsOn("secretKeyGenerator")
public class JwtUtils {

    @Value("${lms.jwtExpirationMs}")
    private int jwtExpirationMs;

//    @Value("${lms.filepath}")
//    private String jwtSecretKeyFilepath = "src/main/java/keys/jwtSecretKey.file";

    @Value("${lms.jwtSecretKey}")
    private String jwtSecretKey;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }


    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("'Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public LocalDateTime getJwtExpirationDate() {
        return Instant.ofEpochMilli((new Date()).getTime() + getJwtExpirationMs())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    //    @PostConstruct
//    public void init() throws IOException {
//        String jwtSecretKey = new String(Files.readAllBytes(Paths.get(jwtSecretKeyFilepath)));
//        jwtSecretKey = jwtSecretKey.substring(jwtSecretKey.indexOf('=') + 1).trim();
//        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
//    }

//    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
}
