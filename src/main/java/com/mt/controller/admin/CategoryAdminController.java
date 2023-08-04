package com.mt.controller.admin;

import com.mt.commons.Commons;
import com.mt.dao.*;
import com.mt.entity.Giay;
import com.mt.entity.KhachHang;
import com.mt.entity.LoaiGiay;
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
@RequestMapping("admin/category")
public class CategoryAdminController {
    @Autowired
    private LoaiGiayDAO loaiGiayDAO;

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
        List<LoaiGiay> list=new ArrayList<>();
        KhachHang user = (KhachHang) session.get("user");
        if(!user.isQuyen()) {
            String error="Khong du quyen truy cap!";
            return "redirect:/login?error="+error;
        }
        model.addAttribute("sessionAdmin", user);
        if(txtSearch.isPresent()) {
            list = loaiGiayDAO.findByName(txtSearch.get());
        }else {
            int soTrang = soTrangString.isEmpty() ? 1 : Integer.parseInt(soTrangString.get());
            model.addAttribute("soTrangHienTai", soTrang);
            int soSanPham = 6;
            model.addAttribute("soSanPhamHienTai", soSanPham);
            int tongSoTrang = Commons.getTotalPage(soSanPham, loaiGiayDAO.findAll().size());
            model.addAttribute("tongSoTrang", tongSoTrang);
            // Trang số "soTrang-1", số sản phẩm hiển thị "soSanPham"
            Pageable pageable = PageRequest.of(soTrang - 1, soSanPham);
            Page<LoaiGiay> pageLoaiGiay = loaiGiayDAO.findAll(pageable);
            list = pageLoaiGiay.getContent();
        }
        model.addAttribute("listLoaiGiay", list);
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
        return "admin/category/index";
    }

    @GetMapping("update")
    public String showUpdate(Model model, @RequestParam("maLoaiGiay") Optional<String> maLoaiGiayString,
                             @RequestParam("message") Optional<Boolean> message) {
        KhachHang user = (KhachHang) session.get("user");
        if(!user.isQuyen()) {
            String error="Khong du quyen truy cap!";
            return "redirect:/login?error="+error;
        }
        model.addAttribute("sessionAdmin", user);
        int maLoaiGiay = maLoaiGiayString.isEmpty() ? 1 : Integer.parseInt(maLoaiGiayString.get());
        LoaiGiay loaiGiay = loaiGiayDAO.findById(maLoaiGiay).get();
        model.addAttribute("loaiGiayItem", loaiGiay);
        if(message.isPresent()) {
            if(message.get()) {
                model.addAttribute("message","Lưu thành công!");
            }else {
                model.addAttribute("message","Lưu thất bại!");
            }
        }
        return "admin/category/update";
    }

    @PostMapping("update")
    public String update(Model model,@RequestParam("maLoaiGiay") Optional<String> maLoaiGiayString,
                         @RequestParam("tenLoaiGiay") Optional<String> tenLoaiGiay,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean message=true;
        List<Optional<String>> list = new ArrayList<>();
        list.add(maLoaiGiayString);
        list.add(tenLoaiGiay);
        if (!Commons.listIsNullOrEmpty(list)) {
            LoaiGiay loaiGiay = loaiGiayDAO.findById(Integer.parseInt(maLoaiGiayString.get())).get();
            loaiGiay.setTenLoai(tenLoaiGiay.get());
            loaiGiayDAO.save(loaiGiay);
            message=true;
        }else {
            message=false;
        }
        return "redirect:/admin/category/update?maLoaiGiay="+Integer.parseInt(maLoaiGiayString.get())+"&message="+message;
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
        return "admin/category/insert";
    }
    @GetMapping("delete")
    public String delete(@RequestParam("maLoaiGiay") Optional<String> maLoaiGiayString) {
        int maLoaiGiay = maLoaiGiayString.isEmpty() ? -1 : Integer.parseInt(maLoaiGiayString.get());
        if(maLoaiGiay!=-1) {
            //Vì khóa ngoại nên khi muốn xóa Giày phải xóa tất cả những CHI TIẾT HÓA ĐƠN có Giày đó
            //Tìm tất cả các giày có mã loại cần xóa
            List<Giay> listGiayDelete=giayDAO.findByMaLoai(maLoaiGiay);
            for(Giay giay:listGiayDelete){{
                orderDetailDAO.deleteByMaGiay(giay.getMaGiay());
            }}
            //Vì khóa ngoại nên khi muốn xóa mã loại phải xóa tất cả những giày thuộc loại đó
            giayDAO.deleteByMaLoai(maLoaiGiay);
            loaiGiayDAO.delete(loaiGiayDAO.findById(maLoaiGiay).get());
            return "redirect:/admin/category?message="+true;
        }else {
            return "redirect:/admin/category?message="+false;
        }
    }

    @PostMapping("insert")
    public String insert(Model model,
                         @RequestParam("tenLoaiGiay") Optional<String> tenLoaiGiay,
                         @RequestParam("maLoaiGiay") Optional<String> maLoaiGiayString,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean message=true;
        List<Optional<String>> list = new ArrayList<>();
        list.add(tenLoaiGiay);
        if (!Commons.listIsNullOrEmpty(list)) {
            LoaiGiay loaiGiay = new LoaiGiay();
            loaiGiay.setTenLoai(tenLoaiGiay.get());
            loaiGiayDAO.save(loaiGiay);
            message=true;
        }else {
            message=false;
        }
        return "redirect:/admin/category/insert?message="+message;
    }
    @GetMapping("/logout")
    public String logout(@RequestParam Optional<String> urlReturn) {
        session.clear();
        return urlReturn.isPresent()?"redirect:/admin/" +urlReturn.get():"redirect:/admin";
    }

}