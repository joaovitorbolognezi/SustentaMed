package com.sustentamed.service;

import com.sustentamed.dao.LoteMedicamentoDAO;
import com.sustentamed.model.LoteMedicamento;
import java.sql.SQLException;
import java.util.List;

public class EstoqueService {

    private final LoteMedicamentoDAO loteDAO = new LoteMedicamentoDAO();

    public List<LoteMedicamento> listarEstoquePorUnidade(int unidadeId) throws SQLException {
        return loteDAO.listarPorUnidade(unidadeId);
    }

    public List<LoteMedicamento> listarTodos() throws SQLException {
        return loteDAO.listarTodos();
    }

    public List<LoteMedicamento> obterMatrizSalvacao() throws SQLException {
        return loteDAO.listarMatrizSalvacao();
    }

    public LoteMedicamento buscarPorId(int id) throws SQLException {
        return loteDAO.buscarPorId(id);
    }

    public void cadastrarLote(LoteMedicamento lote) throws SQLException {
        if (lote.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade do lote deve ser maior que zero.");
        }
        if (lote.getDataValidade() == null) {
            throw new IllegalArgumentException("A data de validade é obrigatória.");
        }
        loteDAO.inserir(lote);
    }
}

