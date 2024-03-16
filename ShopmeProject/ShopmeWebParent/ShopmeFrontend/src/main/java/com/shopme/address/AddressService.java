package com.shopme.address;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class AddressService {
	
	@Autowired private AddressRepository repo;
	
	public List<Address> listAddressBook(Customer customer) {
		return repo.findByCustomer(customer);
	}
	public void save(Address address) {
		repo.save(address);
	}
	
	public Address get(Integer addressId, Integer customerId) {
		return repo.findByIdAndCustomer(addressId, customerId);
	}
	
	public void delete(Integer addressId, Integer customerId) {
		repo.deleteByIdAndCustomer(addressId, customerId);
	}
	
	public void seDefaultAddress(Integer addressId, Integer customerId) {
		if (addressId > 0) {
			repo.setDefaultAddress(addressId);
		}

		repo.setNonDefaultForOthers(addressId, customerId);
	}
	
	public Address getDefaultAddress(Customer customer) {
	return	repo.findDefaultByCustomer(customer.getId());
	}
	
	
}
