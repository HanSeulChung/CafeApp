package com.chs.cafeapp.user.repository;

import com.chs.cafeapp.user.entity.DeleteUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteUserRepository extends JpaRepository<DeleteUser, Long> {

}
