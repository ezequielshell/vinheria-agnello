# Vinheria Agnello — Sprint 2

Aplicação web da Vinheria Agnello: experiência digital premium de curadoria, descoberta e compra de vinhos.

## Stack

- **Java 17** + Jakarta Servlets 6.0 + JSP 3.1 + JSTL 3.0
- **Apache Maven** (build) · **Apache Tomcat 10.1+** (runtime)
- **Oracle 19c+ / MySQL 8** via **JDBC** com pool **HikariCP**
- **BCrypt** (`at.favre.lib:bcrypt`) para senhas
- **AOS 2.3.4** (via CDN) para animações de scroll
- Tipografia Playfair Display + Lato

## Pré‑requisitos

| Ferramenta | Versão recomendada |
|---|---|
| JDK | 17 ou 21 |
| Maven | 3.9+ |
| Apache Tomcat | 10.1+ |
| Banco | Oracle 19c/21c/23c (XE) **ou** MySQL 8 |

---

## 1. Configurar o banco de dados

Edite **`src/main/resources/database.properties`** com as credenciais do seu banco. O arquivo já vem comentado para os dois cenários mais comuns.

### Opção A — Oracle local (XE/Free)

```properties
db.dialect=oracle
db.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
db.user=vinheria
db.password=vinheria
```

Antes de subir a aplicação, crie o usuário no Oracle (uma única vez):

```sql
-- conectado como SYS / SYSTEM
CREATE USER vinheria IDENTIFIED BY vinheria;
GRANT CONNECT, RESOURCE TO vinheria;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, UNLIMITED TABLESPACE TO vinheria;
```

### Opção B — Oracle da FIAP

```properties
db.dialect=oracle
db.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
db.user=RM00000
db.password=suaSenhaFiap
```

### Opção C — MySQL 8

```properties
db.dialect=mysql
db.url=jdbc:mysql://localhost:3306/vinheria?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
db.user=root
db.password=suaSenha
```

E crie o banco:

```sql
CREATE DATABASE vinheria CHARACTER SET utf8mb4;
```

### Como o schema é criado

Com `db.autoBootstrap=true` (padrão), o **`AppStartupListener`** detecta tabelas faltantes na primeira execução, roda automaticamente:

- `src/main/resources/sql/schema-oracle.sql` (ou `schema-mysql.sql`)
- E em seguida o `DataSeeder` popula 8 vinhos do catálogo curado.

Se preferir criar o schema manualmente, execute o `.sql` correspondente e deixe `db.autoBootstrap=false`.

---

## 2. Build e deploy

```powershell
mvn clean package
# Resultado: target/vinheria-agnello.war

Copy-Item target\vinheria-agnello.war $env:CATALINA_HOME\webapps\ -Force
& "$env:CATALINA_HOME\bin\catalina.bat" run
```

App disponível em **http://localhost:8080/vinheria-agnello/**.

---

## 3. O que tem na aplicação

| Rota | Função |
|---|---|
| `/home` | Hero animado + destaque do Giulio + primeiros vinhos |
| `/adega` | Catálogo completo com busca e filtro por tipo |
| `/detalhe?id=N` | Ficha técnica, harmonização, comentário, comprar |
| `/login` · `/cadastro` | Autenticação real com BCrypt |
| `/favoritos` | Lista persistida no DB |
| `/carrinho` | Adicionar/atualizar/remover itens, frete grátis acima de R$ 500 |
| `/checkout` | Endereço + forma de pagamento + revisão |
| `/pedido?id=N` | Detalhe do pedido confirmado |
| `/pedidos` | Histórico do usuário |
| `/perfil` | Dados pessoais + endereços + logout |

---

## 4. Arquitetura

```
src/main/java/com/vinheria/agnello/
  model/        Vinho, Usuario, Endereco, ItemCarrinho, Pedido, ItemPedido
  dao/          VinhoDAO, UsuarioDAO, FavoritoDAO, CarrinhoDAO,
                PedidoDAO, EnderecoDAO
  service/      AuthService (BCrypt + validações)
  controller/   Home, Adega, Detalhe, Login, Cadastro, Favorito,
                Carrinho, Checkout, Pedido, Perfil
  infra/        ConnectionFactory, SchemaBootstrap, DataSeeder,
                AppStartupListener, SessionContextFilter

src/main/resources/
  database.properties          ← credenciais do banco
  sql/schema-oracle.sql        ← DDL Oracle
  sql/schema-mysql.sql         ← DDL MySQL

src/main/webapp/
  *.jsp                        ← páginas principais
  WEB-INF/jspf/*.jspf          ← fragments reutilizáveis (head/header/footer/scripts)
  WEB-INF/web.xml
  css/style.css                ← design system 2.0
  js/app.js                    ← AOS init, qty stepper, toasts
```

### Modelo de dados

`tb_usuario` → `tb_endereco` (1:N)
`tb_vinho` ← `tb_favorito` → `tb_usuario`
`tb_vinho` ← `tb_carrinho_item` → `tb_usuario`
`tb_usuario` → `tb_pedido` → `tb_pedido_item` → `tb_vinho`

---

## 5. UI / UX

- **Design system**: paleta bordô / dourado / creme; tipografia Playfair (títulos) + Lato (corpo); gradientes radiais, shadows em camadas, glassmorphism no header/footer.
- **Animações de scroll**: [AOS 2.3.4](https://michalsnik.github.io/aos/) via CDN — fade-up/right/down, zoom-in nos cards técnicos, com `prefers-reduced-motion` respeitado.
- **Animação contínua**: a garrafa do detalhe flutua suavemente (`@keyframes floatBottle`).
- **Microinterações**: hover lift nos cards, faixa lateral que aparece nos itens da lista, toasts para feedback de ação, badge de carrinho com `pop`.
- **Responsivo**: mobile-first até 460 px com fallback elegante em desktop (cantos arredondados, padding).

---

## 6. Próximos passos sugeridos (não incluídos nesta entrega)

- Recuperação de senha por e-mail
- Recomendação personalizada baseada em favoritos
- Mapa de terroir interativo no detalhe
- Integração com gateway de pagamento real
- Migração da camada de carrinho para `tb_carrinho_item` + cache Redis
