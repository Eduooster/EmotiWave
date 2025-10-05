package org.example.emotiwave.application.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AcessTokenResponseDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
         String refreshToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("token_type")
        String tokenType,



        @JsonProperty("scope")
        String scope
) {

}
