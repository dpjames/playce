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
package org.semanticweb.owlapi.io;

import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.tukaani.xz.XZInputStream;

/**
 * An ontology document source which can read from a XZ stream.
 *
 * @author ses
 * @since 4.0.2
 */
public class XZStreamDocumentSource extends OWLOntologyDocumentSourceBase {

    /**
     * Constructs an input source which will read an ontology from a representation from the
     * specified file.
     *
     * @param is The stream that the ontology representation will be read from.
     */
    public XZStreamDocumentSource(InputStream is) {
        this(is, IRI.getNextDocumentIRI("xzinputstream:ontology"), null, null);
    }

    /**
     * Constructs an input source which will read an ontology from a representation from the
     * specified stream.
     *
     * @param stream The stream that the ontology representation will be read from.
     * @param documentIRI The document IRI
     * @param format ontology format
     * @param mime mime type
     */
    public XZStreamDocumentSource(InputStream stream, IRI documentIRI,
        @Nullable OWLDocumentFormat format, @Nullable String mime) {
        super(documentIRI, () -> new XZInputStream(new ByteArrayInputStream(toByteArray(stream))),
            format, mime);
    }
}
