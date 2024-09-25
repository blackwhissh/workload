package com.blackwhissh.workload.security.jwt;

import com.blackwhissh.workload.entity.RefreshToken;
import com.blackwhissh.workload.exceptions.list.RefreshTokenExpiredException;
import com.blackwhissh.workload.repository.RefreshTokenRepository;
import com.blackwhissh.workload.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findRefreshTokenByToken(token);
    }

    public RefreshToken createRefreshToken(String workId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByEmployee_WorkId(workId).orElseThrow(EntityNotFoundException::new));
        String refreshTokenDurationMs = "86400000";
        refreshToken.setExpiryDate(Instant.now().plusMillis(Long.parseLong(refreshTokenDurationMs)));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException();
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Integer userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
