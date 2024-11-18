package com.camal.microservice_auth.controller.dto;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private long id;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer orden;
    private Long nivel;
    private long padre;
    private boolean menuDisponible;
    private Set<MenuDTO> submenus = new HashSet<>();
}
