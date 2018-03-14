package cz.makub;
import java.util.*;
import java.net.*;
import java.io.*;
import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.common.collect.Multimap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Example how to use an OWL ontology with a reasoner.
 * <p>
 * Run in Maven with <code>mvn exec:java -Dexec.mainClass=cz.makub.Tutorial</code>
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public class Tutorial {

    private static final String BASE_URL = "http://users.csc.calpoly.edu/~dpjames/deadweekfinal.owl";
    private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

    public static void main(String[] args) throws OWLOntologyCreationException {
    	
        //prepare ontology and reasoner
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(BASE_URL));
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        OWLDataFactory factory = manager.getOWLDataFactory();
        PrefixDocumentFormat pm = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat();
        pm.setDefaultPrefix("http://www.semanticweb.org/katie/ontologies/2018/2/untitled-ontology-17" + "#");

        System.out.println("our stuff");
        /*
        System.out.println(ontology);
        OWLClass barClass = factory.getOWLClass(":bar", pm);
        System.out.println(barClass);
        System.out.println(reasoner.getInstances(barClass, false));
        System.out.println(reasoner.getInstances(barClass, false).getFlattened());
        
        OWLNamedIndividual LunaRed = factory.getOWLNamedIndividual(":LunaRed", pm);
        OWLDataProperty property = factory.getOWLDataProperty(":hasPrice", pm);
        System.out.println(reasoner.getDataPropertyValues(LunaRed, property));
        
        for (OWLLiteral pp : reasoner.getDataPropertyValues(LunaRed, property)) {
            System.out.println("LunaRed" + pp.getLiteral());
        }
        System.out.println("bel");
        //System.out.println(LunaRed.data);
        */
        
        
        
        
        
        
        boolean isOutdoor = isOutdoor(); 

        Hashtable<String, Integer> pref = getPref();

        int maxPrice = pref.get("price");
        System.out.println(pref);
        System.out.println("class name is " + findClassName(pref));
        OWLClass results = factory.getOWLClass(findClassName(pref), pm);
        
        ArrayList<String> names = new ArrayList<String>();
        OWLClass outdoor = factory.getOWLClass(":outdoor", pm);
        //System.out.println(isOutdoor);
        
        for(OWLNamedIndividual result : reasoner.getInstances(results, false).getFlattened()) {
        	//loop through all results and filter them out and grab the names.
        	
        	//System.out.println("this is where we need to filter by outdoor");
        	//System.out.println(result.getClass());
        	
        	
        	OWLDataProperty price = factory.getOWLDataProperty(":hasPrice", pm);
        	String pstring = "0";
        	for(OWLLiteral p : reasoner.getDataPropertyValues(result, price)) {
        		pstring = p.getLiteral();
        	}
        	int thePrice = Integer.parseInt(pstring);
        	if(thePrice <= maxPrice) {
        		names.add(renderer.render(result));
        	}
        	
        }
        
        System.out.println(names);
        
        
        
        
        
        //--------------------------------------------------------------------------------------------------------
        System.out.println("end our stuff");
        
        //get class and its individuals
        OWLClass personClass = factory.getOWLClass(":Person", pm);

        for (OWLNamedIndividual person : reasoner.getInstances(personClass, false).getFlattened()) {
            System.out.println("person : " + renderer.render(person));
        }
        //get a given individual
        OWLNamedIndividual martin = factory.getOWLNamedIndividual(":Martin", pm);

        //get values of selected properties on the individual
        OWLDataProperty hasEmailProperty = factory.getOWLDataProperty(":hasEmail", pm);

        OWLObjectProperty isEmployedAtProperty = factory.getOWLObjectProperty(":isEmployedAt", pm);

        for (OWLLiteral email : reasoner.getDataPropertyValues(martin, hasEmailProperty)) {
            System.out.println("Martin has email: " + email.getLiteral());
        }

        for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(martin, isEmployedAtProperty).getFlattened()) {
            System.out.println("Martin is employed at: " + renderer.render(ind));
        }

        //get labels
        LocalizedAnnotationSelector as = new LocalizedAnnotationSelector(ontology, factory, "en", "cs");
        /*for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(martin, isEmployedAtProperty).getFlattened()) {
            System.out.println("Martin is employed at: '" + as.getLabel(ind) + "'");
        }*/

        //get inverse of a property, i.e. which individuals are in relation with a given individual
        OWLNamedIndividual university = factory.getOWLNamedIndividual(":MU", pm);
        OWLObjectPropertyExpression inverse = factory.getOWLObjectInverseOf(isEmployedAtProperty);
        for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(university, inverse).getFlattened()) {
            System.out.println("MU inverseOf(isEmployedAt) -> " + renderer.render(ind));
        }

        //find to which classes the individual belongs
        Collection<OWLClassExpression> assertedClasses = EntitySearcher.getTypes(martin, ontology);
        for (OWLClass c : reasoner.getTypes(martin, false).getFlattened()) {
            boolean asserted = assertedClasses.contains(c);
            System.out.println((asserted ? "asserted" : "inferred") + " class for Martin: " + renderer.render(c));
        }

        //list all object property values for the individual
        Multimap<OWLObjectPropertyExpression, OWLIndividual> assertedValues = EntitySearcher.getObjectPropertyValues(martin, ontology);
        for (OWLObjectProperty objProp : ontology.getObjectPropertiesInSignature(Imports.INCLUDED)) {
            for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(martin, objProp).getFlattened()) {
                boolean asserted = assertedValues.get(objProp).contains(ind);
                System.out.println((asserted ? "asserted" : "inferred") + " object property for Martin: "
                        + renderer.render(objProp) + " -> " + renderer.render(ind));
            }
        }

        //list all same individuals
        for (OWLNamedIndividual ind : reasoner.getSameIndividuals(martin)) {
            System.out.println("same as Martin: " + renderer.render(ind));
        }

        //ask reasoner whether Martin is employed at MU
        boolean result = reasoner.isEntailed(factory.getOWLObjectPropertyAssertionAxiom(isEmployedAtProperty, martin, university));
        System.out.println("Is Martin employed at MU ? : " + result);


        //check whether the SWRL rule is used
        OWLNamedIndividual ivan = factory.getOWLNamedIndividual(":Ivan", pm);
        OWLClass chOMPClass = factory.getOWLClass(":ChildOfMarriedParents", pm);
        OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(chOMPClass, ivan);
        System.out.println("Is Ivan child of married parents ? : " + reasoner.isEntailed(axiomToExplain));


        //explain why Ivan is child of married parents
        DefaultExplanationGenerator explanationGenerator =
                new DefaultExplanationGenerator(
                        manager, reasonerFactory, ontology, reasoner, new SilentExplanationProgressMonitor());
        Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplain);
        ExplanationOrderer deo = new ExplanationOrdererImpl(manager);
        ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplain, explanation);
        System.out.println();
        System.out.println("-- explanation why Ivan is in class ChildOfMarriedParents --");
        printIndented(explanationTree, "");
    }



	private static void printIndented(Tree<OWLAxiom> node, String indent) {
        OWLAxiom axiom = node.getUserObject();
        System.out.println(indent + renderer.render(axiom));
        if (!node.isLeaf()) {
            for (Tree<OWLAxiom> child : node.getChildren()) {
                printIndented(child, indent + "    ");
            }
        }
    }

    /**
     * Helper class for extracting labels, comments and other anotations in preffered languages.
     * Selects the first literal annotation matching the given languages in the given order.
     */
    @SuppressWarnings("WeakerAccess")
    public static class LocalizedAnnotationSelector {
        private final List<String> langs;
        private final OWLOntology ontology;
        private final OWLDataFactory factory;

        /**
         * Constructor.
         *
         * @param ontology ontology
         * @param factory  data factory
         * @param langs    list of prefered languages; if none is provided the Locale.getDefault() is used
         */
        public LocalizedAnnotationSelector(OWLOntology ontology, OWLDataFactory factory, String... langs) {
            this.langs = (langs == null) ? Collections.singletonList(Locale.getDefault().toString()) : Arrays.asList(langs);
            this.ontology = ontology;
            this.factory = factory;
        }

        /**
         * Provides the first label in the first matching language.
         *
         * @param ind individual
         * @return label in one of preferred languages or null if not available
         */
        public String getLabel(OWLNamedIndividual ind) {
            return getAnnotationString(ind, OWLRDFVocabulary.RDFS_LABEL.getIRI());
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getComment(OWLNamedIndividual ind) {
            return getAnnotationString(ind, OWLRDFVocabulary.RDFS_COMMENT.getIRI());
        }

        public String getAnnotationString(OWLNamedIndividual ind, IRI annotationIRI) {
            return getLocalizedString(EntitySearcher.getAnnotations(ind, ontology, factory.getOWLAnnotationProperty(annotationIRI)));
        }

        private String getLocalizedString(Collection<OWLAnnotation> annotations) {
            List<OWLLiteral> literalLabels = new ArrayList<>(annotations.size());
            for (OWLAnnotation label : annotations) {
                if (label.getValue() instanceof OWLLiteral) {
                    literalLabels.add((OWLLiteral) label.getValue());
                }
            }
            for (String lang : langs) {
                for (OWLLiteral literal : literalLabels) {
                    if (literal.hasLang(lang)) return literal.getLiteral();
                }
            }
            for (OWLLiteral literal : literalLabels) {
                if (!literal.hasLang()) return literal.getLiteral();
            }
            return null;
        }
    }
    
    
    
    /*
     * 
     *   
     * 
     * 
     * 
     * 
     * 
     */

    private static Hashtable<String, Integer>  getPref(){
        Hashtable<String, Integer> pref = new Hashtable<String, Integer>();
        getInput("How much money? (0-4): ", "price", pref); //filter in code
        getInput("Is this a date? (0 || 1): ", "isDate", pref); //define in ontology
        getInput("Are you hungry (0 || 1): ", "isHungry", pref); //ontology
        getInput("Are you feeling active? (0 || 1): ", "isActive", pref); //ontology 
        getInput("Are you feeling like being around people? (0 || 1): ", "isSocial", pref); //ontology
        getInput("Are you (and everyone in your group) 21+? (0 || 1): ", "is21", pref);  //ontology
        //pref.put("key",0); 
        return pref;
     }
     private static void getInput(String question, String key,Hashtable<String, Integer> pref){
        Scanner input = new Scanner(System.in);
        boolean b;
        String temp;
        do{
           System.out.print(question); 
           temp = input.nextLine();
           try{
              b = false;
              int val = Integer.parseInt(temp);
              pref.put(key, val);
           }catch(NumberFormatException e){
              b = true;
              System.out.println("bad input");
           }
        } while(b);  
    }
     private static String findClassName(Hashtable<String, Integer> pref) {
 		int index = Integer.parseInt(""+pref.get("isDate")+pref.get("isHungry")+pref.get("isActive")+pref.get("isSocial")+pref.get("is21"), 2);
 		String[] classList = {
 		 		":alone",
 		 		":drinkingAlone",
 		 		":youngSocial",
 		 		":oldSocial",
 		 		":activeAlone",
 		 		":activeAlone",
 		 		":activeSocial",
 		 		":activeSocial",
 		 		":hungry",
 		 		":hungryOld",
 		 		":hungry",
 		 		":hungryOld",
 		 		":hungry",
 		 		":hungryOld",
 		 		":hungry",
 		 		":hungryOld",
 		 		":dateNoFood",
 		 		":dateNoFood",
 		 		":dateNoFood",
 		 		":barDate",
 		 		":activeDate",
 		 		":activeDate",
 		 		":activeDate",
 		 		":activeDate",
 		 		":dateYesFood",
 		 		":dateYesFoodOld",
 		 		":dateYesFood",
 		 		":dateYesFoodOld",
 		 		":dateYesFood",
 		 		":dateYesFood",
 		 		":dateYesFood",
 		 		":dateYesFoodOld" 				
 		};
 		return classList[index];
 	}
     
     public static String readStringFromURL(String requestURL) throws IOException
     {
         try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                 StandardCharsets.UTF_8.toString()))
         {
             scanner.useDelimiter("\\A");
             return scanner.hasNext() ? scanner.next() : "";
         }
     }
     private static boolean isOutdoor() {
     	boolean ret = false;
 		java.net.URL website;
       double val = 0;
 		try {
 			/*
 			website = new URL("http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json");
 			byte[] bytes = new byte[200000];
     		try (java.io.InputStream in = website.openStream()) {
     	    	System.out.println(in.read(bytes, 0, 200000));
     	    	
     		}
     		//System.out.println();
            //new Scanner(System.in).next();
     		//System.err.println(new String(bytes));
     	  //System.out.println(new String(bytes));
          String rec = new String(bytes);
          */
 			String rec = readStringFromURL("http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json");
 			System.out.println(rec);
          FileWriter fw = new FileWriter("tmp"); 
          fw.write(rec);
          fw.close();
          //System.out.println(rec);
     	  int index = rec.indexOf("precip_today_in"); // 15
          int col = rec.indexOf(':', index);
          int fq = rec.indexOf('"', col);
          int lq = rec.indexOf('"', fq+1);          
          System.out.println(col);
          System.out.println(fq);
          System.out.println(lq);
          String sval = rec.substring(fq+1,lq);
          System.out.println("sval is " + sval);
          val = Double.parseDouble(sval); 
          
          //File f = new File("test");
          
 		} catch (Exception e) {
 			e.printStackTrace();
 		}   
       System.out.println(val);
     	return !(val > 0);
     }
     
}