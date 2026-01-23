package com.becajava.ms_user.core.gateway;

public interface PasswordEncoderGateway {
    String encode(String senhaPura);
    boolean matches(String senhaPura, String senhaCriptografada);
}
