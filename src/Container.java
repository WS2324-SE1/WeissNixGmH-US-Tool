import java.io.*;
import java.util.*;
import java.util.function.*;
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
    @SafeVarargs
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
        System.out.println("UserStory-Tool V1.0 by Maximilian Fetz");
        System.out.println("Bitte geben Sie einen Befehl ein (help für Hilfe):");

        while (true)
        {
            // Ausgabe eines Texts zur Begruessung

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

                case "dump" -> {
                    if (liste.isEmpty())
                    {
                        System.out.println("Keine UserStorys vorhanden!");
                    }
                    else
                    {
                        List<Function<UserStory, Boolean>> filters = new ArrayList<>();
                        Comparator<UserStory> sort_comparator = UserStory::compareTo;
                        // nach filtern fragen und optionen präsentieren
                        System.out.println("Möchten Sie die Liste filtern? (y/n)");
                        var filter = scanner.nextLine();
                        if (filter.equals("y"))
                        {
                            ask_for_filters(scanner, filters);
                        }

                        System.out.println("Möchten Sie die Liste sortieren? (y/n)");
                        var sort = scanner.nextLine();
                        if (sort.equals("y"))
                        {
                            sort_comparator = ask_for_sorting(scanner, sort_comparator);
                        }

                        startAusgabe(sort_comparator, filters);
                    }
                }

                case "enter" ->
                {
                    var new_user_story = new UserStory();
                    ask_for_user_story_parameters(new_user_story, scanner);
                    addUserStory(new_user_story);
                }

                case "search" ->
                {
                    if (strings.length == 1)
                    {
                        search(scanner);
                    }
                    else
                    {
                        search_for_serch_term(strings);
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
                    add_example_user_stories();

                }
                default -> System.out.println("Der Befehl '" + strings[0] + "' ist nicht bekannt!");
            }
        } // Ende der Schleife
    }

    private static Comparator<UserStory> ask_for_sorting(Scanner scanner, Comparator<UserStory> sort_comparator)
    {
        // alle Sortieroptionen auf einmal zur auswahl präsentieren
        System.out.println("Wonach möchten Sie sortieren?");
        System.out.println("\t- Projekt");
        System.out.println("\t- Titel");
        System.out.println("\t- Beschreibung");
        System.out.println("\t- Akzeptanzkriterien");
        System.out.println("\t- Aufwand");
        System.out.println("\t- Mehrwert");
        System.out.println("\t- Risiko");
        System.out.println("\t- Strafe");
        System.out.println("\t- Priorität");
        System.out.println("Bitte geben Sie die Sortieroption ein:");
        var sort_option = scanner.nextLine();

        switch (sort_option)
        {
            case "Projekt" -> sort_comparator = Comparator.comparing(UserStory::getProject);
            case "Titel" -> sort_comparator = Comparator.comparing(UserStory::getTitel);
            case "Beschreibung" -> sort_comparator = Comparator.comparing(UserStory::getDescription);
            case "Akzeptanzkriterien" -> sort_comparator = Comparator.comparing(UserStory::getAcceptanceCriteria);
            case "Aufwand" -> sort_comparator = Comparator.comparing(UserStory::getExpense);
            case "Mehrwert" -> sort_comparator = Comparator.comparing(UserStory::getAddedValue);
            case "Risiko" -> sort_comparator = Comparator.comparing(UserStory::getRisk);
            case "Strafe" -> sort_comparator = Comparator.comparing(UserStory::getPenalty);
            case "Priorität" -> sort_comparator = Comparator.comparing(UserStory::getPriority);
            default -> System.out.println("Die Sortieroption '" + sort_option + "' ist nicht bekannt!");
        }
        return sort_comparator;
    }

    private static void ask_for_filters(Scanner scanner, List<Function<UserStory, Boolean>> filters)
    {
        System.out.println("Wonach möchten Sie filtern?");
        System.out.println("\t- Projekt");
        System.out.println("\t- Titel");
        System.out.println("\t- Beschreibung");
        System.out.println("\t- Akzeptanzkriterien");
        System.out.println("\t- Aufwand");
        System.out.println("\t- Mehrwert");
        System.out.println("\t- Risiko");
        System.out.println("\t- Strafe");
        System.out.println("\t- Priorität");
        System.out.println("Bitte geben Sie die Filteroption ein:");
        var filter_option = scanner.nextLine();

        switch (filter_option)
        {
            case "Projekt" -> {
                System.out.println("Bitte geben Sie den Projektnamen ein:");
                var project_name = scanner.nextLine();
                filters.add((user_story) -> user_story.getProject().equals(project_name));
            }
            case "Titel" -> {
                System.out.println("Bitte geben Sie den Titel oder einen Teil des Titels ein:");
                var title = scanner.nextLine();
                filters.add((user_story) -> user_story.getTitel().contains(title));
            }
            case "Beschreibung" -> {
                System.out.println("Bitte geben Sie die Beschreibung oder einen Teil der Beschreibung ein:");
                var description = scanner.nextLine();
                filters.add((user_story) -> user_story.getDescription().contains(description));
            }
            case "Akzeptanzkriterien" -> {
                System.out.println("Bitte geben Sie die Akzeptanzkriterien oder einen Teil der Akzeptanzkriterien ein:");
                var acceptance_criteria = scanner.nextLine();
                filters.add((user_story) -> user_story.getAcceptanceCriteria().contains(acceptance_criteria));
            }
            case "Aufwand" ->
            {
                System.out.println("Bitte geben Sie den Aufwand ein (schreibe > oder < vor die Zahl, um nach größer oder kleiner zu filtern):");
                var expense = scanner.nextLine();
                if (expense.startsWith(">"))
                {
                    var expense_number = Integer.parseInt(expense.substring(1));
                    filters.add((user_story) -> user_story.getExpense() > expense_number);
                } else if (expense.startsWith("<"))
                {
                    var expense_number = Integer.parseInt(expense.substring(1));
                    filters.add((user_story) -> user_story.getExpense() < expense_number);
                } else
                {
                    var expense_number = Integer.parseInt(expense);
                    filters.add((user_story) -> user_story.getExpense() == expense_number);
                }
            }
            case "Mehrwert" ->
            {
                System.out.println("Bitte geben Sie den Mehrwert ein (schreibe > oder < vor die Zahl, um nach größer oder kleiner zu filtern):");
                var added_value = scanner.nextLine();
                if (added_value.startsWith(">"))
                {
                    var added_value_number = Integer.parseInt(added_value.substring(1));
                    filters.add((user_story) -> user_story.getAddedValue() > added_value_number);
                } else if (added_value.startsWith("<"))
                {
                    var added_value_number = Integer.parseInt(added_value.substring(1));
                    filters.add((user_story) -> user_story.getAddedValue() < added_value_number);
                } else
                {
                    var added_value_number = Integer.parseInt(added_value);
                    filters.add((user_story) -> user_story.getAddedValue() == added_value_number);
                }
            }
            case "Risiko" ->
            {
                System.out.println("Bitte geben Sie das Risiko ein (schreibe > oder < vor die Zahl, um nach größer oder kleiner zu filtern):");
                var risk = scanner.nextLine();
                if (risk.startsWith(">"))
                {
                    var risk_number = Integer.parseInt(risk.substring(1));
                    filters.add((user_story) -> user_story.getRisk() > risk_number);
                } else if (risk.startsWith("<"))
                {
                    var risk_number = Integer.parseInt(risk.substring(1));
                    filters.add((user_story) -> user_story.getRisk() < risk_number);
                } else
                {
                    var risk_number = Integer.parseInt(risk);
                    filters.add((user_story) -> user_story.getRisk() == risk_number);
                }
            }
            case "Strafe" ->
            {
                System.out.println("Bitte geben Sie die Strafe ein (schreibe > oder < vor die Zahl, um nach größer oder kleiner zu filtern):");
                var penalty = scanner.nextLine();
                if (penalty.startsWith(">"))
                {
                    var penalty_number = Integer.parseInt(penalty.substring(1));
                    filters.add((user_story) -> user_story.getPenalty() > penalty_number);
                } else if (penalty.startsWith("<"))
                {
                    var penalty_number = Integer.parseInt(penalty.substring(1));
                    filters.add((user_story) -> user_story.getPenalty() < penalty_number);
                } else
                {
                    var penalty_number = Integer.parseInt(penalty);
                    filters.add((user_story) -> user_story.getPenalty() == penalty_number);
                }
            }
            case "Priorität" ->
            {
                System.out.println("Bitte geben Sie die Priorität ein (schreibe > oder < vor die Zahl, um nach größer oder kleiner zu filtern):");
                var priority = scanner.nextLine();
                if (priority.startsWith(">"))
                {
                    var priority_number = Integer.parseInt(priority.substring(1));
                    filters.add((user_story) -> user_story.getPriority() > priority_number);
                } else if (priority.startsWith("<"))
                {
                    var priority_number = Integer.parseInt(priority.substring(1));
                    filters.add((user_story) -> user_story.getPriority() < priority_number);
                } else
                {
                    var priority_number = Integer.parseInt(priority);
                    filters.add((user_story) -> user_story.getPriority() == priority_number);
                }
            }
            default -> System.out.println("Der Filter '" + filter_option + "' ist nicht bekannt!");
        }
    }

    private void ask_for_user_story_parameters(UserStory new_user_story, Scanner scanner)
    {
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
        new_user_story.calculatePriority();
    }

    private void search_for_serch_term(String[] strings)
    {
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

    private void search(Scanner scanner)
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
    }

    private void add_example_user_stories() throws ContainerException
    {
        var first_example_user_story = new UserStory();
        first_example_user_story.setId(UUID.randomUUID());
        first_example_user_story.setTitel("Erste Beispiel-UserStory");
        first_example_user_story.setProject("Beispiel-Projekt");
        first_example_user_story.setDescription("Dies ist eine Beispiel-UserStory");
        first_example_user_story.setAcceptanceCriteria("Dies ist ein Beispiel für Akzeptanzkriterien");
        first_example_user_story.setExpense(1);
        first_example_user_story.setAddedValue(10);
        first_example_user_story.setRisk(1);
        first_example_user_story.setPenalty(1);
        first_example_user_story.calculatePriority();
        addUserStory(first_example_user_story);


        var second_example_user_story = new UserStory();
        second_example_user_story.setId(UUID.randomUUID());
        second_example_user_story.setTitel("Zweite Beispiel-UserStory");
        second_example_user_story.setProject("Beispiel-Projekt");
        second_example_user_story.setDescription("Dies ist eine Beispiel-UserStory");
        second_example_user_story.setAcceptanceCriteria("Dies ist ein Beispiel für Akzeptanzkriterien");
        second_example_user_story.setExpense(2);
        second_example_user_story.setAddedValue(20);
        second_example_user_story.setRisk(2);
        second_example_user_story.setPenalty(2);
        second_example_user_story.calculatePriority();
        addUserStory(second_example_user_story);

        var third_example_user_story = new UserStory();
        third_example_user_story.setId(UUID.randomUUID());
        third_example_user_story.setTitel("Dritte Beispiel-UserStory");
        third_example_user_story.setProject("Beispiel-Projekt");
        third_example_user_story.setDescription("Dies ist eine Beispiel-UserStory");
        third_example_user_story.setAcceptanceCriteria("Dies ist ein Beispiel für Akzeptanzkriterien");
        third_example_user_story.setExpense(3);
        third_example_user_story.setAddedValue(30);
        third_example_user_story.setRisk(3);
        third_example_user_story.setPenalty(3);
        third_example_user_story.calculatePriority();
        addUserStory(third_example_user_story);

        var fourth_example_user_story = new UserStory();
        fourth_example_user_story.setId(UUID.randomUUID());
        fourth_example_user_story.setTitel("Vierte Beispiel-UserStory");
        fourth_example_user_story.setProject("Beispiel-Projekt");
        fourth_example_user_story.setDescription("Dies ist eine weitere Beispiel-UserStory");
        fourth_example_user_story.setAcceptanceCriteria("Akzeptanzkriterien für die vierte UserStory");
        fourth_example_user_story.setExpense(5);
        fourth_example_user_story.setAddedValue(15);
        fourth_example_user_story.setRisk(4);
        fourth_example_user_story.setPenalty(5);
        fourth_example_user_story.calculatePriority();
        addUserStory(fourth_example_user_story);

        var fifth_example_user_story = new UserStory();
        fifth_example_user_story.setId(UUID.randomUUID());
        fifth_example_user_story.setTitel("Fünfte Beispiel-UserStory");
        fifth_example_user_story.setProject("Beispiel-Projekt");
        fifth_example_user_story.setDescription("Ein weiteres Beispiel einer UserStory");
        fifth_example_user_story.setAcceptanceCriteria("Spezifische Akzeptanzkriterien für die fünfte Story");
        fifth_example_user_story.setExpense(1);
        fifth_example_user_story.setAddedValue(25);
        fifth_example_user_story.setRisk(2);
        fifth_example_user_story.setPenalty(3);
        fifth_example_user_story.calculatePriority();
        addUserStory(fifth_example_user_story);

        var sixth_example_user_story = new UserStory();
        sixth_example_user_story.setId(UUID.randomUUID());
        sixth_example_user_story.setTitel("Optimierung der Ladezeiten");
        sixth_example_user_story.setProject("Performance-Projekt");
        sixth_example_user_story.setDescription("Verbesserung der Ladezeiten unserer Hauptseite um 50%");
        sixth_example_user_story.setAcceptanceCriteria("Seite lädt in weniger als 2 Sekunden");
        sixth_example_user_story.setExpense(8);
        sixth_example_user_story.setAddedValue(40);
        sixth_example_user_story.setRisk(5);
        sixth_example_user_story.setPenalty(7);
        sixth_example_user_story.calculatePriority();
        addUserStory(sixth_example_user_story);

        var seventh_example_user_story = new UserStory();
        seventh_example_user_story.setId(UUID.randomUUID());
        seventh_example_user_story.setTitel("Integration eines Chatbots");
        seventh_example_user_story.setProject("Kundenservice-Verbesserung");
        seventh_example_user_story.setDescription("Implementierung eines intelligenten Chatbots zur Kundensupport-Optimierung");
        seventh_example_user_story.setAcceptanceCriteria("Chatbot löst 70% der Anfragen automatisch");
        seventh_example_user_story.setExpense(6);
        seventh_example_user_story.setAddedValue(50);
        seventh_example_user_story.setRisk(4);
        seventh_example_user_story.setPenalty(6);
        seventh_example_user_story.calculatePriority();
        addUserStory(seventh_example_user_story);

        var eighth_example_user_story = new UserStory();
        eighth_example_user_story.setId(UUID.randomUUID());
        eighth_example_user_story.setTitel("Mobile App Sicherheitsupdate");
        eighth_example_user_story.setProject("Mobile Sicherheit");
        eighth_example_user_story.setDescription("Update der Sicherheitsfeatures der mobilen App zur Einhaltung neuer Datenschutzbestimmungen");
        eighth_example_user_story.setAcceptanceCriteria("Erfüllung aller neuen Datenschutzstandards");
        eighth_example_user_story.setExpense(9);
        eighth_example_user_story.setAddedValue(30);
        eighth_example_user_story.setRisk(7);
        eighth_example_user_story.setPenalty(9);
        eighth_example_user_story.calculatePriority();
        addUserStory(eighth_example_user_story);
    }

    public static void printUserStoryTable(List<UserStory> userStories) {
        System.out.println("+------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-36s | %-30s | %-7s |\n", "Titel", "Projekt", "Priorität");
        System.out.println("+------------------------------------------------------------------------------------------------------------------+");

        for (UserStory story : userStories) {
            System.out.printf("| %-36s | %-30s | %-7f |\n",
                    story.getTitel(),
                    story.getProject(),
                    story.getPriority());
        }

        System.out.println("+--------------------------------------------------------+");
    }

    public void startAusgabe(Comparator<UserStory> comparator, List<Function<UserStory, Boolean>> filters)
    {
        // Ausgabe der Liste
        if (liste.isEmpty())
        {
            System.out.println("Keine UserStorys vorhanden!");
        } else
        {
            var user_storys_to_show = new ArrayList<UserStory>();
            // sort list
            liste.sort(comparator);
            // filter list
            for (UserStory user_story : liste)
            {
                boolean show = true;
                for (Function<UserStory, Boolean> filter : filters)
                {
                    if (!show)
                    {
                        break;
                    }
                    show = filter.apply(user_story);
                }
                if (show)
                {
                    user_storys_to_show.add(user_story);
                }
            }

            // output all user storys in a table format
            printUserStoryTable(user_storys_to_show);
        }
    }

    /*
     * Methode zum Speichern der Liste. Es wird die komplette Liste
     * inklusive ihrer gespeicherten UserStory-Objekte gespeichert.
     *
     */
    private void store() throws ContainerException
    {
        ObjectOutputStream oos;
        FileOutputStream fos;
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
