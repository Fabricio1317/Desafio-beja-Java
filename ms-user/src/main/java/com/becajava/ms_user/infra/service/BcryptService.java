package com.becajava.ms_user.infra.service;

import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class BcryptService implements PasswordEncoderGateway {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String senhaPura) {
        return passwordEncoder.encode(senhaPura) ;
    }

    @Override
    public boolean matches(String senhaPura, String senhaCriptografada) {
        return passwordEncoder.matches(senhaPura,senhaCriptografada);
    }
}
