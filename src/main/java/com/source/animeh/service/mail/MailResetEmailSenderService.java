package com.source.animeh.service.mail;

import com.source.animeh.entity.account.User;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.mail.ResetMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailResetEmailSenderService implements ResetMailSender {

  private final JavaMailSenderImpl mailSender;

  /**
   * Gửi mail reset password cho user, kèm token
   */
  @Override
  public void sendResetEmail(
      User user,
      String token) {
    String subject = "Reset Password for your AnimeH account";

    // TODO Sửa link sau này
    // Tạo link
    String resetLink = "http://192.168.1.30:4200/reset-password/" + token;

    // TODO Sửa lại giao diện form mail và thêm logo
    // Giao diện HTML:
    String htmlContent =
        "<html>"
            + "  <head>"
            + "    <meta charset='UTF-8' />"
            + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'/>"
            + "    <title>Reset Password</title>"
            + "  </head>"
            + "  <body style='margin:0; padding:0; background-color:#363333; font-family:Arial,Helvetica,sans-serif;'>"
            + "    <div style='max-width:600px; margin:40px auto 60px auto; background-color:#FFFFFF; border-radius:8px; overflow:hidden;'>"
            + "      <!-- Header -->"
            + "      <div style='background-color:#363333; padding:20px; text-align:center;'>"
            + "        <!-- Thay link ảnh tại src -->"
            + "        <img src='' alt='AnimeH Logo' "
            + "             style='width:180px; margin-bottom:10px;'/>"
            + "        <h1 style='color:#FFFFFF; margin:0; font-size:28px;'>AnimeH</h1>"
            + "      </div>"
            + "      <!-- Content -->"
            + "      <div style='padding:30px; color:#333333;'>"
            + "        <h2 style='margin-top:0; font-size:24px;'>Hello, " + user.getFullName()
            + "</h2>"
            + "        <p style='font-size:16px; line-height:1.5;'>"
            + "          You requested to reset your password. Please click the button below to continue the process. "
            + "          If you did not request this, please ignore this email."
            + "        </p>"
            + "        <div style='text-align:center; margin:30px 0;'>"
            + "          <a href='" + resetLink + "' "
            + "             style='display:inline-block; padding:14px 28px; background-color:#363333; color:#ffffff; "
            + "                    text-decoration:none; border-radius:5px; font-weight:bold; font-size:16px;'>"
            + "            Reset Password"
            + "          </a>"
            + "        </div>"
            + "        <p style='font-size:16px; line-height:1.5;'>"
            + "          If you cannot click the button, copy and paste the following link into your web browser:"
            + "        </p>"
            + "        <p style='font-size:16px; line-height:1.5; word-wrap:break-word;'>"
            + resetLink
            + "        </p>"
            + "        <p style='font-size:16px; line-height:1.5;'>"
            + "          This link will expire in 5 minutes."
            + "        </p>"
            + "      </div>"
            + "      <!-- Footer -->"
            + "      <div style='background-color:#F2F2F2; padding:15px; text-align:center;'>"
            + "        <p style='margin:0; font-size:14px; color:#666;'>"
            + "          Regards,<br/>"
            + "          The AnimeH Team"
            + "        </p>"
            + "      </div>"
            + "    </div>"
            + "  </body>"
            + "</html>";

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(user.getEmail());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new AppException(ErrorCode.FAILED_SEND_MAIL);
    }
  }
}
