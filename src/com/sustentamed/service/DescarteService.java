package com.sustentamed.service;

import com.sustentamed.dao.DescarteDAO;
import com.sustentamed.dao.EmpresaDescarteDAO;
import com.sustentamed.dao.LoteMedicamentoDAO;
import com.sustentamed.dao.UnidadeSaudeDAO;
import com.sustentamed.model.Descarte;
import com.sustentamed.model.EmpresaDescarte;
import com.sustentamed.model.LoteMedicamento;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.enums.StatusDescarte;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DescarteService {

    private final DescarteDAO descarteDAO = new DescarteDAO();
    private final LoteMedicamentoDAO loteDAO = new LoteMedicamentoDAO();
    private final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();
    private final EmpresaDescarteDAO empDAO = new EmpresaDescarteDAO();

    public void solicitarDescarte(int loteId, int ubsId, int empresaId, String motivo, String obs) throws SQLException {
        LoteMedicamento lote = loteDAO.buscarPorId(loteId);
        if (lote == null) {
            throw new IllegalArgumentException("Lote não encontrado.");
        }
        UnidadeSaude ubs = ubsDAO.buscarPorId(ubsId);
        EmpresaDescarte emp = empDAO.buscarPorId(empresaId);

        Descarte descarte = new Descarte();
        descarte.setDataSolicitacao(LocalDateTime.now());
        descarte.setMotivo(motivo);
        descarte.setStatus(StatusDescarte.SOLICITADO);
        descarte.setObservacao(obs);
        descarte.setUnidadeSaude(ubs);
        descarte.setLoteMedicamento(lote);
        descarte.setEmpresaDescarte(emp);

        descarteDAO.inserir(descarte);
    }

    public void confirmarColeta(int descarteId) throws SQLException {
        descarteDAO.confirmarColeta(descarteId);
    }

    public List<Descarte> listarTodos() throws SQLException {
        return descarteDAO.listarTodos();
    }
}

