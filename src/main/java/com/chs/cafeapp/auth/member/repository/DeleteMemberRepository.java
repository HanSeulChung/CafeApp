package com.chs.cafeapp.auth.member.repository;

import com.chs.cafeapp.auth.member.entity.DeleteMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteMemberRepository extends JpaRepository<DeleteMember, Long> {

}
