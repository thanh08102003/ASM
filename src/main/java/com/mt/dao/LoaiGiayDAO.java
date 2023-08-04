package com.mt.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mt.entity.LoaiGiay;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LoaiGiayDAO extends JpaRepository<LoaiGiay, Integer> {
    @Query("SELECT lg FROM LoaiGiay lg WHERE lg.tenLoai like %?1%")
    List<LoaiGiay> findByName(String tenLoai);

}
