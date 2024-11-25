package com.webalo.iliad.Controller;
import com.webalo.iliad.Domain.Login;
import com.webalo.iliad.Utilities.AESUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Login request) {
        try {
            // Decifra la password ricevuta
            String decryptedPassword = AESUtils.decrypt(request.getPassword());

            // Stampa username e password decifrata
            System.out.println("Username: " + request.getUsername());
            System.out.println("Password decifrata: " + decryptedPassword);

            // Chiamata al servizio esterno
            String sessionToken = callIliadService(request.getUsername(), decryptedPassword);

            Map<String, String> response = new HashMap<>();
            response.put("token", sessionToken);

            // Restituisci il token come risposta
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return (ResponseEntity<Map<String, String>>) ResponseEntity.status(500);
        }
    }

    private String callIliadService(String username, String password) {
        try {
            // Configura il RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            /* Iliad endpoint */
            String url = "https://www.iliad.it/account/";

            /* building form-data */
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "login-ident=" + username + "&login-pwd=" + password;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            /* POST */
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            /* taking cookie session */
            String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            if (setCookieHeader != null && setCookieHeader.contains("ACCOUNT_SESSID")) {

                String sessionToken = Arrays.stream(setCookieHeader.split(";"))
                        .filter(cookie -> cookie.trim().startsWith("ACCOUNT_SESSID="))
                        .findFirst()
                        .map(cookie -> cookie.split("=")[1])
                        .orElse(null);

                return sessionToken;
            }

            throw new RuntimeException("Cookie ACCOUNT_SESSID not found");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error calling iliad.it/account web service: " + e.getMessage());
        }
    }
}
