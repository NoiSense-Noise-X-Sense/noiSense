package com.dosion.noisense.module.user.repository;


import com.dosion.noisense.module.user.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

  Optional<Users> findByUserNm(String userNm);

  Optional<Users> findByUserNmOrEmail(String userNm, String email);

}
