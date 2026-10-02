package com.example.easypay.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.easypay.app.dto.response.LinkedAccountResponse;
import com.example.easypay.app.entity.UserLinkedAccount;

@Mapper(componentModel = "Spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserLinkedAccountMapper {
	
	public LinkedAccountResponse toDto(UserLinkedAccount entity);

}
