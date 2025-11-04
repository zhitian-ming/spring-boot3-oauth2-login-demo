package com.oauth2.security;

import com.oauth2.config.security.AppProperties;
import com.oauth2.retrofit.entity.AppleAuthKey;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private final AppProperties appProperties;

    @Value("${app.apple.key-id}")
    private String appleKeyId;

    @Value("${app.apple.team-id}")
    private String appleTeamId;

    @Value("${app.apple.client-id}")
    private String appleClientId;

    @Value("${app.apple.audience}")
    private String appleAudience;

    @Value("${app.apple.private-key}")
    private String applePrivateKey;

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] bytes = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());
        return new SecretKeySpec(bytes, SignatureAlgorithm.HS512.getJcaName());
    }

    private static String generateSecureSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Encoders.BASE64.encode(bytes);
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public Claims parserAppleToken(String token, AppleAuthKey appleAuthKey) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getApplePublicKey(appleAuthKey))
                .requireAudience(this.appleClientId)
                .requireIssuer(this.appleAudience)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAppleClientSecret(long expirationTime) {
        return Jwts.builder()
                .setHeaderParam("kid", this.appleKeyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(this.appleTeamId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setAudience(this.appleAudience)
                .setSubject(this.appleClientId)
                .signWith(getApplePrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getApplePrivateKey() {
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(this.applePrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("获取apple私钥错误。", e);
        }
        return privateKey;
    }

    public RSAPublicKey getApplePublicKey(AppleAuthKey authKey) throws Exception {

        // 解码 Base64URL 编码的模数(n)和指数(e)
        Base64.Decoder decoder = Base64.getUrlDecoder();
        BigInteger modulus = new BigInteger(1, decoder.decode(authKey.getN()));
        BigInteger publicExponent = new BigInteger(1, decoder.decode(authKey.getE()));

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(authKey.getKty());
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException {
//        String secretKey = generateSecureSecret();
//        System.out.println(secretKey);
//        byte[] bytes = Decoders.BASE64.decode(secretKey);
//        SecretKeySpec keySpec = new SecretKeySpec(bytes, SignatureAlgorithm.HS512.getJcaName());
//        SignatureAlgorithm.HS512.assertValidSigningKey(keySpec);
//        System.out.println(keySpec);
//
//        String token = Jwts.builder()
//                .setSubject(Long.toString(1))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
//                .signWith(keySpec)
//                .compact();
//        System.out.println(token);
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(keySpec)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        System.out.println(claims);
    }
}
