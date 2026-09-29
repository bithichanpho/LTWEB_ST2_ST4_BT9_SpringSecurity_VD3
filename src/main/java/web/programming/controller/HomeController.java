package web.programming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import web.programming.service.ProductService;
import web.programming.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final UserService userService;
	private final ProductService productService;

	@GetMapping("/")
	public String root() {
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String home(ModelMap model) {
		// Thong ke (yeu cau de bai: dem so user, dem so san pham)
		model.addAttribute("userCount", userService.countUsers());
		model.addAttribute("productCount", productService.countProducts());
		model.addAttribute("pageTitle", "Dashboard");
		model.addAttribute("pageSubtitle", "Tong quan he thong.");
		return "home";
	}
}
