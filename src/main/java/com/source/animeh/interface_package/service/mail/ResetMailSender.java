package com.source.animeh.interface_package.service.mail;

import com.source.animeh.entity.account.User;

public interface ResetMailSender {

  /**
   * Gửi mail reset password cho user, kèm token
   */
  void sendResetEmail(User user, String token);
}
