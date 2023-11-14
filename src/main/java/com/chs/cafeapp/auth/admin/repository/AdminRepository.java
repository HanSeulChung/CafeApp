package com.chs.cafeapp.auth.admin.repository;

import com.chs.cafeapp.auth.admin.entity.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
  Optional<Admin> findByLoginId(String loginId);
}
