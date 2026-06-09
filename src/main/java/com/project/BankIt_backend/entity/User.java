package com.project.BankIt_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "USERS")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    private Long userId;

    @Column(name = "FULLNAME", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "PHONENUMBER", unique = true)
    private String phoneNumber;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "PASSWORDHASH", nullable = false)
    private String password;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USERID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Take the set of roles Hibernate already fetched from the DB
        return roles.stream()
                // Map each Role object into a SimpleGrantedAuthority using its name
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                // 3. Collect them all into a List that Spring Security understands
                .collect(Collectors.toList());
    }
}
