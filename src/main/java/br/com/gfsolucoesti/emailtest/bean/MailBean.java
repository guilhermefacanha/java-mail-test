package br.com.gfsolucoesti.emailtest.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import br.com.gfsolucoesti.emailtest.service.EmailConfig;
import br.com.gfsolucoesti.emailtest.service.EmailService;
import br.com.gfsolucoesti.emailtest.service.WildMail;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ViewScoped
@Named
public class MailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String jndi, username, from, password, host, port, sendTo, subject, message;

    @Getter
    @Setter
    private boolean debug, ssl, tsl;

    @Inject
    WildMail wildMail;

    public void sendCommonsClient() {

        try {

            validateRequiredFields();
            validateCommonsRequiredFields();
            EmailConfig config = EmailConfig.builder()
                    .host(host)
                    .port(port)
                    .senha(password)
                    .usuario(username)
                    .from(from)
                    .debug(debug)
                    .ssl(ssl)
                    .tsl(tsl)
                    .build();
            EmailService service = new EmailService(config);
            service.enviarEmailSimples("test", sendTo, subject, message);
            addMessage("Email Sent with Commons Client!", false);

        } catch (IllegalArgumentException e) {
            addMessage(e.getMessage(), true);
        } catch (Exception e) {
            addMessage(e.getMessage(), true);
            log.error("error sendCommonsClient: ", e);
        }
    }

    public void sendEapClient() {
        try {
            validateRequiredFields();
            validateEAPRequiredFields();
            wildMail.send(jndi, sendTo, subject, message);
            addMessage("Email Sent with EAP Client!", false);
        } catch (IllegalArgumentException e) {
            addMessage(e.getMessage(), true);
        } catch (Exception e) {
            addMessage(e.getMessage(), true);
            log.error("error sendEapClient: ", e);
        }
    }

    private void validateCommonsRequiredFields() {
        if (StringUtils.isEmpty(this.username))
            throw new IllegalArgumentException("Required field Username");
        if (StringUtils.isEmpty(this.username))
            throw new IllegalArgumentException("Required field Password");

    }

    private void validateEAPRequiredFields() {
        if (StringUtils.isEmpty(this.jndi))
            throw new IllegalArgumentException("Required field Jboss JNDI Lookup");
    }

    private void validateRequiredFields() {
        if (StringUtils.isEmpty(this.sendTo))
            throw new IllegalArgumentException("Required field SendTo");
        if (StringUtils.isEmpty(this.subject))
            throw new IllegalArgumentException("Required field Subject");
        if (StringUtils.isEmpty(this.message))
            throw new IllegalArgumentException("Required field Message");
    }

    private void addMessage(String message, boolean error) {
        if (error)
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", message));
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", message));
    }

}