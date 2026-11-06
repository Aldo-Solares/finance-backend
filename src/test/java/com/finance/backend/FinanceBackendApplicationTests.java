package com.finance.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:finance-backend-test;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false",
		"jwt.secret=rate-limit-tests-only-secret-key-0123456789",
		"jwt.expiration=3600000",
		"FRONTEND_URL=http://localhost:3000",
		"RESEND_API_KEY=test-api-key",
		"RESEND_FROM=tests@example.com",
		"spring.mail.username=tests@example.com",
		"spring.mail.password=test-password",
		"app.frontend-url=http://localhost:3000",
		"app.backend-url=http://localhost:9000",
		"app.storage.profile-image-directory=target/test-profile-images"
})
class FinanceBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
