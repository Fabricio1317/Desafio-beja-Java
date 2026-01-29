package com.becajava.ms_user.core.domain;

public class Usuario {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private UserRole role;


    public Usuario(String nome, String cpf, String email, String senha, UserRole role) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.role = role;

        validar();
    }

    public Usuario(Long id, String nome, String cpf, String email, String senha, UserRole role) {
        this(nome, cpf, email, senha, role);
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
        if (novoEmail != null && !novoEmail.contains("@")){
            throw new IllegalArgumentException("Email invalido");
        }

        if (novoNome != null ){
            this.nome = novoNome;
        }
        if (novoEmail != null){
            this.email = novoEmail;
        }
    }


    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public UserRole getRole() { return role; }
}