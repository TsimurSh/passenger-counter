package pl.goeuropa.counter.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.video-url}")
    private String basePath1;

    @Value("${api.tc-url}")
    private String basePath2;

    @Bean(name = "video")
    public RestClient restClient1() {
        return RestClient.builder()
                .baseUrl(basePath1)
                .defaultHeaders(
                        httpHeaders -> {
                            httpHeaders.set(HttpHeaders.CONTENT_TYPE,
                                    MediaType.APPLICATION_JSON_VALUE
                            );
                        })
                .build();
    }

    @Bean(name = "tc")
    public RestClient restClient2() {
        return RestClient.builder()
                .baseUrl(basePath2)
                .defaultHeaders(
                        httpHeaders -> {
                            httpHeaders.set(HttpHeaders.CONTENT_TYPE,
                                    MediaType.APPLICATION_JSON_VALUE
                            );
                        })
                .build();
    }
}
