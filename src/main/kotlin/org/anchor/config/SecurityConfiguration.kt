package org.anchor.config

import org.anchor.security.ADMIN
import org.anchor.security.oauth2.AudienceValidator
import org.anchor.security.oauth2.JwtGrantedAuthorityConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport
import tech.jhipster.config.JHipsterProperties

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport::class)
class SecurityConfiguration(
    private val jHipsterProperties: JHipsterProperties,
    private val problemSupport: SecurityProblemSupport
) {

    @Value("\${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    private lateinit var issuerUri: String

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            .ignoringAntMatchers("/h2-console/**")
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/authorize").authenticated()
            .antMatchers("/api/auth-info").permitAll()
            .antMatchers("/api/admin/**").hasAuthority(ADMIN)
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/health/**").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(ADMIN)
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(authenticationConverter())
            .and()
            .and()
            .oauth2Client()
        return http.build()
    }

    fun authenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(JwtGrantedAuthorityConverter())
        return jwtAuthenticationConverter
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri) as NimbusJwtDecoder

        val audienceValidator = AudienceValidator(jHipsterProperties.security.oauth2.audience)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri)
        val withAudience = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)

        jwtDecoder.setJwtValidator(withAudience)

        return jwtDecoder
    }
}
