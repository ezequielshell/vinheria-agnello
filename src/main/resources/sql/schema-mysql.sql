CREATE TABLE tb_usuario (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    email       VARCHAR(160) NOT NULL UNIQUE,
    senha_hash  VARCHAR(255) NOT NULL,
    telefone    VARCHAR(20),
    cpf         VARCHAR(14),
    criado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE tb_endereco (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id   BIGINT NOT NULL,
    cep          VARCHAR(9) NOT NULL,
    logradouro   VARCHAR(160) NOT NULL,
    numero       VARCHAR(20) NOT NULL,
    complemento  VARCHAR(80),
    bairro       VARCHAR(80) NOT NULL,
    cidade       VARCHAR(80) NOT NULL,
    uf           CHAR(2) NOT NULL,
    principal    TINYINT(1) DEFAULT 0 NOT NULL,
    CONSTRAINT fk_end_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

CREATE TABLE tb_vinho (
    id                BIGINT PRIMARY KEY,
    nome              VARCHAR(160) NOT NULL,
    regiao            VARCHAR(120) NOT NULL,
    pais              VARCHAR(80) NOT NULL,
    safra             INT NOT NULL,
    uva               VARCHAR(80) NOT NULL,
    tipo              VARCHAR(60) NOT NULL,
    imagem_url        VARCHAR(500),
    preco             DECIMAL(10,2) NOT NULL,
    maturacao         VARCHAR(160),
    potencial_guarda  VARCHAR(80),
    acidez            VARCHAR(40),
    teor_alcoolico    VARCHAR(20),
    harmonizacao      VARCHAR(300),
    comentario_giulio TEXT,
    nota_giulio       TINYINT DEFAULT 0,
    estoque           INT DEFAULT 50 NOT NULL
);

CREATE TABLE tb_favorito (
    usuario_id BIGINT NOT NULL,
    vinho_id   BIGINT NOT NULL,
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_favorito PRIMARY KEY (usuario_id, vinho_id),
    CONSTRAINT fk_fav_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_vinho   FOREIGN KEY (vinho_id)   REFERENCES tb_vinho(id)   ON DELETE CASCADE
);

CREATE TABLE tb_carrinho_item (
    usuario_id    BIGINT NOT NULL,
    vinho_id      BIGINT NOT NULL,
    quantidade    INT NOT NULL,
    adicionado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_carrinho PRIMARY KEY (usuario_id, vinho_id),
    CONSTRAINT fk_carr_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_carr_vinho   FOREIGN KEY (vinho_id)   REFERENCES tb_vinho(id),
    CONSTRAINT ck_carr_qtd CHECK (quantidade > 0)
);

CREATE TABLE tb_pedido (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id       BIGINT NOT NULL,
    criado_em        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status           VARCHAR(20) DEFAULT 'PENDENTE' NOT NULL,
    total            DECIMAL(10,2) NOT NULL,
    forma_pagamento  VARCHAR(30) NOT NULL,
    endereco_entrega VARCHAR(400) NOT NULL,
    CONSTRAINT fk_ped_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id)
);

CREATE TABLE tb_pedido_item (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id      BIGINT NOT NULL,
    vinho_id       BIGINT NOT NULL,
    quantidade     INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_pi_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_vinho  FOREIGN KEY (vinho_id)  REFERENCES tb_vinho(id)
);
