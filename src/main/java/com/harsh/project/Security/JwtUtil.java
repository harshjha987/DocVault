package com.harsh.project.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    //converts plain text secret into a cryptographic key
    //that the JWT can use to sign tokens.
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //Generate Token
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)  //who this token belongs to
                .setIssuedAt(new Date()) //current time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) //expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) //sign with secret
                .compact(); //build and return the token string.
    }

    //Extract email
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    //Check if token is expired
    public boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //Validate Token
    public boolean validateToken(String token,String email){
        String extractedEmail = extractEmail(email);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    //Extract all data from token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
