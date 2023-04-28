package com.alevya.authsber.model;

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
@Table(name = "user")
//do soft delete
@SQLDelete(sql = "update user set deleted = true where id =?")
@Where(clause = "deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class User {

    @Id
    @SequenceGenerator(name = "default_generator",
            sequenceName = "user_seq",
            allocationSize = 10)
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
            name = "user_role",
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

    public User(
            Long id,
            String name,
            String password,
            String email,
            String phone,
            Set<WorkTime> workTimes) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.workTimes = workTimes;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        User user = (User) o;

        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(surname, user.surname)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(phone, user.phone)) return false;
        return Objects.equals(createDate, user.createDate);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
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
