package com.mt.dao;

import com.mt.entity.Giay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mt.entity.ChiTietDonHang;
import com.mt.entity.ChiTietDonHangPK;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderDetailDAO extends JpaRepository<ChiTietDonHang, ChiTietDonHangPK> {
    @Modifying
    @Query(value = "DELETE FROM chitietdonhangs WHERE ma_giay=?1",nativeQuery = true)
    void deleteByMaGiay(int maGiay);
}
