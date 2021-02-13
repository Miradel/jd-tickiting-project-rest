package com.cybertek.util;

import com.cybertek.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
	JWT --> JSON Web Token, is a secure and trustworthy standard for token authentication.
	1. Enter credential
	2. Security first step/ for enable the security ,   WebSecurityConfig Class
	3. DataBase Verification need to be done at "SecurityService Class"
	4. Application create the token and return it , JWTUtil class
	    Decode the token
	5. validate the token, SecurityFilter Class
	6. Send Request with the that generated token
	7. Get success/failed response according to its authorization

 */
@Component
public class JWTUtil {

    @Value("${security.jwt.secret-key}")
    private final String secret = "cybertek";


    // Generating the payload
    public String generateToken(User user){

        Map<String,Object> claims = new HashMap<>();
        claims.put("username",user.getUserName());
        claims.put("id",user.getId());
        claims.put("firstName",user.getFirstName());
        claims.put("lastName",user.getLastName());
        return createToken(claims,user.getUserName());
    }

    private String createToken(Map<String,Object> claims,String username){

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10)) //10 hours token validity
                .signWith(SignatureAlgorithm.HS256,secret).compact();// Set up signature

    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);

    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String currentUser = extractAllClaims(token).get("id").toString();
        return (currentUser.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }



}

