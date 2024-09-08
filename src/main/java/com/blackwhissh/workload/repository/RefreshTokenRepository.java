package com.blackwhissh.workload.repository;


import com.blackwhissh.workload.entity.RefreshToken;
import com.blackwhissh.workload.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByToken(String token);

    int deleteByUser(User user);
}
