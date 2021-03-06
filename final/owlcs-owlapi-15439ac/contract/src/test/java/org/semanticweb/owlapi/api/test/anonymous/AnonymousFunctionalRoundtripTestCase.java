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
package org.semanticweb.owlapi.api.test.anonymous;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnonymousIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ClassAssertion;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataPropertyAssertion;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectHasValue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

@SuppressWarnings("javadoc")
public class AnonymousFunctionalRoundtripTestCase extends TestBase {

    private static final String NS = "http://namespace.owl";
    private static final String BROKEN = "<?xml version=\"1.0\"?>\n"
        + "<rdf:RDF xmlns=\"http://namespace.owl#\"\n" + "     xml:base=\"http://namespace.owl\"\n"
        + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
        + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
        + "    <owl:Ontology rdf:about=\"http://namespace.owl\"/>\n"
        + "    <owl:Class rdf:about=\"http://namespace.owl#A\"/>\n" + "<A/></rdf:RDF>";
    private static final String FIXED =
        "Prefix(:=<http://namespace.owl#>)\n" + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + '\n' + '\n'
            + "Ontology(<http://namespace.owl>\n" + '\n' + "Declaration(Class(:C))\n"
            + "SubClassOf(:C ObjectHasValue(:p _:genid2))\n" + "Declaration(Class(:D))\n"
            + "Declaration(ObjectProperty(:p))\n" + "Declaration(DataProperty(:q))\n"
            + "ClassAssertion(:D _:genid2)\n"
            + "DataPropertyAssertion(:q _:genid2 \"hello\"^^xsd:string)\n" + ')';

    @Test
    public void shouldRoundTripFixed() {
        loadOntologyFromString(FIXED, new FunctionalSyntaxDocumentFormat());
    }

    @Test
    public void shouldRoundTripBroken() throws Exception {
        OWLOntology o = loadOntologyFromString(BROKEN, new RDFXMLDocumentFormat());
        FunctionalSyntaxDocumentFormat format = new FunctionalSyntaxDocumentFormat();
        o.getPrefixManager().withDefaultPrefix(NS + '#');
        OWLOntology o1 = roundTrip(o, format);
        equal(o, o1);
    }

    @Test
    public void shouldRoundTrip() throws Exception {
        OWLClass c = Class(IRI(NS + "#", "C"));
        OWLClass d = Class(IRI(NS + "#", "D"));
        OWLObjectProperty p = ObjectProperty(IRI(NS + "#", "p"));
        OWLDataProperty q = DataProperty(IRI(NS + "#", "q"));
        OWLIndividual i = AnonymousIndividual();
        OWLOntology ontology = getOWLOntology();
        ontology.add(SubClassOf(c, ObjectHasValue(p, i)), ClassAssertion(d, i),
            DataPropertyAssertion(q, i, Literal("hello")));
        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
        ontology.getPrefixManager().withDefaultPrefix(NS + '#');
        ontology = roundTrip(ontology, format);
        FunctionalSyntaxDocumentFormat format2 = new FunctionalSyntaxDocumentFormat();
        ontology = roundTrip(ontology, format2);
    }
}
