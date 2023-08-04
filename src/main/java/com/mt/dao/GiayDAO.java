package com.mt.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mt.entity.Giay;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GiayDAO extends JpaRepository<Giay, Integer> {
	@Query("SELECT g FROM Giay g WHERE g.tenGiay like %?1%")
    List<Giay> findByName(String tenGiay);

    @Query("SELECT g FROM Giay g WHERE g.hangGiay.maHang =?1")
    List<Giay> findByMaHang(int maHang);

    @Query("SELECT g FROM Giay g WHERE g.loaiGiay.maLoaiGiay =?1")
    List<Giay> findByMaLoai(int maLoai);

    @Modifying
    @Query(value = "DELETE FROM Giays WHERE ma_hang=?1",nativeQuery = true)
    void deleteByMaHang(int maHang);

    @Modifying
    @Query(value = "DELETE FROM Giays WHERE ma_loai_giay=?1",nativeQuery = true)
    void deleteByMaLoai(int maLoai);
}
