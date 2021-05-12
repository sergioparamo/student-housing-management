package cat.itb.studenthousingweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private MyUserDetailsService myUserDetailsService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser("Montse")
                .password("secret")
                .roles("vip");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll()
                /*.antMatchers("/", "/hola", "/login", "/register", "/security_error", "/webjars/**", "/css/**").permitAll()
                .antMatchers("/books/new/**", "/books/edit/**", "/books/delete/**").hasRole("ADMIN")
                .antMatchers("/books/list").authenticated()*/


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

