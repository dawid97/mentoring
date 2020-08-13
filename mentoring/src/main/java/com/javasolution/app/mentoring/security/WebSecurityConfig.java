package com.javasolution.app.mentoring.security;


import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.javasolution.app.mentoring.security.SecurityConstants.H2_CONSOLE;


@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        final String STUDENT = UserRole.STUDENT.toString();
        final String MENTOR = UserRole.MENTOR.toString();

        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .authorizeRequests()

                //h2
                .antMatchers(H2_CONSOLE).permitAll()

                //login and registration
                .antMatchers(HttpMethod.POST, "/api/users/sign-in").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/sign-up").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/sign-up/confirm").permitAll()

                //users
                .antMatchers(HttpMethod.PUT, "/api/users/me").hasAnyAuthority(MENTOR,STUDENT)
                .antMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.DELETE, "/api/users/me").hasAuthority(STUDENT)
                .antMatchers(HttpMethod.GET, "/api/users/me").hasAnyAuthority(STUDENT,MENTOR)
                .antMatchers(HttpMethod.GET, "/api/users").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.GET, "/api/users/{userId}").hasAuthority(MENTOR)

                //meetings
                .antMatchers(HttpMethod.POST, "/api/meetings").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.DELETE, "/api/meetings/{meetingId}").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.PUT, "/api/meetings/{meetingId}").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.GET, "/api/meetings").hasAnyAuthority(MENTOR,STUDENT)
                .antMatchers(HttpMethod.GET, "/api/meetings/{meetingId}").hasAnyAuthority(MENTOR,STUDENT)

                //bookings
                .antMatchers(HttpMethod.POST, "/api/meetings/{meetingId}/bookings").hasAuthority(STUDENT)
                .antMatchers(HttpMethod.DELETE, "/api/bookings/{bookingId}").hasAuthority(STUDENT)
                .antMatchers(HttpMethod.GET, "/api/bookings").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.GET, "/api/bookings/{bookingId}").hasAuthority(MENTOR)
                .antMatchers(HttpMethod.GET, "/api/bookings/me/{bookingId}").hasAuthority(STUDENT)
                .antMatchers(HttpMethod.GET, "/api/bookings/me").hasAuthority(STUDENT)


                .anyRequest()
                .authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder());
    }
}
