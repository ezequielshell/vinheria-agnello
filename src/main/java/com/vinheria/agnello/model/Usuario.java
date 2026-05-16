package com.vinheria.agnello.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 2L;

    private long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;
    private String cpf;
    private LocalDateTime criadoEm;

    public Usuario() {}

    public Usuario(long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public String getPrimeiroNome() {
        if (nome == null || nome.isBlank()) return "";
        int sp = nome.indexOf(' ');
        return sp > 0 ? nome.substring(0, sp) : nome;
    }
}
