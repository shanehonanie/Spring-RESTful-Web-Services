package com.appsdeveloperblog.app.ws.restassuredtest;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestMethodOrder(OrderAnnotation.class)
class UsersWebServiceEndpointTest {
	private final String CONTEXT_PATH = "/mobile-app-ws";
	private final String EMAIL_ADDRESS = "shanehonanie@gmail.com";
	private final String JSON = "application/json";
	private static String authHeader;
	private static String UserID;
	private static List<Map<String, String>> addresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Test
	@Order(1) 
	void TestUserLogin() {
		Map<String, Object> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "password");
		
		Response response = given()
		.contentType(JSON)
		.accept(JSON)
		.body(loginDetails)
		.when()
		.post(CONTEXT_PATH + "/users/login")
		.then()
		.statusCode(200).extract().response();
		
		authHeader = response.header("Authorization");
		UserID = response.header("userID");
		
		
		assertNotNull(authHeader);
		assertNotNull(UserID);
	}
	
	@Test
	@Order(2) 
	void TestGetUserDetails() {		
		Response response = given()
		.pathParam("id", UserID)
		.header("Authorization", authHeader)
		.accept(JSON)
		.get(CONTEXT_PATH + "/users/{id}")
		.then()
		.statusCode(200)
		.contentType(JSON)
		.extract()
		.response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);
		
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);
	}
	
	@Test
	@Order(3)
	final void testUpdateUserDetails()
	{
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "SergeUpdated");
		userDetails.put("lastName", "KargopolovUpdated");
		
		 Response response = given()
		 .contentType(JSON)
		 .accept(JSON)
		 .header("Authorization",authHeader)
		 .pathParam("id", UserID)
		 .body(userDetails)
		 .when()
		 .put(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		 
         String firstName = response.jsonPath().getString("firstName");
         String lastName = response.jsonPath().getString("lastName");
         
         List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
         
         assertEquals("SergeUpdated", firstName);
         assertEquals("KargopolovUpdated", lastName);
         assertNotNull(storedAddresses);
         assertTrue(addresses.size() == storedAddresses.size());
         assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
	}
}
