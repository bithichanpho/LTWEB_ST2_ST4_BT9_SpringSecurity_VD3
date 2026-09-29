package web.programming.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import web.programming.dto.ProductDTO;
import web.programming.security.CustomUserDetails;
import web.programming.service.ProductService;

/**
 * Quan ly san pham (da dang nhap): CRUD + tim kiem + phan trang.
 * - San pham tao moi thuoc ve nguoi dung dang nhap (1 user - n product).
 * - User thuong chi sua/xoa san pham cua minh; ADMIN sua/xoa duoc tat ca.
 * - Anh san pham upload len Cloudinary.
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	private boolean isAdmin(CustomUserDetails principal) {
		return principal != null && principal.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
	}

	@GetMapping
	public String list(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model, @AuthenticationPrincipal CustomUserDetails principal) {
		model.addAttribute("products", productService.findAll(keyword, page, size));
		model.addAttribute("keyword", keyword);
		model.addAttribute("size", size);
		model.addAttribute("isAdmin", isAdmin(principal));
		model.addAttribute("currentUserId", principal != null ? principal.getId() : null);
		model.addAttribute("pageTitle", "Quan ly san pham");
		model.addAttribute("pageSubtitle", "Them, sua, xoa, tim kiem, phan trang - san pham thuoc ve nguoi tao.");
		return "products/list";
	}

	@GetMapping("/create")
	public String createForm(Model model) {
		model.addAttribute("productDTO", new ProductDTO());
		model.addAttribute("mode", "create");
		model.addAttribute("pageTitle", "Them san pham");
		return "products/form";
	}

	@PostMapping("/create")
	public String create(@Valid @ModelAttribute("productDTO") ProductDTO dto, BindingResult result,
			Model model, RedirectAttributes redirect, @AuthenticationPrincipal CustomUserDetails principal) {
		if (result.hasErrors()) {
			model.addAttribute("mode", "create");
			model.addAttribute("pageTitle", "Them san pham");
			return "products/form";
		}
		try {
			// Nguoi tao chinh la chu so huu san pham (yeu cau: product thuoc user)
			dto.setUserId(principal != null ? principal.getId() : null);
			productService.create(dto, dto.getImage());
			redirect.addFlashAttribute("message", "Them san pham thanh cong.");
			return "redirect:/products";
		} catch (IllegalArgumentException | IllegalStateException e) {
			result.reject("product.error", e.getMessage());
			model.addAttribute("mode", "create");
			model.addAttribute("pageTitle", "Them san pham");
			return "products/form";
		}
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable String id, Model model, RedirectAttributes redirect,
			@AuthenticationPrincipal CustomUserDetails principal) {
		ProductDTO dto;
		try {
			dto = productService.findById(id);
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
			return "redirect:/products";
		}
		if (!isAdmin(principal) && (principal == null || dto.getUserId() == null
				|| !dto.getUserId().equals(principal.getId()))) {
			redirect.addFlashAttribute("flashError", "Ban khong co quyen sua san pham nay.");
			return "redirect:/products";
		}
		model.addAttribute("productDTO", dto);
		model.addAttribute("mode", "edit");
		model.addAttribute("pageTitle", "Sua san pham");
		return "products/form";
	}

	@PostMapping("/edit/{id}")
	public String edit(@PathVariable String id, @Valid @ModelAttribute("productDTO") ProductDTO dto,
			BindingResult result, Model model, RedirectAttributes redirect,
			@AuthenticationPrincipal CustomUserDetails principal) {
		ProductDTO existing;
		try {
			existing = productService.findById(id);
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
			return "redirect:/products";
		}
		if (!isAdmin(principal) && (principal == null || existing.getUserId() == null
				|| !existing.getUserId().equals(principal.getId()))) {
			redirect.addFlashAttribute("flashError", "Ban khong co quyen sua san pham nay.");
			return "redirect:/products";
		}
		if (result.hasErrors()) {
			model.addAttribute("mode", "edit");
			model.addAttribute("pageTitle", "Sua san pham");
			return "products/form";
		}
		try {
			productService.update(id, dto, dto.getImage());
			redirect.addFlashAttribute("message", "Cap nhat san pham thanh cong.");
			return "redirect:/products";
		} catch (IllegalArgumentException | IllegalStateException e) {
			result.reject("product.error", e.getMessage());
			model.addAttribute("mode", "edit");
			model.addAttribute("pageTitle", "Sua san pham");
			return "products/form";
		}
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable String id, RedirectAttributes redirect,
			@AuthenticationPrincipal CustomUserDetails principal) {
		try {
			ProductDTO dto = productService.findById(id);
			if (!isAdmin(principal) && (principal == null || dto.getUserId() == null
					|| !dto.getUserId().equals(principal.getId()))) {
				redirect.addFlashAttribute("flashError", "Ban khong co quyen xoa san pham nay.");
				return "redirect:/products";
			}
			productService.delete(id);
			redirect.addFlashAttribute("message", "Xoa san pham thanh cong.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
		}
		return "redirect:/products";
	}
}
