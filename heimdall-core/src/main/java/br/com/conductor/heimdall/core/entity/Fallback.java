package br.com.conductor.heimdall.core.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fallback {

	public static final String KEY = "Fallback";
	
	private Integer attempts;
	private LocalDateTime lastRequest;
}
