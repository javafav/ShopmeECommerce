package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired private CountryRepository countryRepo;
	@Autowired private CustomerRepository customerRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);
		return customer == null;
	}
	
	public void registerCustomer(Customer customer) {
		
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		String verificationCode = RandomString.make(64);
		customer.setVerificationCode(verificationCode);
		customerRepo.save(customer);
	
	}

	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
		
	}
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}
	
	public void addCustomer(String email, String name, String countryCode,AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);
		setName(name, customer);
		
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepo.findByCode(countryCode));
		
		customerRepo.save(customer);
		
	}
	


	private void setName(String name, Customer customer) {
	
		String[] nameArray = name.split(" ");
		
		if(nameArray.length < 2) {
			
			customer.setFirstName(name);
			customer.setLastName("");
		}else {
		String firstName = nameArray[0];
		String lastName = name.replaceFirst(firstName, "");
		
		customer.setFirstName(firstName);
        customer.setLastName(lastName);		
		}
		
}

	public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
		customerRepo.updateAuthencation(authenticationType, customer.getId());
	}
	
	public boolean verifyCustomer(String verifyCode) {
		
		Customer customer = customerRepo.findByVerificationCode(verifyCode);
		
		if (customer == null || customer.getVerificationCode() == null) {
			
			return false;
		
		} else {
			
			customerRepo.enable(customer.getId());
			return true;
		}

	}
	
	public void update(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		
		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);			
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}		
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		
		customerRepo.save(customerInForm);
	}	
	
}
