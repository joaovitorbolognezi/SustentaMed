package com.sustentamed.model;

import com.sustentamed.model.enums.TipoUsuario;
import java.time.LocalDateTime;

public class Usuario {
    protected int id;
    protected String nome;
    protected String cpf;
    protected String email;
    protected String senha;
    protected boolean ativo;
    protected TipoUsuario tipoUsuario;
    protected UnidadeSaude unidadeSaude;

    public Usuario() {
        this.ativo = true;
    }

    public Usuario(int id, String nome, String cpf, String email, String senha, boolean ativo, TipoUsuario tipoUsuario, UnidadeSaude unidadeSaude) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        this.tipoUsuario = tipoUsuario;
        this.unidadeSaude = unidadeSaude;
    }

    public boolean autenticar(String senhaInformada) {
        if (!this.ativo) return false;
        return this.senha != null && this.senha.equals(senhaInformada);
    }

    public void alterarSenha(String novaSenha) {
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            this.senha = novaSenha;
        }
    }

    public Descarte encaminharDescarte(LoteMedicamento lote, EmpresaDescarte empresa, String motivo) {
        Descarte descarte = new Descarte();
        descarte.setDataSolicitacao(LocalDateTime.now());
        descarte.setMotivo(motivo);
        descarte.setUnidadeSaude(this.unidadeSaude);
        descarte.setLoteMedicamento(lote);
        descarte.setEmpresaDescarte(empresa);
        return descarte;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    @Override
    public String toString() {
        return nome + " (" + tipoUsuario + ") - Email: " + email;
    }
}

