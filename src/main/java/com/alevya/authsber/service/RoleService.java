package com.alevya.authsber.service;

import com.alevya.authsber.dto.RoleDtoRequest;
import com.alevya.authsber.dto.RoleDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Role;
import com.alevya.authsber.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private static final Log log = LogFactory.getLog(RoleService.class);
    private static final int MAX_LENGTH = 255;

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleDtoResponse createRole(RoleDtoRequest roleDtoRequest) {
        log.info("createRole roleDtoRequest: " + roleDtoRequest);
        //checking fields
        if (roleDtoRequest == null) {
            throw new BadRequestException("Invalid role");
        }
        if (roleDtoRequest.getLevel() == null) {
            throw new BadRequestException("Invalid level");
        }
        if (StringUtils.isBlank(roleDtoRequest.getName())) {
            throw new BadRequestException("Invalid role's name");
        }
        if (roleDtoRequest.getName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long role's name");
        }
        if (roleRepository.existsByName(roleDtoRequest.getName())) {
            throw new BadRequestException("Exist role with this name");
        }
        if (roleDtoRequest.getDescription().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long role's description");
        }
        //save in DB
        return mapToRoleDto(roleRepository.save(mapToRole(roleDtoRequest)));
    }

    public RoleDtoResponse getRoleById(Long id) {
        log.info("getRoleById id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToRoleDto(roleRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Role not found!")));
    }

    public RoleDtoResponse getRoleByName(String name) {
        log.info("getRoleByName name: " + name);
        if (name == null) {
            throw new BadRequestException("Invalid name");
        }
        Role role = roleRepository.findByName(name);
        if (role == null) {
            throw new NotFoundException("Role not found!");
        }
        return mapToRoleDto(role);
    }

    public List<RoleDtoResponse> getAllRoles() {
        log.info("getAllRoles");
        return roleRepository.findAll().stream()
                .map(this::mapToRoleDto)
                .collect(Collectors.toList());
    }

    public Page<RoleDtoResponse> findAllRolesPageable(Pageable pageable) {
        log.info("findAllRolesPageable pageable:" + pageable);
        Page<Role> page = roleRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToRoleDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public RoleDtoResponse updateRole(Long id, RoleDtoRequest roleDtoRequest) {
        log.info("updateRole id: " + id + " roleDtoRequest: " + roleDtoRequest);
        if (roleDtoRequest == null) {
            throw new BadRequestException("Invalid role");
        }
        Role oldRole = roleRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Role not found!"));
        //name
        final String name = roleDtoRequest.getName();
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Invalid role's name");
        } else if (name.length() > MAX_LENGTH) {
            throw new BadRequestException("Too long role's name");
        } else {
            oldRole.setName(name);
        }
        if (roleRepository.existsByName(roleDtoRequest.getName())) {
            throw new BadRequestException("Exist role with this name");
        }
        //level
        final Integer level = roleDtoRequest.getLevel();
        if (level != null) {
            oldRole.setLevel(level);
        }
        //description
        final String description = roleDtoRequest.getDescription();
        if (StringUtils.isBlank(description)) {
            throw new BadRequestException("Invalid role's description");
        } else if (description.length() > MAX_LENGTH) {
            throw new BadRequestException("Too long role's description");
        } else {
            oldRole.setDescription(description);
        }
        return mapToRoleDto(roleRepository.saveAndFlush(oldRole));
    }

    public void deleteRole(Long id) {
        log.info("deleteRole id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        roleRepository.deleteById(id);
    }

    public RoleDtoResponse mapToRoleDto(Role role) {
        RoleDtoResponse roleDto = RoleDtoResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .level(role.getLevel())
                .build();
        return roleDto;
    }

    public Role mapToRole(RoleDtoRequest roleDtoRequest) {
        Role role = new Role();
        role.setName(roleDtoRequest.getName());
        role.setDescription(roleDtoRequest.getDescription());
        role.setLevel(roleDtoRequest.getLevel());
        return role;
    }

    public Role getRoleGuest() {
        Role roleUserFromDB = roleRepository.findByName("GUEST");
        if (roleUserFromDB == null) {
            Role role = new Role();
            role.setName("GUEST");
            role.setDescription("Role for not verified user");
            role.setLevel(30);
            return roleRepository.save(role);
        }
        return roleUserFromDB;
    }

    public Role getRoleUser() {
        Role roleUserFromDB = roleRepository.findByName("VERIFIED USER");
        if (roleUserFromDB == null) {
            Role roleUser = new Role();
            roleUser.setName("VERIFIED USER");
            roleUser.setDescription("Role for simple user");
            roleUser.setLevel(20);
            return roleRepository.save(roleUser);
        }
        return roleUserFromDB;
    }
}
