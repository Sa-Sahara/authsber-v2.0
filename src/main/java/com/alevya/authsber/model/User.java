package com.alevya.authsber.model;

import com.alevya.authsber.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "t_user")
//do soft delete
@SQLDelete(sql = "update t_user set deleted = true where id =?")
@Where(clause = "deleted = false")
@Getter
@Setter
public final class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="user_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    private String patronymic;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Flag verified email: true - verified, false - not verified.
     */
    @Column(name = "email_verified", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailVerified = false;

    @Column(nullable = false, unique = true)
    private String phone;

    /**
     * Flag verified phone: true - verified, false - not verified.
     */
    @Column(name = "phone_verified", nullable = false, columnDefinition = "boolean default false")
    private Boolean phoneVerified = false;

    private String address;

    private LocalDate birthday;

    /**
     * Flag blocked user: true - blocked, false - not blocked.
     */
    @Column(name = "is_blocked", nullable = false, columnDefinition = "boolean default false")
    private Boolean blocked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Singular
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

    /**
     * Soft delete: true - deleted, false - not deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "worker")
    @Singular
    private Set<WorkTime> workTimes = new HashSet<>();

    public User() {
    }

    @Builder
    public User(String name,
                String surname,
                String patronymic,
                String password,
                String email,
                String phone,
                LocalDate birthday,
                String address) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.address = address;
    }

    @Builder
    public User(Long id,
                String name,
                String surname,
                String patronymic,
                String password,
                String email,
                Boolean emailVerified,
                String phone,
                Boolean phoneVerified,
                String address,
                LocalDate birthday,
                Boolean blocked,
                Set<Role> roles,
                Set<WorkTime> workTimes) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.password = password;
        this.email = email;
        this.emailVerified = emailVerified;
        this.phone = phone;
        this.phoneVerified = phoneVerified;
        this.address = address;
        this.birthday = birthday;
        this.blocked = blocked;
        this.roles = roles;
        this.workTimes = workTimes;
    }

    @Builder
    public User(Long id,
                String name,
                String surname,
                String patronymic,
                String password,
                String email,
                Boolean emailVerified,
                String phone,
                Boolean phoneVerified,
                String address,
                LocalDate birthday,
                Boolean blocked,
                Set<Role> roles,
                Date createDate,
                Date modifyDate,
                Boolean deleted,
                Set<WorkTime> workTimes) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.password = password;
        this.email = email;
        this.emailVerified = emailVerified;
        this.phone = phone;
        this.phoneVerified = phoneVerified;
        this.address = address;
        this.birthday = birthday;
        this.blocked = blocked;
        this.roles = roles;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.deleted = deleted;
        this.workTimes = workTimes;
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean addRoles(Set<Role> newRoles) {
        return roles.addAll(newRoles);
    }

    public boolean removeRole(Role role) {
        return roles.remove(role);
    }
    public void removeAllRoles() {
        roles.clear();
    }

    public boolean addWorkTime(WorkTime workTime) {
        return workTimes.add(workTime);
    }

    public boolean removeWorkTime(WorkTime workTime) {
        return workTimes.remove(workTime);
    }

    public void setEmail(String email) {
        if (this.email == null || this.email.equals(email)) {
            this.email = email;
        } else throw new BadRequestException("email cannot be changed");
    }

    public void setPhone(String phone) {
        if (!Objects.equals(this.phone, phone)) {
            setPhoneVerified(false);
        }
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", phone='" + phone + '\'' +
                ", phoneVerified=" + phoneVerified +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                ", blocked=" + blocked +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", deleted=" + deleted +
                '}';
    }
}
