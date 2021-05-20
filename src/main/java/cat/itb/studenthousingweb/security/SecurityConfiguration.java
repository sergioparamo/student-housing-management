package cat.itb.studenthousingweb.security;

import cat.itb.studenthousingweb.services.OwnerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private OwnerDetailsService ownerDetailsService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(ownerDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers( "/login", "/register", "/security_error", "/webjars/**","/images/**" ,"/static/**","/templates/**").permitAll()
                .antMatchers("/applications**", "/houses/**", "/profile/**","/").authenticated()


                .and()

                .formLogin()
                .failureUrl("/security_error")
                .loginPage("/login")


                .and()
                .logout()
                .logoutUrl("/logout");
        http.csrf().disable(); //per h2-console
        http.headers().frameOptions().disable(); //per h2-console

        http.authorizeRequests();

    }


}

