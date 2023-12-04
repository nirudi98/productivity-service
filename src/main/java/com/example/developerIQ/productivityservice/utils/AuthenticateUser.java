package com.example.developerIQ.productivityservice.utils;

import com.example.developerIQ.productivityservice.common.AuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.developerIQ.productivityservice.utils.constants.Constants.GENERATE_TOKEN_REQUEST;

@Service
public class AuthenticateUser {

    @Value("${auth-service.url}")
    private String auth_service_url;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateUser.class);

    @Autowired
    private RestTemplate restTemplate;

    public String validateTokenAuthService() {
        try{
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setUsername("Sula");
            authenticationRequest.setPassword("sulas");

            String complete_url = auth_service_url + GENERATE_TOKEN_REQUEST;

            ResponseEntity<String> responseToken = restTemplate.postForEntity(complete_url, authenticationRequest, String.class);

            return responseToken.getBody();

        } catch(RuntimeException e){
            logger.error("calling auth service failed ", e);
            throw new RuntimeException();
        }
    }
}
