package br.com.bix.images.api.rest.controller;

import br.com.bix.images.api.rest.model.AuthRequest;
import br.com.bix.images.api.rest.model.UserRequest;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.service.UserService;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody UserRequest userRequest)
        throws URISyntaxException {
        UserResponse userResponse = userService.save(userRequest);
        return ResponseEntity.created(new URI("/api/v1/user" + userResponse.getId())).build();
    }
}
