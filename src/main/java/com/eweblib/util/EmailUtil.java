package com.eweblib.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.eweblib.cfg.ConfigManager;

public class EmailUtil {

	public static void sendMail(String subject, List<Object> toList,
			String content) {
		sendMail(subject, toList, content, null);
	}

	public static void sendMail(String subject, String to, String content,
			String file) {
		List<Object> toList = new ArrayList<Object>();
		toList.add(to);
		sendMail(subject, toList, content, file);
	}

	public static void sendMail(String subject, String to,
			Map<String, Object> model, String template, String file) {
		String content = getContent(model, template);
		List<Object> toList = new ArrayList<Object>();
		toList.add(to);
		sendMail(subject, toList, content, file);
	}

	public static void sendMail(String subject, List<Object> toList,
			Map<String, Object> model, String template, String file) {
		String content = getContent(model, template);
		sendMail(subject, toList, content, file);
	}

	/** 发送主方法 **/
	public static void sendMail(String subject, List<Object> toList,
			String content, String file) {
		HtmlEmail email = new HtmlEmail();

		try {
			email.setSSL(true);
			// 如果要求身份验证，设置用户名、密码，分别为发件人在邮件服务器上注册的用户名和密码
			email.setAuthentication(ConfigManager.getProperty("email_account"), ConfigManager.getProperty("email_password"));
			email.setHostName(ConfigManager.getProperty("email_smtp"));// 设置发送主机的服务器地址
			email.setSmtpPort(Integer.parseInt(ConfigManager.getProperty("email_smtp_port")));
			email.setFrom(ConfigManager.getProperty("email_account"), ConfigManager.getProperty("email_customer_service_name"));// 发件人邮箱
			email.setSslSmtpPort(ConfigManager.getProperty("email_smtp_port"));

			List<InternetAddress> address = new ArrayList<InternetAddress>();
			for (Object mail : toList) {
				if (!EweblibUtil.isEmpty(mail)) {
					String[] emailList = mail.toString().split(",");
					for (String em : emailList) {
						address.add(new InternetAddress(em));
					}
				}
			}

			if (!address.isEmpty()) {
				email.setTo(address);// 设置收件人邮箱
				email.setSubject(subject);// 设置邮件的主题

				email.setHtmlMsg(content);
				email.setTextMsg("您的邮箱不支持HTML消息格式");
				email.setCharset("UTF-8");

				if (!EweblibUtil.isEmpty(file) && new File(file).exists()) {
					EmailAttachment attachment = new EmailAttachment();
					attachment.setPath(file);
					attachment.setDisposition(EmailAttachment.ATTACHMENT);
					attachment.setName(new File(file).getName());
					email.attach(attachment);
				}

				email.send();
			}

		} catch (EmailException e) {
			e.printStackTrace();
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return 邮件主体
	 * @param model
	 *            向模版中传递的对象变量
	 * @param tempate
	 *            模版名
	 * */
	public static String getContent(Map<String, Object> model, String template) {

		return EweblibUtil.getContent(model, template, ConfigManager.getProperty("email_template_path"));
	}

}
