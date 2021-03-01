package br.com.gfsolucoesti.emailtest.service;

import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateless
public class WildMail {

    public void send(String jndi, String addresses, String topic, String textMessage) throws AddressException, MessagingException, NamingException {

        Session session = (Session) new InitialContext().lookup(jndi);
        Message message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
        message.setSubject(topic);
        message.setContent(textMessage,"text/html");

        Transport.send(message);

    }

}