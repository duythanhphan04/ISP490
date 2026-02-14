package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.group.group_id = :groupId")
    List<User> findAllUsersByGroupId(@Param("groupId") String groupId);
    @Query("SELECT ug.group FROM UserGroup ug WHERE ug.user.user_id = :userId")
    List<Group> findAllGroupsByUserId(@Param("userId") String userId);
    @Query("SELECT ug FROM UserGroup ug WHERE ug.user.user_id = :userId AND ug.group.group_id = :groupId")
    Optional<UserGroup> findByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);

    boolean existsByUserAndGroup(User user, Group group);
}
