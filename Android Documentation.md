# Android Anwendung

In diesem Kapitel setzen wir uns präzise mit allen Einzelheiten der Android Applikation auseinander. Zum einen wird auf die Struktur des Projekts eingegangen: Wie sieht die Ordnerstruktur aus? Welche Packages gibt es? Welche Klassen gibt es und was ist ihre Daseinsberechtigung? Das Kapitel soll zukünftigen Entwicklern auf möglichst einfache Art und Weise einen Gesamtüberblick über das Projekt zu bekommen, sowie ihm die Möglichkeit bieten, bestimmte Details zeitsparend nachschlagen zu können. 



## Zusammenfassung

Die Android Applikation bildet im Rahmen unseres Projektes das Hauptprodukt für den Nutzer. Eine in Funktionalität vergleichbare Webapplikation ist derzeit nicht vorgesehen, ließe sich aber aufgrund der wiederverwendbaren REST API basierend auf Node JS jedoch problemlos dem Gesamtprodukt hinzufügen. Die Android Anwendung ermöglicht es dem Nutzer Orte aus Google Maps auszuwählen, mit einem Kommentar sowie einem Bild zu versehen und in einer Liste zu speichern. Die Ortsliste kann sowohl in Listenform als auch in einer Kartenansicht durch Marker angezeigt werden. Der Nutzer hat die Möglichkeit Orte als Favorit zu markieren. 

Der Nutzer kann wahlweise per NFC oder E-Mail seine gespeicherten Orte mit Freunden teilen. Dadurch erhält dieser Zugriff auf die Favoritenliste des Nutzers. Die Favoritenlisten von Freunden werden nach Änderungen automatisch aktualisiert. Favoritenlisten von Freunden können in Listenform oder in der Kartenansicht betrachtet werden, wobei es auf der Mapansicht möglich ist Favoritenlisten von mehreren Freunden gleichzeitig zu betrachten. Auf der Kartenansicht lassen sich Ortsmarker explizit nach Freunden filtern. 

Für jeden gespeicherten Freund gibt es eine Detailansicht, die Daten des Nutzers und seine favorisierten Orte anzeigt. Zudem wird eine Möglichkeit geboten, die Orte nach zugehörigem Staat zu filtern. 

Für jeden Ort exisitert zudem eine Detailansicht, die einen Überblick über die Daten des gespeicherten Ortes wie Beschreibung oder Kategorie gewährt. 

Alle relevanten Nutzer- und Ortdaten werden im Backend in einer MongoDB Datenbank gespeichert.



## Projektstruktur

Im diesem Kapitel wird die Struktur des Projektes untersucht. Dabei wird auf den Aufbau der Ordner- und Packagestruktur eingegangen, sowie die Zuständigkeit der verschiedenen Klassen beleuchtet. Sehr bedeutend ist hierfür auch eine kurze Beschreibung der Funktionalität der einzelnen UI-Komponenten wie Activities und Fragments.



### Berechtigungen

In der Datei *AndroidManifest.xml* werden die benötigten Berechtigungen angegeben. Diese werden dem Smartphonebesitzer angezeigt, sobald er sich die App aus dem Play Store herunterladen will. Die Berechtigungen werden benötigt, um auf bestimmte Sensoren oder Funktionen des Handys zuzugreifen.

Die Berechtigungen werden in der Form

 `<uses-permission android:name="android.permission.INTERNET" />` angegeben.

| Berechtigung           | Für welche Funktion?                     |
| ---------------------- | ---------------------------------------- |
| INTERNET               | Kommunikation übers Internet uum Backend und zu Google APIs. |
| ACCESS_NETWORK_STATE   | Einholen von Informationen über das Netzwerk: z.B. zur Prüfung, ob das Handy eine Netzwerkverbindung besitzt. |
| ACCESS_FINE_LOCATION   | Zugriff auf die genaue Position des Handys: Wichtig für den *PlacePicker*. |
| WRITE_EXTERNAL_STORAGE | Schreibzugriff auf den externen Speicher: Zum Speichern der aufgenommenen Bilder im Speicher, um sie daraufhin hochladen zu können. |
| READ_EXTERNAL_STORAGE  | Lesezugriff auf den externen Speicher: Zum Abrufen der aufgenommenen Bilder oder zum Auswählen von bereits gespeicherten Bildern für das eigene Profil oder eine neue Location. |
| NFC                    | Zugriff auf NFC Funktionen: Zum Hinzufügen neuer Freunde. |

Ab Android 6.0 muss, um auf den externen Speicher zuzugreifen, ebenso bei Runtime eine Berechtigung vom Nutzer eingeholt werden. Dies gilt für alle als gefährlich eingestuften Berechtigungen. Siehe hierzu diesen [Leitfaden](https://developer.android.com/training/permissions/requesting.html). 

Zudem wird im Manifest angegeben, welche Features die App verwendet. Dies wird in der Form 

`<uses-feature android:name="android.hardware.camera" />` eingetragen. 

| Feature | Für welche Funktion?                     |
| ------- | ---------------------------------------- |
| nfc     | Siehe oben                               |
| camera  | Aufnehmen von Profilbildern oder Bilder, die man einem Ort hinzufügen kann. |



### Ordnerstruktur

Die allgemeine übergeordnete Struktur des Projektes entspricht dem standardmäßig automatisch erstellten Android Projektes. Spezielle Eigenheiten besitzt nur der Ordner */src/main* innerhalb des */app* Ordners. Dieser ist, wie in Android Projekten üblich, in die Ordner */java* und */res* aufgeteilt, wobei ersterer, wie der Name bereits erahnen lässt, alle Java Dateien enthält, während letzterer alle Ressource-Dateien, seien es *xml*-Dateien oder Bilddateien, beinhaltet. 

app							Enthält alle Dateien des Moduls app

|- build.gradle				Build-Konfiguration: definiert z.B. alle Abhängigkeiten

|- google-services.json		Konfigurationsdatei für Google Services (z.B. Sign In, FCM)

|- keystore

​	|- debug.keystore		Datei zur Zertifizierung der App (für Google Services nötig)

|- src/main					Quellcode

​	|- java 					Alle Java Dateien

​		|- activites 			Enthält alle Activities

​		|- adapters			Enthält alle Listadapter, die in Activities benutzt werden

​		|- fragments			Enthält alle Fragments, die in Activities eingebunden werden

​		|- helpers			Enthält Klassen mit wiederverwendbaren Funktionen

​		|- models			Enthält Models, die für den JSON-Parser genutzt werden

​		|- services			Enthält Services, die sich mit Push Notifications befassen

​	|- res 					Alle Ressource-Dateien (vor allem .xml, aber auch Bilddateien)

​		|- drawable			Enthält Icons und Bilddateien

​		|- layout				Enthält alle Layouts der Activities, Fragments, Adapter etc.

​		|- menu				Enthält xml-Dateien, die Listen für Menüs definieren

​		|- mipmap			Enthält das Launcher Icon in verschiedenen Auflösungen

​		|- values			Enthält Definitionen von verschiedenen Werten (z.b. Texte)

​		|- xml				Sontige xml-Dateien

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
| LoginActivity           | Dies ist die Launcher Activity (d.h. beim Start der App wird diese Activity aufgerufen). Ein Button gibt dem Nutzer die Möglichkeit, sich über Google anzumelden/einzuloggen. Sobald der Nutzer eingeloggt ist, wird er zur *HomeActivity* weitergeleitet. Ist der Nutzer bereits eingeloggt, wird er ohne weitere Nutzerinteraktion direkt weitergeleitet. |
| HomeActivity            | Dies ist die für den Nutzer bedeutendste Activity, da sie die Hauptnavigation (über eine sogenannte *Bottom Navigation*) integriert und somit zwischen 4 verschiedenen Fragments (siehe Kapitel zum Package *fragments*) umschaltet. |
| AddLocationActivity     | Der Nutzer wählt einen Ort über den sogenannenten *PlacePicker* aus. Dieser ist ein integriertes UI-Widget als Teil der *Google Places API*. Dem ausgewählten Ort kann der Nutzer optional Kategorien, eine Beschreibung sowie ein oder mehrere Bilder hinzufügen. |
| FriendDetailsActivity   | Die Activity zeigt dem Nutzer Name und Profilbild eines befreundeten Nutzers mit einer Listenansicht dessen favorisierter Orte. Diese können nach Ländern gefiltert werden. Ein Button bietet die Option, den Nutzer, nach der Bestätigung in einem Popup, aus der Freundesliste zu entfernen. |
| LocationDetailsActivity | Diese Activity zeigt dem Nutzer die Beschreibung, Kategorien und Bilder zu einem Ort an. Wurde dieser Ort von mehreren Nutzern hinzugefügt, so kann über Reiter (z.b. "Name 1", "Name 2") zwischen den Daten und Bildern, die von unterschiedlichen Nutzern hinzugefügt wurden, gewechselt werden. Die Funktionalität wird im *LocationDetailsFragment* implementiert. |
| AddFriendNfcActivity    | Durch eine NFC-Verbindung wird eine digitale Freundesbeziehung zweier Nutzer aufgebaut. Bei der Übertragung werden nur die jeweiligen User-IDs ausgetauscht. Die Freunde werden durch einen Request zum Backend hinzugefügt. Dadurch werden die Nutzer nun in der Freundesliste und deren Orte auf der Karte des jeweils Anderen angezeigt. Im Vergleich zur *AddFriendEmailActivity* wird in diesem Vorgehen auf jeden Fall eine bidirektionale Freundschaftsbeziehung aufgebaut, d.h. beide Nutzer teilen ihre Orte mit dem anderen. |
| AddFriendEmailActivity  | Diese Activity dient als "Fallback", falls eines der Geräte der Nutzer, die ihre Orte miteinander tauschen wollen, kein NFC besitzen, oder falls keine unmittelbare räumliche Nähe der beiden Nutzer besteht. Zu ihr wird automatisch weitergeleitet, wenn das Handy kein NFC besitzt. Außerdem ist sie über einen Button in der *AddFriendNfcActivity* erreichbar. Es kann anhand einer E-Mail-Adresse kann nach einem anderen Nutzer gesucht werden. Nach einer erfolgreichen Suche wird der Name und das Profilbild des Gesuchten angezeigt. Über einen Button kann der Nutzer mit diesem seine Favoritenliste teilen. Der andere Nutzer wird daraufhin (via *Push Notification*) davon benachrichtigt und kann nun ebenfalls seine Orte teilen. |
| EditProfileActivity     | Die aktuellen Nutzerdaten (inklusive Profilbild) werden hier angezeigt, sodass der Nutzer über eine Eingabefläche seinen Vor- und Nachnamen ändern, ein Profilbild hinzufügen oder das bestehende ändern kann. |



####fragments

Dieses Package befasst sich mit ähnlicher Logik wie diejenige, die bereits im vorherigen Kapitel beschrieben wurde. Jedoch handelt es sich bei den den enhaltenen Klassen um Fragments. Diese stellen Bausteine dar, die innerhalb von einer Activity verwendet werden. Ein Fragment ist wiederverwendbar und kann somit von verschiedenen Activities oder in einer merhmals benutzt werden. Die bedeutensten Klassen in diesem Package sind die Fragments *MyMapFragment*, *MyListFragment*, *MyFriendsFragment* und *SettingsFragment*, da sie in der *HomeActivity*, quasi der Hauptanlaufstelle der App, eingebaut werden. Über eine Navigation kann der Nutzer durch diese vier Fragments navigieren.  In folgender Tabelle wird jedes Fragment detaillierter beschrieben. 

| Fragment                | Funktionalität                           |
| ----------------------- | :--------------------------------------- |
| MapFragment             | Dieses Fragment bietet eine große Funktionalität und ist die komplexeste Klasse des Projektes. Es zeigt dem Nutzer eine Kartenansicht mit Markern für seine eigenen Orte und den Favoriten von befreundeten Nutzern. Über eine Filteroption lassen sich Orte nach Freunden filtern. Dies wird über eine Liste innerhalb eines *Drawers* realisiert. Für die Karte wird die von Android bereitgestellte *Mapview* verwendet. Für die jeweiligen Marker wird bei Klick individuelles Popup-Fenster geöffnet, das ein Bild und entsprechende Daten des Ortes anzeigt. Die Marker haben je nach Zugehörigkeit zu einem Freund unterschiedliche Farben. Sollte ein bestimmter Ort von mehreren Freunden gespeichert sein, wird ein entsprechender, spezieller Marker benutzt. Über einen Klick auf das Popup-Fenster wird der Nutzer zu *LocationDetailsActivity* weitergeleitet. Ein runder Button am Rande der Karte leitet den Nutzer zur *AddLocationActivity* weiter. Das *MapFragment* wird nur einmal innerhalb der Applikation verwendet, und zwar wird es in die *HomeActivity* eingebaut. |
| MyListFragment          | Dieses Fragment zeigt dem Nutzer eine Listenansicht seiner eigenen Orte an. Durch einen *ImageViewButton* (in Form eines Herzens), können diese Orte als Favorit gesetzt werden oder dies rückgängig gemacht werden. Nur falls ein Ort Favorit ist, wird dieser den Freunden angezeigt. Klickt der Nutzer einen Listeneintrag, gelangt er zur *LocationDetailsActivity*. Über den gleichen Button wie im *MapFragment* wird der Nutzer zur *AddLocationActivity* weitergeleitet. Wie auch das *MapFragment* wird auch dieses Fragment nur innerhalb der *HomeActivity* verwendet. |
| FriendsFragment         | Hier wird dem Nutzer eine Liste seiner Freunde (Name und Profilbild) angezeigt. Durch Auswahl eines Listenelements wird die *FriendDetailsActivity* aufgerufen.  Über einen Button wird der Nutzer zur *AddFriendNfcActivity* bzw. zur *AddFriendEmailActivity* bei nicht vorhandenem NFC. Dies wird aber erst innerhalb ersterer Activity überprüft. Auch dieses Fragment wird ausschließlich in die *HomeActivity* integriert. |
| SettingsFragment        | Dieses Fragment beinhaltet die kleinste Funktionalität der vier "Hauptfragments". Es besteht aus 3 Buttons: "Edit Profile" leitet zur *EditProfileActivity* weiter, "Show Credits" öffnet ein Popup-Fenster, das Informationen zum Entwicklerteam der Applikation zeigt, "Logout" loggt den Nutzer aus und leitet ihn zurück zur *LoginActivity*. |
| LocationDetailsFragment | Dieses Fragment wird verwendet, um innerhalb der *LocationDetailsActivity* über Tabs zwischen verschiedenen Ansichten von Orten zu navigieren. Dies ist der Fall, wenn ein bestimmer Ort von zwei unterschiedlichen Freunden als Favorit gesetzt ist. Hier werden also in solch einem Falle mehrere Instanzen des gleichen Fragments erstellt. Siehe den Eintrag der *LocationDetailsActivity* für eine genauere Beschreibung der Funktionalität. |
| PictureDialogFragment   | Dieses Fragment erbt von der Android-eigenen Klasse *DialogFragment*, um somit einen individuellen Popup-Dialog zu erstellen. Es wird innerhalb der *AddLocationActivity* und *EditProfileActivity* verwendet, um dem Nutzer, sobald er ein Bild auswählen will, die Möglichkeit anzubieten, dies entweder über den Speicher zu tun oder ein neues Bild aufzunehmen. Das Fragment kann in mehreren Activities auf die gleiche Art und Weise benutzt werden, da es das Interface *PictureDialogListener* bereitstellt. Dieses Interface muss in der entsprechenden Activity implementiert werden, um die Funktionalität bei Klick auf eines der Elemente des Dialogs zu bestimmen. |



#### helpers

In diesem Package befinden sich mehrere Klassen, die den Entwicklern während der Implementierung der Logik das Leben vereinfachen sollen. Die Klassen beinhalten Funktionen, die an verschiedenen Stellen benutzt werden. So wird der zu schreibende Code innerhalb der Activities und Fragments auf das Minimum reduziert und der Code deutlich lesbarer gemacht. Zudem müssen Änderungen jeweils nur an einer Stelle angepasst werden. Die Funktionen sind jeweils als statische Funktionen implementiert, um sie ohne die Instanziierung der Objekte benutzen zu können (vergleichbar mit vielen Android-eigenen Klassen). Wird innerhalb der Funktionen Zugriff auf kontextspezifische Funktionen benötigt (z.B. Abrufen von Daten aus den *SharedPreferences*), so muss in den Activities der jeweilige *Context* als Parameter übergeben werden. In der folgenden Tabelle finden Sie einen Überblick über diese kleinen Helfer.

| Klasse       | Funktion                                 |
| ------------ | ---------------------------------------- |
| Request      | Diese Klasse implementiert Methoden, die ständig benutzt werden. In quasi jeder Activity oder jedem Fragment werden Request and den Server geschickt, sodass es sehr sinnvoll erschien, solchen Code nach dem *dry* Prinzig nicht ständig zu wiederholen. Daher werden hier mehrere statische Klassen zur Verfügung gestellt, die je nach Request-Art (GET, POST, PUT oder PATCH) verwendet werden sollen. Die Verbindung zum Server wird über eine *HttpUrlConnection* hergestellt. Die einzelnen Funktionen arbeiten nicht in einem eigenen Thread, sodass sie der Entwickler vorher in einen neuen Thread oder *AsyncTask* (was in dem Projekt durchgängig verwendet wird) eingliedern muss, da Netzwerkanfragen (verständlicherweise!) nicht im UI-Thread ausgeführt werden dürfen. |
| Preferences  | Diese Klasse stellt einige Funktionen zur Verfügung, um Werte (z.B. den Authentifizierungs-Token) lokal und persistent in den *SharedPreferences* zu speichern und diese auch abzurufen. |
| Storage      | Diese Klasse befasst sich mit einigen Funktionalitäten, die den lokalen Gerätespeicher betreffen. Die beinhalteten Funktionen sind vor allem in den Activities wichtig, in denen Bilder aufgenommen werden oder vom Speicher ausgewählt werden. |
| MarkerColors | Eine sehr kleine Klasse, die die verschiedenen Farben für die Marker, die in der Kartenansicht angezeigt werden sollen, als Variablen hält und eine Funktion zur Berechnung der entsprechenden Farbe (abhängig vom jeweiligen Freund) zur Verfügung stellt. |



#### Sonstige

Das Package *adapters* enthält verschiedene Klassen, die von bereits bestehenden Adapterklassen (in der Regel *ArrayAdapter*) erben und dafür genutzt werden, die *ListViews* innerhalb der Activities oder Fragments zu befüllen und Änderungen zu behandeln.

Das Package *models* enthält die Modelklassen. Eine genauere Beschreibung finden Sie in dem sich explizit damit befassenden Kapitel Models. 

Das Package *services* enthält mehrere Klassen, die von bestimmten Services erben. Diese werden für die Funktionalität der Push Notifications benötigt. Sie werden ebenfalls in einem eigenen Kapitel (Push Notifications) genauer beschrieben.



## Models

Dieses Kapitel befasst sich mit den clientseitigen Modelklassen. Diese Objekte stehen repräsentativ für Objekte der realen Welt. Innerhalb des Projektes wird vielmals auf diese Klassen zugegriffen, um somit einen einfach zu schreibenden und gut lesbaren Code zu ermöglichen. Die Models im Android Projekt besitzen zwar ihr passendes Gegenstück im Backend, jedoch sind sie nicht untrennbar miteinander verbunden und können Unterschiede zu den Models im serverseitigen Code aufweisen. Von großer Bedeutung sind die Models auch aus dem Grund, dass die Bibliothek *Gson* auf sie zugreift, um die in JSON formatierte Antwort des Servers in handhabbare Objekte zu verwandeln.



### User

Das *User* Model repräsentiert einen Menschen im echten Leben. Das Model kann im Rahmen des Projektes jedoch auf verschiedene Arten genutzt werden. Zum einen ist der aktuelle Nutzer ein User, zum Anderen sind dies auch seine Freunde. Ein User besteht nicht nur aus einfacheren Daten wie seines Vornamens und Nachnamens, sondern er besitzt auch eine Liste von Orten (sogenannten Locations, siehe nächstes Kapitel), oder ein Profilbild. Das Bild ist eine einfache URL, die benutzt werden kann, um das Profilbild des Users herunterzuladen und anzuzeigen. Die zum Model dazugehörige Klasse bietet alle nötigen Getter-Methoden, um auf die Felder zugreifen zu können.



### Location

Das *Location* Model repräsentiert einen x-y-beliebigen Ort in der Welt. Die Locations werden von den Nutzern erstellt und gespeichert, damit er sie daraufhin einsehen, verwalten und mit seinen Freunden teilen kann. Eine Location besitzt eine klare Abhängigkeit zum User. Eine Location gehört immer nur einem User an und jeder User kann mehrere Locations besitzen. Jede Location ist einzigartig. Auch wenn verschiedene Nutzer den gleichen realen Ort (z.B. die Stadt London) hinzufügen, so existieren in der Welt unserer Applikation jedoch mehrere Einträge dieses Ortes. Diese unterschiedlichen Einträge können nämlich auch komplett unterschiedliche Daten (z.B. Beschreibung, Kategorien) enthalten. Für eine Location wird jedoch auch die *googleId* (eine eindeutig identifierende Id der Google Places API) gespeichert, damit, wenn nötig, festgestellt werden kann, ob es sich bei zwei Location Einträgen um den gleichen Ort in der realen Welt handelt. Dies ist zum Beispiel für die Funktionalität im *MapFragment* relevant, da dort abhängig davon, ob zwei User den gleichen realen Ort besitzen, ein bestimmter Marker angezeigt werden muss. Eine Location besitzt eine Vielzahl an Daten: 

- Titel (dieser wird vom *PlacePicker* übernommen) 
- eine Liste von Kategorien 
- eine Beschreibung 
- Stadt und Land 
- Koordinaten (diese sind wichtig, damit der entsprechende Marker auf der Karte gesetzt werden kann)
- eine Liste an Bildern (genau wie beim User als URLs) 
- eine Angabe darüber, ob die Location ein Favorit ist oder nicht
- der zugehörige Nutzer (jedoch nicht als User Objekt, sondern nur als Id)

Die Location Objekte können unabhängig vom User (also nicht als Liste innerhalb des User Objektes) auftreten. Zum Beispiel können Requests zu bestimmten Endpunkten der API geschickt werden, die lediglich eine oder mehrere Locations zurückliefern. Hier will man möglicherweise zusätzlich Daten des Users erhalten können, weswegen ein Verweis auf diesen (in Form einer Id) von Nöten ist. Außerdem ist dieser Verweis von Bedeutung, wenn eine neue Location erstellt wird, da ihr nun die Id des Nutzers hinzugefügt und dieses "Paket" an den Server geschickt werden kann. 



## Besonderheiten

In diesem Kapitel werden verschiedene herrausstechende Funktionalitäten, die in die Applikation integriert sind, beschrieben. 



### Bibliotheken und APIs

In den folgenden Kapiteln wird auf die verschiedenen Bibliotheken eingegangen, die von den Entwicklern benutzt wurden, um bestimmte Probleme zu lösen oder die Implementierung von Funktionalität zu vereinfachen.



#### Glide

Für das asynchrone Herunterladen und Anzeigen der auf dem Server gespeicherten Bilder wird die Bibliothek *[Glide](https://bumptech.github.io/glide/)* in der 4. Version verwendet. Diese erleichtert es den Entwicklern immens, den Code für das Herunterladen von Bildern (ohne die umständlichere Nutzung von *HttpUrlConnection*) auf einfache Weise zu implementieren. Der Code wird dabei sehr schlank gehalten. Außerdem bietet die Bibliothek Möglichkeiten, Callbacks zu implementieren, die ausgeführt werden, sobald ein Bild heruntergeladen wurde. Ebenso ist in Glide bereits eine Caching-Strategie integriert, die dazu führt, das bereits heruntergeladene Bilder nicht nochmals geladen werden müssen. Die Api, um Glide zu benutzen, sieht für einen einfachen Beispielsfall wiefolgt aus:

```java
Glide.with(context).load(url).into(imageView);
```

Über die Klasse *RequestOptions* ist es möglich, bestimmte Optionen für das Laden und/oder Anzeigen der Bilder zu definieren. Im folgenden Beispiel wird festgelegt, dass das Bild bei jeder Ausführung des Codes neu vom Server heruntergeladen werden soll. Dies ist in der *EditProfileActivity* nötig, damit, nachdem das Bild vorher vom Nutzer geändert wurde, das aktuelle Bild beim erneuten Aufrufen der Activity angezeigt wird. 

```java
RequestOptions requestOptions = new RequestOptions()
  	.diskCacheStrategy(DiskCacheStrategy.NONE)
  	.skipMemoryCache(true);
Glide.with(context).load(url).apply(requestOptions).into(imageView);
```



#### Gson

Zur *Serialization* und *Deserialization* von Java Objekten zu bzw. von JSON verwenden wir die Bibliothek [*Gson*](https://github.com/google/gson) von Google. Diese macht als den Entwicklern deutlich einfacher als die Verwendung der *JSONObject* Klasse, mit in JSON formatiertem Text umzugehen. Durch Gson kann die Antwort des Servers in die bereits beschriebenen Model-Klassen User und Location umgewandelt werden. Auch können Objekte dieser Klassen in einen String in JSON transformiert werden, um diesen als Body im Request an den Server mitzuschicken. Des Weiteren können individuelle Strategien angelegt werden, um z.B. bestimmte Felder zu ignorieren oder auch umzubennen. 

In diesem Beispiel wird aus einem JSON String ein neues Object der User Klasse erstellt, auf dessen Funktionen daraufhin wie gewohnt zugegriffen werden können.

```java
Gson gson = new Gson();
User user = gson.fromJson(result, User.class);
String firstName = user.getFirstName();
```

Das nächste Beispiel ist entnommen aus der *AddLocationActivity*, in der aus einem Objekt der Klasse Location ein String entsteht, der als Request Body an den Server geschickt wird. 

```java
Location location = new Location(
                        place.getId(),
                        userId,
                        place.getName().toString(),
                        true,
                        description,
                        selectedCategories,
                        new double[]{place.getLatLng().latitude, place.getLatLng().longitude},
                        city,
                        country
                );
                //transform to json via gson
                Gson gson = new Gson();
                String locationJson = gson.toJson(location);
```



#### Firebase Cloud Messaging

Für die Integration von Push Notifications vom Server zum Android Client wird die *[Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/ )* Bibliothek von *Firebase* (mittlerweile Teil von Google) verwendet. FCM hat *Google Cloud Messaging* als die von Google empfohlene Lösung zur Synchronisation von Server und Client abgelöst. Für Details zur Verwendung und Implementierung lesen sie bitte das Kapitel Push Notifications.



#### PlacePicker

Der [*PlacePicker*](https://developers.google.com/places/android-api/placepicker) ist ein UI-Widget, das auf die Google Places API zugreift und mithilfe dessen man es dem Nutzer ermöglicht, auf einer Karte oder über ein Suchfeld einen Ort auszuwählen. 

Der PlacePicker wird in die *AddLocationActivity* integriert. Über den Aufruf von *startActivityForResult* wird der PlacePicker geöffnet. Sobald der Nutzer einen Ort ausgewählt hat, wird in der Activity die Funktion *onActivityResult* aufgerufen, in der auf die Daten dieses Ortes zugegriffen werden kann. 

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
               	String name = place.getName();
                ...
             }
        }
}
```



<img src="https://developers.google.com/places/images/placepicker.png" alt="PlacePicker" style="width: 200px;"/>

​					https://developers.google.com/places/images/placepicker.png



#### Google Maps API

Die Einbindung der [*Google Maps API*](https://developers.google.com/maps/documentation/android-api/?hl=de) für Android wird in das *MyMapFragment*, in dem dem Nutzer die Kartenansicht mit seinen gespeicherten Orten und den Favoriten seiner Freund angezeigt werden, integriert. Es gibt zwei Möglichkeiten, die Karte einzubinden: das *MyMapFragment* (nicht zu verwechseln mit dem des Projekts) und die *MapView*. Dadurch, dass die *BottomNavigation* in der *HomeActivity* mit verschiedenen Fragments arbeitet und die Verschachtelung mehrere Fragments nicht empfohlen wird, greifen wir hier zur zweiten Variante. Hierfür wird die *MapView* in die zum Fragment zugehörige xml-Datei eingefügt

```xml
<com.google.android.gms.maps.MapView
	android:id="@+id/map_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

 In unserem *MyMapFragment* kann nun auf die Karte zugegriffen werden.

```java
mapView = view.findViewById(R.id.map_view);
mapView.onCreate(mapViewBundle);

mapView.getMapAsync(new OnMapReadyCallback() {
	@Override
    public void onMapReady(GoogleMap googleMap) {
    	map = googleMap; //map is a global variable of type GoogleMap
		loadLocationsOfUser();
	}
});
```

Die Methode *loadLocationsOfUser* schickt einen Request an unsere API, um die gespeicherten Orte des Nutzers und daraufhin auch die Favoriten seiner Freunde zu erhalten. Für jede Location eines jeden Users wird nun ein [*Marker*](https://developers.google.com/maps/documentation/android-api/marker?hl=de) auf der Karte gesetzt. Dabei greifen wir auf Methoden der Location Klasse zu, um die Koordinaten des Markers, sowie den Titel zu setzen. Je nachdem, welchem Freund der Ort angehört, wird dementsprechend eine andere Farbe des Markers ausgewählt. 

```java
//computeColor uses the index of the list of users to select the marker's color
BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(MarkerColors.computeColor(index));

MarkerOptions options = new MarkerOptions()
		.position(new LatLng(location.getCoordinates()[0], location.getCoordinates()[1]))
         .title(location.getName())
         .icon(icon);

Marker marker = map.addMarker(options);
```

Zum einfacheren Verständnis der Funktionsweise des Codes, wurden in diesem Codeschnipsel zusätzliche Logik wie das Setzen eines anderen Icons, wenn der gleiche Ort von zwei unterschiedlichen Nutzer gespeichert wurde, ausgelassen. 

Nicht zu vergessen ist die Angabe des in der Google Console kreierten API Keys in der Datei *AndroidManifest.xml*.

```xml
<meta-data
	android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyA9yABz8sHgpRXtGuwzkgbEMY4HbqLpUwg" />
```

 

#### Google Sign In

Für die Authentifizierung des Nutzers verwenden wir Google OAuth. Die dafür notwendige Logik befindet sich in der *LoginActivity*. In der *onCreate* Mehtode wird Google Sign In konfiguriert:

```java
// Configure sign-in to request the user's ID, email address, the ID token, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	.requestEmail()
    .requestIdToken(getString(R.string.server_client_id))
    .build();

// Build a GoogleSignInClient with the options specified by gso.
mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
```

Zu beachten ist hierbei, dass wir explizit angeben müssen, welche Daten wir benötigen. Um den *IdToken*, den wir benötigen werden, um uns im Backend zu authentifizieren, müssen wir dafür die in der Google API Console hinterlegte *client ID* als Argument übergeben. Dieser Token wird daraufhin in den *SharedPreferences* gespeichert und bei jedem zukünftigen Request an den Server im Header mitgegeben, sodass im Backend überprüft werden kann, um welchen Nutzer es sich handelt und ob dieser für diese Anfrage berechtigt ist. 

In der *onStart* Methode der *LoginActivity* wird ein sogenannter *silentSignIn* ausgeführt, um somit zu überprüfen, ob der Nutzer bereits eingeloggt ist. Ist dies nicht der Fall, so wird dem Nutzer das User Interface der Activity angezeigt. Sobald der Nutzer den Login Button klickt, wird folgende Methode aufgerufen:

```java
private void signIn() {
	Intent intent = mGoogleSignInClient.getSignInIntent();
	startActivityForResult(intent, SIGN_IN_REQUEST);
}
```

Das Resultat des Logins wird daraufhin in der *onActivityResult* Methode behandelt.

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    if (requestCode == SIGN_IN_REQUEST) {
	// The Task returned from this call is always completed, no need to attach a listener.
	Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
	handleSignInResult(task);
    }
}
```

In der aufgerufenen Methode *handleSignInResult* wird daraufhin der ID Token persistent auf dem Gerät gespeichert und ein Request an den Server geschickt, um den Nutzer, falls er noch nicht existiert, zu erstellen. 



### NFC

Die Applikation soll den Nutzern eine möglichst interessante und einfache Möglichkeit bieten ihre Orte miteinander zu teilen, sprich eine Freundesbeziehung aufzubauen. Hier haben wir uns dafür entschieden auf einen drahtlose Übertragung von Daten zu setzen, da den Nutzern somit eine lästige Suche von Nutzern durch Texteingabe erspart bleibt. Da es für uns nur nötig ist, eine Id der Nutzer auszutauschen, reicht für diese Übertragung NFC vollkommen aus. Außerdem bietet NFC die intuitive Möglichkeit, die Freundesbeziehung aufzubauen, indem beide Handys aneinandergelegt werden. Zur Nutzung von NFC werden die nötigen Berechtigungen im Android Manifest festgelegt. 

```xml
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" />
```

Hierbei ist es nicht nötig, anzugeben, dass NFC benötigt wird (`<uses-feature ... required="true" />` ), da die Applikation nicht darauf angewiesen ist, dass das Gerät NFC besitzt. Ist dies zum Beispiel der Fall, wird zum "Fallback" über die Suche eines anderen Nutzers über seine E-Mail-Adresse weitergeleitet. Also soll die App auch Nutzern im Play Store angezeigt werden, die kein NFC besitzen. 

Die Logik zum Aufbau der NFC Verbindung ist in der *AddFriendNfcActivity* implementiert. In der *onCreate* Methode wird auf die Klasse *NfcAdapter* zugegriffen und die jeweiligen Callbacks bei Erkennen eines anderen Gerätes und beim erfolgreichen Senden einer Nachrichten festgelegt.

```java
NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//check if nfc is available
if (nfcAdapter == null) {  
	//if that is the case we redirect to the add friend via email activity
    ...
} else {
	//check if NFC is enabled
    if (nfcAdapter.isEnabled()) {
    	//This will refer back to createNdefMessage for what it will send
        nfcAdapter.setNdefPushMessageCallback(this, this);
        //This will be called if the message is sent successfully
        nfcAdapter.setOnNdefPushCompleteCallback(this, this);
    } else {
    	//tell user to enable NFC and return to previous activity
		...
        }
}
```

Zur Vereinfachung des hier dargestellten Codes wurde rausgelassen, was geschieht, falls das Gerät kein NFC besitzt oder es nicht aktiviert ist. 

Die Klasse implementiert zudem die Interfaces *NfcAdapter.CreateNdefMessageCallback* und *NfcAdapter.OnNdefPushCompleteCallback*. Deshalb müssen die Funktionen *createNdefMessage* und *onNdefPushComplete* enthalten sein. In ersterer Methode wird die Nachricht, die geschickt werden soll, zusammengestellt. Hierfür wird die Id des Users aus den *SharedPreferences* ausgelesen und der Nachricht hinzugefügt. 

```java
@Override
public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
	//create ndef message that contains the user id, which we want to send to the other device
    String userId = Preferences.getUserId(this);
    return new NdefMessage(new NdefRecord[]{
    	createMime("text/plain", userId.getBytes(Charset.forName("UTF-8"))),
        NdefRecord.createApplicationRecord(getPackageName())
	});
}
```

NDEF ist ein standardisiertes Datenformat, das dazu verwendet wird, Informationen zwischen einem kompatiblen NFC Gerät und einem anderen NFC Gerät oder Tag auszutauschen.

Der Nutzer muss, sobald er seine Orte mit einem anderen Nutzer austauschen will, lediglich die Activity öffnen (dorthin wird er über einen Button im *FriendsFragment* geleitet) und sein Gerät and das des anderen Nutzers halten. Der zweite Nutzer muss weder in der gleichen Activity sein, noch überhaupt die App geöffnet haben. Sobald das Gerät erkannt wird, wird nämlich direkt die *AddFriendNfcActivity* geöffnet (sie befasst sich nämlich nicht zu mit dem Senden der Daten, sondern auch mit dem Empfangen). Dass dies geschehen soll, wird über einen *Intent Filter* im Android Manifest festgelegt.

```xml
<activity
	android:name=".activities.AddFriendNfcActivity"
	android:label="@string/label_AddFriendNfcActivity">
	<intent-filter>
		<action android:name="android.nfc.action.NDEF_DISCOVERED" />
    	<category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
	</intent-filter>
</activity>
```

In der *onResume* Methode wird nun der Intent empfangen und eine Funktion aufgerufen, die diesen weiterverarbeiten soll.

```java
@Override
public void onResume() {
	super.onResume();
    handleNfcIntent(getIntent());
}
```

Die Methode *handleNfcIntent* liest daraufhin die Daten (die Id des anderen Nutzers) und sendet einen Request an den Server, um die "Freundesbeziehung" zu begründen.



### Push Notifications

Wie bereits angedeutet, wird für die Funktionalität die Cloud Messaging Lösung von Firebase verwendet. Um die Umsetzung der Funktionalität kümmern sich zwei Klassen im Package *services*. 

Die Klasse *MyFirebaseInstanceIDService* erweitert die Firebase Klasse *FirebaseInstanceIdService*. In dem von uns angelegten Service wird die Funktion *onTokenRefresh* implementiert, die bei jeder Erst- und Neugenerierung eines FCM Tokens aufgerufen wird. Dieser Token wird dafür verwendet, um zwischen verschiedenen Geräten zu unterscheiden. Das bedeutet das ein Token für ein Endgerät steht, unabhängig vom Account des Nutzers. Da wir im serverseitigen Code wissen müssen, an welches Gerät eine Push Benachrichtigung geschickt werden soll, wird der Token bei jeder Generierung (z.B. wird ein Token neu kreiert, wenn der Nutzer die App auf seinem neuen Handy installiert) an das Backend zur Speicherung im Eintrag des Nutzers geschickt. 

Die Klasse *MyFirebaseMessagingService* erweitert die Firebase Klasse *FirebaseMessagingService* und definiert in der Methode *onMessageReceived* das Verhalten bei Empfang einer Push Benachrichtigung vom Server. Da wir ausschließlich mit *Data Messages* (nicht mit *Notification Messages*) arbeiten, wird diese Funktion bei jedem Empfang aufgerufen, unabhängig davon, ob die App im Vordergrund oder Hintergrund ist. Für eine genauere Unterscheidung der Nachrichtentypen verweisen wir an diesem Punkt auf den [Leitfaden](https://firebase.google.com/docs/cloud-messaging/android/receive) von Firebase. 

Im Manifest wird angegeben, dass diese Services bei den entsprechenden Ereignissen aufgerufen werden sollen.

```xml
<service
	android:name=".services.MyFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
    	<action android:name="com.google.firebase.MESSAGING_EVENT" />
	</intent-filter>
</service>
<service
	android:name=".services.MyFirebaseInstanceIdService"
    android:exported="false">
    <intent-filter>
    	<action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
	</intent-filter>
</service>
```

Da wir dem Nutzer eine visuelle Benachrichtigung anzeigen wollen, wenn die App im Hintergrund ist, wird bei Empfang einer Push Notification mittels der Klasse *NotificationCombat.Builder* eine Benachrichtigung kreiert. 

Im aktuellen Stand des Produktes verwenden wir Push Benachrichtigungen an zwei Stellen, jeweils das Hinzufügen eines neuen Freundes betreffen. 

Wenn der Nutzer seine Orte in der *AddFriendEmailActivity* mit einem zweiten Nutzer teilt, so soll dieser benachrichtigt werden. Hier kommt auch die visuelle Benachrichtigung, die im letzten Absatz beschrieben wurde, ins Spiel. Diese Benachrichtigung enthält einen Button "Share your's, too", der dem zweiten Nutzer die Möglichkeit gibt direkt ebenfalls seine Orte zu teilen. Dies läuft über den dritten Service im services Package, den *NotificationActionService*, der einen *IntentService* erweitert. Hier wird ein Request an den Server gesendet durch den der Nutzer seine Orte nun auch teilt. 

Die zweite Anwendung der Push Notification Funktion von Firebase ist das Hinzufügen eines Freundes via NFC. Da uns bei der Übertragung durch NFC, ohne zu große negative Beeinflussung der User Experience, nur eine unidirektionale Übertragung möglich ist, dient uns die Möglichkeit der Push Notifications als Lösung dieses Problems. Das Initiator-Gerät der NFC Verbindung schickt die Id seines Users and das zweite Gerät. Dieses sendet nun einen Request an den Server, um die Orte seines Nutzers zu teilen. Das Backend verarbeitet diese Anfrage und schickt daraufhin eine Push Benachrichtigung mit der Id des Nutzers des zweiten Gerätes an das Initiator-Gerät, das nun wiederum einen Request zum Teilen der Orte absendet. Dies läuft jedoch alles im Hintergrund ab (hier wird im Vergleich zum vorherigen Anwendungsfall keine visuelle Benachrichtigung angezeigt), sodass die Nutzer davon nichts mitbekommen, um so eine möglichst reibungslose Nutzererfahrung zu gewährleisten. Dieses etwas kompliziert erscheinende Verfahren ist nötig, um die Sicherheit der Rest API mit einer definierten Authentifizierungsstrategie zu kompromittieren. Jeder User darf nämlich nur seine Orte mit einem anderen teilen, jedoch nicht umgekehrt. Für die Details siehe hierzu das Kapitel, das sich mit der Implementierung des Backends befasst.



### Offline-Nutzung

Die Applikation soll, zwar vorerst mit Einschränkungen, auch offline eine zufriedenstellende User Experience bieten. So wird zum Beispiel in den Fragments der *HomeActivity* abgefragt, ob der Nutzer eine Internet Verbindung besitzt. Die hierfür benötigte Methode wurde in der *Request* Klasse implementiert. Ist der Nutzer zum Beispiel offline, wird im *MyLocationsFragment* und *MyFriendsFragment* anstatt, dass dort die jeweils relevanten Daten geladen werden, eine abgeänderte Anzeige angezeigt. Diese soll den Nutzer darüber informieren, dass er gerade keinen Zugriff zum Internet besitzt. Siehe hierzu das Kapitel Design.

Des Weiteren besteht weiterhin die Möglichkeit Freunde via NFC hinzuzufügen, auch wenn einer der beiden oder beide Nutzer über keine Netzwerkverbindung verfügt. Hierzu wird die während der NFC Verbindung übertragene Id in den *SharedPreferences* zwischengespeichert. Dies funktioniert auch für mehrere neue Freunde. Beim erneuten Start der Applikation (bzw. beim Aufrufen der *HomeActivity*) wird überprüft, ob es Freunde gibt, zu denen noch eine Beziehung aufgebaut aufgebaut werden muss. Daraufhin wird ein Request an den Server geschickt, um dies zu erledigen. 

In einer nächsten Version der App wäre es vorstellbar, eine ausgeweiterte Umsetzung der Offline-Nutzung in die App zu integrieren. Eine Implementation einer vollständigen Synchronisation der Daten mit einer lokalen Datenbank (z.B. SQLite) wäre denkbar. Dadurch wäre eine gänzlich uneingeschränkten Nutzung der App auch ohne Internetverbindung gewährleistet. 