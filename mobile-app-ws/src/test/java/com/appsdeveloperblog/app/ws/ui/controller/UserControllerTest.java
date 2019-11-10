package com.appsdeveloperblog.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;
	
	UserDto userDto;
	
	final String USER_ID = "djd9d3w2u0";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("Shane");
		userDto.setLastName("Test");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("xohwiml");
	}

	@Test
	final void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}
	
	private List<AddressDto> getAddressesDto() {
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Vancouver2");
		addressDto.setCountry("Canada2");
		addressDto.setPostalCode("048202");
		addressDto.setStreetName("123 Street Name2");
		
		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("04820");
		billingAddressDto.setStreetName("123 Street Name");
		
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);
		
		return addresses;
	}
}
