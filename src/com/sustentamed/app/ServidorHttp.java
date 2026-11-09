package com.sustentamed.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sustentamed.dao.MedicamentoDAO;
import com.sustentamed.dao.UnidadeSaudeDAO;
import com.sustentamed.model.*;
import com.sustentamed.model.enums.StatusTransferencia;
import com.sustentamed.service.AuthService;
import com.sustentamed.service.DescarteService;
import com.sustentamed.service.EstoqueService;
import com.sustentamed.service.TransferenciaService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Servidor HTTP REST nativo em Java Puro (sem Spring, sem bibliotecas externas).
 * Permite que o frontend React se conecte com o Backend Java localmente com suporte a CORS!
 */
public class ServidorHttp {

    private static final int PORTA = 8080;
    private static final AuthService authService = new AuthService();
    private static final EstoqueService estoqueService = new EstoqueService();
    private static final TransferenciaService transferenciaService = new TransferenciaService();
    private static final DescarteService descarteService = new DescarteService();
    private static final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();
    private static final MedicamentoDAO medDAO = new MedicamentoDAO();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0);

        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/lotes", new LotesHandler());
        server.createContext("/api/matriz-salvacao", new MatrizHandler());
        server.createContext("/api/transferencias", new TransferenciasHandler());
        server.createContext("/api/descartes", new DescartesHandler());
        server.createContext("/api/unidades", new UnidadesHandler());

        server.setExecutor(null);
        System.out.println("===============================================================");
        System.out.println("  🚀 Servidor REST Java Puro rodando na porta: " + PORTA);
        System.out.println("  🔗 URL Base: http://localhost:" + PORTA);
        System.out.println("  🌐 Pronto para receber chamadas do Frontend React!");
        System.out.println("===============================================================");
        server.start();
    }

    private static void aplicarCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE, PATCH");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void enviarRespostaJson(HttpExchange exchange, int status, String json) throws IOException {
        aplicarCors(exchange);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    // Handler de Login
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = lerCorpo(exchange);
                String email = extrairCampoJson(body, "email");
                String senha = extrairCampoJson(body, "senha");

                try {
                    Usuario u = authService.login(email, senha);
                    if (u != null) {
                        String json = "{\"sucesso\":true,\"usuario\":{\"id\":" + u.getId() +
                                      ",\"nome\":\"" + u.getNome() + "\",\"email\":\"" + u.getEmail() +
                                      "\",\"tipo\":\"" + u.getTipoUsuario() + "\"}}";
                        enviarRespostaJson(exchange, 200, json);
                    } else {
                        enviarRespostaJson(exchange, 401, "{\"sucesso\":false,\"mensagem\":\"Credenciais inválidas\"}");
                    }
                } catch (Exception e) {
                    enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    // Handler de Lotes
    static class LotesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<LoteMedicamento> lotes = estoqueService.listarTodos();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lotes.size(); i++) {
                        LoteMedicamento l = lotes.get(i);
                        if (i > 0) sb.append(",");
                        sb.append("{\"id\":").append(l.getId())
                          .append(",\"medicamento\":\"").append(l.getMedicamento() != null ? l.getMedicamento().getNome() : "")
                          .append("\",\"numeroLote\":\"").append(l.getNumeroLote())
                          .append("\",\"quantidade\":").append(l.getQuantidade())
                          .append(",\"validade\":\"").append(l.getDataValidade())
                          .append("\",\"status\":\"").append(l.getStatus())
                          .append("\",\"unidade\":\"").append(l.getUnidadeSaude() != null ? l.getUnidadeSaude().getNome() : "")
                          .append("\"}");
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String body = lerCorpo(exchange);
                    String numeroLote = extrairCampoJson(body, "numeroLote");
                    int qtd = Integer.parseInt(extrairCampoJson(body, "quantidade"));
                    String valStr = extrairCampoJson(body, "validade");
                    int medId = Integer.parseInt(extrairCampoJson(body, "medicamentoId"));
                    int ubsId = Integer.parseInt(extrairCampoJson(body, "unidadeId"));

                    Medicamento med = medDAO.buscarPorId(medId);
                    UnidadeSaude ubs = ubsDAO.buscarPorId(ubsId);

                    LoteMedicamento lote = new LoteMedicamento();
                    lote.setNumeroLote(numeroLote);
                    lote.setQuantidade(qtd);
                    lote.setDataFabricacao(LocalDate.now());
                    lote.setDataValidade(LocalDate.parse(valStr));
                    lote.setMedicamento(med);
                    lote.setUnidadeSaude(ubs);

                    estoqueService.cadastrarLote(lote);
                    enviarRespostaJson(exchange, 201, "{\"sucesso\":true,\"id\":" + lote.getId() + "}");
                }
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Handler da Matriz de Salvacao
    static class MatrizHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                List<LoteMedicamento> matriz = estoqueService.obterMatrizSalvacao();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < matriz.size(); i++) {
                    LoteMedicamento l = matriz.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{\"id\":").append(l.getId())
                      .append(",\"medicamento\":\"").append(l.getMedicamento() != null ? l.getMedicamento().getNome() : "")
                      .append("\",\"numeroLote\":\"").append(l.getNumeroLote())
                      .append("\",\"quantidade\":").append(l.getQuantidade())
                      .append(",\"validade\":\"").append(l.getDataValidade())
                      .append("\",\"diasRestantes\":").append(l.diasParaVencimento())
                      .append(",\"prioridade\":\"").append(l.getPrioridadeMatriz())
                      .append("\",\"unidade\":\"").append(l.getUnidadeSaude() != null ? l.getUnidadeSaude().getNome() : "")
                      .append("\"}");
                }
                sb.append("]");
                enviarRespostaJson(exchange, 200, sb.toString());
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Handler de Transferencias
    static class TransferenciasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<SolicitacaoTransferencia> lista = transferenciaService.listarTodas();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        SolicitacaoTransferencia t = lista.get(i);
                        if (i > 0) sb.append(",");
                        sb.append("{\"id\":").append(t.getId())
                          .append(",\"medicamento\":\"").append(t.getLoteMedicamento() != null && t.getLoteMedicamento().getMedicamento() != null ? t.getLoteMedicamento().getMedicamento().getNome() : "")
                          .append("\",\"origem\":\"").append(t.getUnidadeOrigem() != null ? t.getUnidadeOrigem().getNome() : "")
                          .append("\",\"destino\":\"").append(t.getUnidadeDestino() != null ? t.getUnidadeDestino().getNome() : "")
                          .append("\",\"quantidade\":").append(t.getQuantidade())
                          .append(",\"status\":\"").append(t.getStatus())
                          .append("\",\"data\":\"").append(t.getDataSolicitacao())
                          .append("\"}");
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String body = lerCorpo(exchange);
                    int loteId = Integer.parseInt(extrairCampoJson(body, "loteId"));
                    int origemId = Integer.parseInt(extrairCampoJson(body, "origemId"));
                    int destinoId = Integer.parseInt(extrairCampoJson(body, "destinoId"));
                    int qtd = Integer.parseInt(extrairCampoJson(body, "quantidade"));
                    String obs = extrairCampoJson(body, "observacao");

                    transferenciaService.solicitarTransferencia(loteId, origemId, destinoId, qtd, obs);
                    enviarRespostaJson(exchange, 201, "{\"sucesso\":true}");
                }
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Handler de Descartes
    static class DescartesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<Descarte> lista = descarteService.listarTodos();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        Descarte d = lista.get(i);
                        if (i > 0) sb.append(",");
                        sb.append("{\"id\":").append(d.getId())
                          .append(",\"motivo\":\"").append(d.getMotivo())
                          .append("\",\"status\":\"").append(d.getStatus())
                          .append("\",\"data\":\"").append(d.getDataSolicitacao())
                          .append("\"}");
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                }
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Handler de Unidades
    static class UnidadesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                List<UnidadeSaude> lista = ubsDAO.listarTodas();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    UnidadeSaude u = lista.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{\"id\":").append(u.getId())
                      .append(",\"cnes\":\"").append(u.getCnes())
                      .append("\",\"nome\":\"").append(u.getNome())
                      .append("\"}");
                }
                sb.append("]");
                enviarRespostaJson(exchange, 200, sb.toString());
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Utilitário simples para extrair propriedades de JSON sem precisar de lib externa (Jackson/Gson)
    private static String extrairCampoJson(String json, String campo) {
        String chave = "\"" + campo + "\":";
        int idx = json.indexOf(chave);
        if (idx == -1) return "";

        int start = idx + chave.length();
        // Ignora espaços
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\"')) {
            start++;
        }

        int end = start;
        boolean dentroDeAspas = (json.charAt(idx + chave.length()) == '\"' || (start > 0 && json.charAt(start - 1) == '\"'));
        if (dentroDeAspas) {
            end = json.indexOf("\"", start);
        } else {
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != '\n' && json.charAt(end) != '\r') {
                end++;
            }
        }

        if (end == -1) end = json.length();
        return json.substring(start, end).trim();
    }
}

