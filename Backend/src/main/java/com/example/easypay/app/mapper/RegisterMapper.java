package com.example.easypay.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.easypay.app.dto.request.RegisterDto;
import com.example.easypay.app.entity.AppUser;

@Mapper(componentModel = "Spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegisterMapper {
	
	@Mapping(ignore = true, target = "userEmail")
	public AppUser toAppUser(RegisterDto registerDto);	

}
