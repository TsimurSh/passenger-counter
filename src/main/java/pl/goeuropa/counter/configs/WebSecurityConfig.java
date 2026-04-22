package pl.goeuropa.counter.configs;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${api.security.user}")
    private String user;
    @Value("${api.security.password}")
    private String password;

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails authz = User.withDefaultPasswordEncoder()
//                .username(user)
//                .password(password)
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(authz);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/v1/upload-json", "/v1/device-snapshot")
//                        .hasRole("ADMIN")
//                        .anyRequest().permitAll()
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(withDefaults());
//        return http.build();
//    }
}
