package com.devteria.identity_service.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleConfig {
  @Value("${google.api.tokens-directory-path:tokens}")
  private String tokensDirectoryPath;

  @Value("${google.api.credentials-file-path:/credentials.json}")
  private String credentialsFilePath;

  @Value("8889")
  private int redirectPort;

  private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/userinfo.email",
          "https://www.googleapis.com/auth/userinfo.profile",
          "openid");

  @Bean
  public JsonFactory googleJsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  @Bean
  public NetHttpTransport googleHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Bean
  public Credential googleCredential(NetHttpTransport httpTransport, JsonFactory jsonFactory)
      throws IOException, GeneralSecurityException {
      InputStream in;
      File file = new File(credentialsFilePath);
      if (file.exists()) {
          in = new java.io.FileInputStream(file);
      } else {
          // fallback cho trường hợp chạy trong môi trường dev (classpath)
          in = getClass().getResourceAsStream(credentialsFilePath);
          if (in == null) {
              throw new IOException("Resource not found: " + credentialsFilePath);
          }
      }
      GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(jsonFactory, new java.io.InputStreamReader(in));
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new File("/app/tokens")))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(redirectPort).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
}
