package com.studymate.studymate.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
//JwtService is responsible for creating JWT tokens and extracting information from them.
//the class that issues and verifies the digital identity token a user receives after logging in.
public class JwtService {
    private final SecretKey signingKey; //The secret cryptographic key used to sign tokens and verify their signatures.
    private final long expirationMs; //How long a token remains valid, in milliseconds.
    //Why final? Both values are initialized in the constructor and should not change while the application is running.

    public JwtService( //It reads values from application.properties
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ){
        byte[] keyBytes= Decoders.BASE64.decode(secret); //The secret is stored as a Base64-encoded string. This converts it into its original byte representation so it can be used cryptographically.
        this.signingKey= Keys.hmacShaKeyFor(keyBytes); //Converts those bytes into a cryptographic key that the JWT library can use for HMAC signing.
        this.expirationMs=expirationMs;
    }

    public String generateToken(String email){ //This method creates a JWT for a user after successful login.
        Date now = new Date();
        Date expiry = new Date(now.getTime()+expirationMs); //Calculate expiry

        return Jwts.builder().subject(email) //Stores the user's email as the token's subject.
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact(); //Converts the JWT into a compact string that can be sent to the frontend.
    }

    private Claims extractAllClaims(String token){ //Claims are pieces of information stored inside a JWT.
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token) //Parses the token and verifies its signature.
                .getPayload(); //Returns the claims stored in the token.
        //- Rejects invalid or expired tokens by throwing an exception.
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token){ //This checks whether the token's expiration time is before the current time
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public boolean isTokenValid(String token, String email){//checks whether email is matching and the token is not expired
        String tokenEmail= extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }
}
