const fs = require("fs");
const path = require("path");

const OUT = path.join(__dirname, "..", "data");
fs.mkdirSync(OUT, { recursive: true });

const VINHOS = [
  [1, "Brunello di Montalcino", "Toscana", "Italia", "Sangiovese", "Tinto Seco", 2016, 890, 5, 24],
  [2, "Barolo Riserva", "Piemonte", "Italia", "Nebbiolo", "Tinto Seco", 2015, 1250, 5, 12],
  [3, "Chablis Premier Cru", "Borgonha", "Franca", "Chardonnay", "Branco Seco", 2019, 420, 4, 30],
  [4, "Malbec Reserva", "Mendoza", "Argentina", "Malbec", "Tinto Seco", 2020, 180, 4, 60],
  [5, "Douro Reserva", "Douro", "Portugal", "Touriga Nacional", "Tinto Seco", 2018, 310, 4, 40],
  [6, "Rioja Gran Reserva", "Rioja", "Espanha", "Tempranillo", "Tinto Seco", 2014, 560, 5, 18],
  [7, "Sauvignon Blanc", "Marlborough", "Nova Zelandia", "Sauvignon Blanc", "Branco Seco", 2022, 150, 3, 80],
  [8, "Amarone della Valpolicella", "Veneto", "Italia", "Corvina", "Tinto Seco", 2017, 780, 5, 15],
];

const UFS = {
  SP: ["Sao Paulo", "Campinas", "Santos"],
  RJ: ["Rio de Janeiro", "Niteroi"],
  MG: ["Belo Horizonte", "Uberlandia"],
  RS: ["Porto Alegre", "Gramado"],
  PR: ["Curitiba"],
  SC: ["Florianopolis"],
  DF: ["Brasilia"],
  BA: ["Salvador"],
  PE: ["Recife"],
};

const CANAIS = ["Site Vinheria", "Instagram", "WhatsApp Giulio", "Evento Degustacao", "Marketplace Parceiro"];
const PAG = ["PIX", "Cartao Credito", "Cartao Debito", "Boleto"];
const SEG = ["Novo", "Recorrente", "Premium", "Corporativo"];
const DIAS = ["Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado", "Domingo"];

const nomes = [
  ["Ana Costa", "ana.costa@email.com"],
  ["Bruno Silva", "bruno.silva@email.com"],
  ["Carla Mendes", "carla.mendes@email.com"],
  ["Diego Almeida", "diego.almeida@email.com"],
  ["Elena Ribeiro", "elena.ribeiro@email.com"],
  ["Felipe Nunes", "felipe.nunes@email.com"],
  ["Gabriela Souza", "gabriela.souza@email.com"],
  ["Henrique Lima", "henrique.lima@email.com"],
  ["Isabela Ferreira", "isabela.ferreira@email.com"],
  ["Joao Martins", "joao.martins@email.com"],
  ["Karina Duarte", "karina.duarte@email.com"],
  ["Lucas Prado", "lucas.prado@email.com"],
  ["Mariana Teixeira", "mariana.teixeira@email.com"],
  ["Nicolas Barbosa", "nicolas.barbosa@email.com"],
  ["Olivia Campos", "olivia.campos@email.com"],
  ["Paulo Rocha", "paulo.rocha@email.com"],
  ["Quiteria Dias", "quiteria.dias@email.com"],
  ["Rafael Gomes", "rafael.gomes@email.com"],
  ["Sofia Carvalho", "sofia.carvalho@email.com"],
  ["Thiago Pires", "thiago.pires@email.com"],
  ["Ursula Lopes", "ursula.lopes@email.com"],
  ["Victor Araujo", "victor.araujo@email.com"],
  ["Wanda Freitas", "wanda.freitas@email.com"],
  ["Xavier Moura", "xavier.moura@email.com"],
  ["Yasmin Veiga", "yasmin.veiga@email.com"],
  ["Zeca Antunes", "zeca.antunes@email.com"],
  ["Amanda Rios", "amanda.rios@corp.com.br"],
  ["Carlos Export", "carlos.export@hotel.com.br"],
  ["Diana Sommelier", "diana.sommelier@wineclub.com"],
  ["Eduardo Restaurante", "eduardo.chef@gastronomia.br"],
];

let seed = 42;
function srnd() {
  seed = (seed * 16807) % 2147483647;
  return (seed - 1) / 2147483646;
}

function schoice(arr, w) {
  if (!w) return arr[Math.floor(srnd() * arr.length)];
  const s = w.reduce((a, b) => a + b, 0);
  let r = srnd() * s;
  for (let i = 0; i < arr.length; i++) {
    r -= w[i];
    if (r <= 0) return arr[i];
  }
  return arr[arr.length - 1];
}

function esc(s) {
  const t = String(s);
  return t.includes(",") || t.includes('"') ? `"${t.replace(/"/g, '""')}"` : t;
}

function writeCsv(file, headers, rows) {
  const lines = [headers.join(",")];
  for (const r of rows) {
    lines.push(headers.map((h) => esc(r[h])).join(","));
  }
  fs.writeFileSync(path.join(OUT, file), "\ufeff" + lines.join("\n"), "utf8");
}

const ufKeys = Object.keys(UFS);
const usuarios = nomes.map((n, i) => {
  const uf = ufKeys[i % ufKeys.length];
  const seg = schoice(SEG, [25, 40, 20, 15]);
  return {
    usuario_id: i + 1,
    nome: n[0],
    email: n[1],
    uf,
    cidade: UFS[uf][i % UFS[uf].length],
    segmento_cliente: seg,
    dias_desde_cadastro: 5 + Math.floor(srnd() * 900),
    qtd_pedidos_anteriores: seg === "Novo" ? 0 : 1 + Math.floor(srnd() * 24),
    qtd_favoritos: Math.floor(srnd() * 6),
  };
});

writeCsv(
  "vinhos_catalogo.csv",
  ["vinho_id", "nome_vinho", "regiao_vinho", "pais_origem", "uva", "tipo_vinho", "safra", "preco_catalogo", "nota_giulio", "estoque_inicial"],
  VINHOS.map((v) => ({
    vinho_id: v[0],
    nome_vinho: v[1],
    regiao_vinho: v[2],
    pais_origem: v[3],
    uva: v[4],
    tipo_vinho: v[5],
    safra: v[6],
    preco_catalogo: v[7].toFixed(2),
    nota_giulio: v[8],
    estoque_inicial: v[9],
  }))
);

writeCsv(
  "usuarios_simulados.csv",
  ["usuario_id", "nome", "email", "uf", "cidade", "segmento_cliente", "dias_desde_cadastro", "qtd_pedidos_anteriores", "qtd_favoritos"],
  usuarios
);

const cols = [
  "id_transacao",
  "data_venda",
  "usuario_id",
  "vinho_id",
  "nome_vinho",
  "tipo_vinho",
  "pais_origem",
  "regiao_vinho",
  "safra",
  "preco_unitario",
  "quantidade",
  "valor_linha",
  "canal_venda",
  "uf_cliente",
  "cidade_cliente",
  "segmento_cliente",
  "forma_pagamento",
  "nota_giulio",
  "dias_desde_cadastro",
  "qtd_pedidos_anteriores",
  "qtd_favoritos",
  "frete_gratis",
  "mes_venda",
  "dia_semana",
  "idade_safra_anos",
  "venda_sucesso",
];

const vendas = [];
const base = new Date(2024, 0, 15);

for (let tid = 1; tid <= 280; tid++) {
  const u = usuarios[Math.floor(srnd() * usuarios.length)];
  const v = VINHOS[Math.floor(srnd() * VINHOS.length)];
  const qtd = schoice([1, 2, 3, 4, 6], [50, 25, 15, 7, 3]);
  const valor = +(v[7] * qtd).toFixed(2);
  const pag = schoice(PAG, [35, 30, 20, 15]);
  const canal = schoice(CANAIS);
  const frete = valor >= 500 ? 1 : 0;
  const dt = new Date(base.getTime() + Math.floor(srnd() * 500) * 86400000);

  let p = 0.55;
  if (["Recorrente", "Premium"].includes(u.segmento_cliente)) p += 0.12;
  if (u.segmento_cliente === "Novo") p -= 0.08;
  if (pag === "Boleto") p -= 0.18;
  if (pag === "PIX") p += 0.1;
  if (canal === "Site Vinheria") p += 0.08;
  if (v[8] >= 5) p += 0.06;
  if (v[8] <= 3) p -= 0.04;
  if (frete) p += 0.07;
  if (valor > 1000) p -= 0.1;
  if (valor < 300) p += 0.05;
  if (u.qtd_pedidos_anteriores >= 3) p += 0.1;
  if (u.qtd_favoritos >= 2) p += 0.05;
  if (dt.getDay() === 0 || dt.getDay() === 6) p += 0.03;
  p = Math.max(0.08, Math.min(0.92, p));

  const dow = dt.getDay();
  const diaIdx = dow === 0 ? 6 : dow - 1;

  vendas.push({
    id_transacao: tid,
    data_venda: dt.toISOString().slice(0, 10),
    usuario_id: u.usuario_id,
    vinho_id: v[0],
    nome_vinho: v[1],
    tipo_vinho: v[5],
    pais_origem: v[3],
    regiao_vinho: v[2],
    safra: v[6],
    preco_unitario: v[7].toFixed(2),
    quantidade: qtd,
    valor_linha: valor.toFixed(2),
    canal_venda: canal,
    uf_cliente: u.uf,
    cidade_cliente: u.cidade,
    segmento_cliente: u.segmento_cliente,
    forma_pagamento: pag,
    nota_giulio: v[8],
    dias_desde_cadastro: u.dias_desde_cadastro,
    qtd_pedidos_anteriores: u.qtd_pedidos_anteriores,
    qtd_favoritos: u.qtd_favoritos,
    frete_gratis: frete,
    mes_venda: dt.getMonth() + 1,
    dia_semana: DIAS[diaIdx],
    idade_safra_anos: 2026 - v[6],
    venda_sucesso: srnd() < p ? 1 : 0,
  });
}

writeCsv("vendas_classificacao.csv", cols, vendas);

const usuarioById = Object.fromEntries(usuarios.map((u) => [u.usuario_id, u]));
const vinhoById = Object.fromEntries(
  VINHOS.map((v) => [
    v[0],
    {
      uva: v[4],
      preco_catalogo: v[7].toFixed(2),
      estoque_inicial: v[9],
    },
  ])
);

const colsCompleto = [
  ...cols,
  "nome_cliente",
  "email_cliente",
  "uva",
  "preco_catalogo",
  "estoque_inicial",
];

const vendasCompleto = vendas.map((row) => {
  const u = usuarioById[row.usuario_id];
  const v = vinhoById[row.vinho_id];
  return {
    ...row,
    nome_cliente: u.nome,
    email_cliente: u.email,
    uva: v.uva,
    preco_catalogo: v.preco_catalogo,
    estoque_inicial: v.estoque_inicial,
  };
});

writeCsv("vendas_base_completa.csv", colsCompleto, vendasCompleto);

const ok = vendas.filter((r) => r.venda_sucesso === 1).length;
console.log(`Gerado em ${OUT}`);
console.log(`vendas: ${vendas.length}, sucesso: ${ok} (${((100 * ok) / vendas.length).toFixed(1)}%)`);
console.log(`vendas_base_completa.csv: ${vendasCompleto.length} linhas (vendas + usuarios + vinhos)`);
