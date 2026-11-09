CREATE DATABASE IF NOT EXISTS sistema_saude;
USE sistema_saude;

-- 1. Unidades de Saude (UBS)
CREATE TABLE IF NOT EXISTS unidades_saude (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cnes VARCHAR(20) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(200),
    telefone VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Usuarios do Sistema (Farmaceuticas, Enfermeiras)
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_usuario VARCHAR(20) NOT NULL, -- 'FARMACEUTICA', 'ENFERMEIRA'
    crf VARCHAR(20),
    coren VARCHAR(20),
    unidade_saude_id INT,
    CONSTRAINT fk_usuarios_unidade FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id)
);

-- 3. Insumos Medicos / Medicamentos
CREATE TABLE IF NOT EXISTS insumos_medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    tipo_insumo VARCHAR(20) NOT NULL DEFAULT 'MEDICAMENTO',
    principio_ativo VARCHAR(150),
    dosagem VARCHAR(50),
    fabricante VARCHAR(100),
    forma_farmaceutica VARCHAR(50),
    tipo_receita VARCHAR(20)
);

-- 4. Lotes de Medicamento
CREATE TABLE IF NOT EXISTS lotes_medicamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_lote VARCHAR(50) NOT NULL,
    quantidade INT NOT NULL,
    data_fabricacao DATE,
    data_validade DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'DISPONIVEL', 'PROXIMO_VENCIMENTO', 'VENCIDO'
    medicamento_id INT NOT NULL,
    unidade_saude_id INT NOT NULL,
    CONSTRAINT fk_lotes_medicamento FOREIGN KEY (medicamento_id) REFERENCES insumos_medicos(id),
    CONSTRAINT fk_lotes_unidade FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id)
);

-- 5. Solicitacoes de Transferencia entre UBSs
CREATE TABLE IF NOT EXISTS solicitacoes_transferencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantidade INT NOT NULL,
    data_solicitacao DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'PENDENTE', 'APROVADA', 'REJEITADA', 'CANCELADA'
    observacao VARCHAR(255),
    unidade_origem_id INT NOT NULL,
    unidade_destino_id INT NOT NULL,
    lote_medicamento_id INT NOT NULL,
    CONSTRAINT fk_transf_origem FOREIGN KEY (unidade_origem_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_transf_destino FOREIGN KEY (unidade_destino_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_transf_lote FOREIGN KEY (lote_medicamento_id) REFERENCES lotes_medicamento(id)
);

-- 6. Empresas de Descarte Ecologico
CREATE TABLE IF NOT EXISTS empresas_descarte (
    id INT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    endereco VARCHAR(200),
    licenca VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 7. Descartes de Lotes Vencidos ou Danificados
CREATE TABLE IF NOT EXISTS descartes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_solicitacao DATETIME NOT NULL,
    data_coleta DATETIME,
    motivo VARCHAR(200),
    status VARCHAR(20) NOT NULL, -- 'SOLICITADO', 'COLETADO', 'CONCLUIDO', 'CANCELADO'
    observacao VARCHAR(255),
    unidade_saude_id INT NOT NULL,
    lote_medicamento_id INT NOT NULL,
    empresa_descarte_id INT NOT NULL,
    CONSTRAINT fk_descartes_unidade FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_descartes_lote FOREIGN KEY (lote_medicamento_id) REFERENCES lotes_medicamento(id),
    CONSTRAINT fk_descartes_empresa FOREIGN KEY (empresa_descarte_id) REFERENCES empresas_descarte(id)
);

