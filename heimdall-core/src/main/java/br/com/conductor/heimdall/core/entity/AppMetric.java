package br.com.conductor.heimdall.core.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Data;

@Entity("app")
@Data
public class AppMetric {

	@Id
	private ObjectId id;
	private String clientId;
	private String name;
	private Long total;
}
