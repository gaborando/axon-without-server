package com.example.demoaxonnoserver.query;

import com.example.demoaxonnoserver.api.FooCreatedEvent;
import com.example.demoaxonnoserver.api.FooEditedEvent;
import com.example.demoaxonnoserver.api.FooFindAllQuery;
import com.example.demoaxonnoserver.api.FooFindByIdQuery;
import com.example.demoaxonnoserver.command.FooAggregate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service
@Profile("query")
public class FooProjection
{
	private static final Logger LOGGER = LogManager.getLogger(FooProjection.class);

	private final HashMap<String, Foo> memory = new HashMap<String, Foo>();

	@EventHandler
	public void on(FooCreatedEvent event){
		LOGGER.info("FooCreatedEvent: {}", event);
		memory.putIfAbsent(event.getFooId(), new Foo(event.getFooId(),event.getValue()));
	}

	@EventHandler
	public void on(FooEditedEvent event){
		LOGGER.info("FooEditedEvent: {}", event);
		memory.put(event.getFooId(), new Foo(event.getFooId(),event.getValue()));
	}

	@QueryHandler
	public Collection<Foo> query(FooFindAllQuery query){
		LOGGER.info("FooFindAllQuery: {}", query);
		return memory.values();
	}
	@QueryHandler
	public Foo query(FooFindByIdQuery query){
		LOGGER.info("FooFindByIdQuery: {}", query);
		return memory.get(query.getFooId());
	}
}
