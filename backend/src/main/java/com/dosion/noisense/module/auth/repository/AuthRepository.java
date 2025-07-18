package com.dosion.noisense.module.auth.repository;

import com.dosion.noisense.module.auth.entity.Auth;
import com.dosion.noisense.module.user.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth,Long> {

  boolean existsByUser(Users user);

  Optional<Auth> findByRefreshToken(String refreshToken);

  Optional<Auth> findByUser(Users user);
}
