package com.appsdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;


@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	@GetMapping(
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public List<UserRest> getAllUsers(@RequestParam(value="page", defaultValue = "0") int page,
			@RequestParam(value="limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getAllUsers(page, limit);
		
			for (UserDto userDto : users) {
				UserRest userModel = new UserRest();
				BeanUtils.copyProperties(userDto, userModel);
				returnValue.add(userModel);
			}
	
		
		return returnValue;
	}
	
	@GetMapping(path="/{id}",
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
		
	}
	
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId) {

		AddressDto addressesDto = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

		AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);
		
		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		
		return new EntityModel<>(addressesRestModel);
	}
	
	// http://localhost:8080/mobile-app-ws/users/:userId/addresses
	@GetMapping(path="/{id}/addresses",
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id){
		List<AddressesRest> addressesListRestModel = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressService.getAddresses(id);
		
		if(addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDto, listType);
			
			for (AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
						.withSelfRel();
				addressRest.add(addressLink);

				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}

		return new CollectionModel<>(addressesListRestModel);
	}
	
	@PostMapping(
			consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException{
		UserRest returnValue = new UserRest();
		
		if(userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty() ||
				userDetails.getEmail().isEmpty() || userDetails.getPassword().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}
	
	@PutMapping(path="/{id}",
			consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}
	
	@DeleteMapping(path="/{id}",
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	)
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}
}
