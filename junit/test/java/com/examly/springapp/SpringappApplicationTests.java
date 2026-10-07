package com.examly.springapp;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
 
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringappApplicationTests {
 
       
        private String userToken;
        private String adminToken;
 
        @Autowired
        private TestRestTemplate restTemplate;
 
        @Autowired
        private ObjectMapper objectMapper;
 
        private HttpHeaders createHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
        }
 
        @Test
        @Order(1)
        void backend_testRegisterAdmin() {
                String requestBody = "{\"userId\": 1,\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\", \"username\": \"admin123\", \"userRole\": \"Admin\", \"mobileNumber\": \"9876543210\"}";
                ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                                new HttpEntity<>(requestBody, createHeaders()), String.class);
 
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
 
        @Test
        @Order(2)
        void backend_testRegisterCustomer() {
                String requestBody = "{\"userId\": 2,\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\", \"username\": \"user123\", \"userRole\": \"Customer\", \"mobileNumber\": \"1122334455\"}";
                ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                                new HttpEntity<>(requestBody, createHeaders()), String.class);
 
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
 
        @Test
        @Order(3)
        void backend_testLoginAdmin() throws Exception {
                String requestBody = "{\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\"}";
 
                ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                                new HttpEntity<>(requestBody, createHeaders()), String.class);
 
                Assertions.assertNotNull(response.getBody(), "Response body is null!");
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                String token = responseBody.get("token").asText();
                adminToken = token;
 
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                Assertions.assertNotNull(token);
        }
 
        @Test
        @Order(4)
        void backend_testLoginUser() throws Exception {
                String requestBody = "{\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\"}";
 
                ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                                new HttpEntity<>(requestBody, createHeaders()), String.class);
 
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                String token = responseBody.get("token").asText();
                userToken = token;
 
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                Assertions.assertNotNull(token);
        }
 
        @Test
        @Order(5)
        void backend_testAddDriverWithRoleValidation() throws Exception {
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
                Assertions.assertNotNull(userToken, "User token should not be null");
				String requestBody = "{"
				+ "\"driverName\": \"Rajesh Kumar\","
				+ "\"licenseNumber\": \"DL123456789\","
				+ "\"experienceYears\": 5,"
				+ "\"contactNumber\": \"9876543210\","
				+ "\"availabilityStatus\": \"Available\","
				+ "\"address\": \"12, Gandhi Street, Chennai\","
				+ "\"vehicleType\": \"SUV\","
				+ "\"hourlyRate\": 350.0,"
				+ "\"image\": \"base64encodedImageDataHere\""
				+ "}";
			
 
                // Try with ADMIN (should be ALLOWED)
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(
                                "/api/driver", HttpMethod.POST, adminRequest, String.class);
 
                JsonNode adminJson = objectMapper.readTree(adminResponse.getBody());
 
                System.out.println(adminResponse.getStatusCode() + " Status code for Admin adding driver");
                Assertions.assertEquals(HttpStatus.CREATED, adminResponse.getStatusCode());
        
 
                // Try with user (should be FORBIDDEN) //User
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(
                                "/api/driver", HttpMethod.POST, userRequest, String.class);
 
                System.out.println(userResponse.getStatusCode() + " Status code for User trying to add driver");
                Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
        }
 
        @Test
        @Order(6)
        void backend_testGetDriverById_AccessibleByAdmin() throws Exception {
                Long driverId = 1L;
 
                // trying with the admin( Should success)
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(
                                "/api/driver/" + driverId, HttpMethod.GET, adminRequest, String.class);
 
                JsonNode adminJson = objectMapper.readTree(adminResponse.getBody());
 
                System.out.println(
                                adminResponse.getStatusCode() + " Status code for admin fetching Driver");
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
                Assertions.assertEquals(driverId, adminJson.get("driverId").asLong());
 
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(
                                "/api/driver/" + driverId, HttpMethod.GET, userRequest, String.class);
 
                // JsonNode userJson = objectMapper.readTree(userResponse.getBody());
 
                System.out.println(userResponse.getStatusCode() + " Status code for user fetching Driver");
                Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
               
               
        }
 
        @Test
        @Order(7)
        void backend_testGetAllDrivers_AccessibleByUserAndAdmin() throws Exception {
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
                // --- user tries to get all drivers ---
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(
                                "/api/driver", HttpMethod.GET, userRequest, String.class);
 
                System.out.println(userResponse.getStatusCode() + " Status code for User fetching all drivers");
                Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
                Assertions.assertTrue(userResponse.getBody().contains("driverId"),
                                "Response body should contain 'driverId'");
 
                // --- admin tries to get all drivers ---
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(
                                "/api/driver", HttpMethod.GET, adminRequest, String.class);
 
                System.out.println(adminResponse.getStatusCode() + " Status code for Admin fetching all driver");
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
                Assertions.assertTrue(adminResponse.getBody().contains("driverId"),
                                "Response body should contain 'driverId'");
        }
 
        @Test
        @Order(8)
        void backend_testUpdateDriver_AccessibleByAdminOnly() throws Exception {
                Long driverId = 1L; // Use an existing Driver ID or create one before running the test
 
                // Ensure tokens are available
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
                Assertions.assertNotNull(userToken, "User token should not be null");
 
                // Driver update request body // needs to change the request body
                String requestBody = "{"
                + "\"driverName\": \"Ravi Kumar updated\","
                + "\"licenseNumber\": \"DL04 2023 567890\","
                + "\"experienceYears\": 12,"
                + "\"contactNumber\": \"9876543210\","
                + "\"availabilityStatus\": \"Active updated\","
                + "\"address\": \"123 MG Road, Bengaluru, Karnataka,updated\","
                + "\"vehicleType\": \"Sedan\","
                + "\"hourlyRate\": 350.50,"
                + "\"image\": \"updatedsampleBase64EncodedImage==\""
                + "}";
 
                // --- Admin tries to update driver (should succeed) ---
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(
                                "/api/driver/" + driverId, HttpMethod.PUT, adminRequest, String.class);
 
                JsonNode adminJson = objectMapper.readTree(adminResponse.getBody());
 
                System.out.println(adminResponse.getStatusCode() + " Status code for Admin updating driver");
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
                Assertions.assertEquals("Ravi Kumar updated", adminJson.get("driverName").asText());
                Assertions.assertEquals(12, adminJson.get("experienceYears").asInt());
 
                // --- User tries to update driver (should be forbidden) ---
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(
                                "/api/driver/" + driverId, HttpMethod.PUT, userRequest, String.class);
 
                System.out.println(userResponse.getStatusCode() + " Status code for user trying to update driver");
                Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
        }
 
 
        @Test
        @Order(9)
        void backend_testAddDriverRequests_AccessibleByuserOnly() throws Exception {
                // Ensure tokens are available
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "admin token should not be null");
 
                // Updated driverRequest add request body
                String requestBody = "{"
                + "\"user\": { \"userId\": 2 },"
                + "\"driver\": { \"driverId\": 1 },"
                + "\"requestDate\": \"2024-05-13\","
                + "\"status\": \"Pending\","
                + "\"tripDate\": \"2024-05-15\","
                + "\"timeSlot\": \"10:30:00\","
                + "\"pickupLocation\": \"Indiranagar, Bengaluru\","
                + "\"dropLocation\": \"Electronic City, Bengaluru\","
                + "\"estimatedDuration\": \"2 hours\","
                + "\"paymentAmount\": 450.0,"
                + "\"comments\": \"Customer requested AC vehicle\","
                + "\"actualDropTime\": \"12:45:00\","
                + "\"actualDropDate\": \"2024-05-15\","
                + "\"actualDuration\": \"2 hours 15 minutes\""
                + "}";
           
                // --- user tries to add driver request (should succeed) ---
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(
                                "/api/driverRequest", HttpMethod.POST, userRequest, String.class);
 
                JsonNode userJson = objectMapper.readTree(userResponse.getBody());
 
                System.out.println(userResponse.getStatusCode() + " Status code for user adding driverRequest");
                Assertions.assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
    
                // --- Admin tries to add driver request (should be forbidden) ---
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(
                                "/api/driverRequest", HttpMethod.POST, adminRequest, String.class);
 
                System.out.println(
                                adminResponse.getStatusCode()
                                                + " Status code for admin trying to add driverRequest");
                Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
        }
 


@Test
@Order(10)
void backend_testGetDriverRequestsByUserId_onlyCustomerAccess() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(userToken, "User token should not be null");
    Assertions.assertNotNull(adminToken, "Admin token should not be null");

    Long userId = 2L; // Assuming this user exists and is a Customer

    String url = "/api/driverRequest/user/" + userId;

    // Test with Customer token (Expecting 200 OK)
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + userToken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(url, HttpMethod.GET, userRequest, String.class);
    System.out.println(userResponse.getStatusCode() + " Status code for Customer fetching Driver Requests");
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());

    // Test with Admin token (Expecting 403 FORBIDDEN)
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + adminToken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(url, HttpMethod.GET, adminRequest, String.class);
    System.out.println(adminResponse.getStatusCode() + " Status code for Admin trying to fetch Driver Requests by User ID");
    Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
}

        @Test
        @Order(11)
        void backend_testGetAllDriverRequests_onlyAdminAccess() throws Exception {
                // Ensure tokens are available
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
                String url = "/api/driverRequest";
 
                // Test with Admin token (Expecting 200 OK)
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
 
                ResponseEntity<String> adminResponse = restTemplate.exchange(url, HttpMethod.GET, adminRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
 
                // Test with user token (Expecting 403 FORBIDDEN)
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
 
                ResponseEntity<String> userResponse = restTemplate.exchange(url, HttpMethod.GET, userRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
        }
 
        @Test
        @Order(12)
        void backend_testAddFeedback() throws Exception {
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
				String requestBody = "{"
                + "\"feedbackText\": \"The driver was punctual and polite.\","
                + "\"date\": \"2024-05-13\","
                + "\"user\": { \"userId\": 2 },"
                + "\"driver\": { \"driverId\": 1 },"
                + "\"category\": \"Driver Performance\","
                + "\"rating\": 5"
                + "}";

 
 
                // user should be able to add feedback
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);
                ResponseEntity<String> userResponse = restTemplate.exchange("/api/feedback", HttpMethod.POST,
                                userRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.CREATED, userResponse.getStatusCode(),
                                "User should be able to add feedback");
 
                // admin should NOT be able to add feedback
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);
                ResponseEntity<String> adminResponse = restTemplate.exchange("/api/feedback", HttpMethod.POST,
                                adminRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
        }
 
        @Test
        @Order(13)
        void backend_testGetAllFeedback() throws Exception {
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
                // admin should be able to get all feedback
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
                ResponseEntity<String> adminResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET,
                                adminRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode(),
                                "admin should be able to view all feedback");
 
                // user should be able to get all feedback
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
                ResponseEntity<String> userResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET,
                                userRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode(),
                                "user should be able to view all feedback");
        }
 
        @Test
        @Order(14)
        void backend_testGetFeedbackByUserId_onlyUserAccess() throws Exception {
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
                // user should be able to get their own feedback
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
                ResponseEntity<String> userResponse = restTemplate.exchange("/api/feedback/user/2", HttpMethod.GET,
                                userRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
 
                // admin should NOT be able to get user feedback
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
                ResponseEntity<String> adminResponse = restTemplate.exchange("/api/feedback/user/2", HttpMethod.GET,
                                adminRequest, String.class);
                Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
        }
 
        @Test
        @Order(15)
        void backend_testGetFeedbackById_userAndAdminAccess() throws Exception {
                Assertions.assertNotNull(userToken, "User token should not be null");
                Assertions.assertNotNull(adminToken, "Admin token should not be null");
 
                // user should be able to get feedback by ID
                HttpHeaders userHeaders = createHeaders();
                userHeaders.set("Authorization", "Bearer " + userToken);
                HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
                ResponseEntity<String> userResponse = restTemplate.exchange("/api/feedback/1", HttpMethod.GET,
                                userRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
 
                // admin should be able to get feedback by ID
                HttpHeaders adminHeaders = createHeaders();
                adminHeaders.set("Authorization", "Bearer " + adminToken);
                HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
                ResponseEntity<String> adminResponse = restTemplate.exchange("/api/feedback/1", HttpMethod.GET,
                                adminRequest,
                                String.class);
                Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
        }
 
}