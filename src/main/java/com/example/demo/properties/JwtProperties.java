/**
 * author @bhupendrasambare
 * Date   :24/07/26
 * Time   :8:10 am
 * Project:rag
 **/
package com.example.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.jwt")
public class JwtProperties {

    private String secret;

    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    private String issuer;

}
