# Base de dados analítica — Vinheria Agnello

Dataset simulado para **previsão de sucesso de vendas** (classificação binária), alinhado ao catálogo e ao modelo relacional do projeto (`tb_vinho`, `tb_usuario`, `tb_pedido`).

## Arquivos

| Arquivo | Conteúdo |
|---------|----------|
| `vendas_classificacao.csv` | **Principal** — 280 transações (linhas de venda) com features + rótulo |
| `vinhos_catalogo.csv` | Os 8 vinhos do `DataSeeder` (ids 1–8) |
| `usuarios_simulados.csv` | 30 clientes fictícios (espelham `tb_usuario` + região) |
| `vendas_base_completa.csv` | **Unificado** — vendas + colunas de usuário (`nome_cliente`, `email_cliente`) + catálogo (`uva`, `preco_catalogo`, `estoque_inicial`) |

## Definição do rótulo: `venda_sucesso`

| Valor | Significado |
|-------|-------------|
| **1** | Venda **concluída com sucesso**: pagamento aprovado, pedido confirmado/entregue, sem cancelamento nem devolução nos 30 dias (equivalente a `status = CONFIRMADO` no app). |
| **0** | Venda **não concluída**: pagamento recusado, boleto vencido, abandono no checkout, cancelamento pelo cliente ou estoque indisponível no momento da compra. |

Cada linha representa uma **tentativa de venda de um vinho** (granularidade item/transação), não o pedido completo multi-item — adequado para modelos de classificação por oportunidade comercial.

## Colunas de `vendas_classificacao.csv` (26 variáveis)

### Identificação e tempo
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id_transacao` | inteiro | Identificador único da transação simulada |
| `data_venda` | data (YYYY-MM-DD) | Data da tentativa de venda |
| `mes_venda` | inteiro (1–12) | Mês — sazonalidade (festas, verão, etc.) |
| `dia_semana` | categórica | Dia da semana da transação |

### Produto (ligado a `tb_vinho`)
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `vinho_id` | inteiro | FK lógica para o catálogo (1–8) |
| `nome_vinho` | texto | Nome do rótulo (ex.: Barolo Riserva) |
| `tipo_vinho` | categórica | Tinto Seco / Branco Seco |
| `pais_origem` | categórica | País do terroir |
| `regiao_vinho` | categórica | Região vitivinícola |
| `safra` | inteiro | Ano da safra |
| `idade_safra_anos` | inteiro | Maturação relativa (2026 − safra) |
| `preco_unitario` | decimal | Preço de catálogo (R$) |
| `nota_giulio` | inteiro (3–5) | Curadoria Giulio — proxy de qualidade percebida |

### Comercial
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `quantidade` | inteiro | Garrafas na linha |
| `valor_linha` | decimal | `preco_unitario × quantidade` |
| `canal_venda` | categórica | Site, Instagram, WhatsApp Giulio, Evento, Marketplace |
| `forma_pagamento` | categórica | PIX, Cartão Crédito/Débito, Boleto |
| `frete_gratis` | 0/1 | 1 se valor da linha ≥ R$ 500 (regra do `CheckoutServlet`) |

### Cliente (ligado a `tb_usuario` / `tb_endereco`)
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `usuario_id` | inteiro | FK lógica para `usuarios_simulados.csv` |
| `uf_cliente` | categórica | UF de entrega/faturamento |
| `cidade_cliente` | texto | Cidade do cliente |
| `segmento_cliente` | categórica | Novo, Recorrente, Premium, Corporativo |
| `dias_desde_cadastro` | inteiro | Antiguidade na base (engajamento) |
| `qtd_pedidos_anteriores` | inteiro | Histórico de compras confirmadas |
| `qtd_favoritos` | inteiro | Vinhos favoritados (proxy de interesse) |

### Rótulo (target)
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| **`venda_sucesso`** | **0 ou 1** | **Variável alvo** da classificação |

## Relação com o banco do projeto

```
tb_vinho (id 1–8)     ←── vinho_id, preço, região, nota_giulio
tb_usuario            ←── usuario_id, segmento, dias_desde_cadastro
tb_endereco (uf)      ←── uf_cliente, cidade_cliente
tb_pedido             ←── canal, pagamento, total, status → venda_sucesso
tb_pedido_item        ←── quantidade, preco_unitario por linha
```

## Uso sugerido (FIAP / ciência de dados)

1. Carregar `vendas_classificacao.csv` em Python (pandas), R ou Excel.
2. Tratar categóricas (`canal_venda`, `segmento_cliente`, etc.) com one-hot ou encoding ordinal.
3. Separar features de `venda_sucesso` para treino de classificador (Random Forest, Regressão Logística, etc.).
4. Para análise em um único arquivo, use `vendas_base_completa.csv` (join pronto dos três CSVs).

## Regenerar os CSVs

```bash
node scripts/gerar-dataset-vendas.js
```

Os dados são **fictícios**, mas coerentes com preços, regiões e regras de negócio da Vinheria Agnello implementadas no código.
