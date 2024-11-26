package com.webalo.iliad.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class IliadService {

    public String callLoginService(String username, String password) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            /* Iliad endpoint */
            String url = "https://www.iliad.it/account/";

            String sessionToken = "";

            /* building form-data */
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "login-ident=" + username + "&login-pwd=" + password;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            /* Calling endpoint  */
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            /* taking cookie session */
            String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

            if (setCookieHeader.contains("ACCOUNT_SESSID")) {

                sessionToken = Arrays.stream(setCookieHeader.split(";"))
                        .filter(cookie -> cookie.trim().startsWith("ACCOUNT_SESSID="))
                        .findFirst()
                        .map(cookie -> cookie.split("=")[1])
                        .orElse(null);
            }

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
            headers.add("Cookie", "ACCOUNT_SESSID=" + sessionToken);

            /* Calling endpoint  */
            ResponseEntity<String> realResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (realResponse.getStatusCode().is2xxSuccessful()) {

                Document doc = Jsoup.parse(realResponse.getBody());

                // Extracting login div
                Element loginForm = doc.selectFirst("div.login-form__content--input");

                if (loginForm != null)
                    throw new RuntimeException("Incorrect username or password");
                else{
                    return sessionToken;

                }
            } else {
                throw new RuntimeException("Error while parsing HTML");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error calling iliad.it/account web service: " + e.getMessage());
        }
    }

}
