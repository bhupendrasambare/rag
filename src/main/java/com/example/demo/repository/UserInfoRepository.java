/**
 * author @bhupendrasambare
 * Date   :24/07/26
 * Time   :8:01 am
 * Project:rag
 **/
package com.example.demo.repository;

import com.example.demo.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    Optional<UserInfo> findByEmail(String email);
}
