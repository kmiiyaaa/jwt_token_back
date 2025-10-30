package com.kmii.home.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.kmii.home.jwt.JwtAuthenticationFilter;
import com.kmii.home.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // csrf 비활성화
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/auth/**").permitAll()  //인증 없이 접근 가능한 요청들
					.anyRequest().authenticated()  // 위 요청을 제외한 나머지 요청들은 전부 인증 필요
					)
					.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
					// 로그인 하지 않고도 JWT 존재하면 요청을 받게하는 설정  → 로그인하지 않아도 토큰가지고 있으면 인증처리 가능
					
					.cors(cors -> cors.configurationSource(request -> {
						CorsConfiguration config = new CorsConfiguration(); // 허용 ip 주소
						config.setAllowCredentials(true);
						config.setAllowedOrigins(List.of(
								"http://localhost:3000","http://cloudfront-s3-bucket-kmii.s3-website.ap-northeast-2.amazonaws.com"		
										)); //허용 ip주소
						config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
						config.setAllowedHeaders(List.of("*"));
						return config;
					})																
			);	
		return http.build();
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();  // 사용자 인증을 처리하는 개체 반환
	}

}
