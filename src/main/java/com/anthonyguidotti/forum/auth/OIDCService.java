package com.anthonyguidotti.forum.auth;

import com.anthonyguidotti.forum.user.UserDataAccess;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class OIDCService {
    private static final String DISCOVERY_DOCUMENT_URI = "https://accounts.google.com/.well-known/openid-configuration";
    private final DiscoveryDocument discoveryDocument = new DiscoveryDocument();
    private final UserDataAccess userDataAccess;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public OIDCService(
            UserDataAccess userDataAccess,
            @Value("${OAUTH_CLIENT-ID}") String clientId,
            @Value("${OAUTH_CLIENT-SECRET}") String clientSecret,
            @Value("${OAUTH_REDIRECT-URI}") String redirectUri
    ) {
        this.userDataAccess = userDataAccess;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public String authenticationUri(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append(discoveryDocument.getAuthorizationEndpoint())
                .append('?').append("client_id=").append(clientId)
                .append('&').append("response_type=").append("code")
                .append('&').append("scope").append(URLEncoder.encode("openid email", StandardCharsets.UTF_8))
                .append('&').append("redirect_uri=").append(redirectUri)
                .append('&').append("state=").append(URLEncoder.encode(session.getId(), StandardCharsets.UTF_8));

        return sb.toString();
    }

    @Before("execution(* com.anthonyguidotti.forum.auth.OIDCService.authenticationUri())")
    private void refreshDiscoveryDocument() {
        LocalDateTime expiration = discoveryDocument.getExpirationTime();
        if (expiration == null || LocalDateTime.now().isAfter(expiration)) {

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DISCOVERY_DOCUMENT_URI))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Set expiration datetime
                Optional<String> age = response.headers().firstValue("age");
                if (age.isPresent()) {
                    int seconds = Integer.parseInt(age.get());
                    discoveryDocument.setExpirationTime(LocalDateTime.now().plusSeconds(seconds));
                }

                JSONObject json = new JSONObject(response.body());
                discoveryDocument.setAuthorizationEndpoint(json.getString("authorization_endpoint"));
                discoveryDocument.setRevocationEndpoint(json.getString("revocation_endpoint"));
                discoveryDocument.setTokenEndpoint(json.getString("token_endpoint"));
                discoveryDocument.setUserinfoEndpoint(json.getString("userinfo_endpoint"));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class DiscoveryDocument {
        private String authorizationEndpoint;
        private String revocationEndpoint;
        private String tokenEndpoint;
        private String userinfoEndpoint;
        private LocalDateTime expirationTime;

        public String getAuthorizationEndpoint() {
            return authorizationEndpoint;
        }

        public void setAuthorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
        }

        public String getRevocationEndpoint() {
            return revocationEndpoint;
        }

        public void setRevocationEndpoint(String revocationEndpoint) {
            this.revocationEndpoint = revocationEndpoint;
        }

        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public void setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
        }

        public String getUserinfoEndpoint() {
            return userinfoEndpoint;
        }

        public void setUserinfoEndpoint(String userinfoEndpoint) {
            this.userinfoEndpoint = userinfoEndpoint;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}
