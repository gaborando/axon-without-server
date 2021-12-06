package com.example.demoaxonnoserver.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FooEditedEvent
{
	private String fooId;
	private String value;
}
