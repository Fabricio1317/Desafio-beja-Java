package com.becajava.ms_user.core.domain;

import java.math.BigDecimal;

public class Usuario {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;


    public Usuario(String nome,String cpf, String email,String senha) {
        this.cpf = cpf;
        this.email = email;
        this.nome = nome;
        this.senha = senha;

        validar();
    }

    public Usuario(Long id,String nome,String cpf, String email,String senha) {
        this(nome, cpf, email, senha);
        this.id = id;
    }



    private void validar() {
        if (this.cpf == null || this.cpf.length() != 11){
            throw new IllegalArgumentException("CPF invalido");
        }
        if (this.email == null || !this.email.contains("@")){
            throw new IllegalArgumentException("Email invalido");
        }

    }

    public void atualizarDados(String novoNome, String novoEmail){
        if (novoEmail != null|| !novoEmail.contains("@")){
            throw new IllegalArgumentException("Email invalido");
        }

        if (novoNome != null ){
            this.nome = novoNome;
        }
        if (novoEmail != null){
            this.email = novoEmail;
        }

    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }
}
