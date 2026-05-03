package com.davidperezmillan.recopilador.infrastructure.nextcloud.config;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class NextcloudConfig {
    @Bean
    public Sardine sardine(NextcloudProperties props) {
        return SardineFactory.begin(props.getUsername(), props.getToken());
    }
}
