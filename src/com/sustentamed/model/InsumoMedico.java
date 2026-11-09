package com.sustentamed.model;

import com.sustentamed.model.enums.StatusInsumo;

public abstract class InsumoMedico {
    protected int id;
    protected String nome;
    protected String descricao;
    protected StatusInsumo status;

    public InsumoMedico() {
        this.status = StatusInsumo.ATIVO;
    }

    public InsumoMedico(int id, String nome, String descricao, StatusInsumo status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
    }

    public abstract int calcularUrgenciaDistribuicao();

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusInsumo getStatus() {
        return status;
    }

    public void setStatus(StatusInsumo status) {
        this.status = status;
    }
}

