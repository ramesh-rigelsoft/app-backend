package com.rigel.app.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RepaireRequest {
	
	private RepaireDeviceDto repaireDeviceDto;
	
	private boolean statusUpdate;
	

}
