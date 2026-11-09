package com.sustentamed.model;

import com.sustentamed.model.enums.StatusDescarte;
import com.sustentamed.model.enums.StatusLote;
import java.time.LocalDateTime;

public class Descarte {
    private int id;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataColeta;
    private String motivo;
    private StatusDescarte status;
    private String observacao;
    private UnidadeSaude unidadeSaude;
    private LoteMedicamento loteMedicamento;
    private EmpresaDescarte empresaDescarte;

    public Descarte() {
        this.dataSolicitacao = LocalDateTime.now();
        this.status = StatusDescarte.SOLICITADO;
    }

    public Descarte(int id, LocalDateTime dataSolicitacao, LocalDateTime dataColeta,
                    String motivo, StatusDescarte status, String observacao,
                    UnidadeSaude unidadeSaude, LoteMedicamento loteMedicamento,
                    EmpresaDescarte empresaDescarte) {
        this.id = id;
        this.dataSolicitacao = dataSolicitacao;
        this.dataColeta = dataColeta;
        this.motivo = motivo;
        this.status = status;
        this.observacao = observacao;
        this.unidadeSaude = unidadeSaude;
        this.loteMedicamento = loteMedicamento;
        this.empresaDescarte = empresaDescarte;
    }

    public void solicitar() {
        this.status = StatusDescarte.SOLICITADO;
        if (loteMedicamento != null) {
            loteMedicamento.setStatus(StatusLote.DESCARTE);
        }
    }

    public void confirmarColeta() {
        this.status = StatusDescarte.COLETADO;
        this.dataColeta = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusDescarte.CANCELADO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDateTime dataColeta) {
        this.dataColeta = dataColeta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public StatusDescarte getStatus() {
        return status;
    }

    public void setStatus(StatusDescarte status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public LoteMedicamento getLoteMedicamento() {
        return loteMedicamento;
    }

    public void setLoteMedicamento(LoteMedicamento loteMedicamento) {
        this.loteMedicamento = loteMedicamento;
    }

    public EmpresaDescarte getEmpresaDescarte() {
        return empresaDescarte;
    }

    public void setEmpresaDescarte(EmpresaDescarte empresaDescarte) {
        this.empresaDescarte = empresaDescarte;
    }

    @Override
    public String toString() {
        String loteStr = (loteMedicamento != null && loteMedicamento.getMedicamento() != null)
                ? loteMedicamento.getMedicamento().getNome() : "Lote";
        String empStr = (empresaDescarte != null) ? empresaDescarte.getRazaoSocial() : "Empresa";
        return "Descarte #" + id + " - " + loteStr + " | Motivo: " + motivo +
               " | Coletor: " + empStr + " | Status: " + status;
    }
}

