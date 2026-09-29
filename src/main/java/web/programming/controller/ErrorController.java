package web.programming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Trang loi than thien (thay cho Whitelabel Error Page).
 * Spring Boot goi /error khi co loi HTTP (403, 404, 500...).
 */
@Controller
public class ErrorController implements org.springframework.boot.webmvc.error.ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		int code = 500;
		if (status != null) {
			try {
				code = Integer.parseInt(status.toString());
			} catch (NumberFormatException ignored) {
				// giu mac dinh 500
			}
		}

		String message = switch (code) {
			case 403 -> "Ban khong co quyen truy cap trang nay.";
			case 404 -> "Khong tim thay trang ban yeu cau.";
			default -> "Da co loi xay ra, vui long thu lai.";
		};

		model.addAttribute("statusCode", code);
		model.addAttribute("errorMessage", message);
		model.addAttribute("pageTitle", "Co loi xay ra");
		return "error";
	}
}
