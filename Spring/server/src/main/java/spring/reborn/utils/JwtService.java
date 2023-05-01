package spring.reborn.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import spring.reborn.config.*;
import spring.reborn.config.secret.Secret;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import spring.reborn.domain.user.model.AppleClient;
import spring.reborn.domain.user.model.ApplePublicKeyResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static spring.reborn.config.BaseResponseStatus.*;

@Service
public class JwtService {
    @Autowired
    private final AppleClient appleClient;

    public JwtService(AppleClient appleClient) {
        this.appleClient = appleClient;
    }

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    public String createRefreshToken(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","rtk")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    public String createEmptyJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(0)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    public String createEmptyRtk(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","rtk")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(0)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            System.out.println(ignored);
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);  // jwt 에서 userIdx를 추출합니다.
    }

    public String getUserEmailApple() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            System.out.println(ignored);
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("email",String.class);  // identityToken 에서 email를 추출합니다.
    }

    public String parseAppleJwt() throws BaseException, InvalidKeySpecException, NoSuchAlgorithmException {
        String token = getJwt();

        if(token == null || token.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        Jws<Claims> claimsJws = null;

        /*for (PublicKey key : getPublicKeys(token)) {
            try {
                claimsJws = Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(token);
                break;
            } catch(SignatureException se) {
                continue;
            }
        }
        return claimsJws.getBody().get("iat",String.class);*/
        return getPublicKeys(token);
    }

    private /*List<PublicKey>*/String getPublicKeys(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        /*List<ApplePublicKeyResponse.Key> responseApplePublicKeys = appleClient.getAppleAuthPublicKey().getKeys();

        List<PublicKey> publicKeys = new ArrayList<>();

        try {
            for (ApplePublicKeyResponse.Key responseApplePublicKey : responseApplePublicKeys) {
                byte[] nBytes = Base64.getUrlDecoder().decode(responseApplePublicKey.getN());
                byte[] eBytes = Base64.getUrlDecoder().decode(responseApplePublicKey.getE());


                BigInteger n = new BigInteger(1, nBytes);
                BigInteger e = new BigInteger(1, eBytes);

                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
                KeyFactory keyFactory = KeyFactory.getInstance(responseApplePublicKey.getKty());
                PublicKey applePublicKey = keyFactory.generatePublic(publicKeySpec);

                publicKeys.add(applePublicKey);
            }
        }catch (
                NoSuchAlgorithmException e) {
        } catch (
                InvalidKeySpecException e) {
        } catch (
                SignatureException e) {
        } catch (
                MalformedJwtException e) {
        } catch (
                ExpiredJwtException e) {
        }

        return publicKeys;*/
        PublicKey publicKey = null;
        try {
            ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

            String headerOfIdentityToken = token.substring(0, token.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
            ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (
                NoSuchAlgorithmException e) {
        } catch (
                InvalidKeySpecException e) {
        } catch (
                SignatureException e) {
        } catch (
                MalformedJwtException e) {
        } catch (
                ExpiredJwtException e) {
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().get("email",String.class);
    }

        public int compareUserIdx(int userIdx) throws BaseException{
            //1. JWT 추출
            String accessToken = getJwt();
            if(accessToken == null || accessToken.length() == 0){
                throw new BaseException(EMPTY_JWT);
            }

            // 2. JWT parsing
            Jws<Claims> claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(Secret.JWT_SECRET_KEY)
                        .parseClaimsJws(accessToken);
            } catch (Exception ignored) {
                System.out.println(ignored);
                throw new BaseException(INVALID_JWT);
            }

            // 3. userIdx 추출
            int jwtIdx = claims.getBody().get("userIdx",Integer.class);  // jwt 에서 userIdx를 추출합니다.
            if(jwtIdx != userIdx){
                return 0;
            }
            else{
                return 1;
            }
        }

}

