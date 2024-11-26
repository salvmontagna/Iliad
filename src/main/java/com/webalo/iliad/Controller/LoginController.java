package com.webalo.iliad.Controller;
import com.webalo.iliad.Dto.Login;
import com.webalo.iliad.Service.IliadService;
import com.webalo.iliad.Utilities.AESUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    IliadService iliadService;

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Login request) {

        Map<String, String> response = new HashMap<>();

        try {

            String decryptedPassword = AESUtils.decrypt(request.getPassword());

            String sessionToken = iliadService.callLoginService(request.getUsername(), decryptedPassword);

            response.put("token", sessionToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
