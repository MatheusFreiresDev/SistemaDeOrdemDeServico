package com.ordemDeServico.Service.Listeners;

import com.ordemDeServico.Service.Event.OrdemServicoDeletada;
import com.ordemDeServico.Service.Event.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.Service.EmailService; // Importe o seu EmailService
import com.ordemDeServico.Service.Event.UsuarioCriado;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.Usuario;
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

    @Async
    @EventListener
    public void handleStatusUpdate(OrdemServicoDeletada event) {
        OrdemServico os = event.getOrdemServico();
        System.out.println("CRIADOR = " + os.getCriador());
        System.out.println("EMAIL = " + (os.getCriador() != null ? os.getCriador().getEmail() : "CRIADOR NULO"));

        // **Ação de envio de e-mail**
        String destinatario = os.getCriador().getEmail(); // Ajuste conforme sua classe Usuario/Cliente
        String assunto = "OS #" + os.getId() + " - Foi fechada..";
        String corpo = "Prezado(a) cliente, sua Ordem de Serviço foi Fechada.";

        // Chama o EmailService que você criou
        emailService.enviarEmail(destinatario, assunto, corpo);
    }

    @Async
    @EventListener
    public void handleStatusUpdate(UsuarioCriado event) {
        Usuario usuario = event.getUsuario();
        System.out.println("Usuario Criado: " + usuario.getEmail());

        // **Ação de envio de e-mail**
        String destinatario = usuario.getEmail();
        String assunto = "Bem-vindo(a) ao Sistema de OS! Comece a gerenciar seus serviços";
        String corpo = "Olá, " + usuario.getUsername() + " Seja bem-vindo(a) à nossa plataforma de Ordem de Serviço! Seu acesso foi criado com sucesso.";

        // Chama o EmailService que você criou
        emailService.enviarEmail(destinatario, assunto, corpo);
    }
}