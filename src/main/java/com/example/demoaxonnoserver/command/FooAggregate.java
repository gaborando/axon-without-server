package com.example.demoaxonnoserver.command;

import com.example.demoaxonnoserver.api.FooCreateCommand;
import com.example.demoaxonnoserver.api.FooCreatedEvent;
import com.example.demoaxonnoserver.api.FooEditCommand;
import com.example.demoaxonnoserver.api.FooEditedEvent;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
@Profile("command")
public class FooAggregate
{
	private static final Logger LOGGER = LogManager.getLogger(FooAggregate.class);

	@AggregateIdentifier
	private String fooId;

	@CommandHandler
	public FooAggregate(FooCreateCommand command){
		LOGGER.info("FooCreateCommand: {}", command);
		apply(new FooCreatedEvent(command.getFooId(), command.getValue()));
	}

	@CommandHandler
	public void handle(FooEditCommand command){
		LOGGER.info("FooEditCommand: {}", command);
		apply(new FooEditedEvent(command.getFooId(), command.getValue()));
	}

	@EventSourcingHandler
	public void on(FooCreatedEvent event){
		fooId = event.getFooId();
	}

	@EventSourcingHandler
	public void on(FooEditedEvent event){
	}

}
