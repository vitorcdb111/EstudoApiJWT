package com.api.integracao.comjwt.controller;

import com.api.integracao.comjwt.config.JwtTokenUtil;
import com.api.integracao.comjwt.model.JwtRequest;
import com.api.integracao.comjwt.model.JwtResponse;
import com.api.integracao.comjwt.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;


    //Pega o username e a password no body
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        //Se a autenticacao for valida, gera um token
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (DisabledException e)
        {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e)
        {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
