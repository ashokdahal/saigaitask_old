/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.web.multipart.MultipartFile;
public class MailUtil {

	/**
	 * メールを送信する。
	 *
	 * @param smtp SMTPサーバ
	 * @param from 送信アドレス
	 * @param to 受信アドレス
	 * @param cc CC受信アドレス
	 * @param bcc BCC受信アドレス
	 * @param title タイトル
	 * @param content メール内容
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendMail(String smtp, String from, String to, String cc, String bcc, String title, String content) throws UnsupportedEncodingException, MessagingException
	{
        Properties props = System.getProperties();
        // SMTPサーバーのアドレスを指定
        if (smtp != null) {
        	props.put("mail.smtp.host", smtp);
        	props.put("mail.smtp.localhost", smtp);
        }

        Session session=Session.getDefaultInstance(props,null);

        MimeMessage mimeMessage=new MimeMessage(session);
        // 送信元メールアドレスと送信者名を指定
        mimeMessage.setFrom(new InternetAddress(from,"","UTF-8"));
        // 送信先メールアドレスを指定
        mimeMessage.setRecipients(Message.RecipientType.TO, to);
        if (cc != null)
        	mimeMessage.setRecipients(Message.RecipientType.CC, cc);
        if (bcc != null)
        	mimeMessage.setRecipients(Message.RecipientType.BCC, bcc);
        // メールのタイトルを指定
        mimeMessage.setSubject(title, "UTF-8");
        // メールの内容を指定
        mimeMessage.setText(content, "UTF-8");
        // メールの形式を指定
        //mimeMessage.setHeader("Content-Type","text/plain");
		mimeMessage.setHeader("Content-Transfer-Encoding", "7bit");
        // 送信日付を指定
        mimeMessage.setSentDate(new java.util.Date());
        // 送信します
        Transport.send(mimeMessage);
	}


	/**
	 * メールを送信する。(添付可)
	 *
	 * @param smtp SMTPサーバ
	 * @param from 送信アドレス
	 * @param to 受信アドレス
	 * @param cc CC受信アドレス
	 * @param bcc BCC受信アドレス
	 * @param title タイトル
	 * @param content メール内容
	 * @param attach 添付ファイル
	 * @param dirPath 添付ファイルの一時保存先パス(終端/)
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendMailAttach(String smtp, String from, String to, String cc, String bcc, String title, String content, MultipartFile[] attach, String dirPath) throws UnsupportedEncodingException, MessagingException
	{
        Properties props = System.getProperties();
        // SMTPサーバーのアドレスを指定
        if (smtp != null) {
        	props.put("mail.smtp.host", smtp);
        	props.put("mail.smtp.localhost", smtp);
        }

        Session session=Session.getDefaultInstance(props,null);

        MimeMessage mimeMessage=new MimeMessage(session);
        // 送信元メールアドレスと送信者名を指定
        mimeMessage.setFrom(new InternetAddress(from,"","UTF-8"));
        // 送信先メールアドレスを指定
        mimeMessage.setRecipients(Message.RecipientType.TO, to);
        if (cc != null)
        	mimeMessage.setRecipients(Message.RecipientType.CC, cc);
        if (bcc != null)
        	mimeMessage.setRecipients(Message.RecipientType.BCC, bcc);
        // メールのタイトルを指定
        mimeMessage.setSubject(title, "UTF-8");
        // メールの形式を指定
        //mimeMessage.setHeader("Content-Type","text/plain");
		mimeMessage.setHeader("Content-Transfer-Encoding", "7bit");
        // 送信日付を指定
        mimeMessage.setSentDate(new java.util.Date());

        Multipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(content, "UTF-8");
        mbp1.setHeader("Content-Transfer-Encoding", "7bit");
        mp.addBodyPart(mbp1);

		boolean attachFlg = false;
		for (MultipartFile file : attach) {
				if (file != null) {
					if(!"".equals(file.getOriginalFilename())) {
						attachFlg = true;
						break;
					}
				}
		}

        // 添付ファイルがある場合
//        if (attach.length > 0) {
		if (attachFlg) {
	        //添付ファイルのデータソースを取得する。
			for (int i=0; i<attach.length; i++) {
				if (attach[i] == null) {
					continue;
				} else if ("".equals(attach[i].getOriginalFilename())) {
					continue;
				}

		        MimeBodyPart mbp2 = new MimeBodyPart();
				File file = new File( (dirPath + attach[i].getOriginalFilename() ) );
				FileDataSource fds= new FileDataSource(file);
				DataHandler imgdh = new DataHandler(fds);
		        mbp2.setDataHandler(imgdh);
		        mbp2.setFileName(MimeUtility.encodeWord(fds.getName()));
		        mp.addBodyPart(mbp2);
			}
		}
        mimeMessage.setContent(mp);
		// 送信
        Transport.send(mimeMessage);
	}
}
