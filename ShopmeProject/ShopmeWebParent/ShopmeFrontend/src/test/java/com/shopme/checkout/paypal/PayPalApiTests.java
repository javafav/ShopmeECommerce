package com.shopme.checkout.paypal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTests {
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID = "Q8FvdyVEqE78giVC0anNNNg6S3YH4CykJV5fO0qlLBCcnLgaa";
	private static final String CLIENT_SECRET = "EFBdgWrdGBUrKsp33mE7UyrAHW2esbKcqCn904qAM0t1uvqrzh3xZCQGStARGzyOSbRtECsBLeX17DTT";
	
	@Test
	public void testGetOrderDetails() {
		String orderId = "0JV794973N436004R";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
				requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
		PayPalOrderResponse orderResponse = response.getBody();

		System.out.println("Order ID: " + orderResponse.getId());
		System.out.println("Validated: " + orderResponse.validate(orderId));
		
	}
}
