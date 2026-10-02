package com.example.easypay.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.easypay.app.dto.response.NotificationResponsDto;
import com.example.easypay.app.entity.Notification;

@Mapper(componentModel = "Spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationCountAndLatestTwoMapper {

	@Mapping(target="appUserId",ignore = true)
	public NotificationResponsDto toDto(Notification notification);
}
