package mail;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class SendMail {
	public static void sendMail(String content) {
		Email email = new SimpleEmail();
		email.setHostName(MailConfig.HOST_NAME);
		email.setSmtpPort(MailConfig.SSL_PORT);
		email.setAuthenticator(new DefaultAuthenticator(MailConfig.APP_EMAIL, MailConfig.APP_PASSWORD));
        email.setSSLOnConnect(true);
        
        try {
			email.setFrom(MailConfig.APP_EMAIL);
			email.addTo(MailConfig.RECEIVE_EMAIL);
			email.setSubject("[ETL PROCESS PROBLEM]");
			email.setMsg(content);
			email.send();
			System.out.println("\nMessage sent successfully");
		} catch (EmailException e) {
			System.out.println("<---> ERROR [Send mail]: " + e.getMessage());
		}    
	}
	
	public static void main(String[] args) throws EmailException {
		 sendMail("Phan Le Huy ok 2");
        
    }
}
