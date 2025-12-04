package com.ordemDeServico.Service.Listeners;

import com.ordemDeServico.Observer.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.Service.EmailService; // Importe o seu EmailService
import com.ordemDeServico.model.OrdemServico;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component // Garante que o Spring gerencie este componente
public class NotificacaoClienteListener {

    @Autowired
    private EmailService emailService; // Injeta o serviço de e-mail

    // Esta anotação diz ao Spring para executar este método quando o evento for publicado
    @Async
    @EventListener
    public void handleStatusUpdate(OrdemServicoStatusAtualizadoEvent event) {
        OrdemServico os = event.getOrdemServico();
        System.out.println("CRIADOR = " + os.getCriador());
        System.out.println("EMAIL = " + (os.getCriador() != null ? os.getCriador().getEmail() : "CRIADOR NULO"));

        // **Ação de envio de e-mail**
        String destinatario = os.getCriador().getEmail(); // Ajuste conforme sua classe Usuario/Cliente
        String assunto = "OS #" + os.getId() + " - Status Atualizado para " + os.getStatus();
        String corpo = "Prezado(a) cliente, sua Ordem de Serviço foi atualizada para: " + os.getStatus();

        // Chama o EmailService que você criou
        emailService.enviarEmail(destinatario, assunto, corpo);
    }
}