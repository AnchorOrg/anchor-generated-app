package org.anchor.security.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

open class OAuthIdpTokenResponseDTO(
    @JsonProperty("token_type")
    var tokenType: String? = null,

    var scope: String? = null,

    @JsonProperty("expires_in")
    var expiresIn: Long? = null,

    @JsonProperty("ext_expires_in")
    var extExpiresIn: Long? = null,

    @JsonProperty("expires_on")
    var expiresOn: Long? = null,

    @JsonProperty("not-before-policy")
    var notBefore: Long? = null,

    var resource: UUID? = null,

    @JsonProperty("access_token")
    var accessToken: String? = null,

    @JsonProperty("refresh_token")
    var refreshToken: String? = null,

    @JsonProperty("id_token")
    var idToken: String? = null,

    @JsonProperty("session_state")
    var sessionState: String? = null,

    @JsonProperty("refresh_expires_in")
    var refreshExpiresIn: String? = null
)
