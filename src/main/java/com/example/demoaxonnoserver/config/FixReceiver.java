package com.example.demoaxonnoserver.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.distributed.*;
import org.axonframework.extensions.jgroups.DistributedCommandBusProperties;
import org.axonframework.extensions.jgroups.commandhandling.JChannelFactory;
import org.axonframework.extensions.jgroups.commandhandling.JGroupsConnectorFactoryBean;
import org.axonframework.serialization.Serializer;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.util.MessageBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FixReceiver
{
	@Autowired
	private DistributedCommandBusProperties properties;

	@ConditionalOnMissingBean({CommandRouter.class, CommandBusConnector.class})
	@Bean
	public JGroupsConnectorFactoryBean jgroupsConnectorFactoryBean(
			@Qualifier("messageSerializer") Serializer messageSerializer,
			@Qualifier("localSegment") CommandBus localSegment,
			RoutingStrategy routingStrategy,
			@Autowired(required = false) ConsistentHashChangeListener consistentHashChangeListener) {
		System.setProperty("jgroups.tunnel.gossip_router_hosts", properties.getJgroups().getGossip().getHosts());
		System.setProperty("jgroups.bind_addr", String.valueOf(properties.getJgroups().getBindAddr()));
		System.setProperty("jgroups.bind_port", String.valueOf(properties.getJgroups().getBindPort()));

		JGroupsConnectorFactoryBean jGroupsConnectorFactoryBean = new JGroupsConnectorFactoryBean();
		jGroupsConnectorFactoryBean.setClusterName(properties.getJgroups().getClusterName());
		jGroupsConnectorFactoryBean.setLocalSegment(localSegment);
		jGroupsConnectorFactoryBean.setSerializer(messageSerializer);
		jGroupsConnectorFactoryBean.setChannelFactory(
				new JChannelFactory(){
					@Override
					public JChannel createChannel() throws Exception {
						return new JChannel(properties.getJgroups().getConfigurationFile()){

							private Message toMyMessage(Message msg){
								Message m = new Message(){
									@Override
									public <T> T getObject()
									{
										return super.getObject(receiver.getClass().getClassLoader());
									}
								};
								m.setBuffer(msg.getBuffer());
								m.setDest(msg.getDest());
								m.setFlag(msg.getFlags());
								m.setSrc(msg.getSrc());
								m.setTransientFlag(msg.getTransientFlags());
								return m;
							}

							@Override
							public Object up(Message msg)
							{
								return super.up(toMyMessage(msg));
							}


							@Override
							public JChannel up(MessageBatch batch)
							{
								MessageBatch mb = new MessageBatch(
										batch.getDest(),
										batch.getSender(),
										batch.getClusterName(),
										batch.multicast(),
										batch.getMode(),
										batch.getCapacity()
								);
								batch.forEach(m -> mb.add(toMyMessage(m)));
								return super.up(mb);
							}
						};
					}
				}
		);
		if (consistentHashChangeListener != null) {
			jGroupsConnectorFactoryBean.setConsistentHashChangeListener(consistentHashChangeListener);
		}
		jGroupsConnectorFactoryBean.setRoutingStrategy(routingStrategy);
		return jGroupsConnectorFactoryBean;
	}
}
