/*
 * This file is part of the OWL API.
 * 
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * 
 * Copyright (C) 2011, The University of Queensland
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see http://www.gnu.org/licenses/.
 * 
 * 
 * Alternatively, the contents of this file may be used under the terms of the Apache License,
 * Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable
 * instead of those above.
 * 
 * Copyright 2011, The University of Queensland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.semanticweb.owlapi.rio;

import static org.semanticweb.owlapi.utilities.OWLAPIPreconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.semanticweb.owlapi.annotations.HasPriority;
import org.semanticweb.owlapi.formats.RioRDFDocumentFormatFactory;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.OWLParserParameters;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.util.AnonymousNodeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@HasPriority(7)
public class RioParserImpl implements OWLParser, RioParser {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RioParserImpl.class);
    private static final RIOAnonymousNodeChecker CHECKER = new RIOAnonymousNodeChecker();
    private final RioRDFDocumentFormatFactory owlFormatFactory;

    /**
     * @param nextFormat format factory
     */
    public RioParserImpl(RioRDFDocumentFormatFactory nextFormat) {
        owlFormatFactory = checkNotNull(nextFormat, "nextFormat cannot be null");
    }

    @Override
    public RioRDFDocumentFormatFactory getSupportedFormat() {
        return owlFormatFactory;
    }

    protected String baseIRI(final OWLOntology ontology) {
        String baseUri = "urn:default:baseUri:";
        // Override the default baseUri for non-anonymous ontologies
        if (!ontology.getOntologyID().isAnonymous()
            && ontology.getOntologyID().getDefaultDocumentIRI().isPresent()) {
            baseUri = ontology.getOntologyID().getDefaultDocumentIRI().get().toString();
        }
        return baseUri;
    }

    @Override
    public OWLDocumentFormat parse(RioMemoryTripleSource r, OWLParserParameters p) {
        RioOWLRDFConsumerAdapter consumer = new RioOWLRDFConsumerAdapter(p, CHECKER);
        consumer.setOntologyFormat(owlFormatFactory.createFormat());
        RioParserRDFHandler handler = new RioParserRDFHandler(consumer);
        Iterator<Statement> statementsIterator = r.getStatementIterator();
        handler.startRDF();
        r.getNamespaces().forEach(handler::handleNamespace);
        while (statementsIterator.hasNext()) {
            handler.handleStatement(statementsIterator.next());
        }
        handler.endRDF();
        return handler.consumer.getOntologyFormat();
    }

    @Override
    public OWLDocumentFormat parse(Reader r, OWLParserParameters p) {
        return parseStream(r, p);
    }

    @Override
    public OWLDocumentFormat parse(InputStream in, OWLParserParameters p) {
        return parseStream(in, p);
    }

    private OWLDocumentFormat parseStream(Object r, OWLParserParameters p) {
        long rioParseStart = System.currentTimeMillis();
        try {
            RioOWLRDFConsumerAdapter consumer = new RioOWLRDFConsumerAdapter(p, CHECKER);
            consumer.setOntologyFormat(owlFormatFactory.createFormat());
            RioParserRDFHandler handler = new RioParserRDFHandler(consumer);
            final RDFParser createParser = Rio.createParser(owlFormatFactory.getRioFormat());
            createParser.getParserConfig()
                .addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
            createParser.getParserConfig()
                .addNonFatalError(BasicParserSettings.VERIFY_LANGUAGE_TAGS);
            createParser.setRDFHandler(handler);
            if (r instanceof Reader) {
                createParser.parse((Reader) r, baseIRI(p.getOntology()));
            } else {
                createParser.parse((InputStream) r, baseIRI(p.getOntology()));
            }
            return consumer.getOntologyFormat();
        } catch (final RDFHandlerException e) {
            // See sourceforge bug 3566820 for more information about this
            // branch
            if (e.getCause() != null && e.getCause().getCause() != null
                && e.getCause().getCause() instanceof UnloadableImportException) {
                throw (UnloadableImportException) e.getCause().getCause();
            } else {
                throw new OWLParserException(e);
            }
        } catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
            throw new OWLParserException(e.getMessage(), e);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("rioParse: timing={}",
                    Long.valueOf(System.currentTimeMillis() - rioParseStart));
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + " : " + owlFormatFactory;
    }

    private static class RIOAnonymousNodeChecker implements AnonymousNodeChecker {

        RIOAnonymousNodeChecker() {}

        @Override
        public boolean isAnonymousNode(final IRI iri) {
            // HACK: FIXME: When the mess of having blank nodes
            // represented as IRIs is
            // finished remove the genid hack below
            return anon(iri.toString());
        }

        @Override
        public boolean isAnonymousNode(final String iri) {
            // HACK: FIXME: When the mess of having blank nodes
            // represented as IRIs is
            // finished remove the genid hack below
            return anon(iri);
        }

        // TODO: apparently we should be tracking whether they
        // gave a name to the blank
        // node themselves
        @Override
        public boolean isAnonymousSharedNode(final String iri) {
            // HACK: FIXME: When the mess of having blank nodes
            // represented as IRIs is
            // finished remove the genid hack below
            return anon(iri);
        }

        boolean anon(final String iri) {
            return iri.startsWith("_:") || iri.contains("genid");
        }
    }

    private static class RioParserRDFHandler implements RDFHandler {

        private static final Logger LOG = LoggerFactory.getLogger(RioParserRDFHandler.class);
        protected final RioOWLRDFConsumerAdapter consumer;
        private final Set<Resource> typedLists = new HashSet<>();
        private final ValueFactory vf = SimpleValueFactory.getInstance();
        private long owlParseStart;

        RioParserRDFHandler(RioOWLRDFConsumerAdapter consumer) {
            this.consumer = consumer;
        }

        @Override
        public void startRDF() {
            owlParseStart = System.currentTimeMillis();
            try {
                consumer.startRDF();
            } catch (RDFHandlerException e) {
                throw new OWLParserException(e);
            }
        }

        @Override
        public void endRDF() {
            try {
                consumer.endRDF();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("owlParse: timing={}",
                        Long.valueOf(System.currentTimeMillis() - owlParseStart));
                }
            } catch (RDFHandlerException e) {
                throw new OWLParserException(e);
            }
        }

        @Override
        public void handleNamespace(@Nullable String prefix, @Nullable String uri) {
            try {
                consumer.handleNamespace(prefix, uri);
            } catch (RDFHandlerException e) {
                throw new OWLParserException(e);
            }
        }

        @Override
        public void handleStatement(@Nullable Statement nextStatement) {
            checkNotNull(nextStatement);
            assert nextStatement != null;
            if (nextStatement.getPredicate().equals(RDF.FIRST)
                || nextStatement.getPredicate().equals(RDF.REST)) {
                if (!typedLists.contains(nextStatement.getSubject())) {
                    typedLists.add(nextStatement.getSubject());
                    try {
                        consumer.handleStatement(
                            vf.createStatement(nextStatement.getSubject(), RDF.TYPE, RDF.LIST));
                    } catch (RDFHandlerException e) {
                        throw new OWLParserException(e);
                    }
                    LOG.debug("Implicitly typing list={}", nextStatement);
                }
            } else if (nextStatement.getPredicate().equals(RDF.TYPE)
                && nextStatement.getObject().equals(RDF.LIST)) {
                if (!typedLists.contains(nextStatement.getSubject())) {
                    LOG.debug("Explicit list type found={}", nextStatement);
                    typedLists.add(nextStatement.getSubject());
                } else {
                    LOG.debug("duplicate rdf:type rdf:List statements found={}", nextStatement);
                }
            }
            try {
                consumer.handleStatement(nextStatement);
            } catch (RDFHandlerException e) {
                throw new OWLParserException(e);
            }
        }

        @Override
        public void handleComment(@Nullable String comment) {
            // do nothing
        }
    }
}