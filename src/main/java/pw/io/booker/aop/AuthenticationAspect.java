package pw.io.booker.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import pw.io.booker.exception.CustomException;
import pw.io.booker.repo.AuthenticationRepository;

@Aspect
@Component
public class AuthenticationAspect {

	Logger logger = Logger.getLogger(AuthenticationAspect.class);

	private AuthenticationRepository authenticationRepository;

	public AuthenticationAspect(AuthenticationRepository authenticationRepository) {
		super();
		this.authenticationRepository = authenticationRepository;
	}

	@Before("execution(* pw.io.booker.controller..*(..))")
	public void beforeMethodExecution(JoinPoint joinPoint) {

		logger.info("Start of method " + joinPoint.getClass().getName());
	}

	@Around("execution(* pw.io.booker.controller..*(..)) && args(token, ..) && !execution(* pw.io.booker.controller.CustomerController.saveAll(..))")
	public Object requestAccess(ProceedingJoinPoint joinPoint, String token) throws Throwable {

		if (token == null) {
			logger.error("Invalid Credentials");
			throw new CustomException("Invalid Credentials");
		}

		if (authenticationRepository.findByToken(token) == null) {
			logger.error("Token not found");
			throw new CustomException("Token not found");
		}
		return joinPoint.proceed();
	}

	@After("execution(* pw.io.booker.controller..*(..))")
	public void afterMethodExecution(JoinPoint joinPoint) {

		logger.info("End of method " + joinPoint.getClass().getName());
	}

}
