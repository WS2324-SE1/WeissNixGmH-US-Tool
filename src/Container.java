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

public class Container
{

    // URL der Datei, in der die Objekte gespeichert werden
    final static String LOCATION = "allStories.ser";
    // Statische Klassen-Variable, um die Referenz
    // auf das einzige Container-Objekt abzuspeichern,
    // diese Variante sei thread-safe, so hat Hr. P. es gehört ... stimmt das? → Richtig!
    // Nachteil: ggf. geringer Speicherbedarf, da Singleton zu Programmstart schon erzeugt
    // → Falsch, es besteht direkt ein hoher Speicherbedarf!
    private static final Container mInstance = new Container();
    // Interne ArrayList zur Abspeicherung der Objekte vom Type UserStory
    private List<UserStory> liste;

    /**
     * Vorschriftsmäßiges Überschreiben des Konstruktors (private) gemäss Singleton-Pattern (oder?)
     * nun auf private gesetzt! Vorher ohne Access Qualifier (--> dann package-private)
     */
    private Container()
    {
        liste = new ArrayList<>();
    }

    public static Container getInstance()
    {
        return mInstance;
    }

    /**
     * Start-Methoden zum Starten des Programms
     * (hier können ggf. weitere Initialisierungsarbeiten gemacht werden später)
     */
    public static void main(String[] args) throws Exception
    {
        Container con = Container.getInstance();
        con.startEingabe();
    }

	private static final Consumer<Integer> not_negative = (i) -> {
		if (i < 0)
		{
			throw new IllegalArgumentException("Bitte geben Sie eine positive Zahl ein!");
		}
	};

	private static final Consumer<Integer> not_zero = (i) -> {
		if (i == 0)
		{
			throw new IllegalArgumentException("Bitte geben Sie eine Zahl ungleich 0 ein!");
		}
	};



    //takes a lambda expression as parameter and a variable number of validators (also lambda expressions)
    private void tryTillNumberEntered(Scanner scanner, String message, Consumer<Integer> consumer, Consumer<Integer>... validators)
    {
        while (true)
        {
            try
            {
                System.out.println(message);
				int i = Integer.parseInt(scanner.nextLine());
				for (Consumer<Integer> validator : validators)
				{
					validator.accept(i);
				}
				consumer.accept(i);
                break;
            } catch (NumberFormatException e)
            {
                System.out.println("Bitte geben Sie eine Zahl ein!");
            }
			catch (IllegalArgumentException e)
			{
				System.out.println(e.getMessage());
			}
        }
    }

    /*
     * Diese Methode realisiert eine Eingabe über einen Scanner
     * Alle Exceptions werden an den aufrufenden Context (hier: main) weitergegeben (throws)
     * Das entlastet den Entwickler zur Entwicklungszeit und den Endanwender zur Laufzeit
     */
    public void startEingabe() throws Exception
    {
        String strInput;

        // Initialisierung des Eingabe-View
        Scanner scanner = new Scanner(System.in);

        while (true)
        {
            // Ausgabe eines Texts zur Begruessung
            System.out.println("UserStory-Tool V1.0 by Julius P. (dedicated to all my friends)");

            System.out.print("> ");
            strInput = scanner.nextLine();

            // Extrahiert ein Array aus der Eingabe
            String[] strings = strInput.split(" ");

            // 	Falls 'help' eingegeben wurde, werden alle Befehle ausgedruckt
            switch (strings[0])
            {
                case "help" -> System.out.println("""
                        Folgende Befehle sind verfügbar:
                        \t- dump
                        \t- enter
                        \t- search
                        \t- store
                        \t- load
                        \t- exit
                        \t- help""");

                // Auswahl der bisher implementierten Befehle:
                case "dump" -> startAusgabe();

                // Auswahl der bisher implementierten Befehle:
                case "enter" ->
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
                    tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für den Aufwand ein:", new_user_story::setExpense, not_negative);
                    tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für den Mehrwert ein:", new_user_story::setAddedValue, not_negative);
                    tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für das Risiko ein:", new_user_story::setRisk, not_negative, not_zero);
                    tryTillNumberEntered(scanner, "Bitte geben Sie einen Wert für die Strafe ein:", new_user_story::setPenalty, not_negative);
                    new_user_story.setPriority((double) (new_user_story.getAddedValue() +
                            new_user_story.getExpense() +
                            new_user_story.getPenalty())
                            / new_user_story.getRisk());
                    addUserStory(new_user_story);
                }
                case "search" ->
                {
                    if (strings.length == 1)
                    {
                        System.out.print("Bitte geben Sie eine ID ein \u001B[90m(z.B. " + UUID.randomUUID().toString().split("-")[0] + ")\u001B[0m: ");

                        var id = scanner.nextLine();
                        var user_storys = getUserStorys(id);
                        if (user_storys.isEmpty())
                        {
                            System.out.println("Keine UserStory mit der ID " + id + " gefunden!");
                        } else
                        {
                            if (user_storys.size() == 1)
                            {
                                System.out.println("Eine UserStory mit der ID " + id + " gefunden!");
                            } else
                            {
                                // output all found user storys
                                for (UserStory user_story : user_storys)
                                {
                                    System.out.println(user_story + "\n");
                                }
                            }
                        }
                    } else
                    {
                        // search for string[1] in all user storys
                        var search_term = strings[1];
                        var found = new ArrayList<UserStory>();
                        for (UserStory user_story : liste)
                        {
                            if (user_story.toString().contains(search_term))
                            {
                                found.add(user_story);
                            }
                        }

						if (found.isEmpty())
						{
							System.out.println("Keine UserStory mit dem Suchbegriff " + search_term + " gefunden!");
						}

                        for (UserStory user_story : found)
                        {
                            System.out.println(user_story + "\n");
                        }
                    }
                }
                case "store" ->
                {
                    // Beispiel-Code
                    UserStory userStory = new UserStory();
                    this.addUserStory(userStory);
                    this.store();
                }
                case "load" -> this.load();
                case "exit" ->
                {
                    System.out.println("Programm wird beendet!");
                    System.exit(0);
                }
				case "add_example_user-story" ->
				{
					var example_user_story = new UserStory();

					example_user_story.setId(UUID.randomUUID());
					example_user_story.setTitel("Beispiel-User-Story");
					example_user_story.setProject("Coll@HBRS");
					example_user_story.setDescription("Dies ist eine Beispiel-User-Story");
					example_user_story.setAcceptanceCriteria("Diese User-Story ist ein Beispiel");
					example_user_story.setExpense(1);
					example_user_story.setAddedValue(1);
					example_user_story.setRisk(1);
					example_user_story.setPenalty(1);
					example_user_story.setPriority(1);

					addUserStory(example_user_story);
				}
                default -> System.out.println("Der Befehl '" + strings[0] + "' ist nicht bekannt!");
            }
        } // Ende der Schleife
    }

    /**
     * Diese Methode realisiert die Ausgabe.
     */
    public void startAusgabe()
    {

        // Hier möchte Herr P. die Liste mit einem eigenen Sortieralgorithmus sortieren und dann
        // ausgeben. Allerdings weiß der Student hier nicht weiter

        // [Sortierung ausgelassen]
        Collections.sort(this.liste);

        // Klassische Ausgabe über eine For-Each-Schleife
        for (UserStory story : liste)
        {
            System.out.println(story.toString());
        }

        // [Variante mit forEach-Methode / Streams (--> Kapitel 9, Lösung Übung Nr. 2)?
        //  Gerne auch mit Beachtung der neuen US1
        // (Filterung Projekt = "ein Wert (z.B. Coll@HBRS)" und Risiko >=5
        List<UserStory> reduzierteListe = this.liste.stream()
                .filter(story -> story.getProject().equals("Coll@HBRS"))
                .filter(story -> story.getRisk() >= 5)
                .toList();
    }

    /*
     * Methode zum Speichern der Liste. Es wird die komplette Liste
     * inklusive ihrer gespeicherten UserStory-Objekte gespeichert.
     *
     */
    private void store() throws ContainerException
    {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(Container.LOCATION);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(this.liste);
            System.out.println(this.size() + " UserStory wurden erfolgreich gespeichert!");
        } catch (IOException e)
        {
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
    public void load()
    {
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(Container.LOCATION);
            ois = new ObjectInputStream(fis);

            // Auslesen der Liste
            Object obj = ois.readObject();
            if (obj instanceof List<?>)
            {
                this.liste = (List) obj;
            }
            System.out.println("Es wurden " + this.size() + " UserStory erfolgreich reingeladen!");
        } catch (IOException e)
        {
            System.out.println("LOG (für Admin): Datei konnte nicht gefunden werden!");
        } catch (ClassNotFoundException e)
        {
            System.out.println("LOG (für Admin): Liste konnte nicht extrahiert werden (ClassNotFound)!");
        } finally
        {
            if (ois != null) try
            {
                ois.close();
            } catch (IOException e)
            {
            }
            if (fis != null) try
            {
                fis.close();
            } catch (IOException e)
            {
            }
        }
    }

    /**
     * Methode zum Hinzufügen eines Mitarbeiters unter Wahrung der Schlüsseleigenschaft
     *
     * @param userStory
     * @throws ContainerException
     */
    public void addUserStory(UserStory userStory) throws ContainerException
    {
        if (contains(userStory))
        {
            throw new ContainerException("ID bereits vorhanden!");
        }
        liste.add(userStory);
    }

    /**
     * Prüft, ob eine UserStory bereits vorhanden ist
     *
     * @param userStory
     * @return
     */
    private boolean contains(UserStory userStory)
    {
        var ID = userStory.getId();
        for (UserStory userStory1 : liste)
        {
            if (userStory1.getId() == ID)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Ermittlung der Anzahl von internen UserStory
     * -Objekten
     *
     * @return Returns size of
     */
    public int size()
    {
        return liste.size();
    }

    /**
     * Methode zur Rückgabe der aktuellen Liste mit Stories
     * Findet aktuell keine Anwendung bei Hr. P.
     *
     * @return
     */
    public List<UserStory> getCurrentList()
    {
        return this.liste;
    }

    /**
     * Liefert eine bestimmte UserStory zurück
     *
     * @param id
     * @return
     */
    private List<UserStory> getUserStorys(String id)
    {
		List<UserStory> found = new ArrayList<>();
        for (UserStory userStory : liste)
        {
            if (userStory.getId().toString().startsWith(id))
            {
                found.add(userStory);
            }
        }
        return found;
    }
}
