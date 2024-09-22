package com.camal.microservice_auth.persistence.especification;
import com.camal.microservice_auth.persistence.entity.UserEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class UserSpecification {

    public static Specification<UserEntity> getUsers(Long id, String username, Boolean isEnabled) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (username != null) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }
            if (isEnabled != null) {
                predicates.add(criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
