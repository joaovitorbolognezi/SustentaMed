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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final ConcurrentHashMap<String, Integer> SESSOES = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0);

        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/auth/me", new AuthMeHandler());
        server.createContext("/api/auth/logout", new AuthLogoutHandler());
        server.createContext("/api/lotes", new LotesHandler());
        server.createContext("/api/matriz-salvacao", new MatrizHandler());
        server.createContext("/api/transferencias", new TransferenciasHandler());
        server.createContext("/api/descartes", new DescartesHandler());
        server.createContext("/api/unidades", new UnidadesHandler());
        server.createContext("/api/medicamentos", new MedicamentosHandler());

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

    private static String escaparJson(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String criarTokenSessao(int usuarioId) {
        String token = UUID.randomUUID().toString();
        SESSOES.put(token, usuarioId);
        return token;
    }

    private static String extrairBearerToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return auth.substring(7).trim();
        }
        return null;
    }

    private static Integer resolverUsuarioDaRequisicao(HttpExchange exchange) {
        String token = extrairBearerToken(exchange);
        if (token == null || token.isEmpty()) {
            return null;
        }
        return SESSOES.get(token);
    }

    private static void encerrarSessao(String token) {
        if (token != null) {
            SESSOES.remove(token);
        }
    }

    private static String serializarUsuarioJson(Usuario u) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":").append(u.getId())
          .append(",\"nome\":\"").append(escaparJson(u.getNome()))
          .append("\",\"email\":\"").append(escaparJson(u.getEmail()))
          .append("\",\"tipo\":\"").append(u.getTipoUsuario()).append("\"");
        if (u.getUnidadeSaude() != null) {
            sb.append(",\"unidade\":{\"id\":").append(u.getUnidadeSaude().getId())
              .append(",\"nome\":\"").append(escaparJson(u.getUnidadeSaude().getNome())).append("\"}");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String serializarLoteJson(LoteMedicamento l) {
        return "{\"id\":" + l.getId()
                + ",\"medicamento\":\"" + escaparJson(l.getMedicamento() != null ? l.getMedicamento().getNome() : "")
                + "\",\"numeroLote\":\"" + escaparJson(l.getNumeroLote())
                + "\",\"quantidade\":" + l.getQuantidade()
                + ",\"validade\":\"" + l.getDataValidade()
                + "\",\"status\":\"" + l.getStatus()
                + "\",\"unidade\":\"" + escaparJson(l.getUnidadeSaude() != null ? l.getUnidadeSaude().getNome() : "")
                + "\",\"unidadeId\":" + (l.getUnidadeSaude() != null ? l.getUnidadeSaude().getId() : 0)
                + ",\"medicamentoId\":" + (l.getMedicamento() != null ? l.getMedicamento().getId() : 0)
                + ",\"diasRestantes\":" + l.diasParaVencimento()
                + ",\"prioridade\":\"" + escaparJson(l.getPrioridadeMatriz()) + "\"}";
    }

    private static String serializarDescarteJson(Descarte d) {
        String med = (d.getLoteMedicamento() != null && d.getLoteMedicamento().getMedicamento() != null)
                ? d.getLoteMedicamento().getMedicamento().getNome() : "";
        String lote = (d.getLoteMedicamento() != null) ? d.getLoteMedicamento().getNumeroLote() : "";
        String ubs = (d.getUnidadeSaude() != null) ? d.getUnidadeSaude().getNome() : "";
        String emp = (d.getEmpresaDescarte() != null) ? d.getEmpresaDescarte().getRazaoSocial() : "";
        return "{\"id\":" + d.getId()
                + ",\"motivo\":\"" + escaparJson(d.getMotivo())
                + "\",\"status\":\"" + d.getStatus()
                + "\",\"data\":\"" + d.getDataSolicitacao()
                + "\",\"medicamento\":\"" + escaparJson(med)
                + "\",\"numeroLote\":\"" + escaparJson(lote)
                + "\",\"unidade\":\"" + escaparJson(ubs)
                + "\",\"empresa\":\"" + escaparJson(emp) + "\"}";
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
                        String token = criarTokenSessao(u.getId());
                        String json = "{\"sucesso\":true,\"token\":\"" + token + "\",\"usuario\":"
                                + serializarUsuarioJson(u) + "}";
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

    static class AuthMeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try {
                Integer userId = resolverUsuarioDaRequisicao(exchange);
                if (userId == null) {
                    enviarRespostaJson(exchange, 401, "{\"sucesso\":false,\"mensagem\":\"Sessão inválida ou expirada\"}");
                    return;
                }
                Usuario u = authService.obterPorId(userId);
                if (u == null) {
                    enviarRespostaJson(exchange, 401, "{\"sucesso\":false,\"mensagem\":\"Usuário não encontrado\"}");
                    return;
                }
                enviarRespostaJson(exchange, 200, "{\"usuario\":" + serializarUsuarioJson(u) + "}");
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + escaparJson(e.getMessage()) + "\"}");
            }
        }
    }

    static class AuthLogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String token = extrairBearerToken(exchange);
            encerrarSessao(token);
            enviarRespostaJson(exchange, 200, "{\"sucesso\":true}");
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

            String path = exchange.getRequestURI().getPath();

            try {
                if (path.matches("/api/lotes/\\d+") && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    int id = Integer.parseInt(path.replaceAll(".*/(\\d+)$", "$1"));
                    LoteMedicamento lote = estoqueService.buscarPorId(id);
                    if (lote == null) {
                        enviarRespostaJson(exchange, 404, "{\"sucesso\":false,\"mensagem\":\"Lote não encontrado\"}");
                    } else {
                        enviarRespostaJson(exchange, 200, serializarLoteJson(lote));
                    }
                    return;
                }

                if ("/api/lotes".equals(path) && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<LoteMedicamento> lotes = estoqueService.listarTodos();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lotes.size(); i++) {
                        if (i > 0) sb.append(",");
                        sb.append(serializarLoteJson(lotes.get(i)));
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                } else if ("/api/lotes".equals(path) && "POST".equalsIgnoreCase(exchange.getRequestMethod())) {
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

            String path = exchange.getRequestURI().getPath();

            try {
                if (path.matches("/api/transferencias/\\d+/status")
                        && "PATCH".equalsIgnoreCase(exchange.getRequestMethod())) {
                    int id = Integer.parseInt(path.replaceAll(".*/(\\d+)/status", "$1"));
                    String body = lerCorpo(exchange);
                    String status = extrairCampoJson(body, "status").toUpperCase();

                    if ("APROVADA".equals(status)) {
                        transferenciaService.aprovarTransferencia(id);
                    } else if ("REJEITADA".equals(status)) {
                        transferenciaService.rejeitarTransferencia(id);
                    } else if ("CANCELADA".equals(status)) {
                        transferenciaService.cancelarTransferencia(id);
                    } else {
                        enviarRespostaJson(exchange, 400,
                                "{\"sucesso\":false,\"erro\":\"Status inválido. Use APROVADA, REJEITADA ou CANCELADA.\"}");
                        return;
                    }
                    enviarRespostaJson(exchange, 200, "{\"sucesso\":true}");
                    return;
                }

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
                        if (i > 0) sb.append(",");
                        sb.append(serializarDescarteJson(lista.get(i)));
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                }
            } catch (Exception e) {
                enviarRespostaJson(exchange, 500, "{\"sucesso\":false,\"erro\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // Handler de Medicamentos (catálogo para cadastro de lotes)
    static class MedicamentosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aplicarCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<Medicamento> lista = medDAO.listarTodos();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        Medicamento m = lista.get(i);
                        if (i > 0) sb.append(",");
                        sb.append("{\"id\":").append(m.getId())
                          .append(",\"nome\":\"").append(m.getNome()).append("\"}");
                    }
                    sb.append("]");
                    enviarRespostaJson(exchange, 200, sb.toString());
                } else {
                    exchange.sendResponseHeaders(405, -1);
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

