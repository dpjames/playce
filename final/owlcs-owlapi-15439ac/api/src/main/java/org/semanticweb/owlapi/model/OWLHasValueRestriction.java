/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.model;

import java.util.stream.Stream;

/**
 * @param <V> the value type
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 2.0.0
 */
public interface OWLHasValueRestriction<V extends OWLObject> extends OWLRestriction, HasFiller<V> {

    @Override
    default Stream<?> components() {
        return Stream.of(getProperty(), getFiller());
    }

    @Override
    default int initHashCode() {
        int hash = hashIndex();
        hash = OWLObject.hashIteration(hash, getProperty().hashCode());
        return OWLObject.hashIteration(hash, getFiller().hashCode());
    }

    /**
     * @return the value
     * @deprecated use getFiller instead
     */
    @Deprecated
    default V getValue() {
        return getFiller();
    }

    /**
     * A convenience method that obtains this restriction as an existential restriction with a
     * nominal filler.
     *
     * @return The existential equivalent of this value restriction. simp(HasValue(p a)) = some(p
     * {a})
     */
    OWLClassExpression asSomeValuesFrom();
}