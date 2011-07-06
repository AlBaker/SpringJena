/*
* Copyright (c) 2011 by Al Baker and Michael Soren
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

import com.hp.hpl.jena.query.ResultSet;

/**
 * <code>SolutionMapper<T></code>
 * 
 * Template interface for abstracting how SPARQL result sets are mapped to other objects
 * Inspired by Spring's jdbcTemplate
 * 
 * @author Al Baker
 * @author Michael Soren
 *
 */
public interface SolutionMapper<T> {

	/**
	 * <code>mapSelect</code>
	 * 
	 * Method to map a Jena SELECT ResultSet to an object of type T
	 * 
	 * @param rs Jena ResultSet
	 * @param rowNum represents the current row number (not really used)
	 * @return template type T
	 */
	T mapSelect(ResultSet rs, int rowNum);
	
}