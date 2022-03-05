package br.com.gfsolucoesti.emailtest.service;

import java.io.File;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailService {

  int SOCKET_TIMEOUT = 30000;

  private String usuario;
  private String from;
  private String senha;
  private String host;
  private int port;
  private boolean debug, ssl, tsl;

  Properties properties = new Properties();

  public EmailService(EmailConfig config) {
    carregarProperties(config);
  }

  private void carregarProperties(EmailConfig emailConfig) {
    usuario = emailConfig.getUsuario();
    from = emailConfig.getFrom();
    senha = emailConfig.getSenha();
    host = emailConfig.getHost();
    port = NumberUtils.toInt(emailConfig.getPort(), 0);
    this.ssl = emailConfig.isSsl();
    this.tsl = emailConfig.isTsl();
    this.debug = emailConfig.isDebug();
  }

  public void enviarEmailGmail(String remetente, String destinatario, String assunto,
      String mensagem) {
    Properties props = new Properties();
    props.put("mail.smtp.host", host);

    if (port > 0)
      props.put("mail.smtp.socketFactory.port", port);

    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");

    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(usuario, senha);
      }

    });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(usuario));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
      message.setSubject(assunto);
      message.setText(mensagem);

      Transport.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public void enviarEmailSimples(String destinatario, String assunto, String mensagem)
      throws Exception {
    String[] d = {destinatario};
    enviarEmailSimples(null, d, assunto, mensagem);
  }

  public void enviarEmailSimples(String remetente, String destinatario, String assunto,
      String mensagem) throws Exception {
    String[] d = {destinatario};
    enviarEmailSimples(remetente, d, assunto, mensagem);
  }

  public void enviarEmailSimples(String remetente, String[] destinatario, String assunto,
      String mensagem) throws Exception {
    SimpleEmail email = new SimpleEmail();
    email.setSocketConnectionTimeout(SOCKET_TIMEOUT);
    email.setSocketTimeout(SOCKET_TIMEOUT);

    remetente = StringUtils.defaultString(remetente, usuario);
    try {
      email.setDebug(this.debug);

      if (port > 0) {
        email.setSmtpPort(port);
        email.setSslSmtpPort(port + "");
      }
      email.setHostName(host);
      email.setAuthentication(usuario, senha);
//      if(this.ssl)
//        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
        
      email.setSSLOnConnect(this.ssl);
      email.setStartTLSEnabled(this.tsl);
      email.setStartTLSRequired(this.tsl);
      for (String d : destinatario)
        email.addTo(d);
      email.setFrom(usuario, remetente == null ? from : remetente);
      email.setSubject(assunto);
      email.setContent(mensagem, "text/html");
      email.getMailSession().getProperties().put("mail.smtp.ssl.trust", "*");
      email.send();
    } catch (EmailException e) {
      log.error("Erro ao enviar email ", e);
      throw new Exception("Erro ao enviar email: " + e.getMessage());
    }
  }

  public void enviarEmailAnexo(String remetente, String[] destinatario, String assunto,
      String mensagem, File[] arquivos) throws Exception {
    try {
      remetente = StringUtils.defaultString(remetente, usuario);
      MultiPartEmail email = new MultiPartEmail();
      email.setDebug(false);
      if (port > 0)
        email.setSmtpPort(port);
      email.setHostName(host);
      email.setAuthentication(usuario, senha);
      email.setSSLOnConnect(this.ssl);
      email.setStartTLSEnabled(this.tsl);
      for (String d : destinatario)
        email.addTo(d);
      email.setFrom(usuario, remetente);
      email.setSubject(assunto);
      email.setMsg(mensagem);

      for (File f : arquivos) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(f.getPath());
        attachment.setDisposition("attachment");
        attachment.setDescription(f.getName());
        attachment.setName(f.getName());
        email.attach(attachment);
      }

      email.send();
    } catch (EmailException e) {
      log.error("Erro ao enviar email com anexos", e);
      throw new Exception("Erro ao enviar email: " + e.getMessage());
    }
  }

  public boolean enviarEmailIntranet(String remetentes, String destinatario, String assunto,
      String mensagem) throws EmailException {
    try {
      SimpleEmail email = new SimpleEmail();
      email.setHostName(host);
      email.setFrom(usuario, remetentes);
      email.addTo(destinatario);
      email.setSubject(assunto);
      email.setAuthentication(usuario, senha);
      email.setMsg(mensagem);
      email.send();
      return true;
    } catch (EmailException e) {
      log.error("Erro ao enviar email intranet", e);
      throw e;
    }
  }

}
