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

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Al Baker
 * @author Michael Soren
 *
 */
public class JenaTemplate {

	private Model model;

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}
	
	/**
	 * <code>exists</code>
	 * @param args resource, predicate, object - tests if any of those exist
	 * @return boolean
	 */
	public boolean exists(String... args) {
		
		if (args == null) {
			return false;
		}
		
		if (args.length == 0) {
			return false;
		}
		String resource = null;
		String property = null;
		String object = null;
		
		int index = 0;
		for (String arg : args) {
				
				switch (index) {
				case 0: 
					resource = arg;
					break;
				case 1: 
					property = arg;
					break;
				case 2: 
					object = arg;
					break;
				}
			 
			index++;
		}
 		
		Resource r = null;
		Property p = null;
		Literal l = null;
		Resource r2 = null;
		
		if (object != null) {
			r = model.createResource(resource);
			p = model.createProperty(property);
			l = model.createLiteral(object);
			r2 = model.createResource(object);
			
			if (model.contains(r, p, l)) {
				return true;
			} else if (model.contains(r, p, r2)) {
				return true;
			} else {
				return false;
			}
		} else if (property != null) {
			r = model.createResource(resource);
			p = model.createProperty(property);
			if (model.contains(r, p)) { 
				return true;
			} else {
				return false;
			}
		} else  {
			r = model.createResource(resource);
			if (model.containsResource(r)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * <code>add</code>
	 * @param resource URI
	 * @param predicate predicate URI
	 * @param value value - literal
	 */
	public void add(String resource, String predicate, String value) {
		
		Resource r = model.createResource(resource);
		
		r.addProperty(model.createProperty(predicate), value);
		
	}
	
	/**
	 * <code>add</code>
	 * @param resource resource URI
	 * @param predicate string
	 * @param date
	 */
	public void add(String resource, String predicate, Date d) {
		Calendar c = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(d);
		Literal dateLiteral = model.createTypedLiteral(c);	
		Resource r = model.createResource(resource);
		r.addProperty(model.createProperty(predicate), dateLiteral);
	}
	
	/**
	 * <code>add</code>
	 * Connect two resources with a predicate using URIs to indicate resources
	 * @param resource1
	 * @param predicate
	 * @param resource2
	 */
	public void add(URI resource1, String predicate, URI resource2) {
		Resource r = model.createResource(resource1.toString());
		Resource r2 = model.createResource(resource2.toString());
		r.addProperty(model.createProperty(predicate), r2);
	}
	
	/**
	 * <code>add</code>
	 * Connect two resources with a predicate using URIs to indicate resources
	 * @param resource1
	 * @param predicate
	 * @param resource2
	 */
	public void add(String resource1, String predicate, URI resource2) {
		Resource r = model.createResource(resource1);
		Resource r2 = model.createResource(resource2.toString());
		r.addProperty(model.createProperty(predicate), r2);
	}
	
	/**
	 * <code>setSingleton</code>
	 * Removes existing predicate first and then adds the new one
	 * @param resource
	 * @param predicate
	 * @param value
	 */
	public void setSingleton(String resource, String predicate, String value) {
		Resource r = model.createResource(resource);
		Property p = model.createProperty(predicate);
		model.removeAll(r, p, null);
		
		r.addProperty(p, value);
	}
	
	/**
	 * <code>setSingleton</code>
	 * Removes existing predicate first and then adds the new one
	 * @param resource
	 * @param predicate
	 * @param value
	 */
	public void setSingleton(String resource, String predicate, Date value) {
		Resource r = model.createResource(resource);
		Property p = model.createProperty(predicate);
		model.removeAll(r, p, null);
		add(resource, predicate, value);
	}
	
	/**
	 * <code>removeResource</code>
	 * @param uri
	 */
	public void removeResource(String uri) {
		Resource r = model.createResource(uri);
		model.removeAll(r, null, null);
	}
	
	/**
	 * <code>removeProperty</code>
	 * Remove a property based on resource URI and property URI
	 * @param uri of the resource
	 * @param property resource
	 */
	public void removeProperty(String uri, String property) {
		Resource r = model.createResource(uri);
		Property p = model.createProperty(property);
		model.removeAll(r, p, null);
	}
	
	/**
	 * <code>removePropertyValue</code>
	 * Remove a full triple, including value
	 * @param uri
	 * @param property
	 * @param value
	 */
	public void removePropertyValue(String uri, String property, String value) {
		Resource r = model.createResource(uri);
		Property p = model.createProperty(property);
		Literal l = model.createLiteral(value);
		model.removeAll(r, p, l);		
	}
	
	/**
	 * <code>connect</code>
	 * Connect two resources with a predicate
	 * @param resource1
	 * @param predicate
	 * @param resource2
	 */
	public void connect(String resource1, String predicate, String resource2) {
		Resource r = model.createResource(resource1);
		Resource r2 = model.createResource(resource2);
		r.addProperty(model.createProperty(predicate), r2);
	}
	
	/**
	 * <code>disconnect</code>
	 * disconnect two resources based on a predicate enum
	 * @param resource1
	 * @param predicate
	 * @param resource2
	 */
	public void disconnect(String resource1, String predicate, String resource2) {
		Resource r = model.createResource(resource1);
		Resource r2 = model.createResource(resource2);
		Property p = model.createProperty(predicate);
		model.removeAll(r, p, r2);
	}
	
}
