package com.mt.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mt.entity.HangGiay;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HangGiayDAO extends JpaRepository<HangGiay, Integer> {
    @Query("SELECT hg FROM HangGiay hg WHERE hg.tenHang like %?1%")
    List<HangGiay> findByName(String tenHang);

}
