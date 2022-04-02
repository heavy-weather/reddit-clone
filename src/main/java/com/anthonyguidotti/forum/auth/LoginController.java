package com.anthonyguidotti.forum.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {
    private final OIDCService oidcService;

    public LoginController(OIDCService oidcService) {
        this.oidcService = oidcService;
    }

    @PostMapping(
            value = "/users/login"
    )
    public String login() {
        return "";
    }


    @GetMapping(value = "/test")
    public String test(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return oidcService.authenticationUri(session);
    }
}
