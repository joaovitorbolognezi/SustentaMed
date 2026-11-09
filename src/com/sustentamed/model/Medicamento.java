package com.sustentamed.model;

import com.sustentamed.model.enums.StatusInsumo;
import com.sustentamed.model.enums.TipoReceita;

public class Medicamento extends InsumoMedico {
    private String principioAtivo;
    private String dosagem;
    private String fabricante;
    private String formaFarmaceutica;
    private TipoReceita tipoReceita;

    public Medicamento() {
        super();
        this.tipoReceita = TipoReceita.SIMPLES;
    }

    public Medicamento(int id, String nome, String descricao, StatusInsumo status,
                       String principioAtivo, String dosagem, String fabricante,
                       String formaFarmaceutica, TipoReceita tipoReceita) {
        super(id, nome, descricao, status);
        this.principioAtivo = principioAtivo;
        this.dosagem = dosagem;
        this.fabricante = fabricante;
        this.formaFarmaceutica = formaFarmaceutica;
        this.tipoReceita = tipoReceita;
    }

    @Override
    public int calcularUrgenciaDistribuicao() {
        // Medicamentos de receita controlada ou de alto impacto podem ter urgência diferenciada
        if (tipoReceita == TipoReceita.CONTROLADA || tipoReceita == TipoReceita.RETENCAO) {
            return 2; // Alta urgência
        }
        return 1; // Padrão
    }

    public boolean verificarValidade() {
        return this.status == StatusInsumo.ATIVO;
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getFormaFarmaceutica() {
        return formaFarmaceutica;
    }

    public void setFormaFarmaceutica(String formaFarmaceutica) {
        this.formaFarmaceutica = formaFarmaceutica;
    }

    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    @Override
    public String toString() {
        return nome + " " + dosagem + " (" + fabricante + ") - " + formaFarmaceutica;
    }
}

