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
import web.programming.dto.UserDTO;
import web.programming.security.CustomUserDetails;
import web.programming.service.UserService;

/**
 * Quan ly nguoi dung (chi ROLE_ADMIN): CRUD + tim kiem + phan trang.
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public String list(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		model.addAttribute("users", userService.findAll(keyword, page, size));
		model.addAttribute("keyword", keyword);
		model.addAttribute("size", size);
		model.addAttribute("pageTitle", "Quan ly nguoi dung");
		model.addAttribute("pageSubtitle", "Them, sua, xoa, tim kiem va phan trang tai khoan.");
		return "users/list";
	}

	@GetMapping("/create")
	public String createForm(Model model) {
		UserDTO dto = new UserDTO();
		dto.setEnabled(true);
		dto.setRoleName("ROLE_USER");
		model.addAttribute("userDTO", dto);
		model.addAttribute("mode", "create");
		model.addAttribute("pageTitle", "Them nguoi dung");
		model.addAttribute("pageSubtitle", "Mat khau mac dinh cua user moi: 123456");
		return "users/form";
	}

	@PostMapping("/create")
	public String create(@Valid @ModelAttribute("userDTO") UserDTO dto, BindingResult result,
			Model model, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			model.addAttribute("mode", "create");
			model.addAttribute("pageTitle", "Them nguoi dung");
			model.addAttribute("pageSubtitle", "Mat khau mac dinh cua user moi: 123456");
			return "users/form";
		}
		try {
			userService.create(dto);
			redirect.addFlashAttribute("message", "Tao user thanh cong. Mat khau mac dinh: 123456");
			return "redirect:/users";
		} catch (IllegalArgumentException e) {
			result.reject("user.error", e.getMessage());
			model.addAttribute("mode", "create");
			model.addAttribute("pageTitle", "Them nguoi dung");
			model.addAttribute("pageSubtitle", "Mat khau mac dinh cua user moi: 123456");
			return "users/form";
		}
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		UserDTO dto;
		try {
			dto = userService.findById(id);
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
			return "redirect:/users";
		}
		model.addAttribute("userDTO", dto);
		model.addAttribute("mode", "edit");
		model.addAttribute("pageTitle", "Sua nguoi dung");
		return "users/form";
	}

	@PostMapping("/edit/{id}")
	public String edit(@PathVariable Long id, @Valid @ModelAttribute("userDTO") UserDTO dto,
			BindingResult result, Model model, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			model.addAttribute("mode", "edit");
			model.addAttribute("pageTitle", "Sua nguoi dung");
			return "users/form";
		}
		try {
			userService.update(id, dto);
			redirect.addFlashAttribute("message", "Cap nhat user thanh cong.");
			return "redirect:/users";
		} catch (IllegalArgumentException e) {
			result.reject("user.error", e.getMessage());
			model.addAttribute("mode", "edit");
			model.addAttribute("pageTitle", "Sua nguoi dung");
			return "users/form";
		}
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirect) {
		if (principal != null && principal.getId().equals(id)) {
			redirect.addFlashAttribute("flashError", "Khong the tu xoa tai khoan dang dang nhap.");
			return "redirect:/users";
		}
		try {
			userService.delete(id);
			redirect.addFlashAttribute("message", "Xoa user thanh cong.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
		}
		return "redirect:/users";
	}
}
