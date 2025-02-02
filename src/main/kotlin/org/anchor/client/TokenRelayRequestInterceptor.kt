package org.anchor.client

import feign.RequestInterceptor
import feign.RequestTemplate
import org.anchor.security.oauth2.AuthorizationHeaderUtil

class TokenRelayRequestInterceptor(
    private val authorizationHeaderUtil: AuthorizationHeaderUtil
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val authorizationHeader = authorizationHeaderUtil.getAuthorizationHeader()
        authorizationHeader.ifPresent { template.header(AUTHORIZATION, it) }
    }

    companion object {
        const val AUTHORIZATION: String = "Authorization"
    }
}
