package com.personal_loan.personal_loan.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Component
public class JWTUtil {
    private final String SECRET_KEY = "ThisIsASecretKeyThatIsAtLeastThirtyTwoCharactersLong";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 + 60 * 60))   // 1hr expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}










































































/*

 public String generateToken(UserDetails userDetails) {

     Map<String, Object> claims = new HashMap<>();

         // Extract roles and put them in claims
     List<String> roles = userDetails.getAuthorities()
             .stream().map(GrantedAuthority::getAuthority)
             .collect(Collectors.toList());

     claims.put("roles",roles);

     return Jwts.builder()
             .setClaims(claims)
             .setSubject(userDetails.getUsername())
             .setIssuedAt(new Date(System.currentTimeMillis()))
             .setExpiration(new Date(System.currentTimeMillis()  + 1000 * 60 * 60  *10))  // 10 hr
             .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
             .compact();
 }

 public boolean validateToken(String token, UserDetails userDetails) {
     final String username = extractUsername(token);
     return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
 }

 public String extractUsername(String token) {
     return extractClaims(token).getSubject();
 }

 public Date extractExpiration(String token) {
     return extractClaims(token).getExpiration();
 }


 public List<String> extractRoles(String token) {
     Claims claims = extractClaims(token);
     Object rolesObj = claims.get("roles");
     if(rolesObj instanceof  List) {
         return (List<String>) rolesObj;
     }
     return  Collections.emptyList();
 }





 private Claims extractClaims(String token) {
     //return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

     return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();


 }



 private boolean isTokenExpired(String token) {
     return extractExpiration(token).before(new Date());
 }
*/


