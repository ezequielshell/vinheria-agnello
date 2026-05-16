<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Criar conta — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="auth-container" data-aos="fade-up">
        <div class="logo-box"><img src="https://img.icons8.com/?size=100&id=wB7OSDpKBQsG&format=png&color=FFFFFF" alt=""></div>
        <h2>Sua adega começa aqui</h2>
        <p>Crie uma conta para favoritar, comprar e receber recomendações do Giulio.</p>

        <c:if test="${not empty erro}">
            <div class="error-msg" data-aos="fade-down">${erro}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/cadastro" method="post" class="form" style="margin-top:18px;">

            <div class="form-group">
                <label for="nome">Nome completo</label>
                <input type="text" id="nome" name="nome" required minlength="2" autocomplete="name"
                       value="${nomePrefill != null ? nomePrefill : ''}">
            </div>

            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email" required autocomplete="email"
                       value="${emailPrefill != null ? emailPrefill : ''}">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="telefone">Telefone</label>
                    <input type="tel" id="telefone" name="telefone" autocomplete="tel"
                           placeholder="(11) 98765-4321"
                           value="${telefonePrefill != null ? telefonePrefill : ''}">
                </div>
                <div class="form-group">
                    <label for="cpf">CPF</label>
                    <input type="text" id="cpf" name="cpf" maxlength="14" placeholder="000.000.000-00"
                           value="${cpfPrefill != null ? cpfPrefill : ''}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="senha">Senha</label>
                    <input type="password" id="senha" name="senha" required minlength="6" autocomplete="new-password">
                </div>
                <div class="form-group">
                    <label for="confirmarSenha">Confirmar senha</label>
                    <input type="password" id="confirmarSenha" name="confirmarSenha" required minlength="6" autocomplete="new-password">
                </div>
            </div>

            <p class="form-helper">Use ao menos 6 caracteres. A senha é armazenada com hash BCrypt.</p>

            <button type="submit" class="btn-wine btn-block" style="margin-top:6px;">Criar conta</button>
        </form>

        <div class="auth-divider">já tem conta?</div>
        <a href="${pageContext.request.contextPath}/login" class="btn-wine-outline btn-block">Entrar</a>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
