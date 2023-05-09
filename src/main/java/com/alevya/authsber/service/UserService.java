package com.alevya.authsber.service;

import com.alevya.authsber.dto.*;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.User;
import com.alevya.authsber.model.enums.MessageType;
import com.alevya.authsber.email.EmailSender;
import com.alevya.authsber.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Value("${url.check.phone}")
    private String URL_CHECK_PHONE;
    private static final Log log = LogFactory.getLog(UserService.class);
    private static final int MAX_LENGTH_NAME = 150;
    private static final int MIN_LENGTH_PASSWORD = 6;
    private static final int MAX_LENGTH_PASSWORD = 100;

    private static final long CODE_DURATION_MILLIS = 900000;


    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessageService messageService;
    private final EmailSender emailSender;

    public UserService(UserRepository userRepository
            , RoleService roleService
            , BCryptPasswordEncoder bCryptPasswordEncoder
            , MessageService messageService
            , EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.messageService = messageService;
        this.emailSender = emailSender;
    }

    public UserDtoResponse createUser(UserDtoRequest dto) {
        log.info("createUser userDtoRequest: " + dto);
        //checking fields
        checkFields(dto);

        //set default fields
        if (dto.getRoles().isEmpty()) {
            dto.addRole(roleService.getRoleGuest());
        }
        dto.setBlocked(false);
        dto.setDeleted(false);
        dto.setEmailVerified(false);
        dto.setPhoneVerified(false);
        dto.setCreateDate(new Date());

        //save in DB
        return mapToUserDto(userRepository.save(mapToUser(dto)));
    }

    public UserDtoResponse getUserById(Long id) {
        log.info("getUserById id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToUserDto(userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User not found!")));
    }

    public UserDtoResponse getUserByPhone(String phone) {
        log.info("getUserByPhone phone: " + phone);
        if (phone == null) {
            throw new BadRequestException("Invalid phone");
        }
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        return mapToUserDto(user);
    }

    public UserDtoResponse getUserByEmail(String email) {
        log.info("getUserByEmail email: " + email);
        if (StringUtils.isBlank(email)) {
            throw new BadRequestException("Invalid email");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        return mapToUserDto(user);
    }

    public User getUserByPhoneEmail(String phoneEmail) { //todo
        log.info("getUserByPhoneEmail phoneEmail: " + phoneEmail);
        if (StringUtils.isBlank(phoneEmail)) {
            throw new BadRequestException("Invalid phoneEmail");
        }
        if (phoneEmail.contains("@")) {
            User user = userRepository.findByEmail(phoneEmail);
            if (user == null) {
                throw new NotFoundException("User not found!");
            }
            return user;
        } else {
            User user = userRepository.findByPhone(phoneEmail);
            if (user == null) {
                throw new NotFoundException("User not found!");
            }
            return user;
        }
    }

    public List<UserDtoResponse> getAllUsers() {
        log.info("getAllUsers");
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Page<UserDtoResponse> findAllUsersPageable(Pageable pageable) {
        log.info("findAllUsersPageable pageable:" + pageable);
        Page<User> page = userRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToUserDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public UserDtoResponse updateUser(Long id, UserDtoRequest dto) {
        log.info("updateUser id: " + id + " userDtoRequest: " + dto);
        checkFields(dto);
        User oldUser = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User not found!"));
        //name
            oldUser.setName(dto.getName()); //todo may change to Mapper?
        //patronymic
            oldUser.setPatronymic(dto.getPatronymic());
        //surname
            oldUser.setSurname(dto.getSurname());
        //address
              oldUser.setAddress(dto.getAddress());
        //blocked
        final Boolean blocked = dto.getBlocked(); //todo may be separate method??
        if (blocked != null) {
            oldUser.setBlocked(blocked);
        }
        //password
            oldUser.setPassword(
                    //  passwordEncoder.encode(
                    dto.getPassword()
                    //   )
            );
        //phone
            oldUser.setPhone(dto.getPhone());
        //roles
        if (!dto.getRoles().isEmpty()) {
            oldUser.addRoles(dto.getRoles());
        }
        //ModifyDate
        oldUser.setModifyDate(new Date());
        //save
        return mapToUserDto(userRepository.saveAndFlush(oldUser));
    }

    public void deleteUser(Long id) {
        log.info("deleteUser id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        userRepository.deleteById(id);
    }

    public UserDtoResponse mapToUserDto(User user) {
        UserDtoResponse userDto = new UserDtoResponse();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setPatronymic(user.getPatronymic());
        userDto.setEmail(user.getEmail());
        userDto.setEmailVerified(user.getEmailVerified());
        userDto.setPhone(user.getPhone());
        userDto.setPhoneVerified(user.getPhoneVerified());
        userDto.setAddress(user.getAddress());
        userDto.setBirthday(user.getBirthday());
        userDto.setBlocked(user.getBlocked());
        userDto.setRoles(user.getRoles());
        userDto.setCreateDate(user.getCreateDate());
        userDto.setModifyDate(user.getModifyDate());
        userDto.setDeleted(user.getDeleted());
        return userDto;
    }

    public UserRegistrationDto mapToUserRegistrationDto(User user) {
        UserRegistrationDto dto = UserRegistrationDto.builder()
        .name(user.getName())
        .surname(user.getSurname())
        .patronymic(user.getPatronymic())
        .email(user.getEmail())
        .phone(user.getPhone())
        .address(user.getAddress())
        .birthday(user.getBirthday())
        .build();
        return dto;
    }

    public User mapToUser(UserDtoRequest userDtoRequest) {
        User user = new User();
        user.setName(userDtoRequest.getName());
        user.setSurname(userDtoRequest.getSurname());
        user.setPatronymic(userDtoRequest.getPatronymic());
        user.setPassword(userDtoRequest.getPassword());
        user.setEmail(userDtoRequest.getEmail());
        user.setEmailVerified(userDtoRequest.getEmailVerified());
        user.setPhone(userDtoRequest.getPhone());
        user.setPhoneVerified(userDtoRequest.getPhoneVerified());
        user.setAddress(userDtoRequest.getAddress());
        user.setBirthday(userDtoRequest.getBirthday());
        user.setBlocked(userDtoRequest.getBlocked());
        user.setRoles(userDtoRequest.getRoles());
        user.setCreateDate(userDtoRequest.getCreateDate());
        user.setModifyDate(userDtoRequest.getModifyDate());
        user.setDeleted(userDtoRequest.getDeleted());
        return user;
    }

    public User mapRegistrationDtoToUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setName(userRegistrationDto.getName());
        user.setSurname(userRegistrationDto.getSurname());
        user.setPhone(userRegistrationDto.getPhone());
        user.setPatronymic(userRegistrationDto.getPatronymic());
        user.setPassword(userRegistrationDto.getPassword());
        user.setEmail(userRegistrationDto.getEmail());
        user.setBirthday(userRegistrationDto.getBirthday());
        return user;
    }

    public UserRegistrationDto registrationUser(UserRegistrationDto dto) {
        log.info("registrationUser userDtoRequest: " + dto);
        //checking fields
        checkFields(dto);
        User user = mapRegistrationDtoToUser(dto);
        //set default user role
        user.addRole(roleService.getRoleGuest());
        //set default fields
        user.setBlocked(false);
        user.setDeleted(false);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setCreateDate(new Date());
        //save in DB
        return mapToUserRegistrationDto(userRepository.save(user));
    }

    public String sendPhoneRegistration(User user) {
        if (user == null) {
            throw new BadRequestException("Invalid user");
        }
        if (StringUtils.isBlank(user.getPhone())) {
            throw new BadRequestException("Invalid user's phone");
        }
        String phone = user.getPhone();
        //generate code
        Date date = new Date();
        String codeSend = (long) (Math.random() * 153 * date.getTime()) + "";
        codeSend = codeSend.substring(codeSend.length() - 6);
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
        String codeSend = UUID.randomUUID().toString();
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

    boolean sendPhoneCode(String phone, String code) {
        String keyString = "" + code + "/" + phone;
        log.info("UserPersonServiceImpl-sendPhoneCode. keyString: " + keyString);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(keyString.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            String keyMd = stringBuilder.toString();
            String urlSend = URL_CHECK_PHONE + "/?phone=" + phone + "&code=" + code + "&key=" + keyMd;
            log.info("UserPersonServiceImpl-sendPhoneCode. keyMd: " + keyMd + ". urlSend: " + urlSend);
//            this.webClient = WebClient.create();
//            webClient
//                    .get()
//                    .uri(urlSend);
            return true;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public String checkPhoneRegistration(User user, String code) {
        log.info("checkPhoneRegistration user id: " + user.getId() + " code: " + code);
        if (user.getId() == null) {
            throw new BadRequestException("Invalid user");
        }
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("Invalid code");
        }
        //give code from DB
        String codeFromDb = messageService
                .getLastMessageByUserIdAndMessageType(user.getId(), MessageType.SMS_CHECK_PHONE)
                .getAccessCode()
                .replaceAll("[^0-9]", "");
        String codeOnlyNumber = code.replaceAll("[^0-9]", "");
        if (codeFromDb.equals(codeOnlyNumber)) {
            //set user role
            user.addRole(roleService.getRoleUser());
            userRepository.saveAndFlush(user);
            return "OK";
        }
        throw new BadRequestException("codes do not match");
    }

    public String checkEmailRegistration(User user, String code) {
        log.info("checkEmailRegistration code: " + code);
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("Invalid code");
        }
        //give code from DB
        MessageDtoResponse messageFromDb = messageService
                .getLastMessageByUserIdAndMessageType(user.getId(), MessageType.EMAIL_CHECK_CODE);
        String codeFromDb = messageFromDb.getAccessCode();
        if (codeFromDb.equals(code)) {
            //set user role
            user.setEmailVerified(true);
            userRepository.saveAndFlush(user);
            return "OK";
        }
        throw new BadRequestException("codes do not match");
    }

    private void checkFields(UserRegistrationDto dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid user");
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new BadRequestException("Invalid user's name");
        }
        if (dto.getName().length() > MAX_LENGTH_NAME) {
            throw new BadRequestException("Too long user's name");
        }
        if (StringUtils.isBlank(dto.getSurname())) {
            throw new BadRequestException("Invalid user's surname");
        }
        if (dto.getSurname().length() > MAX_LENGTH_NAME) {
            throw new BadRequestException("Too long user's surname");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            throw new BadRequestException("Invalid user's password");
        }
        if (dto.getPassword().length() > MAX_LENGTH_PASSWORD) {
            throw new BadRequestException("Too long user's password");
        }
        if (dto.getPassword().length() < MIN_LENGTH_PASSWORD) {
            throw new BadRequestException("Too short user's password");
        }
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        if (StringUtils.isBlank(dto.getPhone())) {
            throw new BadRequestException("Invalid user's phone");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new BadRequestException("Exist user with this phone");
        }
        if (StringUtils.isBlank(dto.getEmail())) {
            throw new BadRequestException("Invalid user's email");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Exist user with this email");
        }
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
