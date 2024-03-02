package com.shopme.customer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Customer;

@Controller
public class PasswordController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/change_password")
	public String showChangePasswordForm(Model model) {
		model.addAttribute("pageTitle", "Change Expired Password");
		return "customer/change_password";
	}

	@PostMapping("/change_password")
	public String processChangePassword(HttpServletRequest request, HttpServletResponse response, Model model,
			RedirectAttributes ra, @AuthenticationPrincipal Authentication authentication) throws ServletException {
		CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
		Customer customer = userDetails.getCustomer();

		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");

		model.addAttribute("pageTitle", "Change Expired Password");

		if (oldPassword.equals(newPassword)) {
			model.addAttribute("message", "Your new password must be different than the old one.");

			return "customer/change_password";
		}

		if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
			model.addAttribute("message", "Your old password is incorrect.");
			return "customer/change_password";

		} else {
			customerService.changePassword(customer, newPassword);
			request.logout();
			ra.addFlashAttribute("message", "You have changed your password successfully. " + "Please login again.");

			return "redirect:/login";
		}

	}

}