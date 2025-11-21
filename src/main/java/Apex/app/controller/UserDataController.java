package Apex.app.controller;

import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.dto.UserRequestDto;
import Apex.app.service.UserDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @GetMapping("/health")
    public String healthCheck(){
        return "Health Check -> OK";
    }

    @PostMapping("/process-user-data")
    public ResponseEntity<ExternalApiResponseDto> processUserData(@Valid @RequestBody UserRequestDto request) {
        ExternalApiResponseDto response = userDataService.processUserData(request);
        return ResponseEntity.ok(response);
    }
}