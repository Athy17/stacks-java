package com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.impl;

import static com.xxAMIDOxx.xxSTACKSxx.util.TestHelper.getBaseURL;
import static com.xxAMIDOxx.xxSTACKSxx.util.TestHelper.getRequestHttpEntity;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;


import com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.dto.request.GenerateTokenRequest;
import com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.dto.response.GenerateTokenResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("Integration")
class AuthControllerImplTest {

  public static final String GENERATE_TOKEN_URI = "%s/v1/token";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate testRestTemplate;

  @Value(value = "${auth0.client_id}")
  private String clientId;

  @Value(value = "${auth0.client_secret}")
  private String clientSecret;

  @Value(value = "${auth0.apiAudience}")
  private String apiAudience;

  @Value(value = "${auth0.grant_type}")
  private String grantType;

  @Test
  void testGenerateTokenWhenInvalidCredentials() {
    // Given
    GenerateTokenRequest requestBody = new GenerateTokenRequest();
    requestBody.setClient_id("mock_Client_id");
    requestBody.setClient_secret("mock_Client_secret");
    requestBody.setAudience("https://mock.eu.auth0.com/api/v2/");
    requestBody.setGrant_type("client_credentials");

    // When
    String requestUrl = String.format(GENERATE_TOKEN_URI, getBaseURL(port));
    var response =
        this.testRestTemplate.exchange(
            requestUrl,
            HttpMethod.POST,
            new HttpEntity<>(requestBody, getRequestHttpEntity()),
            GenerateTokenResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
  }


  @Test
  void testGenerateTokenWhenValidCredentials() {
    // Given
    GenerateTokenRequest requestBody = new GenerateTokenRequest();
    requestBody.setClient_id(clientId);
    requestBody.setClient_secret(clientSecret);
    requestBody.setAudience(apiAudience);
    requestBody.setGrant_type(grantType);

    // When
    String requestUrl = String.format(GENERATE_TOKEN_URI, getBaseURL(port));
    var response =
            this.testRestTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, getRequestHttpEntity()),
                    GenerateTokenResponse.class);

    // Then
    then(response.getStatusCode()).isEqualTo(OK);
  }
}
