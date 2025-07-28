package com.personal_loan.personal_loan.controller;

import com.personal_loan.personal_loan.entity.AppUser;
import com.personal_loan.personal_loan.repository.UserRepository;
//import com.personal_loan.personal_loan.service.GoogleAuthService;
import com.personal_loan.personal_loan.service_impl.UserDetailsServeceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    UserDetailsServeceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //new
    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {

            //  exchange auth code for token
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            Map<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("redirect_uri", "https://developers.google.com/oauthplayground");
            params.put("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (userDetails == null) {
                    AppUser user = new AppUser();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole(Arrays.asList("USER", "ADMIN"));
                    userRepository.save(user);
                    userDetails = userDetailsService.loadUserByUsername(email);

                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Exception occur ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}














/*
package com.personal_loan.personal_loan.controller;

import com.personal_loan.personal_loan.entity.AppUser;
import com.personal_loan.personal_loan.repository.UserRepository;
import com.personal_loan.personal_loan.service_impl.UserDetailsServeceImpl;
import com.personal_loan.personal_loan.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;



//pass authorization code to exchange with token
@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    */
/*
     WebClient.Builder = blueprint or machine to build a phone
    .build() = presses the button to make the actual phone
     WebClient = the phone you use to call someone (i.e., send a web request)
     *//*

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserDetailsServeceImpl userDetailsServece;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {

        try {
            //1) Exchange authorization code for token
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            Map<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
//            params.put("redirect_uri", "https://developers.google.com/oauthplayground");
            params.put("redirect_uri", "http://localhost:8080/auth/google");
            params.put("grant_type", "authorization_code");

            // Convert to MultiValueMap for WebClient
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.setAll(params);

            WebClient webClient = webClientBuilder.build();
            Map tokenResponse = webClient
                    .post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))  // formdata require key value pair. so that store in MultiValueMap that and use MultiValueMap that reference
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();   //  for synchronous

            //String idToken =(String) tokenResponse.get("id_token");   //#
            //  String userInfoUrl = "https://oauth2.googleapis.com/token" + idToken;  // send token and take user information
            //   ResponseEntity<Map> userInfoResponse = webClient.getForEntity(userInfoUrl,Map.class);

            String accessToken = (String) tokenResponse.get("access_token");
            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

            //  Map<String, Object> userInfo = webClient.get()
            Map userInfo = webClient.get()
                    .uri(userInfoUrl)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();


            if(userInfo != null)
            {
//                Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            UserDetails userDetails = null;

            try {
                userDetails= userDetailsServece.loadUserByUsername(email);

            }catch (Exception e) {
                //  if (userDetails == null) {
                AppUser user = new AppUser();
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRole(Arrays.asList("USER", "ADMIN"));
                userRepository.save(user);
             //   userDetails = userDetailsServece.loadUserByUsername(email);
                //   }
            }

            String jwtToken = jwtUtil.generateToken(email);
            return ResponseEntity.ok(Collections.singletonMap("token",jwtToken));




//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }catch (Exception e) {
            log.error("Exception occure : ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
*/
/*
*
*   // @Autowired
    //private GoogleAuthService googleAuthService;

//    @GetMapping
//    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
//        return googleAuthService.processGoogleLogin(code);
//    }*/