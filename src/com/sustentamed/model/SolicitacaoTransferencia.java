package com.sustentamed.model;

import com.sustentamed.model.enums.StatusTransferencia;
import java.time.LocalDateTime;

public class SolicitacaoTransferencia {
    private int id;
    private int quantidade;
    private LocalDateTime dataSolicitacao;
    private StatusTransferencia status;
    private String observacao;
    private UnidadeSaude unidadeOrigem;
    private UnidadeSaude unidadeDestino;
    private LoteMedicamento loteMedicamento;

    public SolicitacaoTransferencia() {
        this.dataSolicitacao = LocalDateTime.now();
        this.status = StatusTransferencia.PENDENTE;
    }

    public SolicitacaoTransferencia(int id, int quantidade, LocalDateTime dataSolicitacao,
                                   StatusTransferencia status, String observacao,
                                   UnidadeSaude unidadeOrigem, UnidadeSaude unidadeDestino,
                                   LoteMedicamento loteMedicamento) {
        this.id = id;
        this.quantidade = quantidade;
        this.dataSolicitacao = dataSolicitacao;
        this.status = status;
        this.observacao = observacao;
        this.unidadeOrigem = unidadeOrigem;
        this.unidadeDestino = unidadeDestino;
        this.loteMedicamento = loteMedicamento;
    }

    public void aprovar() {
        if (this.status == StatusTransferencia.PENDENTE) {
            this.status = StatusTransferencia.APROVADA;
            // Efetua transferência se houver lote e estoque suficiente
            if (loteMedicamento != null && loteMedicamento.getQuantidade() >= quantidade) {
                loteMedicamento.setQuantidade(loteMedicamento.getQuantidade() - quantidade);
            }
        }
    }

    public void rejeitar() {
        if (this.status == StatusTransferencia.PENDENTE) {
            this.status = StatusTransferencia.REJEITADA;
        }
    }

    public void cancelar() {
        if (this.status == StatusTransferencia.PENDENTE) {
            this.status = StatusTransferencia.CANCELADA;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public StatusTransferencia getStatus() {
        return status;
    }

    public void setStatus(StatusTransferencia status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UnidadeSaude getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeSaude unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public UnidadeSaude getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeSaude unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public LoteMedicamento getLoteMedicamento() {
        return loteMedicamento;
    }

    public void setLoteMedicamento(LoteMedicamento loteMedicamento) {
        this.loteMedicamento = loteMedicamento;
    }

    @Override
    public String toString() {
        String loteStr = (loteMedicamento != null && loteMedicamento.getMedicamento() != null)
                ? loteMedicamento.getMedicamento().getNome() : "Medicamento";
        String origStr = (unidadeOrigem != null) ? unidadeOrigem.getNome() : "Origem";
        String destStr = (unidadeDestino != null) ? unidadeDestino.getNome() : "Destino";
        return "#" + id + " - " + loteStr + " (" + quantidade + " un.) | " +
               origStr + " -> " + destStr + " | Status: " + status;
    }
}

