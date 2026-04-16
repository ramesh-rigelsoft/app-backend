package com.rigel.app.exception;

public class TaskTitleNotFound extends RuntimeException{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7017079417584249462L;

	public TaskTitleNotFound(String msg){
		super(msg);
	}
	
	public TaskTitleNotFound(){
	}

}
