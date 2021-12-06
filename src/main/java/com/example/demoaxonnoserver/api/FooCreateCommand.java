package com.example.demoaxonnoserver.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FooCreateCommand
{
	@TargetAggregateIdentifier
	private String fooId;
	private String value;
}
