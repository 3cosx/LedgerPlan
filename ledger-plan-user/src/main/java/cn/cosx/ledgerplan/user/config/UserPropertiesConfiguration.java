package cn.cosx.ledgerplan.user.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EmailProperties.class, AuthProperties.class})
public class UserPropertiesConfiguration {
}
