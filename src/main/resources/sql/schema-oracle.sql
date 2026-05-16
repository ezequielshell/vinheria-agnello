CREATE TABLE tb_usuario (
    id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome        VARCHAR2(120) NOT NULL,
    email       VARCHAR2(160) NOT NULL UNIQUE,
    senha_hash  VARCHAR2(255) NOT NULL,
    telefone    VARCHAR2(20),
    cpf         VARCHAR2(14),
    criado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE tb_endereco (
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id   NUMBER NOT NULL,
    cep          VARCHAR2(9) NOT NULL,
    logradouro   VARCHAR2(160) NOT NULL,
    numero       VARCHAR2(20) NOT NULL,
    complemento  VARCHAR2(80),
    bairro       VARCHAR2(80) NOT NULL,
    cidade       VARCHAR2(80) NOT NULL,
    uf           CHAR(2) NOT NULL,
    principal    NUMBER(1) DEFAULT 0 NOT NULL,
    CONSTRAINT fk_end_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

CREATE TABLE tb_vinho (
    id                NUMBER PRIMARY KEY,
    nome              VARCHAR2(160) NOT NULL,
    regiao            VARCHAR2(120) NOT NULL,
    pais              VARCHAR2(80) NOT NULL,
    safra             NUMBER(4) NOT NULL,
    uva               VARCHAR2(80) NOT NULL,
    tipo              VARCHAR2(60) NOT NULL,
    imagem_url        VARCHAR2(500),
    preco             NUMBER(10,2) NOT NULL,
    maturacao         VARCHAR2(160),
    potencial_guarda  VARCHAR2(80),
    acidez            VARCHAR2(40),
    teor_alcoolico    VARCHAR2(20),
    harmonizacao      VARCHAR2(300),
    comentario_giulio CLOB,
    nota_giulio       NUMBER(1) DEFAULT 0,
    estoque           NUMBER DEFAULT 50 NOT NULL
);

CREATE TABLE tb_favorito (
    usuario_id NUMBER NOT NULL,
    vinho_id   NUMBER NOT NULL,
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_favorito PRIMARY KEY (usuario_id, vinho_id),
    CONSTRAINT fk_fav_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_vinho   FOREIGN KEY (vinho_id)   REFERENCES tb_vinho(id)   ON DELETE CASCADE
);

CREATE TABLE tb_carrinho_item (
    usuario_id    NUMBER NOT NULL,
    vinho_id      NUMBER NOT NULL,
    quantidade    NUMBER NOT NULL,
    adicionado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_carrinho PRIMARY KEY (usuario_id, vinho_id),
    CONSTRAINT fk_carr_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_carr_vinho   FOREIGN KEY (vinho_id)   REFERENCES tb_vinho(id),
    CONSTRAINT ck_carr_qtd CHECK (quantidade > 0)
);

CREATE TABLE tb_pedido (
    id               NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id       NUMBER NOT NULL,
    criado_em        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status           VARCHAR2(20) DEFAULT 'PENDENTE' NOT NULL,
    total            NUMBER(10,2) NOT NULL,
    forma_pagamento  VARCHAR2(30) NOT NULL,
    endereco_entrega VARCHAR2(400) NOT NULL,
    CONSTRAINT fk_ped_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id)
);

CREATE TABLE tb_pedido_item (
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pedido_id      NUMBER NOT NULL,
    vinho_id       NUMBER NOT NULL,
    quantidade     NUMBER NOT NULL,
    preco_unitario NUMBER(10,2) NOT NULL,
    CONSTRAINT fk_pi_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_vinho  FOREIGN KEY (vinho_id)  REFERENCES tb_vinho(id)
);
