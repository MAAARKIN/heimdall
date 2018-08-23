package br.com.conductor.heimdall.core.repository;

import javax.annotation.PostConstruct;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.twsoftware.alfred.object.Objeto;

public abstract class MongoRepository {

	private String databaseName;

	@Autowired
	private Property property;

	private MongoClient client;

	@PostConstruct
	public void init() {
		this.databaseName = property.getMongo().getDataBase();
	}

	private Datastore datastore() {

		Morphia morphia = new Morphia();

		if (this.client == null) {
			this.createMongoClient();
		}

		return morphia.createDatastore(this.client, this.databaseName);
	}

	private void createMongoClient() {

		if (Objeto.notBlank(property.getMongo().getUrl())) {

			MongoClientURI uri = new MongoClientURI(property.getMongo().getUrl());
			this.client = new MongoClient(uri);
		} else {
			ServerAddress address = new ServerAddress(property.getMongo().getServerName(), property.getMongo().getPort().intValue());
			MongoCredential mongoCredential = MongoCredential.createCredential(property.getMongo().getUsername(), property.getMongo().getUsername(), property.getMongo().getPassword().toCharArray());
			MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
			this.client = new MongoClient(address, mongoCredential, mongoClientOptions);
		}
	}
}
