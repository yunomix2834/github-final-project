package com.source.animeh.interface_package.service.account;

import com.source.animeh.dto.request.account.request_moderator.ApprovalModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.GuestModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.UserModeratorRequest;
import com.source.animeh.dto.response.account.ModeratorRequest_Response;

public interface ModeratorRequestServiceInterface {

  // Đăng ký Moderator cho Guest
  ModeratorRequest_Response requestModeratorForGuest(GuestModeratorRequest guestModeratorRequest);

  // Đăng ký Moderator cho User
  ModeratorRequest_Response requestModeratorForUser(UserModeratorRequest userModeratorRequest);

  // Duyệt Moderator
  ModeratorRequest_Response approveModeratorRequest(String requestId);

  // Từ chối Moderator
  ModeratorRequest_Response rejectModeratorRequest(
      ApprovalModeratorRequest approvalModeratorRequest,
      String requestId);
}
