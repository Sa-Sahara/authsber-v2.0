package com.alevya.authsber.service;

import com.alevya.authsber.dto.MessageDtoRequest;
import com.alevya.authsber.dto.MessageDtoResponse;
import com.alevya.authsber.email.EmailSender;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.model.User;
import com.alevya.authsber.model.enums.MessageType;
import com.alevya.authsber.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class RegistrationService {
    @Value("${url.check.phone}")
    private String URL_CHECK_PHONE;
    private static final long CODE_DURATION_MILLIS = 18_000_000;
    private final MessageService messageService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public RegistrationService(
            MessageService messageService,
            RoleService roleService,
            UserRepository userRepository,
            EmailSender emailSender) {
        this.messageService = messageService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public String sendPhoneRegistration(User user) {
        if (user == null) {
            throw new BadRequestException("Invalid user");
        }
        if (StringUtils.isBlank(user.getPhone())) {
            throw new BadRequestException("Invalid user's phone");
        }
        if (user.getPhoneVerified()) {
            throw new BadRequestException("Phone already verified");
        }
        String phone = user.getPhone();
        //generate code
        Long codeSend = generateCode();
        //save in DB
        MessageDtoRequest messageDtoRequest = new MessageDtoRequest(
                MessageType.SMS_CHECK_PHONE,
                codeSend,
                user.getId(),
                System.currentTimeMillis()
        );
        messageService.createCheckMessage(messageDtoRequest);
        //send code
        if (sendPhoneCode(phone, codeSend)) {
            return "OK";
        }
        throw new BadRequestException("Code not sent");
    }

    public void sendEmailRegistration(User user) {
        if (user == null) {
            throw new BadRequestException("Invalid user");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            throw new BadRequestException("Invalid user's Email");
        }
        if (user.getEmailVerified()) {
            throw new BadRequestException("Email already verified");
        }
        String email = user.getEmail();
        //generate code
        Long codeSend = generateCode();
        //save in DB
        MessageDtoRequest messageDtoRequest = new MessageDtoRequest(
                MessageType.EMAIL_CHECK_CODE,
                codeSend,
                user.getId(),
                System.currentTimeMillis()
        );
        messageService.createCheckMessage(messageDtoRequest);
        //System.out.println(codeSend);
        //send code
        String link = "http:/localhost:8080/api/v1/user/CREATE_USER/confirm?token+" + codeSend;
        emailSender.send(
                email,
                buildEmail(user.getName(), link));
    }

    boolean sendPhoneCode(String phone, Long code) {
        String keyString = "" + code + "/" + phone;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(keyString.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            String keyMd = stringBuilder.toString();
            String urlSend = URL_CHECK_PHONE + "/?phone=" + phone + "&code=" + code + "&key=" + keyMd;
//            this.webClient = WebClient.create();
//            webClient
//                    .get()
//                    .uri(urlSend);
            return true;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private Long generateCode() {
        return Math.round(Math.random() * 10_000_000);
    }

    public String checkPhoneRegistration(User user, Long code) {
        if (user.getId() == null) {
            throw new BadRequestException("Invalid user");
        }
        if (code == null) {
            throw new BadRequestException("Invalid code");
        }
        //give code from DB
        Long codeFromDb = messageService
                .getLastMessageByUserIdAndMessageType(user.getId(), MessageType.SMS_CHECK_PHONE)
                .getAccessCode();
        if (codeFromDb.equals(code)) {
            //set user role
            user.addRole(roleService.getRoleUser());
            userRepository.saveAndFlush(user);
            return "OK";
        }
        throw new BadRequestException("Incorrect code");
    }

    public String checkEmailRegistration(User user, Long code) {
        if (code == null) {
            throw new BadRequestException("Invalid code");
        }
        //give code from DB
        MessageDtoResponse messageFromDb = messageService
                .getLastMessageByUserIdAndMessageType(user.getId(), MessageType.EMAIL_CHECK_CODE);
        Long codeFromDb = messageFromDb.getAccessCode();
        if (codeFromDb.equals(code)) {
            //set user role
            user.setEmailVerified(true);
            userRepository.saveAndFlush(user);
            return "OK";
        }
        throw new BadRequestException("codes do not match");
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
