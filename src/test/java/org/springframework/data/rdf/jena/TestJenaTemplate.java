/*
* Copyright (c) 2011 by Al Baker, Michael Soren
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.springframework.data.rdf.jena;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * @author Al Baker
 * @author Michael Soren
 *
 */
public class TestJenaTemplate {

	private Model model;
	private JenaTemplate template;
	
	private String resource = "http://www.test.com/test";
	private String property = "http://www.test.com/testprop";
	private String value = "test";
	private String resource2 = "http://www.test.com/test2";
	private String connect = "http://www.test.com/connect";
	private String value2 = "test2";
	private String property2 = "http://www.test.com/testprop2";
	private String connectProperty = "http://www.test.com/connectIt";
	private Date date = new Date();
	private URI uri;
	private URI uri2;
	
	@Before
	public void setUp() throws Exception {
		model = ModelFactory.createDefaultModel();
		template = new JenaTemplate();
		template.setModel(model);
		uri = new URI("urn:something");
		uri2 = new URI("urn:something2");
	}


	@Test
	public void testExists() {
		
		model.createResource(resource).addProperty(model.createProperty(property), value);
		model.createResource(resource2).addProperty(model.createProperty(property2), value2);
		template.connect(resource, connectProperty, resource2);
		
		assertTrue(template.exists(resource, property, value));
		assertFalse(template.exists(resource, property, "abcdefghijklmnopqrstuvwxyz"));
		assertTrue(template.exists(resource, connectProperty, resource2));
		assertTrue(template.exists(resource));
		assertTrue(template.exists(resource2));
		assertTrue(template.exists(resource, property));
		assertTrue(template.exists(resource, connectProperty));
	}

	@Test
	public void testAddStringStringString() {
		template.add(resource, property, value);
		assertTrue(template.exists(resource, property, value));
	}

	@Test
	public void testAddStringStringDate() {
		template.add(resource, property, date);
		assertTrue(model.size() == 1);
	}

	@Test
	public void testAddURIStringURI() {
		template.add(uri, property, uri2);
		assertTrue(model.size() == 1);
	}

	@Test
	public void testAddStringStringURI() {
		template.add(resource, property, uri);
		assertTrue(model.size() == 1);
	}

	@Test
	public void testSetSingletonStringStringString() {
		template.add(resource, property, value);
		template.setSingleton(resource, property, "test2");
		assertFalse(template.exists(resource, property, value));
		assertTrue(template.exists(resource, property, "test2"));
	}


	@Test
	public void testRemoveResource() {
		template.add(resource, property, value);
		template.removeResource(resource);
		assertFalse(template.exists(resource, property, value));
	}

	@Test
	public void testRemoveProperty() {
		template.add(resource, property, value);
		template.add(resource, property2, value2);
		template.removeProperty(resource, property);
		assertTrue(template.exists(resource, property2, value2));
		assertFalse(template.exists(resource, property, value));
	}

	@Test
	public void testRemovePropertyValue() {
		template.add(resource, property, value);
		template.add(resource, property, value2);
		template.removePropertyValue(resource, property, value);
		assertTrue(template.exists(resource, property, value2));
		assertFalse(template.exists(resource, property, value));
	}

	@Test
	public void testConnect() {
		template.add(resource, property, value);
		template.add(resource2, property2, value2);
		template.connect(resource, connect, resource2);
		
		assertTrue(template.exists(resource, property, value));
		assertTrue(template.exists(resource2, property2, value2));
		assertTrue(template.exists(resource, connect, null));
		assertEquals(template.getModel().size(), (long) 3 );
	}

	@Test
	public void testDisconnect() {
		template.add(resource, property, value);
		template.add(resource2, property2, value2);
		template.connect(resource, connect, resource2);
		
		assertTrue(template.exists(resource, property, value));
		assertTrue(template.exists(resource2, property2, value2));
		assertTrue(template.exists(resource, connect, null));
		assertEquals(template.getModel().size(), (long) 3 );
		template.disconnect(resource, connect, resource2);
		assertFalse(template.exists(resource, connect, null));
		assertEquals(template.getModel().size(), (long) 2 );
	}

}
