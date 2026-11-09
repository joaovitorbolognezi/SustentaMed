package com.sustentamed.model;

import com.sustentamed.model.enums.TipoUsuario;

public class Farmaceutica extends Usuario {
    private String crf;

    public Farmaceutica() {
        super();
        this.tipoUsuario = TipoUsuario.FARMACEUTICA;
    }

    public Farmaceutica(int id, String nome, String cpf, String email, String senha, boolean ativo, UnidadeSaude unidadeSaude, String crf) {
        super(id, nome, cpf, email, senha, ativo, TipoUsuario.FARMACEUTICA, unidadeSaude);
        this.crf = crf;
    }

    public void cadastrarMedicamento(Medicamento medicamento) {
        System.out.println("Farmacêutica (CRF: " + this.crf + ") cadastrou o medicamento: " + medicamento.getNome());
    }

    public void cadastrarLote(LoteMedicamento lote) {
        System.out.println("Farmacêutica (CRF: " + this.crf + ") cadastrou o lote: " + lote.getNumeroLote());
    }

    public String getCrf() {
        return crf;
    }

    public void setCrf(String crf) {
        this.crf = crf;
    }

    @Override
    public String toString() {
        return "Dra. " + nome + " (Farmacêutica - CRF: " + crf + ") - " + email;
    }
}

