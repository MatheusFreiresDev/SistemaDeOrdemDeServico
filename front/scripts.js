// =================================================================
// CONFIG
// =================================================================
const BASE_URL = 'https://sistemadeordemdeservico-1.onrender.com';
const API_URL  = `${BASE_URL}/os`;

// =================================================================
// SECURITY GUARD
// =================================================================
(function securityCheck() {
    const path = window.location.pathname;
    const isPublic = path.includes('login.html') || path.includes('register.html');
    const token = localStorage.getItem('authToken');
    if (!isPublic && !token) window.location.href = 'login.html';
    if (isPublic && token) window.location.href = 'index.html';
})();

// =================================================================
// UTILS
// =================================================================

const getToken = () => localStorage.getItem('authToken');

const authHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
});

async function extractError(response) {
    try {
        const text = await response.text();
        try { const j = JSON.parse(text); return j.message || j.error || `Erro ${response.status}`; }
        catch { return text || `Erro ${response.status}`; }
    } catch { return 'Erro de comunicação.'; }
}

function getPayload() {
    const token = getToken();
    if (!token) return { name: 'Visitante', role: null, initial: '?' };
    try {
        const b64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        const p = JSON.parse(decodeURIComponent(atob(b64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')));
        let name = p.nome || p.sub?.split('@')[0] || 'Usuário';
        name = name.charAt(0).toUpperCase() + name.slice(1);
        const role = p.role || 'CLIENTE';
        return { name, role, initial: name.charAt(0).toUpperCase() };
    } catch { return { name: 'Usuário', role: null, initial: '?' }; }
}

// =================================================================
// TOAST
// =================================================================

function showToast(msg, type = 'success') {
    const t = document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.className = `toast ${type} show`;
    setTimeout(() => t.classList.remove('show'), 3500);
}

// =================================================================
// INIT
// =================================================================

document.addEventListener('DOMContentLoaded', () => {
    // Auth pages
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    document.getElementById('registerForm')?.addEventListener('submit', handleRegister);

    // Dashboard
    if (document.getElementById('ordens-list')) {
        initDashboard();
    }
});

function initDashboard() {
    const { name, role, initial } = getPayload();

    // Update sidebar user info
    const nameEl = document.getElementById('userNameDisplay');
    const roleEl = document.getElementById('userRole');
    const avatarEl = document.getElementById('userAvatar');
    if (nameEl) nameEl.textContent = name;
    if (roleEl) roleEl.textContent = role || '—';
    if (avatarEl) avatarEl.textContent = initial;

    fetchOrders();

    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        localStorage.removeItem('authToken');
        window.location.href = 'login.html';
    });

    document.getElementById('open-modal-btn')?.addEventListener('click', () => openModal());
    document.getElementById('closeModal')?.addEventListener('click', closeModal);
    document.getElementById('create-form')?.addEventListener('submit', handleFormSubmit);

    window.addEventListener('click', e => {
        if (e.target === document.getElementById('os-modal')) closeModal();
        if (e.target === document.getElementById('confirm-modal')) closeConfirm();
    });
}

// =================================================================
// AUTH
// =================================================================

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const msg   = document.getElementById('message');

    if (msg) { msg.textContent = 'Autenticando...'; msg.style.color = 'var(--text-2)'; }

    try {
        const res  = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });
        const text = await res.text();
        if (res.ok) {
            localStorage.setItem('authToken', JSON.parse(text).token);
            window.location.href = 'index.html';
        } else {
            if (msg) { msg.textContent = 'E-mail ou senha incorretos.'; msg.style.color = 'var(--red)'; }
        }
    } catch {
        if (msg) { msg.textContent = 'Erro de conexão.'; msg.style.color = 'var(--red)'; }
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const nome  = document.getElementById('registerNome').value;
    const email = document.getElementById('registerEmail').value;
    const senha = document.getElementById('registerSenha').value;
    const msg   = document.getElementById('registerMessage');

    try {
        const res = await fetch(`${BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha, nome })
        });
        if (res.ok || res.status === 201) {
            if (msg) { msg.textContent = '✅ Conta criada! Redirecionando...'; msg.style.color = 'var(--green)'; }
            setTimeout(() => window.location.href = 'login.html', 2000);
        } else {
            const err = await res.text();
            if (msg) { msg.textContent = err; msg.style.color = 'var(--red)'; }
        }
    } catch {
        if (msg) { msg.textContent = 'Erro de conexão.'; msg.style.color = 'var(--red)'; }
    }
}

// =================================================================
// FETCH ORDERS
// =================================================================

const STATUS = {
    ABERTO:     { label: 'Aberto',       css: 'badge-open' },
    EM_EXECUCAO:{ label: 'Em Execução',  css: 'badge-exec' },
    CONCLUIDO:  { label: 'Concluído',    css: 'badge-done' },
};

const PRIORIDADE = {
    BAIXA: { label: 'Baixa', css: 'badge-low' },
    MEDIA: { label: 'Média', css: 'badge-mid' },
    ALTA:  { label: 'Alta',  css: 'badge-high' },
};

const CATEGORIA = {
    RECLAMACAO: '🔴 Reclamação',
    MANUTENCAO: '🔧 Manutenção',
    DUVIDA:     '💬 Dúvida',
};

async function fetchOrders() {
    const grid = document.getElementById('ordens-list');
    if (!grid) return;

    grid.innerHTML = `<div class="loading-state"><div class="spinner"></div><p>Carregando ordens...</p></div>`;

    try {
        const res = await fetch(API_URL, { headers: { 'Authorization': `Bearer ${getToken()}` } });

        if (res.status === 401) { localStorage.removeItem('authToken'); window.location.href = 'login.html'; return; }
        if (!res.ok) { grid.innerHTML = `<div class="empty-state">Acesso negado ou erro no servidor.</div>`; return; }

        const ordens = await res.json();

        // Update stats
        updateStats(ordens);

        grid.innerHTML = '';

        if (!ordens.length) {
            grid.innerHTML = `<div class="empty-state">📋 Nenhuma OS encontrada. Crie sua primeira ordem de serviço!</div>`;
            return;
        }

        const { role } = getPayload();
        const isExecutor = role === 'EXECUTOR';

        ordens.forEach((os, i) => {
            const status    = STATUS[os.status]       || { label: os.status,    css: '' };
            const prioridade= PRIORIDADE[os.prioridade]|| { label: os.prioridade, css: '' };
            const categoria = CATEGORIA[os.categoria]  || os.categoria;

            const card = document.createElement('div');
            card.className = 'os-card';
            card.style.animationDelay = `${i * 0.05}s`;

            card.innerHTML = `
                <div class="card-top">
                    <span class="card-title">${os.titulo}</span>
                </div>
                <div class="card-badges">
                    <span class="badge ${status.css}">${status.label}</span>
                    <span class="badge ${prioridade.css}">${prioridade.label}</span>
                </div>
                <div class="card-meta">${categoria}</div>
                ${os.descricao ? `<p class="card-desc">${os.descricao}</p>` : ''}
                <div class="card-actions">
                    ${isExecutor ? `<button class="card-btn advance" onclick="handleAdvance(${os.id})">▶ Avançar</button>` : ''}
                    <button class="card-btn edit" onclick='openModal(${JSON.stringify(os)})'>✏️ Editar</button>
                    <button class="card-btn delete" onclick="confirmDelete(${os.id})">🗑️ Excluir</button>
                </div>
            `;
            grid.appendChild(card);
        });

    } catch (err) {
        grid.innerHTML = `<div class="empty-state">Erro ao carregar dados.</div>`;
        console.error(err);
    }
}

function updateStats(ordens) {
    document.getElementById('stat-total').textContent = ordens.length;
    document.getElementById('stat-open').textContent  = ordens.filter(o => o.status === 'ABERTO').length;
    document.getElementById('stat-exec').textContent  = ordens.filter(o => o.status === 'EM_EXECUCAO').length;
    document.getElementById('stat-done').textContent  = ordens.filter(o => o.status === 'CONCLUIDO').length;
}

// =================================================================
// MODAL
// =================================================================

function openModal(osData = null) {
    const modal     = document.getElementById('os-modal');
    const form      = document.getElementById('create-form');
    const title     = document.getElementById('modal-title');
    const submitBtn = document.getElementById('submit-btn');
    const statusGrp = document.getElementById('status-group');
    const msgEl     = document.getElementById('form-message');

    form.reset();
    msgEl.style.display = 'none';

    if (osData) {
        document.getElementById('osId').value      = osData.id;
        document.getElementById('titulo').value    = osData.titulo;
        document.getElementById('prioridade').value= osData.prioridade;
        document.getElementById('categoria').value = osData.categoria;
        document.getElementById('status').value    = osData.status;
        document.getElementById('descricao').value = osData.descricao || '';
        title.textContent = 'Editar OS';
        submitBtn.textContent = 'Salvar';
        statusGrp.style.display = 'block';
          const { role } = getPayload();
    const isCliente = role === 'CLIENTE';

    // Esconde campos que cliente não pode editar
    document.getElementById('categoria').closest('.field').style.display = isCliente ? 'none' : 'block';
    document.getElementById('prioridade').closest('.field').style.display = isCliente ? 'none' : 'block';
    document.getElementById('status-group').style.display = isCliente ? 'none' : 'block';
    } else {
        document.getElementById('osId').value = '';
        title.textContent = 'Nova Ordem de Serviço';
        submitBtn.textContent = 'Criar OS';
        statusGrp.style.display = 'none';
    }

    modal.classList.add('open');
}

function closeModal() {
    document.getElementById('os-modal').classList.remove('open');
}

// =================================================================
// FORM SUBMIT
// =================================================================

async function handleFormSubmit(e) {
    e.preventDefault();
    const msgEl     = document.getElementById('form-message');
    const submitBtn = document.getElementById('submit-btn');
    const osId      = document.getElementById('osId').value;
    const isEdit    = !!osId;

    const data = {
        titulo:    document.getElementById('titulo').value,
        prioridade:document.getElementById('prioridade').value,
        categoria: document.getElementById('categoria').value,
        status:    document.getElementById('status').value,
        descricao: document.getElementById('descricao').value,
    };

    submitBtn.disabled = true;
    submitBtn.textContent = 'Salvando...';
    msgEl.style.display = 'none';

    try {
        const res = await fetch(isEdit ? `${API_URL}/${osId}` : API_URL, {
            method: isEdit ? 'PUT' : 'POST',
            headers: authHeaders(),
            body: JSON.stringify(data)
        });

        if (!res.ok) throw new Error(await extractError(res));

        showToast(isEdit ? '✅ OS atualizada!' : '✅ OS criada com sucesso!');
        setTimeout(() => { closeModal(); fetchOrders(); }, 800);

    } catch (err) {
        msgEl.style.display = 'block';
        msgEl.className = 'form-msg error';
        msgEl.textContent = err.message;
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = isEdit ? 'Salvar' : 'Criar OS';
    }
}

// =================================================================
// DELETE WITH CONFIRM
// =================================================================

let pendingDeleteId = null;

function confirmDelete(osId) {
    pendingDeleteId = osId;
    document.getElementById('confirm-modal').classList.add('open');
    document.getElementById('confirm-delete-btn').onclick = executeDelete;
}

function closeConfirm() {
    document.getElementById('confirm-modal').classList.remove('open');
    pendingDeleteId = null;
}

async function executeDelete() {
    if (!pendingDeleteId) return;
    closeConfirm();

    try {
        const res = await fetch(`${API_URL}/${pendingDeleteId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });

        if (res.ok || res.status === 204) {
            showToast('🗑️ OS removida com sucesso.');
            fetchOrders();
        } else {
            throw new Error(await extractError(res));
        }
    } catch (err) {
        showToast('❌ ' + err.message, 'error');
    }
}

// =================================================================
// ADVANCE STATUS
// =================================================================

async function handleAdvance(osId) {
    try {
        const res = await fetch(`${API_URL}/${osId}/avancar-status`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });
        if (!res.ok) throw new Error(await extractError(res));
        showToast('✅ Status avançado!');
        fetchOrders();
    } catch (err) {
        showToast('❌ ' + err.message, 'error');
    }
}
