package com.ganesh.creatorflow.specification;

import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.enums.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {

    public static Specification<Project> hasStatus(ProjectStatus status) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);

    }
    public static Specification<Project> hasKeyword(String keyword) {

        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };

    }
}