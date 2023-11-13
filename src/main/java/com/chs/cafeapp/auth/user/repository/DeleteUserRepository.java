package com.chs.cafeapp.auth.user.repository;

import com.chs.cafeapp.auth.user.entity.DeleteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteUserRepository extends JpaRepository<DeleteUser, Long> {

}
