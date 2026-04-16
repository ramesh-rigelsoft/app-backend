package com.rigel.app.exception;

public class TaskTitleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6059265599816983339L;

	public TaskTitleException(String msg){
		super(msg);
	}
	
	public TaskTitleException(){
	}
}
