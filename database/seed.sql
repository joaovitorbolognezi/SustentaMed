-- ========================================================
-- SustentaMed - Dados Iniciais de Teste (Seed Data)
-- ========================================================

USE sistema_saude;

-- 1. Unidades de Saude (UBS)
INSERT INTO unidades_saude (id, cnes, nome, endereco, telefone, ativo) VALUES
(1, '1234567', 'UBS Central', 'Av. Brasil, 1500 - Centro', '(44) 3001-1001', TRUE),
(2, '2345678', 'UBS Jardim', 'Rua das Flores, 420 - Jardim', '(44) 3001-1002', TRUE),
(3, '3456789', 'UBS Norte', 'Av. Colombo, 850 - Zona Norte', '(44) 3001-1003', TRUE),
(4, '4567890', 'UBS Sul', 'Rua Pioneiros, 120 - Zona Sul', '(44) 3001-1004', TRUE);

-- 2. Usuarios (Farmaceutica e Enfermeira)
INSERT INTO usuarios (id, nome, cpf, email, senha, ativo, tipo_usuario, crf, coren, unidade_saude_id) VALUES
(1, 'Dra. Camila Rocha', '111.222.333-44', 'camila.farmacia@ubs.gov.br', '123456', TRUE, 'FARMACEUTICA', 'CRF-PR 12345', NULL, 1),
(2, 'Enf. Juliana Silveira', '555.666.777-88', 'juliana.enfermagem@ubs.gov.br', '123456', TRUE, 'ENFERMEIRA', NULL, 'COREN-PR 67890', 2);

-- 3. Insumos Medicos / Medicamentos
INSERT INTO insumos_medicos (id, nome, descricao, status, tipo_insumo, principio_ativo, dosagem, fabricante, forma_farmaceutica, tipo_receita) VALUES
(1, 'Amoxicilina 500mg', 'Antibiotico de amplo espectro', 'ATIVO', 'MEDICAMENTO', 'Amoxicilina Tri-hidratada', '500mg', 'EMS', 'Capsula', 'CONTROLADA'),
(2, 'Dipirona 500mg', 'Analgesico e antipiretico', 'ATIVO', 'MEDICAMENTO', 'Dipirona Monoidratada', '500mg', 'Medley', 'Comprimido', 'SIMPLES'),
(3, 'Paracetamol 750mg', 'Analgesico e antipiretico', 'ATIVO', 'MEDICAMENTO', 'Paracetamol', '750mg', 'Eurofarma', 'Comprimido', 'SIMPLES'),
(4, 'Ibuprofeno 600mg', 'Anti-inflamatorio nao esteroide', 'ATIVO', 'MEDICAMENTO', 'Ibuprofeno', '600mg', 'Neo Quimica', 'Comprimido', 'SIMPLES'),
(5, 'Losartana Potassica 50mg', 'Anti-hipertensivo', 'ATIVO', 'MEDICAMENTO', 'Losartana Potassica', '50mg', 'Ache', 'Comprimido', 'SIMPLES');

-- 4. Lotes de Medicamento (com validades diferentes para testar a Matriz de Salvacao)
-- Lote Critico (vence em poucos dias)
INSERT INTO lotes_medicamento (id, numero_lote, quantidade, data_fabricacao, data_validade, status, medicamento_id, unidade_saude_id) VALUES
(1, 'AMX-2026-014', 120, '2025-05-10', '2026-11-12', 'PROXIMO_VENCIMENTO', 1, 1),
-- Lote em Atencao (vence em menos de 30 dias)
(2, 'DIP-2026-001', 250, '2025-06-01', '2026-11-28', 'PROXIMO_VENCIMENTO', 2, 2),
-- Lote Vencido (para encaminhar ao descarte)
(3, 'IBU-2025-032', 80, '2024-10-01', '2026-10-15', 'VENCIDO', 4, 4),
-- Lotes Disponiveis com validade distante
(4, 'PAR-2026-008', 300, '2026-01-15', '2027-08-15', 'DISPONIVEL', 3, 3),
(5, 'LOS-2026-021', 180, '2026-02-20', '2027-05-30', 'DISPONIVEL', 5, 1);

-- 5. Empresa de Descarte Ecologico
INSERT INTO empresas_descarte (id, razao_social, cnpj, telefone, endereco, licenca, ativo) VALUES
(1, 'EcoResiduos Hospitalares Ltda', '12.345.678/0001-90', '(44) 3222-9900', 'Rodovia PR-317, KM 5 - Maringa', 'IAP-LIC-2026-88', TRUE);

