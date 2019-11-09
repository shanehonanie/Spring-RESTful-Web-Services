package com.appsdeveloperblog.app.ws.service.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	final void testGetUser() {

		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Shane");
		userEntity.setUserId("abcde");
		userEntity.setEncryptedPassword("38hd38d");

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("test@test.com");

		assertNotNull(userDto);
		assertEquals("Shane", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("test@test.com");
		}

		);
	}
	
	
}
