package maskun.quietchatter.security.adaptor.out;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@NullMarked
@Component
public class NaverClient {
    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;

    public NaverClient(RestClient.Builder restClientBuilder,
                       @Value("${naver.api.client-id}") String clientId,
                       @Value("${naver.api.client-secret}") String clientSecret) {
        this.restClient = restClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public NaverTokenResponse getAccessToken(String code, String state) {
        String uri = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(NaverTokenResponse.class);
    }

    public NaverProfileResponse getProfile(String accessToken) {
        return restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(NaverProfileResponse.class);
    }
}
