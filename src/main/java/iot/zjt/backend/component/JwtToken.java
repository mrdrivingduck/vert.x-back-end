package iot.zjt.backend.component;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.ini4j.Profile.Section;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;

/**
 * Class for signing or verifying JSON Web Tokens.
 * 
 * @author Mr Dk.
 * @since 2020/03/10
 */
public class JwtToken {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static void init() throws Exception {
        Section keyStoreSection = Config.getConfig().get("keystore");
        FileInputStream is = new FileInputStream(
                                new File(keyStoreSection.get("path")));
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(is, keyStoreSection.get("secret").toCharArray());
        
        privateKey = (PrivateKey) keyStore.getKey(
                                    keyStoreSection.get("tokenAliase"),
                                    keyStoreSection.get("tokenSecret").toCharArray());
        publicKey = keyStore.getCertificate(keyStoreSection.get("tokenAliase"))
                            .getPublicKey();
        
        // String token = Jwts.builder()
        //     .setExpiration(new Date(System.currentTimeMillis() + 10000))
        //     // .claim("emm", "wodema")
        //     .claim("cao", "tainanle")
        //     .signWith(privateKey).compact();
        // System.out.println(token);

        // JwtParser jws = Jwts.parserBuilder()
        //     .setSigningKey(publicKey)
        //     .require("emm", "wodema")
        //     .build();

        // try {
        //     jws.parse(token);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

    public static String signToken(JwtBuilder jwt) {
        return jwt.signWith(privateKey).compact();
    }

    public static JwtParser verifyToken(JwtParserBuilder jws) {
        return jws.setSigningKey(publicKey).build();
    } 
}