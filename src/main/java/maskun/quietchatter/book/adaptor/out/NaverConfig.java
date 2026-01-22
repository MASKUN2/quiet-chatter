package maskun.quietchatter.book.adaptor.out;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class NaverConfig {

    @Bean("naverRestClient")
    RestClient naverRestClient(
            RestClient.Builder builder,
            @Value("${naver.api.client-id}") String clientId,
            @Value("${naver.api.client-secret}") String clientSecret
    ) {
        return builder.clone()
                .baseUrl("https://openapi.naver.com/v1/search/book.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }
}
