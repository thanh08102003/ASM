package com.mt.commons;

import java.util.List;
import java.util.Optional;

public class Commons {
    public static int getTotalPage(int soSanPham, int tongSoSanPham) {
        int tongSoTrang = 1;
        float tempFloat = (float) tongSoSanPham / soSanPham;
        int tempInt = (int) tempFloat;
        if (tempFloat - tempInt > 0) {
            tongSoTrang = tempInt + 1;
        } else {
            tongSoTrang = tempInt;
        }
        return tongSoTrang;
    }

    public static boolean checkNumber(String id) {
        try {
            Integer.parseInt(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean listIsNullOrEmpty(List<Optional<String>> list) {
        return (list.isEmpty()|| list==null)?true:false;
    }
}
