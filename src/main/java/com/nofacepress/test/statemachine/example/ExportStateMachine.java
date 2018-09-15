/*
 * Copyright 2018 No Face Press, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nofacepress.test.statemachine.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableWithStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.config.StateMachineFactory;

import com.nofacepress.statemachine.exporter.StateMachineLucidChartExporter;
import com.nofacepress.statemachine.exporter.StateMachinePlantUMLExporter;
import com.nofacepress.statemachine.exporter.StateMachineSCXMLExporter;

@SpringBootApplication
@EnableWithStateMachine
public class ExportStateMachine {

	@Autowired
	Handler handler;
	
	@Autowired
	ExportStateMachine( StateMachineFactory<MyStates, MyEvents> factory, Handler handler) throws Exception {

	//	System.out.println("ExportStateMachine: " + beanFactory.toString());

		//StateMachine<MyStates, MyEvents> machine = buildMachine(beanFactory) ;
		StateMachine<MyStates, MyEvents> machine = factory.getStateMachine(MyStateMachineConfiguration.MACHINE_NAME);
		machine.start();
	
		machine.sendEvent(MyEvents.RETRY);
		machine.sendEvent(MyEvents.EVENT_1);


		String filename = "statemachine.plantuml";
		StateMachinePlantUMLExporter.export(machine, null, filename);
		System.out.println("Saved state machine to " + filename);

		filename = "statemachine.scxml";
		StateMachineSCXMLExporter.export(machine, filename);
		System.out.println("Saved state machine to " + filename);

		filename = "statemachine-lucid.csv";
		StateMachineLucidChartExporter.export(machine, "State Machine", filename);
		System.out.println("Saved state machine to " + filename);

		machine = factory.getStateMachine(MyStateMachineConfiguration.MACHINE_NAME);
		machine.start();
	
		machine.sendEvent(MyEvents.RETRY);
		machine.sendEvent(MyEvents.EVENT_1);
		//System.exit(0);
	}

	public static StateMachine<MyStates, MyEvents> buildMachine(BeanFactory beanFactory) throws Exception {
	    Builder<MyStates, MyEvents> builder = StateMachineBuilder.builder();

	    builder.configureConfiguration()
	        .withConfiguration()
	     //      .machineId("myMachineId")
	        	
	            .beanFactory(beanFactory);

	    builder.configureStates()
	        .withStates().initial(MyStates.STATE_A).end(MyStates.STATE_F).states(new HashSet<MyStates>(
					Arrays.asList(MyStates.STATE_B, MyStates.STATE_C, MyStates.STATE_D, MyStates.STATE_E)));
;

	    builder.configureTransitions().withExternal().source(MyStates.STATE_A).target(MyStates.STATE_B).event(MyEvents.EVENT_1)
		.and()
		.withExternal().source(MyStates.STATE_A).target(MyStates.STATE_A).event(MyEvents.RETRY).and()

			.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_E).event(MyEvents.EVENT_3).and()
			.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_C).event(MyEvents.EVENT_2).and()
			.withExternal().source(MyStates.STATE_D).target(MyStates.STATE_B).event(MyEvents.EVENT_1).and()
			.withExternal().source(MyStates.STATE_D).target(MyStates.STATE_E).event(MyEvents.EVENT_3).and()
			.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_G).event(MyEvents.EVENT_4);

	    return builder.build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ExportStateMachine.class, args);
	}
}
