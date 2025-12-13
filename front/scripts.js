// =================================================================
// CONFIGURAÇÕES GLOBAIS
// =================================================================
const BASE_URL = 'http://localhost:8080';
const API_URL = `${BASE_URL}/os`; 

// =================================================================
// 0. FUNÇÃO AUXILIAR: EXTRAIR MENSAGEM DE ERRO (Backend Spring)
// =================================================================
async function extractErrorMessage(response) {
    try {
        const text = await response.text(); // Lê tudo que veio do back
        
        // 1. Tenta ler como JSON
        try {
            const json = JSON.parse(text);
            
            // Se o backend mandou { "message": "O Executor da os é teste" }
            if (json.message) return json.message;
            
            // Se mandou { "error": "Forbidden" }
            if (json.error && typeof json.error === 'string') return json.error;
            
        } catch (e) {
            // Se NÃO for JSON, significa que o backend mandou a mensagem como texto puro!
            // Ex: throw new RuntimeException("O Executor da os é teste");
            if (text && text.length > 0) {
                return text; 
            }
        }
        
        // Se não conseguiu ler nada, aí sim mostra o status
        return `Erro HTTP ${response.status} (Sem mensagem)`;
        
    } catch (e) {
        return "Erro de comunicação com o servidor.";
    }
}   
// =================================================================
// 1. GUARDA DE SEGURANÇA (Executa Imediatamente)
// =================================================================
(function securityCheck() {
    const currentPath = window.location.pathname;
    const isPublicPage = currentPath.includes('login.html') || currentPath.includes('register.html');
    const token = localStorage.getItem('authToken');

    // Se NÃO for página pública e NÃO tiver token, chuta pro login
    if (!isPublicPage && !token) {
        window.location.href = 'login.html';
    }
    
    // Se for página de login mas JÁ tem token, manda pra home
    if (isPublicPage && token) {
        window.location.href = 'index.html';
    }
})();

// =================================================================
// 2. VARIÁVEIS GLOBAIS E INICIALIZAÇÃO
// =================================================================
let listBody, createForm, formMessage, modal, openModalBtn, closeBtn, userNameDisplay;

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM Carregado.");

    // Captura elementos
    listBody = document.getElementById('ordens-list');
    createForm = document.getElementById('create-form');
    formMessage = document.getElementById('form-message');
    modal = document.getElementById('os-modal');
    openModalBtn = document.getElementById('open-modal-btn'); 
    closeBtn = document.getElementsByClassName('close-btn')[0];
    userNameDisplay = document.getElementById('userNameDisplay'); 

    // Listeners de Login/Registro
    const loginForm = document.getElementById('loginForm');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);
    
    const registerForm = document.getElementById('registerForm');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    // Listeners da Página Principal (Dashboard)
    if (document.getElementById('osList')) { 
        fetchOrders(); // Carrega a lista
        
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

        // Modal: Modo Criar
        if (openModalBtn) openModalBtn.addEventListener('click', () => openModal()); 
        
        // Modal: Fechar
        if (closeBtn) closeBtn.addEventListener('click', closeModal);
        
        // Modal: Submit (Criar ou Editar)
        if (createForm) createForm.addEventListener('submit', handleFormSubmit);
        
        // Fecha modal ao clicar fora
        window.onclick = function(event) {
            if (event.target == modal) closeModal();
        }
    }
});

// =================================================================
// 3. DECODIFICAÇÃO DE TOKEN (Pegar Nome e Role)
// =================================================================
function getPayloadFromToken() {
    const token = localStorage.getItem('authToken');
    if (!token) return { username: 'Visitante', name: '', role: null, userId: null };

    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const payload = JSON.parse(jsonPayload);
        
        // Lógica para pegar o Nome Real
        const username = payload.sub || 'Usuário'; // Email
        let realName = payload.nome; // Claim 'nome' do TokenService
        
        // Fallback: se não tiver nome, usa a primeira parte do email
        if (!realName && username.includes('@')) {
            realName = username.split('@')[0];
        }
        
        // Formata (Primeira letra maiúscula)
        if (realName) {
            realName = realName.charAt(0).toUpperCase() + realName.slice(1);
        }

        const userId = payload.userId || payload.id; 
        const role = payload.role || (payload.authorities && payload.authorities.length > 0 ? payload.authorities[0].authority : 'ROLE_CLIENTE');
        
        return { username, name: realName || 'Usuário', role, userId };
    } catch (e) {
        console.error("Erro token:", e);
        return { username: 'Erro', name: 'Erro', role: null, userId: null };
    }
}

// =================================================================
// 4. AUTENTICAÇÃO
// =================================================================

async function handleLogin(event) {
    event.preventDefault(); 
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const messageArea = document.getElementById('message');

    if(messageArea) messageArea.textContent = 'Autenticando...';

    try {
        const response = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        const responseText = await response.text();

        if (response.ok) {
            let token = responseText;
            // Limpa o token se vier com texto extra "Logado com sucesso..."
            if (token.includes("Logado")) {
                const parts = token.split(':');
                token = parts.length > 1 ? parts[1].trim() : token.replace('Logado Com Sucesso.', '').trim();
            }

            localStorage.setItem('authToken', token); 
            window.location.href = 'index.html'; 
        } else {
            if(messageArea) {
                // Tenta ler o erro do backend
                const errorMsg = await extractErrorMessage({ text: () => Promise.resolve(responseText) });
                messageArea.textContent = 'Erro: ' + errorMsg;
                messageArea.classList.add('error');
            }
        }
    } catch (error) {
        if(messageArea) messageArea.textContent = 'Erro de conexão.';
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

        const responseText = await response.text(); 
        if (response.status === 201 || response.ok) {
            if(messageArea) messageArea.textContent = 'Cadastro realizado! Faça login.';
            setTimeout(() => window.location.href = 'login.html', 2000); 
        } else {
            if(messageArea) messageArea.textContent = 'Erro: ' + responseText;
        }
    } catch (error) {
        console.error(error);
    }
}

function handleLogout() {
    localStorage.removeItem('authToken'); 
    window.location.href = 'login.html';
}

// =================================================================
// 5. MODAL (CRIAR E EDITAR)
// =================================================================

function openModal(osData = null) {
    if(modal) modal.style.display = 'block';
    
    if(formMessage) {
        formMessage.style.display = 'none';
        formMessage.className = 'message';
    }

    if (osData) {
        // --- MODO EDIÇÃO ---
        // Preenche o campo oculto e os visíveis
        document.getElementById('osId').value = osData.id;
        document.getElementById('titulo').value = osData.titulo;
        document.getElementById('prioridade').value = osData.prioridade;
        document.getElementById('categoria').value = osData.categoria;
        document.getElementById('status').value = osData.status;
        document.getElementById('descricao').value = osData.descricao || '';
        
        const btn = createForm.querySelector('button[type="submit"]');
        if(btn) btn.textContent = "Salvar Alterações";
    } else {
        // --- MODO CRIAÇÃO ---
        createForm.reset();
        document.getElementById('osId').value = ''; // Limpa ID
        const btn = createForm.querySelector('button[type="submit"]');
        if(btn) btn.textContent = "Cadastrar OS";
    }
}

function closeModal() {
    if(modal) modal.style.display = 'none';
    if(createForm) createForm.reset();
}

// =================================================================
// 6. LISTAGEM (GET)
// =================================================================

async function fetchOrders() {
    const token = localStorage.getItem('authToken');
    const { name, role } = getPayloadFromToken(); 

    // Atualiza Saudação
    if (userNameDisplay) userNameDisplay.innerHTML = `Olá, <strong>${name}</strong>!`; 

    if(listBody) listBody.innerHTML = '<tr><td colspan="6">Carregando...</td></tr>'; 

    try {
        const response = await fetch(API_URL, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) {
            const msg = await extractErrorMessage(response);
            // Se for 403, avisa o usuário
            if (response.status === 403) {
                 if(listBody) listBody.innerHTML = `<tr><td colspan="6" class="error">Sessão expirada ou acesso negado.</td></tr>`;
                 return;
            }
            throw new Error(msg);
        }

        const ordens = await response.json();
        
        if(listBody) listBody.innerHTML = ''; 
        
        if (!Array.isArray(ordens) || ordens.length === 0) {
            if(listBody) listBody.innerHTML = '<tr><td colspan="6">Nenhuma OS encontrada.</td></tr>';
            return;
        }

        const isExecutor = (role === 'ROLE_ADMIN' || role === 'ROLE_EXECUTOR' || role === 'EXECUTOR');
        
        ordens.forEach(ordem => {
            const row = listBody.insertRow();
            row.insertCell().textContent = ordem.id;
            row.insertCell().textContent = ordem.titulo; 
            row.insertCell().textContent = ordem.status; 
            row.insertCell().textContent = ordem.prioridade;
            row.insertCell().textContent = ordem.categoria; 
            
            const actionsCell = row.insertCell();
            
            // Botão Avançar (Só Executor)
            if (isExecutor) {
                const advanceBtn = document.createElement('button');
                advanceBtn.textContent = '▶️';
                advanceBtn.className = 'action-btn advance-btn';
                advanceBtn.title = 'Avançar Status';
                advanceBtn.onclick = () => handleAdvance(ordem.id);
                actionsCell.appendChild(advanceBtn);
            }

            // Botão Editar (Preenche a modal)
            const editBtn = document.createElement('button');
            editBtn.textContent = '✏️';
            editBtn.className = 'action-btn edit-btn';
            editBtn.title = 'Editar';
            editBtn.onclick = () => openModal(ordem); 
            actionsCell.appendChild(editBtn);

            // Botão Deletar
            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = '🗑️';
            deleteBtn.className = 'action-btn delete-btn';
            deleteBtn.title = 'Deletar';
            deleteBtn.onclick = () => handleDelete(ordem.id);
            actionsCell.appendChild(deleteBtn);
        });

    } catch (error) {
        console.error(error);
        if(listBody) listBody.innerHTML = `<tr><td colspan="6" class="error">Erro ao carregar dados.</td></tr>`;
    }
}

// =================================================================
// 7. SUBMIT DO FORMULÁRIO (CRIAR E EDITAR)
// =================================================================

async function handleFormSubmit(event) {
    event.preventDefault();
    const token = localStorage.getItem('authToken');
    
    // Pega o ID do campo oculto
    const osId = document.getElementById('osId').value;
    const isEdit = !!osId; // True se tiver ID, False se estiver vazio

    const orderData = {
        titulo: document.getElementById('titulo').value,
        prioridade: document.getElementById('prioridade').value,
        categoria: document.getElementById('categoria').value,
        status: document.getElementById('status').value, 
        descricao: document.getElementById('descricao').value 
    };

    // --- LÓGICA DE URL E MÉTODO ---
    // Se for EDITAR: PUT /os/{id} (Resolvendo erro 'Method not supported')
    // Se for CRIAR:  POST /os
    const url = isEdit ? `${API_URL}/${osId}` : API_URL; 
    const method = isEdit ? 'PUT' : 'POST';

    if(formMessage) {
        formMessage.style.display = 'block';
        formMessage.textContent = 'Enviando...';
        formMessage.className = 'message';
    }

    try {
        const response = await fetch(url, { 
            method: method,
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': `Bearer ${token}` 
            },
            body: JSON.stringify(orderData)
        });
        
        if (!response.ok) {
            // Extrai mensagem real do erro (Exception do Java)
            const errorMessage = await extractErrorMessage(response);
            throw new Error(errorMessage);
        }

        if(formMessage) {
            formMessage.className = 'message success';
            formMessage.textContent = isEdit ? 'OS Atualizada!' : 'OS Criada!';
        }
        
        setTimeout(() => { 
            closeModal(); 
            fetchOrders(); 
        }, 1500);

    } catch (error) {
        if(formMessage) {
            formMessage.className = 'message error';
            // Mostra a mensagem exata do erro na tela
            formMessage.textContent = error.message; 
        }
    }
}

// =================================================================
// 8. AÇÕES (AVANÇAR E DELETAR)
// =================================================================

async function handleAdvance(osId) {
    const token = localStorage.getItem('authToken');
    try {
        const response = await fetch(`${API_URL}/${osId}/avancar-status`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) {
            const msg = await extractErrorMessage(response);
            throw new Error(msg);
        }
        
        setTimeout(() => {
            alert(`Status avançado com sucesso!`);
            fetchOrders(); 
        }, 300);
        
    } catch (error) { 
        alert("Atenção: " + error.message); 
    }
}

async function handleDelete(osId) {
    const token = localStorage.getItem('authToken');
    if (!confirm(`Apagar OS #${osId}?`)) return;
    
    try {
        const response = await fetch(`${API_URL}/${osId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 204 || response.ok) {
            alert(`OS apagada!`);
            fetchOrders(); 
        } else {
            const msg = await extractErrorMessage(response);
            throw new Error(msg);
        }
    } catch (error) { 
        alert("Erro ao deletar: " + error.message); 
    }
}