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

import java.util.Map;

import com.hp.hpl.jena.query.ResultSet;

/**
 * SolutionDimensionalMapper
 * 
 * Map a result set based on the key and value of the result set
 * 
 * @author Al Baker
 * @author Michael Soren
 *
 */
public interface SolutionDimensionalMapper<T, V> {

	Map<T, V> mapSelect(ResultSet rs, int rowNum);
	
}
