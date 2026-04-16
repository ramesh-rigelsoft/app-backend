package com.rigel.app.actuator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rigel.app.exception.TaskTitleNotFound;

//@Component
//@RestControllerEndpoint(id="serverStatus")
@Component
@Endpoint(id = "serverStatus")
public class ActuatorController {

	@ReadOperation
	public @ResponseBody ResponseEntity<Map<String, Object>> getServerStorageStatus(){
		HashMap<String, Object> response=new HashMap<>();
		try {
		Runtime runtime = Runtime.getRuntime();
		NumberFormat f = new DecimalFormat("###,##0.0");
	       
		double byteConversion=1024 * 1024;
//		-Xms1024m -Xmx1024m, the value you get from totalMemory() and maxMemory()
		//maxMemory() returns the -Xmx 
		// totalMemory() return -Xms1024m
		//final long usedMem = totalMemory() - freeMemory();
		response.put("maxMemoryInMB", f.format(runtime.maxMemory()/byteConversion));//Returns the maximum amount of memory that the Java virtual machine will attempt to use.
		response.put("totalMemoryInMB", f.format(runtime.totalMemory()/byteConversion));//Returns the total amount of memory in the Java virtual machine
		response.put("usedMemoryInMB", f.format((runtime.totalMemory()-runtime.freeMemory())/byteConversion));
		response.put("freeMemoryInMB", f.format(runtime.freeMemory()/byteConversion));//Returns the amount of free memory in the Java virtual machine
		response.put("availableProcessors", f.format(runtime.availableProcessors()));		
		return new ResponseEntity<>(response,HttpStatus.OK);	
		} catch (Exception e) {
			throw new TaskTitleNotFound("Something went wrong");
		}
		
	}
//	
//	java -Xms64m -Xmx1024m Foo
//	Your process starts with 64mb of memory, and if and when it needs more (up to 1024m), it will allocate memory. totalMemory()
}
