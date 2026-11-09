package com.sustentamed.app;

import com.sustentamed.dao.MedicamentoDAO;
import com.sustentamed.dao.UnidadeSaudeDAO;
import com.sustentamed.model.*;
import com.sustentamed.model.enums.StatusLote;
import com.sustentamed.model.enums.StatusTransferencia;
import com.sustentamed.service.AuthService;
import com.sustentamed.service.DescarteService;
import com.sustentamed.service.EstoqueService;
import com.sustentamed.service.TransferenciaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final EstoqueService estoqueService = new EstoqueService();
    private static final TransferenciaService transferenciaService = new TransferenciaService();
    private static final DescarteService descarteService = new DescarteService();
    private static final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();
    private static final MedicamentoDAO medDAO = new MedicamentoDAO();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static Usuario usuarioLogado = null;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  [ SustentaMed ] - Banco Comunitario de Medicamentos (AEP)");
        System.out.println("  ODS 3 - Saude e Bem-Estar | UniCesumar");
        System.out.println("===============================================================");

        while (true) {
            if (usuarioLogado == null) {
                exibirMenuLogin();
            } else {
                exibirMenuPrincipal();
            }
        }
    }

    private static void exibirMenuLogin() {
        System.out.println("\n--- [ ACESSO AO SISTEMA ] ---");
        System.out.println("1. Entrar (Login)");
        System.out.println("2. Ver credenciais de teste (Seed)");
        System.out.println("0. Encerrar");
        System.out.print("Escolha uma opcao: ");

        String opcao = scanner.nextLine().trim();
        switch (opcao) {
            case "1":
                realizarLogin();
                break;
            case "2":
                exibirCredenciaisTeste();
                break;
            case "0":
                System.out.println("Encerrando o SustentaMed. Ate logo!");
                System.exit(0);
            default:
                System.out.println("[!] Opcao invalida!");
        }
    }

    private static void realizarLogin() {
        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        try {
            usuarioLogado = authService.login(email, senha);
            if (usuarioLogado != null) {
                System.out.println("\n[OK] Login efetuado com sucesso!");
                System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
                if (usuarioLogado.getUnidadeSaude() != null) {
                    System.out.println("Unidade Ativa: " + usuarioLogado.getUnidadeSaude().getNome());
                }
            } else {
                System.out.println("[X] Falha no login: E-mail ou senha incorretos.");
            }
        } catch (Exception e) {
            System.err.println("[X] Erro durante o login: " + e.getMessage());
        }
    }

    private static void exibirCredenciaisTeste() {
        System.out.println("\n--- Usuarios de Teste Disponiveis no seed.sql ---");
        System.out.println("Farmaceutica: camila.farmacia@ubs.gov.br | Senha: 123456 (UBS Central)");
        System.out.println("Enfermeira:   juliana.enfermagem@ubs.gov.br | Senha: 123456 (UBS Jardim)");
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n===============================================================");
        System.out.println("  SustentaMed - Menu Principal | Operador: " + usuarioLogado.getNome());
        System.out.println("===============================================================");
        System.out.println("1. Consultar Estoque de Medicamentos");
        System.out.println("2. [*] Visualizar MATRIZ DE SALVACAO (Lotes Proximos do Vencimento)");
        System.out.println("3. [+] Cadastrar Novo Lote de Medicamento");
        System.out.println("4. [>] Solicitar Transferencia entre UBSs");
        System.out.println("5. [=] Listar e Gerenciar Solicitacoes de Transferencia");
        System.out.println("6. [-] Encaminhar Lote Vencido para Descarte Ecologico");
        System.out.println("7. [~] Listar Descartes e Confirmar Coleta");
        System.out.println("8. [0] Desconectar (Logout)");
        System.out.print("Escolha uma opcao: ");

        String opcao = scanner.nextLine().trim();
        try {
            switch (opcao) {
                case "1":
                    consultarEstoque();
                    break;
                case "2":
                    visualizarMatrizSalvacao();
                    break;
                case "3":
                    cadastrarLote();
                    break;
                case "4":
                    solicitarTransferencia();
                    break;
                case "5":
                    gerenciarTransferencias();
                    break;
                case "6":
                    encaminharDescarte();
                    break;
                case "7":
                    gerenciarDescartes();
                    break;
                case "8":
                    usuarioLogado = null;
                    System.out.println("Desconectado com sucesso.");
                    break;
                default:
                    System.out.println("[!] Opcao invalida!");
            }
        } catch (Exception e) {
            System.err.println("[X] Erro na operacao: " + e.getMessage());
        }
    }

    private static void consultarEstoque() throws Exception {
        System.out.println("\n--- ESTOQUE DE MEDICAMENTOS ---");
        List<LoteMedicamento> lotes = estoqueService.listarTodos();
        if (lotes.isEmpty()) {
            System.out.println("Nenhum lote cadastrado no momento.");
            return;
        }

        System.out.printf("%-6s | %-15s | %-25s | %-6s | %-12s | %-15s | %-15s%n",
                "ID", "N LOTE", "MEDICAMENTO", "QTD", "VALIDADE", "STATUS", "UNIDADE");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (LoteMedicamento l : lotes) {
            String med = l.getMedicamento() != null ? l.getMedicamento().getNome() : "-";
            String ubs = l.getUnidadeSaude() != null ? l.getUnidadeSaude().getNome() : "-";
            System.out.printf("%-6d | %-15s | %-25s | %-6d | %-12s | %-15s | %-15s%n",
                    l.getId(), l.getNumeroLote(), med, l.getQuantidade(),
                    l.getDataValidade().format(dtf), l.getStatus(), ubs);
        }
    }

    private static void visualizarMatrizSalvacao() throws Exception {
        System.out.println("\n===============================================================");
        System.out.println("  [*] MATRIZ DE SALVACAO - REDISTRIBUICAO COMUNITARIA");
        System.out.println("  Identificacao preventiva de medicamentos antes da expiracao");
        System.out.println("===============================================================");

        List<LoteMedicamento> matriz = estoqueService.obterMatrizSalvacao();
        if (matriz.isEmpty()) {
            System.out.println("Todos os lotes estao em estado seguro com validade ampla.");
            return;
        }

        System.out.printf("%-6s | %-12s | %-22s | %-5s | %-10s | %-12s | %-15s%n",
                "ID", "N LOTE", "MEDICAMENTO", "QTD", "DIAS REST.", "PRIORIDADE", "UBS ATUAL");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (LoteMedicamento l : matriz) {
            String prioridade = l.getPrioridadeMatriz();
            String alerta = prioridade.equalsIgnoreCase("Crítico") || prioridade.equalsIgnoreCase("Critico") ? "[CRITICO]" :
                            prioridade.equalsIgnoreCase("Vencido") ? "[VENCIDO]" : "[ATENCAO]";

            String med = l.getMedicamento() != null ? l.getMedicamento().getNome() : "-";
            String ubs = l.getUnidadeSaude() != null ? l.getUnidadeSaude().getNome() : "-";

            System.out.printf("%-6d | %-12s | %-22s | %-5d | %-10d | %-12s | %-15s%n",
                    l.getId(), l.getNumeroLote(), med, l.getQuantidade(),
                    l.diasParaVencimento(), alerta, ubs);
        }
    }

    private static void cadastrarLote() throws Exception {
        System.out.println("\n--- CADASTRO DE NOVO LOTE ---");
        List<Medicamento> medicamentos = medDAO.listarTodos();
        if (medicamentos.isEmpty()) {
            System.out.println("Nenhum medicamento disponivel no catalogo.");
            return;
        }

        System.out.println("Selecione o medicamento:");
        for (Medicamento m : medicamentos) {
            System.out.println(m.getId() + ". " + m.getNome() + " " + m.getDosagem() + " (" + m.getFabricante() + ")");
        }
        System.out.print("ID do Medicamento: ");
        int medId = Integer.parseInt(scanner.nextLine().trim());
        Medicamento med = medDAO.buscarPorId(medId);
        if (med == null) {
            System.out.println("[!] Medicamento invalido!");
            return;
        }

        System.out.print("Numero do Lote (ex: DIP-2026-099): ");
        String numeroLote = scanner.nextLine().trim();

        System.out.print("Quantidade: ");
        int qtd = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Data de Validade (dd/MM/yyyy): ");
        String valStr = scanner.nextLine().trim();
        LocalDate validade = LocalDate.parse(valStr, dtf);

        LoteMedicamento novoLote = new LoteMedicamento();
        novoLote.setNumeroLote(numeroLote);
        novoLote.setQuantidade(qtd);
        novoLote.setDataFabricacao(LocalDate.now());
        novoLote.setDataValidade(validade);
        novoLote.setMedicamento(med);
        novoLote.setUnidadeSaude(usuarioLogado.getUnidadeSaude());

        estoqueService.cadastrarLote(novoLote);
        System.out.println("[OK] Lote cadastrado com sucesso! ID gerado: " + novoLote.getId());
    }

    private static void solicitarTransferencia() throws Exception {
        System.out.println("\n--- SOLICITACAO DE TRANSFERENCIA DE MEDICAMENTOS ---");
        System.out.println("Lotes disponiveis no sistema:");
        consultarEstoque();

        System.out.print("\nInforme o ID do lote que deseja transferir: ");
        int loteId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Quantidade a ser transferida: ");
        int qtd = Integer.parseInt(scanner.nextLine().trim());

        List<UnidadeSaude> unidades = ubsDAO.listarTodas();
        System.out.println("\nSelecione a Unidade de Destino:");
        for (UnidadeSaude u : unidades) {
            System.out.println(u.getId() + ". " + u.getNome());
        }
        System.out.print("ID da Unidade de Destino: ");
        int destinoId = Integer.parseInt(scanner.nextLine().trim());

        int origemId = usuarioLogado.getUnidadeSaude() != null ? usuarioLogado.getUnidadeSaude().getId() : 1;

        System.out.print("Observacao/Motivo da redistribuicao: ");
        String obs = scanner.nextLine().trim();

        transferenciaService.solicitarTransferencia(loteId, origemId, destinoId, qtd, obs);
        System.out.println("[OK] Solicitacao de transferencia registrada com sucesso!");
    }

    private static void gerenciarTransferencias() throws Exception {
        System.out.println("\n--- SOLICITACOES DE TRANSFERENCIA REGISTRADAS ---");
        List<SolicitacaoTransferencia> lista = transferenciaService.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma transferencia registrada.");
            return;
        }

        for (SolicitacaoTransferencia st : lista) {
            System.out.println(st);
            if (st.getObservacao() != null && !st.getObservacao().isEmpty()) {
                System.out.println("   Obs: " + st.getObservacao());
            }
        }

        System.out.print("\nDeseja atualizar alguma transferencia pendente? (S/N): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
            System.out.print("Informe o ID da transferencia: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("1. Aprovar (deduzir do estoque de origem)");
            System.out.println("2. Rejeitar");
            System.out.print("Escolha: ");
            String acao = scanner.nextLine().trim();

            if ("1".equals(acao)) {
                transferenciaService.aprovarTransferencia(id);
                System.out.println("[OK] Transferencia aprovada e estoque atualizado!");
            } else if ("2".equals(acao)) {
                transferenciaService.rejeitarTransferencia(id);
                System.out.println("[X] Transferencia rejeitada.");
            }
        }
    }

    private static void encaminharDescarte() throws Exception {
        System.out.println("\n--- ENCAMINHAMENTO PARA DESCARTE ECOLOGICO ---");
        System.out.println("Lotes no sistema:");
        consultarEstoque();

        System.out.print("\nID do lote a ser descartado: ");
        int loteId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Motivo do descarte (ex: Vencido / Embalagem danificada): ");
        String motivo = scanner.nextLine().trim();

        System.out.print("Observacoes: ");
        String obs = scanner.nextLine().trim();

        int ubsId = usuarioLogado.getUnidadeSaude() != null ? usuarioLogado.getUnidadeSaude().getId() : 1;
        int empresaId = 1; // Empresa cadastrada no seed

        descarteService.solicitarDescarte(loteId, ubsId, empresaId, motivo, obs);
        System.out.println("[OK] Lote encaminhado para descarte ecologico com sucesso!");
    }

    private static void gerenciarDescartes() throws Exception {
        System.out.println("\n--- REGISTRO DE DESCARTES E COLETA AMBIENTAL ---");
        List<Descarte> lista = descarteService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum descarte registrado.");
            return;
        }

        for (Descarte d : lista) {
            System.out.println(d);
        }

        System.out.print("\nDeseja confirmar a coleta de algum descarte? (S/N): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
            System.out.print("ID do Descarte: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            descarteService.confirmarColeta(id);
            System.out.println("[OK] Coleta ecologica confirmada!");
        }
    }
}
