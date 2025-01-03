package com.jmarqb.ms.auth.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_name",columnList = "name"),
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "is_default_role")
    private boolean isDefaultRole;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}
