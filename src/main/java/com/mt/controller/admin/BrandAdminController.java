package com.mt.controller.admin;

import com.mt.commons.Commons;
import com.mt.dao.*;
import com.mt.entity.Giay;
import com.mt.entity.HangGiay;
import com.mt.entity.KhachHang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/brand")
public class BrandAdminController {
    @Autowired
    private HangGiayDAO hangGiayDAO;

    @Autowired
    private GiayDAO giayDAO;

    @Autowired
    private OrderDetailDAO orderDetailDAO;

    @Autowired
    SessionDAO session;

    @GetMapping("")
    public String index(Model model, @RequestParam Optional<String> txtSearch,
                        @RequestParam("soTrang") Optional<String> soTrangString,
                        @RequestParam("message") Optional<Boolean> message ) {
        List<HangGiay> list=new ArrayList<>();
        KhachHang user = (KhachHang) session.get("user");
        if(!user.isQuyen()) {
            String error="Khong du quyen truy cap!";
            return "redirect:/login?error="+error;
        }
        model.addAttribute("sessionAdmin", user);
        if(txtSearch.isPresent()) {
            list = hangGiayDAO.findByName(txtSearch.get());
        }else {
            int soTrang = soTrangString.isEmpty() ? 1 : Integer.parseInt(soTrangString.get());
            model.addAttribute("soTrangHienTai", soTrang);
            int soSanPham = 6;
            model.addAttribute("soSanPhamHienTai", soSanPham);
            int tongSoTrang = Commons.getTotalPage(soSanPham, hangGiayDAO.findAll().size());
            model.addAttribute("tongSoTrang", tongSoTrang);
            // Trang số "soTrang-1", số sản phẩm hiển thị "soSanPham"
            Pageable pageable = PageRequest.of(soTrang - 1, soSanPham);
            Page<HangGiay> pageHangGiay = hangGiayDAO.findAll(pageable);
            list = pageHangGiay.getContent();
        }
        model.addAttribute("listHangGiay", list);
        KhachHang khachHang = (KhachHang) session.get("userAdmin");
        if (khachHang != null) {
            model.addAttribute("sessionUsername", khachHang.getTaiKhoan());
        }
        if(message.isPresent()) {
            if(message.get()) {
                model.addAttribute("message","Xóa thành công!");
            }else {
                model.addAttribute("message","Xóa thất thất bại!");
            }
        }
        return "admin/brand/index";
    }

    @GetMapping("update")
    public String showUpdate(Model model, @RequestParam("maHangGiay") Optional<String> maHangGiayString,
                             @RequestParam("message") Optional<Boolean> message) {
        KhachHang user = (KhachHang) session.get("user");
        if(!user.isQuyen()) {
            String error="Khong du quyen truy cap!";
            return "redirect:/login?error="+error;
        }
        model.addAttribute("sessionAdmin", user);
        int maHangGiay = maHangGiayString.isEmpty() ? 1 : Integer.parseInt(maHangGiayString.get());
        HangGiay hangGiay = hangGiayDAO.findById(maHangGiay).get();
        model.addAttribute("hangGiayItem", hangGiay);
        if(message.isPresent()) {
            if(message.get()) {
                model.addAttribute("message","Lưu thành công!");
            }else {
                model.addAttribute("message","Lưu thất bại!");
            }
        }
        return "admin/brand/update";
    }

    @PostMapping("update")
    public String update(Model model,@RequestParam("maHangGiay") Optional<String> maHangGiayString,
                         @RequestParam("tenHangGiay") Optional<String> tenHangGiay,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean message=true;
        List<Optional<String>> list = new ArrayList<>();
        list.add(maHangGiayString);
        list.add(tenHangGiay);
        if (!Commons.listIsNullOrEmpty(list)) {
            HangGiay hangGiay = hangGiayDAO.findById(Integer.parseInt(maHangGiayString.get())).get();
            hangGiay.setTenHang(tenHangGiay.get());
            hangGiayDAO.save(hangGiay);
            message=true;
        }else {
            message=false;
        }
        return "redirect:/admin/brand/update?maHangGiay="+Integer.parseInt(maHangGiayString.get())+"&message="+message;
    }
    @GetMapping("insert")
    public String showInsert(Model model,
                             @RequestParam("message") Optional<Boolean> message) {
        KhachHang user = (KhachHang) session.get("user");
        if(!user.isQuyen()) {
            String error="Khong du quyen truy cap!";
            return "redirect:/login?error="+error;
        }
        model.addAttribute("sessionAdmin", user);
        if(message.isPresent()) {
            if(message.get()) {
                model.addAttribute("message","Thêm thành công!");
            }else {
                model.addAttribute("message","Thêm thất thất bại!");
            }
        }
        return "admin/brand/insert";
    }
    @GetMapping("delete")
    public String delete(@RequestParam("maHangGiay") Optional<String> maHangGiayString) {
        int maHangGiay = maHangGiayString.isEmpty() ? -1 : Integer.parseInt(maHangGiayString.get());
        if(maHangGiay!=-1) {
            //Vì khóa ngoại nên khi muốn xóa Giày phải xóa tất cả những CHI TIẾT HÓA ĐƠN có Giày đó
            //Tìm tất cả các giày có mã hãng cần xóa
            List<Giay> listGiayDelete=giayDAO.findByMaHang(maHangGiay);
            for(Giay giay:listGiayDelete){{
                orderDetailDAO.deleteByMaGiay(giay.getMaGiay());
            }}
            //Vì khóa ngoại nên khi muốn xóa mã hãng phải xóa tất cả những giày thuộc hãng đó
            giayDAO.deleteByMaHang(maHangGiay);
            hangGiayDAO.delete(hangGiayDAO.findById(maHangGiay).get());
            return "redirect:/admin/brand?message="+true;
        }else {
            return "redirect:/admin/brand?message="+false;
        }
    }

    @PostMapping("insert")
    public String insert(Model model,
                         @RequestParam("tenHangGiay") Optional<String> tenHangGiay,
                         @RequestParam("maHangGiay") Optional<String> maHangGiayString,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean message=true;
        List<Optional<String>> list = new ArrayList<>();
        list.add(tenHangGiay);
        if (!Commons.listIsNullOrEmpty(list)) {
            HangGiay hangGiay = new HangGiay();
            hangGiay.setTenHang(tenHangGiay.get());
            hangGiayDAO.save(hangGiay);
            message=true;
        }else {
            message=false;
        }
        return "redirect:/admin/brand/insert?message="+message;
    }
    @GetMapping("/logout")
    public String logout(@RequestParam Optional<String> urlReturn) {
        session.clear();
        return urlReturn.isPresent()?"redirect:/admin/" +urlReturn.get():"redirect:/admin";
    }

}