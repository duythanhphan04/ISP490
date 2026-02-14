package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.enums.GroupStatus;
import com.devteria.identity_service.enums.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, String> {

    List<Group> findByStatus(GroupStatus status);

    List<Group> findByGroupType(GroupType groupType);
}
