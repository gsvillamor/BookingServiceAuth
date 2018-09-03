package pw.io.booker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.model.Authentication;
import pw.io.booker.model.Customer;
import pw.io.booker.repo.AuthenticationRepository;
import pw.io.booker.repo.CustomerRepository;
import pw.io.booker.util.TokenCreator;

@RestController
@Transactional
public class AuthenticationController {

	private AuthenticationRepository authenticationRepository;
	private CustomerRepository customerRepository;

	public AuthenticationController(AuthenticationRepository authenticationRepository,
			CustomerRepository customerRepository) {
		super();
		this.authenticationRepository = authenticationRepository;
		this.customerRepository = customerRepository;
	}

	@PostMapping("/login")
	public String login(@RequestBody Customer customer) {

		String tokenGenerated = null;
		if (!customerRepository.findByUserNameAndPassword(customer.getUserName(), customer.getPassword())
				.equals(null)) {

			List<Authentication> authentications = (List<Authentication>) authenticationRepository.findAll();
			for (Authentication auth : authentications) {
				authenticationRepository.delete(auth);
			}

			Authentication authentication = new Authentication();

			TokenCreator tokenCreator = new TokenCreator();
			tokenGenerated = tokenCreator.encode(customer);
			authentication.setToken(tokenGenerated);
			authentication.setCustomer(
					customerRepository.findByUserNameAndPassword(customer.getUserName(), customer.getPassword()));
			authentication.setLoginDate(LocalDate.now());
			authenticationRepository.save(authentication);
		} else {
			System.out.println("Customer is not existing!");
		}
		return tokenGenerated;
	}

	@PostMapping("/logout")
	public void logout(@RequestHeader("Authentication-Token") String token, @RequestBody Customer customer) {
//
//		if (authenticationRepository.findById(customer)) {
//
//		}
	}

}
