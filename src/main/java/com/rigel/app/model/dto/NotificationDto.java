package com.rigel.app.model.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class NotificationDto{
	
	private String id;
	private String notificationType;
	private String description;
	private boolean status;	
	private boolean seenStatus;	
	private int ownerId;		

}
