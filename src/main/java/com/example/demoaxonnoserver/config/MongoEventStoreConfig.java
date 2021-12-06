package com.example.demoaxonnoserver.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoEventStoreConfig
{

	@Bean
	public MongoClient mongoEventStoreClient(@Value("${es.mongo.connection.string}") String connectionString)
	{
		return MongoClients.create(connectionString);
	}


	// The `MongoEventStorageEngine` stores each event in a separate MongoDB document
	@Bean
	public EventStorageEngine storageEngine(MongoClient mongoEventStoreClient, Serializer serializer)
	{
		return MongoEventStorageEngine
				.builder()
				.mongoTemplate(
						DefaultMongoTemplate
								.builder()
								.mongoDatabase(mongoEventStoreClient)
								.build()
				)
				.eventSerializer(serializer)
				.snapshotSerializer(serializer)
				.build();
	}

	// The Event store `EmbeddedEventStore` delegates actual storage and retrieval of events to an `EventStorageEngine`.
	@Bean
	public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration)
	{
		return EmbeddedEventStore.builder()
				.storageEngine(storageEngine)
				.messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
				.build();
	}
}
