package maskun.quietchatter.adaptor.naver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.api")
public class NaverApiEnvironment {
    private final String clientId;
    private final String clientSecret;

    public NaverApiEnvironment(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
