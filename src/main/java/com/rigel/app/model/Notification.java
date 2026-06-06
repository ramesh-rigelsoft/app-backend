package com.rigel.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Setter
@Getter
@Entity
@Table(name="NOTIFICATION")
public class Notification implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7016888389920150869L;
	/// its not be Serialize

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;
	
	private String notificationType;
	private String type;
	private String time;
	private String description;
	private boolean status;	
	private boolean seenStatus;	
	private LocalDateTime seenAt;
	private int ownerId;	
	private LocalDateTime createdAt;
}
