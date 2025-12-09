const API_URL = 'http://localhost:8080/os';
const listBody = document.getElementById('ordens-list');
const createForm = document.getElementById('create-form');
const formMessage = document.getElementById('form-message');

// Referências dos elementos da Modal
const modal = document.getElementById('os-modal');
const openModalBtn = document.getElementById('open-modal-btn');
const closeBtn = document.getElementsByClassName('close-btn')[0];

// --- FUNÇÕES DE CONTROLE DA MODAL ---
function openModal() {
    modal.style.display = 'block';
}

function closeModal() {
    modal.style.display = 'none';
    formMessage.style.display = 'none'; // Limpa a mensagem ao fechar
    createForm.reset();
}

// Fecha a modal se o usuário clicar fora dela
window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}

// --- FUNÇÕES DE INICIALIZAÇÃO E LISTAGEM (GET) ---

document.addEventListener('DOMContentLoaded', () => {
    fetchOrders();
    createForm.addEventListener('submit', handleCreateSubmit);
    
    // Configura os eventos da Modal
    openModalBtn.addEventListener('click', openModal);
    closeBtn.addEventListener('click', closeModal);
});

async function fetchOrders() {
    // Colspan ajustado para 6 colunas (5 dados + 1 ações)
    listBody.innerHTML = '<tr><td colspan="6">Carregando...</td></tr>'; 

    try {
        const response = await fetch(API_URL);
        
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const ordens = await response.json(); 
        listBody.innerHTML = ''; 
        
        if (ordens.length === 0) {
            listBody.innerHTML = '<tr><td colspan="6">Nenhuma Ordem de Serviço encontrada.</td></tr>';
            return;
        }

        ordens.forEach(ordem => {
            const row = listBody.insertRow();
            row.insertCell().textContent = ordem.id;
            row.insertCell().textContent = ordem.titulo; 
            row.insertCell().textContent = ordem.status; 
            row.insertCell().textContent = ordem.prioridade;
            row.insertCell().textContent = ordem.categoria; 
            
            // CÉLULA DE AÇÕES
            const actionsCell = row.insertCell();
            
            // 1. Botão AVANÇAR STATUS (PUT)
            const advanceBtn = document.createElement('button');
            advanceBtn.textContent = '▶️ Avançar';
            advanceBtn.className = 'action-btn advance-btn';
            advanceBtn.onclick = () => handleAdvance(ordem.id);
            actionsCell.appendChild(advanceBtn);
            
            // 2. Botão EDITAR (PUT - Apenas placeholder)
            const editBtn = document.createElement('button');
            editBtn.textContent = '✏️ Editar';
            editBtn.className = 'action-btn edit-btn';
            editBtn.onclick = () => alert(`EDITAR OS ID: ${ordem.id}. Requer lógica adicional de PUT.`);
            actionsCell.appendChild(editBtn);
            
            // 3. Botão APAGAR (DELETE)
            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = '🗑️ Apagar';
            deleteBtn.className = 'action-btn delete-btn';
            deleteBtn.onclick = () => handleDelete(ordem.id);
            actionsCell.appendChild(deleteBtn);
        });

    } catch (error) {
        console.error('Erro ao buscar ordens:', error);
        listBody.innerHTML = `<tr><td colspan="6" class="error">Falha ao conectar na API (${API_URL}): ${error.message}</td></tr>`;
    }
}

// --- FUNÇÃO POST (Criação) ---
async function handleCreateSubmit(event) {
    event.preventDefault();

    const usuarioId = document.getElementById('usuarioId').value;
    const CREATE_URL = `${API_URL}/${usuarioId}`;
    
    if (!usuarioId) {
        formMessage.className = 'message error';
        formMessage.textContent = 'Erro: O ID do Criador é obrigatório.';
        return; 
    }

    const newOrder = {
        titulo: document.getElementById('titulo').value,
        prioridade: document.getElementById('prioridade').value,
        categoria: document.getElementById('categoria').value,
        status: document.getElementById('status').value, 
        descricao: document.getElementById('descricao').value 
    };
    
    formMessage.style.display = 'block';
    formMessage.className = 'message';
    formMessage.textContent = 'Enviando...';

    try {
        const response = await fetch(CREATE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newOrder)
        });
        
        const result = await response.json(); 

        if (!response.ok) {
            const errorMessage = result.message || `Erro ao cadastrar: ${response.status} ${response.statusText}`;
            throw new Error(errorMessage);
        }

        formMessage.className = 'message success';
        formMessage.textContent = `Ordem de Serviço #${result.id} criada! Recarregando lista...`;
        
        setTimeout(() => {
            closeModal();
            fetchOrders(); 
        }, 1500);

    } catch (error) {
        console.error('Erro no cadastro:', error);
        formMessage.className = 'message error';
        formMessage.textContent = `Erro: ${error.message}. Verifique o log do servidor.`;
    }
}


// --- 1. FUNÇÕES PARA AVANÇAR STATUS (PUT) ---

function handleAdvance(id) {
    const executorId = prompt(`Para avançar o status da OS #${id}, qual é o ID do Executor?`);
    
    if (executorId && !isNaN(executorId)) {
        sendAdvanceRequest(id, parseInt(executorId));
    } else if (executorId !== null) {
        alert("ID do Executor inválido. Tente novamente.");
    }
}

async function sendAdvanceRequest(osId, executorId) {
    // URL: /os/{id}/avancar-status/{executorId}
    const ADVANCE_URL = `${API_URL}/${osId}/avancar-status/${executorId}`;
    
    listBody.innerHTML = `<tr><td colspan="6" style="color: orange; font-weight: bold;">Avançando OS #${osId} (Executor: ${executorId})...</td></tr>`;

    try {
        const response = await fetch(ADVANCE_URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Tenta obter o texto/JSON do erro se o status não for 2xx
        if (!response.ok) {
            const errorData = await response.text(); 
            throw new Error(errorData || response.statusText);
        }

        alert(`Status da OS #${osId} avançado com sucesso!`);
        fetchOrders(); // Recarrega a lista

    } catch (error) {
        console.error('Erro ao avançar status:', error);
        listBody.innerHTML = `<tr><td colspan="6" class="error">Erro ao avançar status: ${error.message}</td></tr>`;
    }
}


// --- 2. FUNÇÕES PARA APAGAR OS (DELETE) ---

function handleDelete(id) {
    const executorId = prompt(`Para APAGAR a OS #${id}, qual é o ID do Executor?`);
    
    if (executorId && !isNaN(executorId)) {
        if (confirm(`Tem certeza que deseja apagar a OS #${id} (Executor ID: ${executorId})?`)) {
            sendDeleteRequest(id, parseInt(executorId));
        }
    } else if (executorId !== null) {
        alert("ID do Executor inválido. Tente novamente.");
    }
}

async function sendDeleteRequest(osId, executorId) {
    // URL: /os/{id}/{executorId}
    const DELETE_URL = `${API_URL}/${osId}/${executorId}`;
    
    listBody.innerHTML = `<tr><td colspan="6" style="color: orange; font-weight: bold;">Apagando OS #${osId} (Executor: ${executorId})...</td></tr>`;

    try {
        const response = await fetch(DELETE_URL, {
            method: 'DELETE' // DELETE não precisa de Content-Type ou body
        });

        // O endpoint DELETE retorna 204 No Content se for bem-sucedido.
        if (response.status !== 204 && !response.ok) {
            const errorData = await response.text(); 
            throw new Error(errorData || response.statusText);
        }

        alert(`OS #${osId} deletada com sucesso!`);
        fetchOrders(); // Recarrega a lista

    } catch (error) {
        console.error('Erro ao deletar OS:', error);
        listBody.innerHTML = `<tr><td colspan="6" class="error">Erro ao deletar OS: ${error.message}</td></tr>`;
    }
}