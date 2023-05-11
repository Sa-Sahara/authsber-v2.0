package com.alevya.authsber.service;

import com.alevya.authsber.dto.PermissionDtoRequest;
import com.alevya.authsber.dto.PermissionDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Permission;
import com.alevya.authsber.repository.PermissionRepository;
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
public class PermissionService{

    private static final Log log = LogFactory.getLog(PermissionService.class);
    private static final int MAX_LENGTH = 255;

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionDtoResponse createPermission(PermissionDtoRequest permissionDtoRequest) {
        log.info("createPermission permissionDtoRequest: " + permissionDtoRequest);
        //checking fields
        if (permissionDtoRequest == null) {
            throw new BadRequestException("Invalid permission");
        }
        if (StringUtils.isBlank(permissionDtoRequest.getName())) {
            throw new BadRequestException("Invalid permission's name");
        }
        if (permissionDtoRequest.getName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long permission's name");
        }
        if (permissionRepository.existsByName(permissionDtoRequest.getName())) {
            throw new BadRequestException("Exist permission with this name");
        }
        if (StringUtils.isBlank(permissionDtoRequest.getDescription())) {
            throw new BadRequestException("Invalid permission's description");
        }
        if (permissionDtoRequest.getDescription().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long permission's description");
        }
        //save in DB
        return mapToPermissionDto(permissionRepository.save(mapToPermission(permissionDtoRequest)));
    }

    public PermissionDtoResponse getPermissionById(Long id) {
        log.info("getPermissionById id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToPermissionDto(permissionRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Permission not found!")));
    }

    public PermissionDtoResponse getPermissionByName(String name) {
        log.info("getPermissionByName name: " + name);
        if (name == null) {
            throw new BadRequestException("Invalid name");
        }
        Permission permission = permissionRepository.findByName(name);
        if (permission == null) {
            throw new NotFoundException("Permission not found!");
        }
        return mapToPermissionDto(permission);
    }

    public List<PermissionDtoResponse> getAllPermissions() {
        log.info("getAllPermissions");
        return permissionRepository.findAll().stream()
                .map(this::mapToPermissionDto)
                .collect(Collectors.toList());
    }

    public Page<PermissionDtoResponse> findAllPermissionsPageable(Pageable pageable) {
        log.info("findAllPermissionsPageable pageable:"  + pageable);
        Page<Permission> page = permissionRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToPermissionDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public PermissionDtoResponse updatePermission(Long id, PermissionDtoRequest permissionDtoRequest) {
        log.info("updatePermission id: " + id + " permissionDtoRequest: " + permissionDtoRequest);
        if (permissionDtoRequest == null) {
            throw new BadRequestException("Invalid permission");
        }
        Permission oldPermission = permissionRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Permission not found!"));
        //name
        final String name = permissionDtoRequest.getName();
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Invalid permission's name");
        } else if (name.length() > MAX_LENGTH) {
            throw new BadRequestException("Too long permission's name");
        } else {
            oldPermission.setName(name);
        }
        if (permissionRepository.existsByName(permissionDtoRequest.getName())) {
            throw new BadRequestException("Exist permission with this name");
        }
        //description
        final String description = permissionDtoRequest.getDescription();
        if (StringUtils.isBlank(description)) {
            throw new BadRequestException("Invalid permission's description");
        } else if (description.length() > MAX_LENGTH) {
            throw new BadRequestException("Too long permission's description");
        } else {
            oldPermission.setDescription(description);
        }
        return mapToPermissionDto(permissionRepository.saveAndFlush(oldPermission));
    }

    public void deletePermission(Long id) {
        log.info("deletePermission id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        permissionRepository.deleteById(id);
    }

    public PermissionDtoResponse mapToPermissionDto(Permission permission) {
        PermissionDtoResponse permissionDto = PermissionDtoResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
        return permissionDto;
    }

    public Permission mapToPermission(PermissionDtoRequest permissionDtoRequest) {
        Permission permission = new Permission();
        permission.setName(permissionDtoRequest.getName());
        permission.setDescription(permissionDtoRequest.getDescription());
        return permission;
    }
}
