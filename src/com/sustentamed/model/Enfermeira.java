package com.sustentamed.model;

import com.sustentamed.model.enums.TipoUsuario;

public class Enfermeira extends Usuario {
    private String coren;

    public Enfermeira() {
        super();
        this.tipoUsuario = TipoUsuario.ENFERMEIRA;
    }

    public Enfermeira(int id, String nome, String cpf, String email, String senha, boolean ativo, UnidadeSaude unidadeSaude, String coren) {
        super(id, nome, cpf, email, senha, ativo, TipoUsuario.ENFERMEIRA, unidadeSaude);
        this.coren = coren;
    }

    public void cadastrarMedicamento(Medicamento medicamento) {
        System.out.println("Enfermeira (COREN: " + this.coren + ") cadastrou o medicamento: " + medicamento.getNome());
    }

    public void cadastrarLote(LoteMedicamento lote) {
        System.out.println("Enfermeira (COREN: " + this.coren + ") cadastrou o lote: " + lote.getNumeroLote());
    }

    public String getCoren() {
        return coren;
    }

    public void setCoren(String coren) {
        this.coren = coren;
    }

    @Override
    public String toString() {
        return "Enf. " + nome + " (COREN: " + coren + ") - " + email;
    }
}

