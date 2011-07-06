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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;



/**
 * <code>SparqlTemplate</code>
 * 
 * Template design for abstracting the details of working with Jena ARQ's SPARQL
 * query API.  Specifically the repetitive Query, QueryFActory, QueryExecution, and QueryExecutionFactory
 * 
 * Inspired by Spring's jdbcTemplate
 * 
 * Implements the template design pattern for the main Jena methods (e.g. execSelect)
 * 
 * Relies upon the SolutionMapper<T> interface for returning the mapped result
 * 
 * @author Al Baker
 * @author Michael Soren
 *
 */
public class SparqlTemplate {

	
	/**
	 * Constructor - Jena Model
	 * @param model
	 */
	SparqlTemplate(Model model) {
		this.model = model;
	}
	
	SparqlTemplate() { }

	private Model model;

	/**
	 * <code>setModel</code>
	 * 
	 * Setter to set the model that will be queried
	 * 
	 * @param model Jena Model Interface
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * <code>getModel</code>
	 * 
	 * getter method to return the current Jena model being queried.  
	 * 
	 * @return Model Jena Model interface
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * <code>execSelectList</code>
	 * 
	 * Template design pattern for simplifying SPARQL queries with ARQ
	 * Inspired by Spring's jdbcTemplate
	 * Differs the mapping algorithm to the SolutionMapper<T> interface
	 * 
	 * Additional comments:
	 *   - This implementation generates the list at this point, a more advanced design 
	 *     would be to return an iterator and not iterate through the results until the client
	 *     of this code was invoked, i.e. lazy-loading the results until they were absolutely needed
	 *     
	 *   - The sparql query string is just a string, presumably with the right parameters already populated. 
	 *     To avoid SPARQL-Injection, we should consider some automatic way of cleansing this string similar
	 *     to the JDBC PreparedStatement object
	 * 
	 * @param <T> Template for the List of objects to be returned
	 * @param sparql the String representing the SPARQL query
	 * @param mapper the Implementation of SolutionMapper<T>
	 * @return List<T> which is mapped to the results of the query
	 */
	public <T> List<T> execSelectList(String sparql, SolutionMapper<T> mapper) {
		if (sparql == null || sparql.equals(""))
			return null;
		QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(sparql, Syntax.syntaxARQ), model);
		ArrayList<T> list = new ArrayList<T>();
		try {
			for (ResultSet rs = qe.execSelect(); rs.hasNext() ; ) {
				list.add(mapper.mapSelect(rs, rs.getRowNumber()));
			}
		} finally {
			qe.close();
		}
		return list;
	}

	public <T> T execSelectOne(String sparql, SolutionMapper<T> mapper) {
		if (sparql == null || sparql.equals("")) {
			return null;
		}

		QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(sparql, Syntax.syntaxARQ), model);
		try {
			ResultSet rs = qe.execSelect();
			if (rs.hasNext()) {
				return mapper.mapSelect(rs, rs.getRowNumber());
			} else {
				return null;
			}
		} finally {
			qe.close();
		}

	}

	/**
	 * <code>execSelectMap</code>
	 * @param <T> Key
	 * @param <V> Value
	 * @param sparql Sparql query to be executed
	 * @param mapper SolutionDimensionalMapper
	 * @return combined map of all the results (HashMap)
	 */
	public <T, V> Map<T, V> execSelectMap(String sparql, SolutionDimensionalMapper<T, V> mapper) {
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		Map<T, V> list = new HashMap<T, V>();
		try {
			for (ResultSet rs = qe.execSelect(); rs.hasNext() ; ) {
				list.putAll(mapper.mapSelect(rs, rs.getRowNumber()));
			}
		} finally {
			qe.close();
		}
		return list;
	}

	/**
	 * <code>execConstruct</code>
	 * 
	 * Template method for executing a CONSTRUCT statement
	 * No mapper is used as the mapping is performed in the SPARQL
	 * 
	 * @param sparql
	 * @return new Model
	 */
	public Model execConstruct(String sparql) {
		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		Model m = qe.execConstruct();
		qe.close();
		return m;
	}


	/**
	 * <code>mapSolutionMapper creates a map for the next solution</code>
	 * whose keys are variable names and values are the String values for those variables
	 * @return Map<String, String> of variable names and values for the solution
	 */
	class mapSolutionMapper implements SolutionMapper<Map<String, String>> {
		public Map<String, String> mapSelect(ResultSet rs, int number) {
			QuerySolution sol = rs.nextSolution();
			Map<String, String> row = new HashMap<String, String>();
			for (Iterator<String> varNames = sol.varNames(); varNames.hasNext(); ) {
				String varName = varNames.next();
				row.put(varName, sol.get(varName).toString());
			}
			return row;
		}
	}

	/**
	 * <code>mapSolutionMapper creates a map for the next solution</code>
	 * whose keys are variable names and values are the (possibly typed) values for those variables
	 * @return Map<String, Object> of variable names and values for the solution
	 */
	class genericMapSolutionMapper implements SolutionMapper<Map<String, Object>> {
		public Map<String, Object> mapSelect(ResultSet rs, int number) {
			QuerySolution sol = rs.nextSolution();
			Map<String, Object> row = new HashMap<String, Object>();
			for (Iterator<String> varNames = sol.varNames(); varNames.hasNext(); ) {
				String varName = varNames.next();
				RDFNode varNode = sol.get(varName);
				row.put(varName, (varNode.isLiteral() ? varNode.asLiteral().getValue() : varNode.toString()));
			}
			return row;
		}
	}
	
	/**
	 * <code>execSelect(String query)</code>
	 * @param sparql - SELECT based SPARQL to execute
	 * @return map of parameters in the result set
	 */
	public List<Map<String, Object>> execSelectGenericMap(String sparql) {
		return execSelectList(sparql, new genericMapSolutionMapper());
	}
	
	public Map<String, Object> execSelectSingleGenericMap(String sparql) {
		return execSelectOne(sparql, new genericMapSolutionMapper());
	}


	/**
	 * <code>execSelectOne</code>
	 * @param model - model to be queried
	 * @param sparql - SELECT based sparql query
	 * @return single map of property to value
	 */
	public Map<String, String> execSelectStringMap(String sparql) {
		if (sparql == null || sparql.equals(""))
			return null;
		Map<String, String> result = new HashMap<String, String>();
		QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(sparql), model);
		try {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution sol = rs.nextSolution();
				for (Iterator<String> varNames = sol.varNames(); varNames.hasNext(); ) {
					String varName = varNames.next();
					result.put(varName, sol.get(varName).toString());
				}
			}
		} finally {
			qe.close();
		}
		return result;
	}

	/**
	 * <code>execSelectSingleString</code>
	 * @param sparql - SELECT based sparql query
	 * @return single string of the sparql query result
	 */
	public String execSelectString(String sparql) {
		if (sparql == null || sparql.equals(""))
			return null;
		String result = null;
		QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(sparql, Syntax.syntaxARQ), model);
		try {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution sol = rs.nextSolution();
				for (Iterator<String> varNames = sol.varNames(); varNames.hasNext(); ) {
					String varName = varNames.next();
					result = sol.get(varName).toString();
				}
			}
		} finally {
			qe.close();
		}
		return result;
	}


	/**
	 * <code>debug</code>
	 * Prints entire model attached to this sparqltemplate with
	 * ResultSetFormatter to System.out
	 */
	public void debug() {
		String sparql = "SELECT ?x ?y ?z WHERE { ?x ?y ?z} ";

		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.create(query, model);

		try {
			ResultSet rs = qe.execSelect();
			ResultSetFormatter.out(System.out, rs);
		} finally {
			qe.close();
		}

	}

}
