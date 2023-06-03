package com.alevya.authsber.service;

import com.alevya.authsber.dto.UserGeneralInfoDtoRequest;
import com.alevya.authsber.dto.UserGeneralInfoDtoResponse;
import com.alevya.authsber.dto.UserWithSettingsDtoRequest;
import com.alevya.authsber.dto.UserWithSettingsDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Role;
import com.alevya.authsber.model.User;
import com.alevya.authsber.repository.RoleRepository;
import com.alevya.authsber.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final int MAX_LENGTH_NAME = 150;
    private static final int MIN_LENGTH_PASSWORD = 6;
    private static final int MAX_LENGTH_PASSWORD = 100;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserGeneralInfoDtoResponse createMyUser(UserGeneralInfoDtoRequest dto) {
        //checking fields
        checkIfExists(dto);
        checkDtoFields(dto);

        //encode password
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        User user = mapGeneralInfoDtoToUser(dto);

        //set default user role
        user.addRole(roleService.getRoleGuest());

        //set default fields
        user.setBlocked(false);
        user.setDeleted(false);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setCreateDate(new Date());

        //save in DB
        return mapToUserGeneralInfoDto(userRepository.save(user));
    }

    public UserWithSettingsDtoResponse createUserWithSettings(UserWithSettingsDtoRequest dto) {
        //checking fields
        checkIfExists(dto);
        checkDtoFields(dto);

        //encode password
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        //checking roles
        if (dto.getRoleNames().isEmpty()) {
            dto.addRole(roleService.getRoleGuest().getName());
        }
        dto.setCreateDate(new Date());

        //save in DB
        return mapToUserWithSettingsDto(userRepository.save(mapWithSettingsDtoToUser(dto)));
    }

    public UserWithSettingsDtoResponse getUserById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToUserWithSettingsDto(userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User not found!")));
    }

    public User getUserByPhoneEmail(String phoneEmail) {
        if (StringUtils.isBlank(phoneEmail)) {
            throw new BadRequestException("Invalid phone/email");
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

    public List<UserWithSettingsDtoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserWithSettingsDto)
                .collect(Collectors.toList());
    }

    public Page<UserWithSettingsDtoResponse> findAllUsersPageable(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToUserWithSettingsDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public UserGeneralInfoDtoResponse updateUserNoSettings(
            Long id, UserGeneralInfoDtoRequest dto) {
        checkDtoFields(dto);

        User oldUser = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User not found!"));

        oldUser.setName(dto.getName());
        oldUser.setPatronymic(dto.getPatronymic());
        oldUser.setSurname(dto.getSurname());
        oldUser.setAddress(dto.getAddress());
        oldUser.setPassword(
                //  passwordEncoder.encode(
                dto.getPassword()
                //   )
        );
        oldUser.setPhone(dto.getPhone());
        oldUser.setModifyDate(new Date());

        return mapToUserWithSettingsDto(userRepository.saveAndFlush(oldUser));
    }

    public UserWithSettingsDtoResponse updateUserWithSettings(
            Long id, UserWithSettingsDtoRequest dto) {
        updateUserNoSettings(id, dto);
        User oldUser = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User not found!"));
        //blocked
        final Boolean blocked = dto.getBlocked();
        if (blocked != null) {
            oldUser.setBlocked(blocked);
        }
        //roles
        if (dto.getRoleNames().isEmpty()) {
            oldUser.addRole(roleService.getRoleUser());
        } else {
            Set<Role> newRoles = dto.getRoleNames().stream()
                    .map(roleRepository::findByName)
                    .collect(Collectors.toSet());
            oldUser.removeAllRoles();
            oldUser.addRoles(newRoles);
        }
        //ModifyDate
        oldUser.setModifyDate(new Date());

        return mapToUserWithSettingsDto(userRepository.saveAndFlush(oldUser));
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        userRepository.deleteById(id);
    }

    public UserWithSettingsDtoResponse mapToUserWithSettingsDto(User user) {
        UserWithSettingsDtoResponse dto = UserWithSettingsDtoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .patronymic(user.getPatronymic())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthday(user.getBirthday())
                .address(user.getAddress())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .blocked(user.getBlocked())
                .roleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createDate(user.getCreateDate())
                .modifyDate(user.getModifyDate())
                .deleted(user.getDeleted())
                .build();
        return dto;
    }

    public UserGeneralInfoDtoResponse mapToUserGeneralInfoDto(User user) {
        UserGeneralInfoDtoResponse dto = UserGeneralInfoDtoResponse.builder()
                .id(user.getId())
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

    public User mapWithSettingsDtoToUser(UserWithSettingsDtoRequest dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPatronymic(dto.getPatronymic());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEmailVerified(dto.getEmailVerified());
        user.setPhone(dto.getPhone());
        user.setPhoneVerified(dto.getPhoneVerified());
        user.setAddress(dto.getAddress());
        user.setBirthday(dto.getBirthday());
        user.setBlocked(dto.getBlocked());
        user.setRoles(dto.getRoleNames().stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet()));
        user.setCreateDate(dto.getCreateDate());
        user.setModifyDate(dto.getModifyDate());
        user.setDeleted(dto.getDeleted());
        return user;
    }

    public User mapGeneralInfoDtoToUser(UserGeneralInfoDtoRequest dto) {
        User user = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .patronymic(dto.getPatronymic())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birthday(dto.getBirthday())
                .address(dto.getAddress())
                .build();
        return user;
    }

    private void checkIfExists(UserGeneralInfoDtoRequest dto) {
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new BadRequestException("Exist user with this phone");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Exist user with this email");
        }
    }

    private void checkDtoFields(UserGeneralInfoDtoRequest dto) {
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
        if (StringUtils.isBlank(dto.getPhone())) {
            throw new BadRequestException("Invalid user's phone");
        }
        if (StringUtils.isBlank(dto.getEmail())) {
            throw new BadRequestException("Invalid user's email");
        }
    }
}
