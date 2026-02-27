// =================================================================
// CONFIGURAÇÕES GLOBAIS
// =================================================================
const BASE_URL = 'https://sistemadeordemdeservico-1.onrender.com';
const API_URL = `${BASE_URL}/os`;

// =================================================================
// 1. GUARDA DE SEGURANÇA
// =================================================================
(function securityCheck() {
    const currentPath = window.location.pathname;
    const isPublicPage = currentPath.includes('login.html') || currentPath.includes('register.html');
    const token = localStorage.getItem('authToken');

    // Verifica se o token existe E ainda é válido (não expirado)
    function isTokenValid(tkn) {
        if (!tkn) return false;
        try {
            const base64Url = tkn.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const payload = JSON.parse(atob(base64));
            // payload.exp é em segundos, Date.now() em milissegundos
            return payload.exp * 1000 > Date.now();
        } catch {
            return false;
        }
    }

    const tokenValid = isTokenValid(token);

    // Token inválido/expirado? Limpa e garante que está na página pública
    if (!tokenValid && token) {
        localStorage.removeItem('authToken');
    }

    if (!isPublicPage && !tokenValid) window.location.href = 'login.html';
    if (isPublicPage && tokenValid) window.location.href = 'index.html';
})();

// =================================================================
// 2. UTILITÁRIOS
// =================================================================

function getToken() {
    return localStorage.getItem('authToken');
}

function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
    };
}

async function extractErrorMessage(response) {
    try {
        const text = await response.text();
        try {
            const json = JSON.parse(text);
            return json.message || json.error || `Erro ${response.status}`;
        } catch {
            return text || `Erro ${response.status}`;
        }
    } catch {
        return 'Erro de comunicação com o servidor.';
    }
}

function getPayloadFromToken() {
    const token = getToken();
    if (!token) return { username: 'Visitante', name: '', role: null };

    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(decodeURIComponent(
            atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
        ));

        let name = payload.nome || payload.sub?.split('@')[0] || 'Usuário';
        name = name.charAt(0).toUpperCase() + name.slice(1);

        const role = payload.role || payload.authorities?.[0]?.authority || 'ROLE_CLIENTE';
        return { username: payload.sub, name, role };
    } catch {
        return { username: 'Erro', name: 'Erro', role: null };
    }
}

// =================================================================
// 3. TOAST (substitui alert)
// =================================================================

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;

    toast.textContent = message;
    toast.className = `toast toast-${type} show`;

    setTimeout(() => toast.classList.remove('show'), 3000);
}

// =================================================================
// 4. INICIALIZAÇÃO
// =================================================================

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);

    const registerForm = document.getElementById('registerForm');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    if (document.getElementById('osList')) {
        initDashboard();
    }
});

function initDashboard() {
    fetchOrders();

    document.getElementById('logoutBtn')?.addEventListener('click', handleLogout);
    document.getElementById('open-modal-btn')?.addEventListener('click', () => openModal());
    document.querySelector('.close-btn')?.addEventListener('click', closeModal);
    document.getElementById('create-form')?.addEventListener('submit', handleFormSubmit);

    window.addEventListener('click', e => {
        if (e.target === document.getElementById('os-modal')) closeModal();
    });
}

// =================================================================
// 5. AUTENTICAÇÃO
// =================================================================

async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const messageArea = document.getElementById('message');

    if (messageArea) {
        messageArea.textContent = 'Autenticando...';
        messageArea.style.color = 'var(--text-secondary)';
    }

    try {
        const response = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        const responseText = await response.text();

        if (response.ok) {
            const data = JSON.parse(responseText);
            localStorage.setItem('authToken', data.token);
            window.location.href = 'index.html';
        } else {
            const errorMsg = await extractErrorMessage({ text: () => Promise.resolve(responseText) });
            if (messageArea) {
                messageArea.textContent = 'Erro: ' + errorMsg;
                messageArea.style.color = 'var(--danger)';
            }
        }
    } catch {
        if (messageArea) messageArea.textContent = 'Erro de conexão.';
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const nome = document.getElementById('registerNome').value;
    const email = document.getElementById('registerEmail').value;
    const senha = document.getElementById('registerSenha').value;
    const messageArea = document.getElementById('registerMessage');

    try {
        const response = await fetch(`${BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha, nome })
        });

        if (response.ok || response.status === 201) {
            if (messageArea) {
                messageArea.textContent = '✅ Cadastro realizado! Redirecionando...';
                messageArea.style.color = 'var(--status-open)';
            }
            setTimeout(() => window.location.href = 'login.html', 2000);
        } else {
            const errorText = await response.text();
            if (messageArea) {
                messageArea.textContent = 'Erro: ' + errorText;
                messageArea.style.color = 'var(--danger)';
            }
        }
    } catch {
        if (messageArea) {
            messageArea.textContent = 'Erro de conexão.';
            messageArea.style.color = 'var(--danger)';
        }
    }
}

function handleLogout() {
    localStorage.removeItem('authToken');
    window.location.href = 'login.html';
}

// =================================================================
// 6. LISTAGEM
// =================================================================

const STATUS_LABELS = {
    ABERTO: { label: 'Aberto', css: 'badge-open' },
    EM_EXECUCAO: { label: 'Em Execução', css: 'badge-exec' },
    CONCLUIDO: { label: 'Concluído', css: 'badge-done' },
};

const PRIORIDADE_LABELS = {
    BAIXA: { label: 'Baixa', css: 'pri-low' },
    MEDIA: { label: 'Média', css: 'pri-mid' },
    ALTA: { label: 'Alta', css: 'pri-high' },
};

const CATEGORIA_LABELS = {
    RECLAMACAO: 'Reclamação',
    MANUTENCAO: 'Manutenção',
    DUVIDA: 'Dúvida',
};

async function fetchOrders() {
    const { name, role } = getPayloadFromToken();
    const listBody = document.getElementById('ordens-list');
    const userNameDisplay = document.getElementById('userNameDisplay');

    if (userNameDisplay) userNameDisplay.innerHTML = `Olá, <strong>${name}</strong>!`;
    if (listBody) listBody.innerHTML = `<tr><td colspan="5" class="loading-cell">Carregando...</td></tr>`;

    try {
        const response = await fetch(API_URL, {
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('authToken');
                window.location.href = 'login.html';
                return;
            }
            throw new Error(await extractErrorMessage(response));
        }

        const ordens = await response.json();

        if (!listBody) return;
        listBody.innerHTML = '';

        if (!Array.isArray(ordens) || ordens.length === 0) {
            listBody.innerHTML = `<tr><td colspan="5" class="empty-cell">Nenhuma OS encontrada.</td></tr>`;
            return;
        }

        const isExecutor = role?.includes('EXECUTOR');

        ordens.forEach(ordem => {
            const status = STATUS_LABELS[ordem.status] || { label: ordem.status, css: '' };
            const prioridade = PRIORIDADE_LABELS[ordem.prioridade] || { label: ordem.prioridade, css: '' };
            const categoria = CATEGORIA_LABELS[ordem.categoria] || ordem.categoria;

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${ordem.titulo}</td>
                <td><span class="badge ${status.css}">${status.label}</span></td>
                <td><span class="badge ${prioridade.css}">${prioridade.label}</span></td>
                <td>${categoria}</td>
                <td class="actions-cell">
                    ${isExecutor ? `<button class="action-btn advance-btn" title="Avançar Status" onclick="handleAdvance(${ordem.id})">▶</button>` : ''}
                    <button class="action-btn edit-btn" title="Editar" onclick='openModal(${JSON.stringify(ordem)})'>✏️</button>
                    <button class="action-btn delete-btn" title="Deletar" onclick="handleDelete(${ordem.id})">🗑️</button>
                </td>
            `;
            listBody.appendChild(row);
        });

    } catch (error) {
        if (listBody) listBody.innerHTML = `<tr><td colspan="5" class="error">Erro ao carregar dados.</td></tr>`;
        console.error(error);
    }
}

// =================================================================
// 7. MODAL
// =================================================================

function openModal(osData = null) {
    const modal = document.getElementById('os-modal');
    const form = document.getElementById('create-form');
    const modalTitle = document.getElementById('modal-title');
    const submitBtn = document.getElementById('submit-btn');
    const statusGroup = document.getElementById('status-group');
    const formMessage = document.getElementById('form-message');

    formMessage.style.display = 'none';
    form.reset();

    if (osData) {
        document.getElementById('osId').value = osData.id;
        document.getElementById('titulo').value = osData.titulo;
        document.getElementById('prioridade').value = osData.prioridade;
        document.getElementById('categoria').value = osData.categoria;
        document.getElementById('status').value = osData.status;
        document.getElementById('descricao').value = osData.descricao || '';
        modalTitle.textContent = 'Editar OS';
        submitBtn.textContent = 'Salvar Alterações';
        statusGroup.style.display = 'block';
    } else {
        document.getElementById('osId').value = '';
        modalTitle.textContent = 'Nova Ordem de Serviço';
        submitBtn.textContent = 'Criar OS';
        statusGroup.style.display = 'none';
    }

    modal.style.display = 'block';
}

function closeModal() {
    document.getElementById('os-modal').style.display = 'none';
    document.getElementById('create-form').reset();
}

// =================================================================
// 8. CRIAR / EDITAR
// =================================================================

async function handleFormSubmit(event) {
    event.preventDefault();
    const formMessage = document.getElementById('form-message');
    const submitBtn = document.getElementById('submit-btn');
    const osId = document.getElementById('osId').value;
    const isEdit = !!osId;

    const orderData = {
        titulo: document.getElementById('titulo').value,
        prioridade: document.getElementById('prioridade').value,
        categoria: document.getElementById('categoria').value,
        status: document.getElementById('status').value,
        descricao: document.getElementById('descricao').value
    };

    const url = isEdit ? `${API_URL}/${osId}` : API_URL;
    const method = isEdit ? 'PUT' : 'POST';

    submitBtn.disabled = true;
    submitBtn.textContent = 'Enviando...';
    formMessage.style.display = 'none';

    try {
        const response = await fetch(url, {
            method,
            headers: getAuthHeaders(),
            body: JSON.stringify(orderData)
        });

        if (!response.ok) throw new Error(await extractErrorMessage(response));

        showToast(isEdit ? '✅ OS atualizada!' : '✅ OS criada com sucesso!');
        setTimeout(() => { closeModal(); fetchOrders(); }, 1000);

    } catch (error) {
        formMessage.style.display = 'block';
        formMessage.className = 'message error';
        formMessage.textContent = error.message;
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = isEdit ? 'Salvar Alterações' : 'Criar OS';
    }
}

// =================================================================
// 9. AÇÕES
// =================================================================

async function handleAdvance(osId) {
    try {
        const response = await fetch(`${API_URL}/${osId}/avancar-status`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });

        if (!response.ok) throw new Error(await extractErrorMessage(response));

        showToast('✅ Status avançado!');
        fetchOrders();
    } catch (error) {
        showToast('❌ ' + error.message, 'error');
    }
}

async function handleDelete(osId) {
    if (!confirm(`Tem certeza que deseja apagar a OS #${osId}?`)) return;

    try {
        const response = await fetch(`${API_URL}/${osId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });

        if (response.status === 204 || response.ok) {
            showToast('🗑️ OS removida.');
            fetchOrders();
        } else {
            throw new Error(await extractErrorMessage(response));
        }
    } catch (error) {
        showToast('❌ ' + error.message, 'error');
    }
}
