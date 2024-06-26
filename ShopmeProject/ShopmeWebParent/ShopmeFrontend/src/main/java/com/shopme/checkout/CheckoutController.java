package com.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.VerificationCodeGenerator;
import com.shopme.address.AddressService;
import com.shopme.checkout.paypal.PayPalApiException;
import com.shopme.checkout.paypal.PayPalService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.order.OrderService;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;

@Controller
@CrossOrigin()
public class CheckoutController {

	@Autowired private CheckoutService checkoutService;

	@Autowired private AddressService addressService;
	@Autowired private ShippingRateService shipService;
	@Autowired private ShoppingCartService cartService;
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	@Autowired private PayPalService paypalService;
	@Autowired private ControllerHelper controllerHelper;
	
	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer =controllerHelper.getAuthenticatedCustomer(request);
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if (defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			model.addAttribute("shippingAddress", customer.toString());
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
		
		if (shippingRate == null) {
			return "redirect:/cart";
		}
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		

		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
		String paypalClientId = paymentSettings.getClientID();
		
		model.addAttribute("paypalClientId", paypalClientId);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("customer", customer);
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);
		
		return "checkout/checkout";
	}
	

	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request, Model model, HttpSession session) throws UnsupportedEncodingException, MessagingException {
		String paymentType = request.getParameter("paymentMethod");
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
		
		
		Customer customer =controllerHelper.getAuthenticatedCustomer(request);
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
	
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
	    if (paymentMethod.equals(PaymentMethod.COD)) {
	        Order order = orderService.createOrderForCOD(customer, defaultAddress, paymentMethod, cartItems, checkoutInfo);

	        // Send verification email
	        sendOrderVerificationEmail(request, session);

	        model.addAttribute("customer", customer);
	        model.addAttribute("defaultAddress", defaultAddress);
	        model.addAttribute("paymentMethod", paymentMethod);
	        model.addAttribute("cartItems", cartItems);
	        model.addAttribute("checkoutInfo", checkoutInfo);
	        session.setAttribute("order", order);

	        return "checkout/order_verification";
	    } else {
	        Order createdOrder = orderService.createOrder(customer, defaultAddress, paymentMethod, cartItems, checkoutInfo);
	        cartService.deleteByCustomer(customer);
	        sendOrderConfirmationEmail(request, createdOrder);
	        return "checkout/order_completed";
	    }
		
	}

	@PostMapping("/verify_order")
	public String verifyOrder(HttpServletRequest request, HttpSession session, Model model) {
	    String enteredCode = request.getParameter("verificationCode");
	    String storedCode = (String) session.getAttribute("verificationCode");
	    Order order = (Order) session.getAttribute("order");

	    if (order == null) {
	        model.addAttribute("error", "Order session has expired. Please try placing the order again.");
	        return "checkout/order_verification";
	    }

	    if (enteredCode != null && enteredCode.equals(storedCode)) {
	        session.removeAttribute("verificationCode");
	        session.removeAttribute("order");

	        // Check if the order is not null before saving it
	        if (order != null) {
	            orderService.saveOrder(order);
	            cartService.deleteByCustomer(order.getCustomer());

	            try {
	                sendOrderConfirmationEmail(request, order);
	            } catch (UnsupportedEncodingException | MessagingException e) {
	                return "error/500";
	            }

	            return "checkout/order_completed";
	        } else {
	            model.addAttribute("error", "Failed to process the order. Order is null.");
	            return "checkout/order_verification";
	        }
	    } else {
	        // Code is incorrect, show an error message
	        model.addAttribute("error", "Invalid verification code. Please try again.");
	        model.addAttribute("order", order);
	        return "checkout/order_verification";
	    }
	}


	
	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());
		
		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", order.getShippingAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
		
		
		helper.setText(content, true);
		mailSender.send(message);		
	}
	
	
	private void sendOrderVerificationEmail(HttpServletRequest request,  HttpSession session)
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		Customer customer =controllerHelper.getAuthenticatedCustomer(request);
		
		String verificationCode = VerificationCodeGenerator.generateVerificationCode();
	    session.setAttribute("verificationCode", verificationCode);
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getOrderVerificationSubject();
		String content = emailSettings.getOrderVerificationContent();

		//subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
       
		content = content.replace("[[customerName]]", customer.getFullName());
		


		content = content.replace("[[verificationCode]]", verificationCode);

		helper.setText(content, true);
		mailSender.send(message);
		
	}
	
	
	
	
	@PostMapping("/process_paypal_order")
	public String processPayPalOrder(HttpServletRequest request, Model model,HttpSession session) 
			throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");
		
		String pageTitle = "Checkout Failure";
		String message = null;
		
		try {
			if (paypalService.validateOrder(orderId)) {
				return placeOrder(request, model, session);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid";
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
		}
		
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
		
		return "message";
	}
}
