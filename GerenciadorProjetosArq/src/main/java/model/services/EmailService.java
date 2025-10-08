package model.services;

import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.apache.commons.mail.SimpleEmail;

/**
 * Serviço responsável pelo envio de e-mails.
 * As credenciais são carregadas de um arquivo de configuração externo (config.properties).
 * @author juans
 */
public class EmailService {

    private String emailRemetente;
    private String senhaEmailRemetente;

    public EmailService() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("properties/config.properties")) {
            
            if (input == null) {
                System.out.println("ERRO: Não foi possível encontrar o arquivo config.properties");
                JOptionPane.showMessageDialog(null, "Arquivo de configuração de e-mail não encontrado.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
                return;
            }

            props.load(input);

            this.emailRemetente = props.getProperty("email.remetente");
            this.senhaEmailRemetente = props.getProperty("email.senha");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enviarEmailRecuperacao(String emailDestinatario, String codigo) {
        if (emailRemetente == null || senhaEmailRemetente == null) {
            System.out.println("As credenciais de e-mail não foram carregadas. O envio foi cancelado.");
            return; 
        }
        
        SimpleEmail email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        email.setAuthentication(this.emailRemetente, this.senhaEmailRemetente);
        email.setSSLOnConnect(true);

        try {
            email.setFrom(this.emailRemetente, "ArchFlow - Suporte");
            email.setSubject("ArchFlow - Código de Recuperação de Senha");
            
            String mensagem = "Olá,\n\n"
                    + "Você solicitou a recuperação de senha para sua conta no ArchFlow.\n\n"
                    + "Use o seguinte código de verificação para redefinir sua senha:\n\n"
                    + "Código: " + codigo + "\n\n"
                    + "Este código é válido por 15 minutos.\n\n"
                    + "Se você não solicitou esta alteração, pode ignorar este e-mail com segurança.\n\n"
                    + "Atenciosamente,\n"
                    + "Equipe ArchFlow";
            
            email.setMsg(mensagem);
            email.addTo(emailDestinatario);
            email.send();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao enviar o e--mail de recuperação. Verifique a conexão ou as configurações.", "Erro de E-mail", JOptionPane.ERROR_MESSAGE);
        }
    }
}