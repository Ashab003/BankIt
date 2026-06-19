package com.project.BankIt_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECURITY_KEY = "4960137a1d03c27ead415d28fb5341f94567ac82a89d4c0be4ad9e73903a1bd3";

    private final RedisTemplate<String, String> redisTemplate;


    //takes the SECURITY_KEY assumes it is encoded in base64, decodes it into bytes[] and
    // converts it into a specialized cryptographic secret key using HMAC-SHA algorithm
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECURITY_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //isTokenValid -> extractUsername ->extractClaim->extractAllClaims
    //----------------------------------------------------------------------------------------------------

    public boolean isTokenValid(String token, UserDetails userDetails){
        //extracts username from the token
        final String username = extractUsername(token);
        //checks if username in the token matches the username  of the UserDetails object pulled from db
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String extractUsername(String JwtToken){
        return extractClaim(JwtToken, Claims::getSubject);
    }

    //when request comes with a token , filter needs to read it , it calls this metho for it
    public<T> T extractClaim(String JwtToken, Function<Claims,T> claimsResolver){
        //calls extract allClaims
        final Claims claims = extractAllClaims(JwtToken);
        return claimsResolver.apply(claims);
    }

    //most important method here,
    //it takes the SecretKey, parses the token
    public Claims extractAllClaims(String JwtToken){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()

                //the library automatically recalculates signature using the key ,
                // if signature does not match perfectly or is expired
                //it throws exceptions immediately
                //if its valid it returns Claims object
                .parseClaimsJws(JwtToken)

                .getBody();
    }

    //----------------------------------------------------------------------------------------------------


    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ){
        return Jwts
                .builder()

                //claims -> pieces of jwt data
                .setClaims(extraClaims)

                //subject -> main entity this token is about, using username here
                .setSubject(userDetails.getUsername())

                //stamping the token with exact millisecond it was made in
                .setIssuedAt(new Date(System.currentTimeMillis()))

                //setting the expiration of token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60*60*24))

                //locking the token using SECRET_KEY
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                //squashes teh whole builder configuration into the final url safe string which gets sent to the front end
                .compact();

    }


    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        RedisTemplate<String, String> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        return template;
    }

    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(token)
        );
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}

//we have a separate isTokenExpired method even though extract allClaims handles it because
// allClaims throws ExpiredJwtException on token expiration before the code ever reaches !isTokenExpired(token) check

