package com.example.demoaxonnoserver;

import com.example.demoaxonnoserver.api.FooCreateCommand;
import com.example.demoaxonnoserver.api.FooEditCommand;
import com.example.demoaxonnoserver.api.FooFindAllQuery;
import com.example.demoaxonnoserver.query.Foo;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;

import static org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf;

@Component
@Profile("script")
public class Runner implements CommandLineRunner
{
	@Autowired
	private CommandGateway commandGateway;

	@Autowired
	private QueryGateway queryGateway;


	@Override
	public void run(String... args) throws Exception
	{
		Scanner scanner = new Scanner(System.in);
		while (!scanner.next().equals("q"))
		{
			String fooId = UUID.randomUUID().toString();
			commandGateway.sendAndWait(new FooCreateCommand(fooId, "a"));
			commandGateway.sendAndWait(new FooEditCommand(fooId, "b"));
			commandGateway.sendAndWait(new FooEditCommand(fooId, "c"));



			var resp = queryGateway.query(new FooFindAllQuery(), multipleInstancesOf(Foo.class)).get();
			resp.forEach(System.out::println);
		}

		System.exit(0);




	}
}
