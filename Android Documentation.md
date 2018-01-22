# Android Anwendung

In diesem Kapitel setzen wir uns präzise mit allen Einzelheiten der Android Applikation auseinander. Zum einen wird auf die Struktur des Projekts eingegangen: Wie sieht die Ordnerstruktur aus? Welche Packages gibt es? Welche Klassen gibt es und was ist ihre Daseinsberechtigung? Das Kapitel soll zukünftigen Entwicklern auf möglichst einfache Art und Weise einen Gesamtüberblick über das Projekt zu bekommen, sowie ihm die Möglichkeit bieten, bestimmte Details zeitsparend nachschlagen zu können. 

## Zusammenfassung

Die Android Applikation bildet im Rahmen unseres Projektes das Hauptprodukt für den Nutzer. Eine in Funktionalität vergleichbare Webapplikation ist derzeit nicht vorgesehen, ließe sich aber aufgrund der wiederverwendbaren REST API basierend auf Node JS jedoch problemlos dem Gesamtprodukt hinzufügen. Die Android Anwendung ermöglicht es dem Nutzer Orte aus Google Maps auszuwählen, mit einem Kommentar sowie einem Bild zu versehen und in einer Liste zu speichern. Die Ortsliste kann sowohl in Listenform als auch in einer Kartenansicht durch Marker angezeigt werden. Der Nutzer hat die Möglichkeit Orte als Favorit zu markieren. 

Der Nutzer kann wahlweise per NFC oder E-Mail seine gespeicherten Orte mit Freunden teilen. Dadurch erhält dieser Zugriff auf die Favoritenliste des Nutzers. Die Favoritenlisten von Freunden werden nach Änderungen automatisch aktualisiert. Favoritenlisten von Freunden können in Listenform oder in der Kartenansicht betrachtet werden, wobei es auf der Mapansicht möglich ist Favoritenlisten von mehreren Freunden gleichzeitig zu betrachten. Auf der Kartenansicht lassen sich Ortsmarker explizit nach Freunden filtern. 

Für jeden gespeicherten Freund gibt es eine Detailansicht, die Daten des Nutzers und seine favorisierten Orte anzeigt. Zudem wird eine Möglichkeit geboten, die Orte nach zugehörigem Staat zu filtern. 

Für jeden Ort exisitert zudem eine Detailansicht, die einen Überblick über die Daten des gespeicherten Ortes wie Beschreibung oder Kategorie gewährt. 

Alle relevanten Nutzer- und Ortdaten werden im Backend in einer MongoDB Datenbank gespeichert.



## Ordnerstruktur

Die allgemeine übergeordnete Struktur des Projektes entspricht dem standardmäßig automatisch erstellten Android Projektes. Spezielle Eigenheiten besitzt nur der Ordner */src/main* innerhalb des */app* Ordners. Dieser ist, wie in Android Projekten üblich, in die Ordner */java* und */res* aufgeteilt, wobei ersterer, wie der Name bereits erahnen lässt, alle Java Dateien enthält, während letzterer alle Ressource-Dateien, seien es *xml*-Dateien oder Bilddateien, beinhaltet. 

src/main

​	|- java 					Alle Java Dateien

​		|- activites 			Enthält alle Activities

​		|- adapters			Enthält alle Listadapter, die in Activities benutzt werden

​		|- fragments			Enthält alle Fragments, die in Activities eingebunden werden

​		|- helpers			Enthält Klassen mit wiederverwendbaren Funktionen

​		|- models			Enthält Models, die für den JSON-Parser genutzt werden

​		|- services			Enthält Services, die sich mit Push-Notifications befassen

​	|- res 					Alle Ressource-Dateien (vor allem .xml, aber auch Bilddateien)

​		|- drawable			Enthält Icons und Bilddateien

​		|- layout				Enthält alle Layouts der Activities, Fragments, Adapter etc.

​		|- menu				Enthält xml-Dateien, die Listen für Menüs definieren

​		|- mipmap			Enthält das Launcher Icon in verschiedenen Auflösungen

​		|- values			Enthält Definitionen von verschiedenen Werten (z.b. Texte)

​		|- xml				???

​	|- AndroidManifest.xml	Wichtige Konfigurationsdatei der Anwendung (z.b. Berechtigungen)



Die Bedeutung der Ordner innerhalb */res* ist in der Regel selbsterklärend und bereits von Android standardmäßig vorgegeben, weswegen in dieser Dokumentation nicht weiter darauf eingegangen wird.

Viel interessanter sind jedoch die, individuell für dieses Projekt erstellte, Strukture der Packages innerhalb des */java* Ordners. Diese Struktur wurde unabhängig von Vorgaben auf Basis von Entscheidungen der Entwickler festgelegt, um um eine möglichst eindeutige Spaltung des Quellcodes in verschiedene Aufgabenbereiche zu gewährleisten. Die Vorgabe des standardmäßigen Android Projektes ist, im Vergleich zum Ressourcen-Ordner, nur einzelnes Package.

### Java Packages

Das *Java-Package*, das den gesamten Java-Quellcode enthält besteht aus mehreren Packages, die sich mit jeweils unterschiedlichen Teilen der Logik befassen.   

####activities

Dieses Package enthält sämtlichen Activities, sprich die komplette UI Logik. In den Activities spielt sich die komplette Interaktion zwischen Nutzer und App. Im Projekt herrscht keine strikte Trennung in View- und Controller-Komponenten, sodass die Activities ebenfalls viel Business-Logik enthalten. So ist zum Beispiel auch das die Interaktion der App mit der REST API über sogenannte Async Tasks in den Activities aufzufinden. 

Jede Activity greift auf eine entsprechende xml-Datei zu, um das User Interface anzeigen zu lassen. Über die in den Activities implementierten Logik wird die Anzeige verändert und Interaktionen des Users werden behandelt. 

In folgender Tabelle werden die einzelnen Activities kurz beschrieben. Um eine detaillierte Dokumentation der Funktionen innerhalb der Activities zu erhalten, können Sie auf die javadocs zurückgreifen.

| Activity                | Funktionalität                           |
| ----------------------- | ---------------------------------------- |
| LoginActivity           | Dies ist die Launcher Activity (d.h. beim Start der App wird diese Activity aufgerufen). Ein Button gibt dem Nutzer die Möglichkeit, sich über Google anzumelden/einzuloggen. Sobald der Nutzer eingeloggt ist, wird er zur HomeActivity weitergeleitet. Ist der Nutzer bereits eingeloggt, wird er ohne weitere Nutzerinteraktion direkt zur HomeActivity weitergeleitet. |
| HomeActivity            | Dies ist die für den Nutzer bedeutendste Activity, da sie die Hauptnavigation (über eine sogenannte *Bottom Navigation*) integriert und somit zwischen 4 verschiedenen Fragments (siehe Kapitel zum Package *fragments*) umschaltet. |
| AddLocationActivity     | Der Nutzer wählt einen Ort aus der Google Place API aus. Diesem kann er optional Kategorien, einen Kommentar sowie ein oder mehrere Bilder hinzufügen. |
| FriendDetailsActivity   | Die Activity zeigt dem Nutzer Name und Profilbild eines befreundeten Nutzers mit einer Listenansicht dessen favorisierter Orte. Diese können nach Ländern gefiltert werden. Ein Button bietet die Option, den Nutzer, nach der Bestätigung in einem Popup, aus der Freundesliste zu entfernen. |
| EditProfileActivity     | Der Nutzer kann über eine Eingabefläche seinen Vor- und Nachnamen ändern, ein Profilbild hinzufügen oder das bestehende ändern. |
| LocationDetailsActivity | Diese Activity zeigt dem Nutzer die Beschreibung, Kategorien und Bilder zu einem Ort an. Wurde dieser Ort von mehreren Nutzern hinzugefügt, so kann über Reiter (z.b. "Name 1", "Name 2") zwischen den Daten und Bildern, die von unterschiedlichen Nutzern hinzugefügt wurden, gewechselt werden. |
| AddFriendNfcActivity    | Durch eine NFC-Verbindung wird eine digitale Freundesbeziehung zweier Nutzer aufgebaut. Bei der Übertragung werden nur die jeweiligen User-IDs ausgetauscht. Die Freunde werden durch einen Request zum Backend hinzugefügt. Dadurch werden die Nutzer nun in der Freundesliste und deren Orte auf der Karte des jeweils Anderen angezeigt. Im Vergleich zur *AddFriendEmailActivity* wird in diesem Vorgehen auf jeden Fall eine bidirektionale Freundschaftsbeziehung aufgebaut, d.h. beide Nutzer teilen ihre Orte mit dem anderen. |
| AddFriendEmailActivity  | Diese Activity dient als "Fallback", falls eines der Geräte der Nutzer, die ihre Orte miteinander tauschen wollen, kein NFC besitzen, oder falls keine unmittelbare räumliche Nähe der beiden Nutzer besteht. Zu ihr wird automatisch weitergeleitet, wenn das Handy kein NFC besitzt. Außerdem ist sie über einen Button in der AddFriendNfcActivity erreichbar. Es kann anhand einer E-Mail-Adresse kann nach einem anderen Nutzer gesucht werden. Nach einer erfolgreichen Suche wird der Name und das Profilbild des Gesuchten angezeigt. Über einen Button kann der Nutzer mit diesem seine Favoritenliste teilen. Der andere Nutzer wird daraufhin (via *Push Notification*) davon benachrichtigt und kann nun ebenfalls seine Orte teilen. |



####fragments

Dieses Package befasst sich mit ähnlicher Logik wie diejenige, die bereits im vorherigen Kapitel beschrieben wurde. Jedoch handelt es sich bei den den enhaltenen Klassen um Fragments. Diese stellen Bausteine dar, die innerhalb von einer Activity verwendet werden. Ein Fragment ist wiederverwendbar und kann somit von verschiedenen Activities oder in einer merhmals benutzt werden. Die bedeutensten Klassen in diesem Package sind die Fragments *MapFragment*, *MyListFragment*, *FriendsFragment* und *SettingsFragment*, da sie in der *HomeActivity*, quasi der Hauptanlaufstelle der App, eingebaut werden. Über eine Navigation kann der Nutzer durch diese vier Fragments navigieren.  In folgender Tabelle wird jedes Fragment detaillierter beschrieben. 

| Fragment    | Funktionalität                           |
| ----------- | ---------------------------------------- |
| MapFragment | Dieses Fragment bietet eine große Funktionalität und ist die komplexeste Klasse des Projektes. Es zeigt dem Nutzer eine Kartenansicht mit Markern für seine eigenen Orte und den Favoriten von befreundeten Nutzern. Über eine Filteroption lassen sich Orte nach Freunden filtern. Dies wird über eine Liste innerhalb eines *Drawers* realisiert. Für die Karte wird die von Android bereitgestellte *Mapview* verwendet. Für die jeweiligen Marker wird bei Klick individuelles Popup-Fenster geöffnet, das ein Bild und entsprechende Daten des Ortes anzeigt. Die Marker haben je nach Zugehörigkeit zu einem Freund unterschiedliche Farben. Sollte ein bestimmter Ort von mehreren Freunden gespeichert sein, wird ein entsprechender, spezieller Marker benutzt. Über einen Klick auf das Popup-Fenster wird der Nutzer zu *LocationDetailsActivity* weitergeleitet. Ein runder Button am Rande der Karte leitet den Nutzer zur *AddLocationActivity* weiter. |
|             |                                          |
|             |                                          |
|             |                                          |
|             |                                          |

