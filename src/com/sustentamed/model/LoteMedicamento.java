package com.sustentamed.model;

import com.sustentamed.model.enums.StatusLote;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoteMedicamento {
    private int id;
    private String numeroLote;
    private int quantidade;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private StatusLote status;
    private Medicamento medicamento;
    private UnidadeSaude unidadeSaude;

    public LoteMedicamento() {
        this.status = StatusLote.DISPONIVEL;
    }

    public LoteMedicamento(int id, String numeroLote, int quantidade,
                           LocalDate dataFabricacao, LocalDate dataValidade,
                           StatusLote status, Medicamento medicamento, UnidadeSaude unidadeSaude) {
        this.id = id;
        this.numeroLote = numeroLote;
        this.quantidade = quantidade;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.status = status;
        this.medicamento = medicamento;
        this.unidadeSaude = unidadeSaude;
    }

    /**
     * Calcula quantos dias restam até a data de validade do lote.
     * Retorna número negativo se já estiver vencido.
     */
    public int diasParaVencimento() {
        if (dataValidade == null) return 0;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
    }

    /**
     * Verifica se o lote está próximo do vencimento (30 dias ou menos).
     */
    public boolean isProxVencimento() {
        int dias = diasParaVencimento();
        return dias >= 0 && dias <= 30;
    }

    /**
     * Verifica se o lote já está vencido.
     */
    public boolean isVencido() {
        return diasParaVencimento() < 0;
    }

    /**
     * Calcula o nível numérico de urgência para distribuição comunitária:
     * 3: Crítico (<= 7 dias)
     * 2: Atenção (<= 30 dias)
     * 1: Normal (> 30 dias)
     * 0: Vencido (inapto para consumo, deve ser descartado)
     */
    public int calcularUrgenciaDistribuicao() {
        int dias = diasParaVencimento();
        if (dias < 0) return 0;
        if (dias <= 7) return 3;
        if (dias <= 30) return 2;
        return 1;
    }

    /**
     * Retorna a prioridade textual para a Matriz de Salvação
     * Exatamente como o Frontend React consome ("Crítico", "Atenção", "Normal", "Vencido").
     */
    public String getPrioridadeMatriz() {
        int urgencia = calcularUrgenciaDistribuicao();
        switch (urgencia) {
            case 3: return "Crítico";
            case 2: return "Atenção";
            case 1: return "Normal";
            default: return "Vencido";
        }
    }

    /**
     * Atualiza o status do lote automaticamente com base na validade.
     */
    public void atualizarStatusAutomatico() {
        if (isVencido()) {
            this.status = StatusLote.VENCIDO;
        } else if (isProxVencimento()) {
            this.status = StatusLote.PROXIMO_VENCIMENTO;
        } else {
            this.status = StatusLote.DISPONIVEL;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDate dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
        atualizarStatusAutomatico();
    }

    public StatusLote getStatus() {
        return status;
    }

    public void setStatus(StatusLote status) {
        this.status = status;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    @Override
    public String toString() {
        String medNome = (medicamento != null) ? medicamento.getNome() : "Medicamento";
        String ubsNome = (unidadeSaude != null) ? unidadeSaude.getNome() : "Sem UBS";
        return "[" + numeroLote + "] " + medNome + " - Qtd: " + quantidade + " - Validade: " + dataValidade +
               " (" + getPrioridadeMatriz() + " - " + diasParaVencimento() + " dias restantes) | " + ubsNome;
    }
}

