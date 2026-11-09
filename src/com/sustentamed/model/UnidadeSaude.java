package com.sustentamed.model;

import java.util.ArrayList;
import java.util.List;

public class UnidadeSaude {
    private int id;
    private String cnes;
    private String nome;
    private String endereco;
    private String telefone;
    private boolean ativo;
    private List<LoteMedicamento> estoque;

    public UnidadeSaude() {
        this.ativo = true;
        this.estoque = new ArrayList<>();
    }

    public UnidadeSaude(int id, String cnes, String nome, String endereco, String telefone, boolean ativo) {
        this.id = id;
        this.cnes = cnes;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.ativo = ativo;
        this.estoque = new ArrayList<>();
    }

    public List<LoteMedicamento> consultarEstoque() {
        return this.estoque;
    }

    public void adicionarAoEstoque(LoteMedicamento lote) {
        this.estoque.add(lote);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<LoteMedicamento> getEstoque() {
        return estoque;
    }

    public void setEstoque(List<LoteMedicamento> estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return nome + " (CNES: " + cnes + ") - Tel: " + telefone;
    }
}

