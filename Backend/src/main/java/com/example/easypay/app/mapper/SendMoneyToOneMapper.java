package com.example.easypay.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.easypay.app.dto.response.SendMoneyResponseDto;
import com.example.easypay.app.entity.AppTransaction;

@Mapper(componentModel = "Spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SendMoneyToOneMapper {
	
	public SendMoneyResponseDto toDto(AppTransaction txn);

}
