package server.yakssok.global.infra.fcm;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FcmConfig {

	@PostConstruct
	public void init() throws IOException {
		try {
			ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
			InputStream serviceAccount = resource.getInputStream();

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
		} catch (IOException e) {
			throw new RuntimeException("Firebase initialization error", e);
		}
	}
}
