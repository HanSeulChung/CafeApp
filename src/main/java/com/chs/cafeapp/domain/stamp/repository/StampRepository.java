package com.chs.cafeapp.domain.stamp.repository;

import com.chs.cafeapp.domain.stamp.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {

}
