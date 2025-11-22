package Apex.app.service;

import Apex.app.dto.ExternalApiRequestDto;
import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.exception.ExternalApiException;
import Apex.app.model.CombinedUserData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

@Service
public class ExternalApiService {

    @Value("${app.external.api.url:http://localhost:8080/mock/api/xyz}")
    private String externalApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ExternalApiResponseDto callExternalApi(ExternalApiRequestDto requestData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");

            String jsonBody = objectMapper.writeValueAsString(requestData);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<ExternalApiResponseDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    ExternalApiResponseDto.class
            );

            if (response.getBody() == null) {
                throw new ExternalApiException("Empty response from loan calculation service");
            }

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new ExternalApiException("Invalid request to loan service: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ExternalApiException("Loan calculation service is temporarily down: " + e.getMessage());
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new ExternalApiException("Loan calculation service timed out. Please try again.");
            }
            throw new ExternalApiException("Unable to reach loan calculation service: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("Unexpected error calling loan service: " + e.getMessage(), e);
        }
    }
}