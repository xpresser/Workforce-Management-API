package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.security.UserPrincipal;
import com.metodi.workforcemanagement.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JWTTokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Override
    public String generateToken(User user) {

        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        Date date = Date.from(expirationDate);

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        String token = Jwts.builder()
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("sub", user.getEmail())
                .claim("admin", user.isAdmin())
                .setExpiration(date)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + token;
    }

    @Override
    public UserPrincipal parseToken(String token) {

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET.getBytes())
                .build()
                .parseClaimsJws(token);

        Claims body = claimsJws.getBody();

        String email = body.getSubject();

        long id = body.get("id", Long.class);

        String username = body.get("username", String.class);

        boolean isAdmin = body.get("admin", Boolean.class);

        return new UserPrincipal(id, username, email, isAdmin);
    }
}
