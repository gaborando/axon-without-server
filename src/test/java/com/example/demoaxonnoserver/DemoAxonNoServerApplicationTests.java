package com.example.demoaxonnoserver;

import com.example.demoaxonnoserver.api.FooCreateCommand;
import com.example.demoaxonnoserver.api.FooEditCommand;
import com.example.demoaxonnoserver.api.FooEditedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
class DemoAxonNoServerApplicationTests
{

	@Autowired
	private CommandGateway commandGateway;

	@Test
	void contextLoads()
	{
		String fooId = UUID.randomUUID().toString();
		commandGateway.sendAndWait(new FooCreateCommand(fooId, "a"));
		commandGateway.sendAndWait(new FooEditCommand(fooId, "b"));
		commandGateway.sendAndWait(new FooEditCommand(fooId, "c"));
	}

}
