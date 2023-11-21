import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/*
 * Klasse zum Management sowie zur Eingabe unnd Ausgabe von User-Stories.
 * Die Anwendung wird über dies Klasse auch gestartet (main-Methode hier vorhanden)
 *
 * erstellt von Julius P., H-BRS 2023, Version 1.0
 *
 * Strategie für die Wiederverwendung (Reuse):
 * - Anlegen der Klasse UserStory
 * - Anpassen des Generic in der List-Klasse (ALT: Member, NEU: UserStory)
 * - Anpassen der Methodennamen
 *
 * (Was ist ihre Strategie zur Wiederverwendung?)
 *
 * Klasse UserStory implementiert Interface Member (UserStory implements Member)
 * Vorteil: Wiederverwendung von Member, ID verwenden; Strenge Implementierung gegen Interface
 * Nachteil: Viele Casts notwendig, um auf die anderen Methoden zuzugreifen
 *
 * Alternative: Container mit Generic entwickeln (z.B. Container<E>))
 *
 * Achtung: eine weitere Aufteilung dieser Klasse ist notwendig (siehe F2, vgl auch Klassendiagramm für 4-2)
 * 
 */

public class Container {
	 
	// Interne ArrayList zur Abspeicherung der Objekte vom Type UserStory
	private List<UserStory> liste;
	
	// Statische Klassen-Variable, um die Referenz
	// auf das einzige Container-Objekt abzuspeichern,
	// diese Variante sei thread-safe, so hat Hr. P. es gehört ... stimmt das? → Richtig!
	// Nachteil: ggf. geringer Speicherbedarf, da Singleton zu Programmstart schon erzeugt
	// → Falsch, es besteht direkt ein hoher Speicherbedarf!
	private static final Container mInstance = new Container();
	
	// URL der Datei, in der die Objekte gespeichert werden 
	final static String LOCATION = "allStories.ser";

	public static Container getInstance() {
		return mInstance;
	}
	
	/**
	 * Vorschriftsmäßiges Überschreiben des Konstruktors (private) gemäss Singleton-Pattern (oder?)
	 * nun auf private gesetzt! Vorher ohne Access Qualifier (--> dann package-private)
	 */
	private Container(){
		liste = new ArrayList<>();
	}
	
	/**
	 * Start-Methoden zum Starten des Programms 
	 * (hier können ggf. weitere Initialisierungsarbeiten gemacht werden später)
	 */
	public static void main (String[] args) throws Exception {
		Container con = Container.getInstance();
		con.startEingabe(); 
	}

	//takes a lambda expression as parameter
	private void tryTillNumberEntered(Scanner scanner, String message, Consumer<Integer> consumer) {
		while (true) {
			try {
				System.out.println(message);
				consumer.accept(Integer.parseInt(scanner.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("Bitte geben Sie eine Zahl ein!");
			}
		}
	}
	
	/*
	 * Diese Methode realisiert eine Eingabe über einen Scanner
	 * Alle Exceptions werden an den aufrufenden Context (hier: main) weitergegeben (throws)
	 * Das entlastet den Entwickler zur Entwicklungszeit und den Endanwender zur Laufzeit
	 */
	public void startEingabe() throws Exception {
		String strInput;
		
		// Initialisierung des Eingabe-View
		Scanner scanner = new Scanner( System.in );

		while ( true ) {
			// Ausgabe eines Texts zur Begruessung
			System.out.println("UserStory-Tool V1.0 by Julius P. (dedicated to all my friends)");

			System.out.print( "> "  );
			strInput = scanner.nextLine();
		
			// Extrahiert ein Array aus der Eingabe
			String[] strings = strInput.split(" ");

			// 	Falls 'help' eingegeben wurde, werden alle Befehle ausgedruckt
			if ( strings[0].equals("help") ) {
				System.out.println("Folgende Befehle stehen zur Verfuegung: help, dump....");
			}
			// Auswahl der bisher implementierten Befehle:
			if ( strings[0].equals("dump") ) {
				startAusgabe();
			}
			// Auswahl der bisher implementierten Befehle:
			if ( strings[0].equals("enter") )
			{
				var new_user_story = new UserStory();

				new_user_story.setId(UUID.randomUUID());

				System.out.println("Bitte geben Sie einen Titel ein:");
				new_user_story.setTitel(scanner.nextLine());

				System.out.println("Bitte geben Sie einen Wert für das Projekt ein:");
				new_user_story.setProject(scanner.nextLine());

				System.out.println("Bitte geben Sie eine Beschreibung ein:");
				new_user_story.setDescription(scanner.nextLine());

				System.out.println("Bitte geben Sie Akzeptanzkriterien ein:");
				new_user_story.setAcceptanceCriteria(scanner.nextLine());

				tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für den Aufwand ein:", new_user_story::setExpense);

				tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für den Mehrwert ein:", new_user_story::setAddedValue);

				tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für das Risiko ein:", new_user_story::setRisk);

				tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für die Strafe ein:", new_user_story::setPenalty);

				// Formel nach Gloger
				new_user_story.setPriority( (new_user_story.getAddedValue() + new_user_story.getExpense() + new_user_story.getPenalty()) / new_user_story.getRisk() );

				addUserStory(new_user_story);
			}

			if ( strings[0].equals("search") ) {
				// Suche nach einer bestimmten UserStory
				// this.getUserStory( id ) um das Objekt zu erhalten.
			}
								
			if (  strings[0].equals("store")  ) {
				// Beispiel-Code
				UserStory userStory = new UserStory();
				this.addUserStory( userStory );
				this.store();
			}

			if (  strings[0].equals("load")  ) {
				this.load();
			}

			if (  strings[0].equals("exit")  ) {
				System.out.println("Programm wird beendet!");
				System.exit(0);
			}
		} // Ende der Schleife
	}

	/**
	 * Diese Methode realisiert die Ausgabe.
	 */
	public void startAusgabe() {

		// Hier möchte Herr P. die Liste mit einem eigenen Sortieralgorithmus sortieren und dann
		// ausgeben. Allerdings weiß der Student hier nicht weiter

		// [Sortierung ausgelassen]
		Collections.sort( this.liste );

		// Klassische Ausgabe über eine For-Each-Schleife
		for (UserStory story : liste) {
			System.out.println(story.toString());
		}

		// [Variante mit forEach-Methode / Streams (--> Kapitel 9, Lösung Übung Nr. 2)?
		//  Gerne auch mit Beachtung der neuen US1
		// (Filterung Projekt = "ein Wert (z.B. Coll@HBRS)" und Risiko >=5
		List<UserStory> reduzierteListe = this.liste.stream()
				.filter( story -> story.getProject().equals("Coll@HBRS") )
				.filter(  story -> story.getRisk()  >= 5 )
				.toList();
	}

	/*
	 * Methode zum Speichern der Liste. Es wird die komplette Liste
	 * inklusive ihrer gespeicherten UserStory-Objekte gespeichert.
	 * 
	 */
	private void store() throws ContainerException {
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( Container.LOCATION );
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject( this.liste );
			System.out.println( this.size() + " UserStory wurden erfolgreich gespeichert!");
		}
		catch (IOException e) {
			e.printStackTrace();
		  //  Delegation in den aufrufendem Context
		  // (Anwendung Pattern "Chain Of Responsibility)
		  throw new ContainerException("Fehler beim Abspeichern");
		}
	}

	/*
	 * Methode zum Laden der Liste. Es wird die komplette Liste
	 * inklusive ihrer gespeicherten UserStory-Objekte geladen.
	 * 
	 */
	public void load() {
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
		  fis = new FileInputStream( Container.LOCATION );
		  ois = new ObjectInputStream(fis);
		  
		  // Auslesen der Liste
		  Object obj = ois.readObject();
		  if (obj instanceof List<?>) {
			  this.liste = (List) obj;
		  }
		  System.out.println("Es wurden " + this.size() + " UserStory erfolgreich reingeladen!");
		}
		catch (IOException e) {
			System.out.println("LOG (für Admin): Datei konnte nicht gefunden werden!");
		}
		catch (ClassNotFoundException e) {
			System.out.println("LOG (für Admin): Liste konnte nicht extrahiert werden (ClassNotFound)!");
		}
		finally {
		  if (ois != null) try { ois.close(); } catch (IOException e) {}
		  if (fis != null) try { fis.close(); } catch (IOException e) {}
		}
	}

	/**
	 * Methode zum Hinzufügen eines Mitarbeiters unter Wahrung der Schlüsseleigenschaft
	 * @param userStory
	 * @throws ContainerException
	 */
	public void addUserStory ( UserStory userStory ) throws ContainerException {
		if (contains(userStory)) {
            throw new ContainerException("ID bereits vorhanden!");
		}
		liste.add(userStory);
	}

	/**
	 * Prüft, ob eine UserStory bereits vorhanden ist
	 * @param userStory
	 * @return
	 */
	private boolean contains( UserStory userStory ) {
		var ID = userStory.getId();
		for ( UserStory userStory1 : liste) {
			if ( userStory1.getId() == ID ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ermittlung der Anzahl von internen UserStory
	 * -Objekten
	 * @return Returns size of
	 */
	public int size() {
		return liste.size();
	}

	/**
	 * Methode zur Rückgabe der aktuellen Liste mit Stories
	 * Findet aktuell keine Anwendung bei Hr. P.
	 * @return
	 */
	public List<UserStory> getCurrentList() {
		return this.liste;
	}

	/**
	 * Liefert eine bestimmte UserStory zurück
	 * @param id
	 * @return
	 */
	private UserStory getUserStory(UUID id) {
		for ( UserStory userStory : liste) {
			if (id == userStory.getId() ){
				return userStory;
			}
		}
		return null;
	}
}
