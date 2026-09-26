package com.ecommerce.backend.entity;


import com.ecommerce.backend.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(
                        name = "idx_role_name",
                        columnList = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "name",
            nullable = false,
            unique = true,
            length = 50
    )
    private RoleName name;

    @Builder.Default
    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private Set<User> users = new LinkedHashSet<>();
}
