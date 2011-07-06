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
package org.springframework.data.rdf.sparql;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;



/**
 * @author Al Baker
 * @author Michael Soren
 *
 */
public class TestSparqlTemplate {

	Model m;
	SparqlTemplate tmp;
	
	@Before
	public void setUp() throws Exception {
		m = ModelFactory.createDefaultModel();
		m.createResource("urn:something").addProperty(m.createProperty("urn:test"), "testval");
		m.createResource("urn:something2").addProperty(m.createProperty("urn:test2"), "testval2");
		tmp = new SparqlTemplate();
		tmp.setModel(m);
	}

	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execSelectList(java.lang.String, org.springframework.data.rdf.sparql.SolutionMapper)}.
	 */
	@Test
	public void testExecSelectList() {

		
		String sparql = "SELECT ?x ?y ?z WHERE { ?x ?y ?z }";
		List<String> list = tmp.execSelectList(sparql, new SolutionMapper<String>() {

			public String mapSelect(ResultSet rs, int rowNum) {
				QuerySolution sln = rs.nextSolution();
				RDFNode x = sln.get("x");
				RDFNode y = sln.get("y");
				RDFNode z = sln.get("z");
				return x.toString() + "|" + y.toString() + "|" + z.toString();
			} });
		
		assertTrue(list.size() == 2);
	}

	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execSelectOne(java.lang.String, org.springframework.data.rdf.sparql.SolutionMapper)}.
	 */
	@Test
	public void testExecSelectOne() {
		
		String sparql = "SELECT ?x ?y ?z WHERE { ?x ?y ?z }";

		MyDomain obj  = tmp.execSelectOne(sparql, new SolutionMapper<MyDomain>() {

			@Override
			public MyDomain mapSelect(ResultSet rs, int rowNum) {
				QuerySolution sln = rs.nextSolution();
				RDFNode x = sln.get("x");
				RDFNode y = sln.get("y");
				RDFNode z = sln.get("z");
				MyDomain result = new MyDomain();
				result.resource = x.toString();
				result.predicate = y.toString();
				result.value = z.toString();
				
				return result;
			}
			
		});
		assertNotNull(obj.resource);
		assertNotNull(obj.predicate);
		assertNotNull(obj.value);
	}

	class MyDomain { 
		public String resource;
		public String predicate;
		public String value;
	}
	
	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execSelectMap(java.lang.String, org.springframework.data.rdf.sparql.SolutionDimensionalMapper)}.
	 */
	@Test
	public void testExecSelectMap() {
		
		String sparql = "SELECT ?x ?y ?z WHERE { ?x ?y ?z }";
		Map<String, String> list = tmp.execSelectMap(sparql, new SolutionDimensionalMapper<String,String>() {

			public Map<String, String> mapSelect(ResultSet rs, int rowNum) {
				QuerySolution sln = rs.nextSolution();
				RDFNode x = sln.get("x");
				RDFNode y = sln.get("y");
				RDFNode z = sln.get("z");
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("x", x.toString());
				result.put("y", y.toString());
				result.put("z", z.toString());
				return result;
			} });
		
		assertTrue(list.size() == 3);
	}

	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execConstruct(java.lang.String)}.
	 */
	@Test
	public void testExecConstruct() {
		String sparql = "CONSTRUCT { ?x ?y ?z } WHERE { ?x ?y ?z } ";
		Model m = tmp.execConstruct(sparql);
		assertTrue(m.size() > 0);
	}

	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execSelectStringMap(java.lang.String)}.
	 */
	@Test
	public void testExecSelectStringMap() {
		
		String sparql = "SELECT ?x ?y ?z WHERE { ?x ?y ?z }";
		Map<String, String> list = tmp.execSelectStringMap(sparql);
		assertTrue(list.size() == 3);
	}

	/**
	 * Test method for {@link org.springframework.data.rdf.sparql.SparqlTemplate#execSelectString(java.lang.String)}.
	 */
	@Test
	public void testExecSelectString() {
		
		String sparql = "SELECT ?z WHERE { <urn:something> ?y ?z }";
		String result  = tmp.execSelectString(sparql);
		assertNotNull(result);
		assertEquals(result, "testval");
	}


}
