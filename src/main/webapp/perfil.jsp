<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Meu perfil — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>Olá, ${sessionScope.usuario.primeiroNome}</h2>
        <p>Gerencie seus dados, endereços e veja seu histórico.</p>
    </div>

    <div class="cart-summary" data-aos="fade-up" style="margin:0 18px 16px;">
        <h4>Dados pessoais</h4>
        <form action="${pageContext.request.contextPath}/perfil" method="post" class="form">
            <input type="hidden" name="acao" value="atualizarPerfil">
            <div class="form-group">
                <label for="p-nome">Nome</label>
                <input type="text" id="p-nome" name="nome" required value="${sessionScope.usuario.nome}">
            </div>
            <div class="form-group">
                <label>E-mail</label>
                <input type="email" value="${sessionScope.usuario.email}" disabled style="opacity:0.6; cursor:not-allowed;">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label for="p-tel">Telefone</label>
                    <input type="tel" id="p-tel" name="telefone" value="${sessionScope.usuario.telefone}">
                </div>
                <div class="form-group">
                    <label for="p-cpf">CPF</label>
                    <input type="text" id="p-cpf" name="cpf" maxlength="14" value="${sessionScope.usuario.cpf}">
                </div>
            </div>
            <button type="submit" class="btn-wine">Salvar alterações</button>
        </form>
    </div>

    <div class="cart-summary" data-aos="fade-up" style="margin:0 18px 16px;">
        <h4>Endereços</h4>

        <c:forEach var="e" items="${enderecos}">
            <div style="padding:10px 0; border-bottom: 1px dashed var(--border); display:flex; gap:10px; justify-content:space-between; align-items:flex-start;">
                <div style="flex:1; font-size:13px;">
                    <strong>${e.logradouro}, ${e.numero}</strong>
                    <c:if test="${not empty e.complemento}"> - ${e.complemento}</c:if>
                    <c:if test="${e.principal}"><span class="badge gold" style="margin-left:8px;">Principal</span></c:if>
                    <div style="font-size:12px; color:var(--ink-500);">${e.bairro}, ${e.cidade}/${e.uf} &bull; CEP ${e.cep}</div>
                </div>
                <form action="${pageContext.request.contextPath}/perfil" method="post" style="margin:0;">
                    <input type="hidden" name="acao" value="removerEndereco">
                    <input type="hidden" name="enderecoId" value="${e.id}">
                    <button type="submit" class="cart-remove" aria-label="Remover">&times;</button>
                </form>
            </div>
        </c:forEach>

        <details style="margin-top:14px;">
            <summary style="cursor:pointer; color:var(--wine-700); font-weight:700; font-size:13px; text-transform:uppercase; letter-spacing:1px;">+ Novo endereço</summary>
            <form action="${pageContext.request.contextPath}/perfil" method="post" class="form" style="margin-top:12px;">
                <input type="hidden" name="acao" value="novoEndereco">
                <div class="form-row">
                    <div class="form-group">
                        <label for="ne-cep">CEP</label>
                        <input type="text" id="ne-cep" name="cep" required maxlength="9">
                    </div>
                    <div class="form-group">
                        <label for="ne-uf">UF</label>
                        <input type="text" id="ne-uf" name="uf" required maxlength="2">
                    </div>
                </div>
                <div class="form-group">
                    <label for="ne-log">Logradouro</label>
                    <input type="text" id="ne-log" name="logradouro" required>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="ne-num">Número</label>
                        <input type="text" id="ne-num" name="numero" required>
                    </div>
                    <div class="form-group">
                        <label for="ne-comp">Complemento</label>
                        <input type="text" id="ne-comp" name="complemento">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="ne-bairro">Bairro</label>
                        <input type="text" id="ne-bairro" name="bairro" required>
                    </div>
                    <div class="form-group">
                        <label for="ne-cidade">Cidade</label>
                        <input type="text" id="ne-cidade" name="cidade" required>
                    </div>
                </div>
                <label style="display:flex; align-items:center; gap:8px; font-size:13px;">
                    <input type="checkbox" name="principal"> Definir como principal
                </label>
                <button type="submit" class="btn-wine">Adicionar endereço</button>
            </form>
        </details>
    </div>

    <div style="padding: 0 18px 12px;">
        <a href="${pageContext.request.contextPath}/pedidos" class="btn-wine-outline btn-block">Meus pedidos</a>
        <a href="${pageContext.request.contextPath}/login?acao=logout" class="btn-ghost btn-block" style="margin-top:10px;">Sair</a>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
