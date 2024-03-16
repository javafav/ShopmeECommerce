package com.shopme.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;

@Service
public class ShippingRateService {

	@Autowired
	private ShippingRateRepository repo;

	public ShippingRate getShippingRateForCustomer(Customer customer) {
		Country country = customer.getCountry();
		String state = customer.getState();
		if (state == null || state.isEmpty()) {
			state = customer.getCity();
		}
		return repo.findByCountryAndState(country, state);
	}

	public ShippingRate getShippingRateForAddress(Address address) {

		Country country = address.getCountry();
		String state = address.getState();
		if (state == null || state.isEmpty()) {
			state = address.getCity();
		}
		return repo.findByCountryAndState(country, state);
	}
}
