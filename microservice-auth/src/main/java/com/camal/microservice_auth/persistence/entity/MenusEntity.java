package com.camal.microservice_auth.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menus")
public class MenusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "menu_name")
    private String menuName;
    @Column(name = "menu_url")
    private String menuUrl;
    private String icon;
    private Integer orden;
    private Long nivel;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "menus")
    private Set<UserEntity> usuarios = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "padre_id")
    @JsonBackReference
    private MenusEntity padre;
    @OneToMany(mappedBy = "padre", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private Set<MenusEntity> submenus = new HashSet<>();
}
