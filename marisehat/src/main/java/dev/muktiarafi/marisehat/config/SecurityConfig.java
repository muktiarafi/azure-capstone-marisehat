package dev.muktiarafi.marisehat.config;

import com.azure.spring.aad.webapp.AADWebSecurityConfigurerAdapter;
import com.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig extends AADWebSecurityConfigurerAdapter {
    private final AADAppRoleStatelessAuthenticationFilter filter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.authorizeRequests()
                .mvcMatchers("/actuator/**").permitAll()
                .mvcMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .mvcMatchers(HttpMethod.POST, "/users").permitAll()
                .mvcMatchers("/admins/**").hasRole("Admin")
                .mvcMatchers(HttpMethod.GET, "/patients/**").hasAnyRole("Admin", "User", "Partner")
                .mvcMatchers(HttpMethod.POST, "/patients/**").hasAnyRole("Admin", "User")
                .mvcMatchers(HttpMethod.PUT, "/patients/**").hasAnyRole("Admin", "User")
                .mvcMatchers(HttpMethod.DELETE, "/patients/**").hasAnyRole("Admin", "User")
                .anyRequest().authenticated();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring()
                .mvcMatchers(HttpMethod.POST, "/users"); // disable login redirection on this path
    }
}
