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
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;

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
 * Run in Maven with
 * <code>mvn exec:java -Dexec.mainClass=cz.makub.Tutorial</code>
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public class Tutorial {
	private static final boolean train = true;
	private static final String BASE_URL = "http://users.csc.calpoly.edu/~dpjames/deadweekfinal.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

	public static void main(String[] args) throws OWLOntologyCreationException {

		// prepare ontology and reasoner
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(BASE_URL));
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixDocumentFormat pm = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat();
		pm.setDefaultPrefix("http://www.semanticweb.org/katie/ontologies/2018/2/untitled-ontology-17" + "#");

		//System.out.println("our stuff");

		Hashtable<OWLClass, Integer> liked = new Hashtable<OWLClass, Integer>();
		System.out.println("pref file: ");
		Scanner t = new Scanner(System.in);
		String filename = t.next();
		// t.close();

		FileInputStream fis;
		Object hash = null;
		try {
			fis = new FileInputStream(filename);

			ObjectInputStream ois = new ObjectInputStream(fis);

			hash = ois.readObject();
			// ois.readObject(liked);
			ois.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("file not found, will use default settings");
		}

		Hashtable<String, Integer> pref;
		boolean isOutdoor = isOutdoor();
		if (hash instanceof Hashtable) {
			liked = (Hashtable<OWLClass, Integer>) hash;
		} else {
			liked = new Hashtable<OWLClass, Integer>();
		}
		System.out.println(liked.size());
		pref = getPref();
		int maxPrice = pref.get("price");
		System.out.println(pref);
		System.out.println("class name is " + findClassName(pref));
		OWLClass results = factory.getOWLClass(findClassName(pref), pm);

		ArrayList<Place> places = new ArrayList<Place>();
		OWLClass outdoor = factory.getOWLClass(":Outdoor", pm);
		for (OWLNamedIndividual result : reasoner.getInstances(results, false).getFlattened()) {
			boolean contflag = false;
			for (Node<OWLClass> c : reasoner.getTypes(result, false)) {
				if (c.contains(outdoor) && !isOutdoor) {
					contflag = true;
				}
			}
			if (contflag) {
				continue;
			}

			OWLDataProperty price = factory.getOWLDataProperty(":hasPrice", pm);
			String pstring = "0";
			for (OWLLiteral p : reasoner.getDataPropertyValues(result, price)) {
				pstring = p.getLiteral();
			}
			int thePrice = Integer.parseInt(pstring);
			if (thePrice <= maxPrice) {
				Place p = new Tutorial.Place(result.toString(), reasoner.getTypes(result, false));
				places.add(p);
			}
		}
		
		
		//System.out.println("found " + places.size() + " places");
		
		//This next block trains, the else actually runs.
		if (train) {
			Scanner input = new Scanner(System.in);
			int v = 1;
			int maxScore = 0;
			while (places.size() > 0) {
				// train
				Random rand = new Random();
				int index = rand.nextInt(places.size());
				String name = parseClassName(places.get(index).name);
				int score = findScore(places.get(index).classes, liked);
				System.out.println(score);
				maxScore = maxScore < score ? score : maxScore;
				if (score < maxScore && maxScore - score > score && rand.nextInt(maxScore - score) != 1) {
					continue;
				}
				System.out.print("Do you like " + name + "? (y/n): ");
				String answer = input.next();
				if (answer.charAt(0) == 'n' || answer.charAt(0) == 'N') {
					updateScore(places.get(index).classes, liked, -1);
					places.remove(index);
				} else if (answer.charAt(0) == 'e') {
					break;
				} else {
					updateScore(places.get(index).classes, liked, 1);
				}
				System.out.println(answer);
			}
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(liked);
				oos.close();
			} catch (Exception e) {

			}
		} else {
			Scanner input = new Scanner(System.in);
			int v = 1;
			int maxScore = 0;
			while (places.size() > 0) {
				// train
				Random rand = new Random();
				int index = rand.nextInt(places.size());
				String name = parseClassName(places.get(index).name);
				int score = findScore(places.get(index).classes, liked);
				// System.out.println(score);
				maxScore = maxScore < score ? score : maxScore;
				if (score < maxScore && rand.nextInt(maxScore - score) != 0) {
					continue;
				}
				System.out.print("Do you like " + name + "? (y/n): ");
				String answer = input.next();
				if (answer.charAt(0) == 'n' || answer.charAt(0) == 'N') {
					updateScore(places.get(index).classes, liked, -1);
					places.remove(index);
				} else if (answer.charAt(0) == 'e') {
					break;
				} else {
					updateScore(places.get(index).classes, liked, 1);
					break;
				}
				System.out.println(answer);
			}
			if (places.isEmpty()) {
				System.out.println("we cannot help you");
			} else {
				System.out.println("yay");
			}
		}

		// input.close();
	}

	private static void updateScore(NodeSet<OWLClass> classes, Hashtable<OWLClass, Integer> liked, int i) {
		for (Node<OWLClass> c : classes) {
			for (OWLClass clss : c.getEntities()) {
				if (liked.get(clss) == null) {
					liked.put(clss, 0);
				} else {
					liked.put(clss, liked.get(clss) + i);
				}
			}
		}
	}

	private static int findScore(NodeSet<OWLClass> classes, Hashtable<OWLClass, Integer> liked) {
		int sum = 0;
		for (Node<OWLClass> c : classes) {
			//System.out.println(c);
			for (OWLClass clss : c.getEntities()) {
				if (liked.get(clss) == null) {
					sum += 0;
				} else {
					sum += liked.get(clss);
				}
			}
		}
		return sum;
	}

	private static Hashtable<String, Integer> getPref() {
		Hashtable<String, Integer> pref = new Hashtable<String, Integer>();
		getInput("How much money? (0-4): ", "price", pref); // filter in code
		getInput("Is this a date? (0 || 1): ", "isDate", pref); // define in ontology
		getInput("Are you hungry (0 || 1): ", "isHungry", pref); // ontology
		getInput("Are you feeling active? (0 || 1): ", "isActive", pref); // ontology
		getInput("Are you feeling like being around people? (0 || 1): ", "isSocial", pref); // ontology
		getInput("Are you (and everyone in your group) 21+? (0 || 1): ", "is21", pref); // ontology
		// pref.put("key",0);
		return pref;
	}

	private static void getInput(String question, String key, Hashtable<String, Integer> pref) {
		Scanner input = new Scanner(System.in);
		boolean b;
		String temp;
		do {
			System.out.print(question);
			temp = input.nextLine();
			try {
				b = false;
				int val = Integer.parseInt(temp);
				pref.put(key, val);
			} catch (NumberFormatException e) {
				b = true;
				System.out.println("bad input");
			}
		} while (b);
	}

	private static String findClassName(Hashtable<String, Integer> pref) {
		int index = Integer.parseInt("" + pref.get("isDate") + pref.get("isHungry") + pref.get("isActive")
				+ pref.get("isSocial") + pref.get("is21"), 2);
		String[] classList = { ":alone", ":drinkingAlone", ":youngSocial", ":oldSocial", ":activeAlone", ":activeAlone",
				":activeSocial", ":activeSocial", ":hungry", ":hungryOld", ":hungry", ":hungryOld", ":hungry",
				":hungryOld", ":hungry", ":hungryOld", ":dateNoFood", ":dateNoFood", ":dateNoFood", ":barDate",
				":activeDate", ":activeDate", ":activeDate", ":activeDate", ":dateYesFood", ":dateYesFoodOld",
				":dateYesFood", ":dateYesFoodOld", ":dateYesFood", ":dateYesFood", ":dateYesFood", ":dateYesFoodOld" };
		return classList[index];
	}

	public static String readStringFromURL(String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

	private static boolean isOutdoor() {
		double val = 0;
		try {
			String rec = readStringFromURL(
					"http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json");
			int index = rec.indexOf("precip_today_in"); // 15
			int col = rec.indexOf(':', index);
			int fq = rec.indexOf('"', col);
			int lq = rec.indexOf('"', fq + 1);
			String sval = rec.substring(fq + 1, lq);
			System.out.println("sval is " + sval);
			val = Double.parseDouble(sval);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(val);
		return !(val > 0);
	}

	private static String parseClassName(String n) {
		int tagindex = n.indexOf('#');
		int closeindex = n.indexOf('>');
		String name = n.substring(tagindex + 1, closeindex);
		name = name.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
		return name;
	}

	public static class Place {
		String name;
		NodeSet<OWLClass> classes;

		Place(String name, NodeSet<OWLClass> classes) {
			this.name = name;
			this.classes = classes;
		}
	}

}
