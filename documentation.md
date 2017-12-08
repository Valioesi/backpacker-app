# Dokumentation zum Projekt wanderlust
Teammitglieder: 
+ Jonas Scheffner (js332)
+ Julien Sander (js335)
+ Rebecca Durm (rd029) 
+ Valentin Schagerl (vs071)

*Anmerkung*
In folgendem Bericht wurde das generische Maskulin verwendet (z. B. Nutzer), um die Leserlichkeit des Textes zu erhöhen. 
Es impliziert gleichermaßen die weibliche Form (Nutzerin). 

## Konzept
### Zielgruppe 
Die Anwendung sieht als Hauptzielgruppe Backpacker vor. Backpacker sind meist junge Menschen zwischen 18 und 33, 
die für eine längere Zeit ein oder mehrere Länder in Selbstorganisation bereisen. 
Dabei haben sie keinen fest definierten Startpunkt, sondern reisen nicht linear von einem Ziel zum anderen. 
Dabei sind sie häufig daran interessiert, Bewohner des Landes, aber auch Mitreisende kennenzulernen 
und ihre Erfahrungen mit ihnen zu teilen.<sub>1</sub> 
Backpacker bereisen oft mehrere Länder, bevorzugt in Asien, Australien und Neuseeland. 
Dabei bleiben sie häufig nur wenige Tage an einem Ort, da es vorwiegend darum geht, ein Land zu durchqueren, 
neue Kulturen kennen zu lernen und auf der Reise Erfahrungen zu sammeln. <sub>2</sub>

Die Orte werden dabei nicht nur in einer Listenform angezeigt, sondern können auch auf einer Kartenansicht betrachtet werden. 
Für ihn besondere Orte kann er als Favorit markieren. Die Anzahl der Favoriten ist dabei pro Stadt auf 5, pro Land auf 20 beschränkt. 

Wenn der Nutzer auf der Reise andere Backpacker trifft, die die App ebenfalls nutzen, können sie ihre Favoriten per NFC austauschen.
Nach dem Austausch der Daten bleiben die Backpacker miteinander verbunden und bleiben, auch wenn sie sich auf getrennte Wege begeben, 
miteinander verbunden und werden über Änderungen der Favoritenliste informiert. 

Wenn die Verbindung einmal hergestellt ist, kann der Backpacker eine Liste bzw. die Karte sehen mit den eingetragenen Favoriten
seiner Backpacker-Bekanntschaften. Bei der Ansicht kann der Nutzer nach den eingetragenen Metadaten (z. B. der Kategorie) 
oder nach seinen Freunden filtern. 

> 1. Biesalski, Constanze (2009): Backpacking @ Latin America - The Role of Communikcation, Mobility, and ICTs. 
> Universität Salzburg. Fachbereich Kommunikationswissenschaften. Magisterarbeit (S. 53-56)
> [https://s3.amazonaws.com/ALoB/Biesalski_Backpacking+%40+Latin+America.pdf]

> 2. Biesalski, Constanze (2009): Backpacking @ Latin America - The Role of Communikcation, Mobility, and ICTs. 
> Universität Salzburg. Fachbereich Kommunikationswissenschaften. Magisterarbeit (S. 63-65)
> [https://s3.amazonaws.com/ALoB/Biesalski_Backpacking+%40+Latin+America.pdf]


### Technische Anforderungen 
Aus den oben beschriebenen Funktionalitäten ergeben sich folgende Anforderungen an die Anwendung: 

Für die Verwaltung der Orte muss eine Datenbank auf dem Backend eingerichtet werden. 
Die App muss Teilaufgabe offline erledigen können (neuen Eintrag erfassen) 
3 Sensoren müssen eingebaut werden (GPS, Kamera, NFC) 
Die Anbindung an Google maps oder ein ähnliches Programm sollte auch im Offline-Modus möglich sein. Die benötigte API sollte es möglich machen, Orte zu erkennen und vorzuschlagen, um Mehrfacheinträge in der Datenbank zu vermeiden. 

## Umsetzung
### Software-Architektur
In der Anwendung gibt es folgende Activities: 

#### WelcomeActivity
Hier loggt sich der Nutzer mit seiner Email-Adresse und seinem Passwort ein. 
Wenn er sich noch nicht registriert hat, kann er über “registrieren” zur Activity _RegisterActivity_

#### RegisterActivity
Der Nutzer gibt seine Email-Adresse, Vor und Nachnamen, Passwort (inkl. Wiederholung) und ein Foto ein und kreiert seinen Account. 

#### HomeActivity
In der HomeActivity gibt es eine Bottom-Navigationsleiste, die zwischen 4 Fragments umschaltet: 

+ MyListFragment:  
Der Nutzer sieht die Orte, die er in seine persönliche Liste aufgenommen hat. Dabei kann er festlegen, 
welche Orte er als Favoriten markieren möchte. Die Favoriten erscheinen oben in der Liste, die anderen Orte weiter unten.  
Durch einen Button kann der Nutzer zur Activity _AddLocationActivity_, um einen Ort zu seiner Liste hinzuzufügen. 

+ MapFragment:  
Der Nutzer sieht auf einer Karte die Orte, die er und seine Freunde markiert hatten. 
Hierbei sind die Orte seiner Freunde nur deren Favoriten; seine eigenen Orte werden vollständig dargestellt. 
Hierbei hat er die Möglichkeit, durch einen Filter nur bestimmte Freunde anzuzeigen. 
Auch hier kommt der Nutzer durch einen Button zur Activity _AddLocationActivity_, um einen Ort zu seiner Liste hinzuzufügen.  

+ FriendsFragment:  
Der Nutzer hat eine Übersicht über seine Freunde. durch einen Klick auf den Namen eines Freundes, 
öffnet sich die Activity _FriendDetailsActivity_. 
Durch einen Button kann der Nutzer zur Activity _AddFriendActivity_, um einen Freund hinzuzufügen.

+ SettingsFragment:  
Der Nutzer ändert die Einstellungen zu seinem Konto. 
Hier kann er auch seinen Account löschen. Durch die Bestätigung nach einem Pop-up, werden seine Daten vom Server gelöscht. 


#### AddLocationActivity
Der Nutzer wählt einen Ort aus der Google Place API aus. 
Danach hat er die Möglichkeit, seinem Ort Kategorien, einen Kommentar und ein Foto hinzuzufügen, 
ebenso wie er wählen kann, ob dieser Ort ein Favorit sein soll. Die Festlegung des Favoriten kann er später noch ändern. 

#### FriendDetailsActivity
Hier können die Orte von einzelnen Freunden listenartig angezeigt werden. 
Dabei ist es möglich, die Ortsliste nach einzelnen Ländern zu filtern. 
Durch einen Button “Freund entfernen” kann nach der Bestätigung in einem Popup-Menü die Verbindung zu einem Freund beendet werden. 

**_[ACHTUNG, EVENTUELL LÖSCHEN]_**
Durch das Info-Icon können Details vom Freund angezeigt werden, beispielsweise, wann man ihn getroffen hat und woher er kommt. 

#### AddFriendActivity
Durch die XY-Verbindung werden die Favoritenlisten des neuen Freundes ausgetauscht 
inkl. seines Namens und seines Fotos. 
Bei der Übertragung werden nur die Orts-IDs ausgetauscht, weshalb die Übertragungsmenge recht gering ist. 
Die anderen Informationen hinter der Orts-ID werden beim nächsten Zugang zum Internet nachgeladen. 


### Besonderheiten:
Die Navigation zwischen den verschiedenen Listen und Views erfolgt über eine Bottom-Navigation. Dies hat folgende Gründe: 
+ wenig Top-Level-Views wegen weniger Hauptaufgaben der App
+ hauptsächlich für Smartphones → näher an der gewöhnlichen Navigation und somit natürlich
+ geläufig auch in anderen Apps (bspw. Youtube) <sub>3</sub>

Die alternative Navigation mit Tabs wurde ausgeschlossen, weil die Swipe-Gesten, 
die für das Navigieren zwischen den Tabs genutzt wird, auch für das View der Map genutzt wird. Es kann zu Verwirrung kommen. <sub>4</sub>

>3. [https://material.io/guidelines/patterns/navigation.html#navigation-patterns]
>4. [https://material.io/guidelines/components/tabs.html]

### Verwendete APIs
[HILFREICHE LINKS FÜR UNS: 

https://developers.google.com/maps/documentation/api-picker → Begründung für Wahl der API

https://developers.google.com/places/ PLACES API

https://developers.google.com/maps/documentation/android-api/ MAPS API



Es wurden folgende APIs implementiert: 

+ Google Place API:  
BESCHREIBUNG

+ Google Maps API:  
BESCHREIBUNG BESCHREIBUNG 


Die Google-APIs wurden gewählt, weil Google Maps API und Google Places API die gleiche Basis haben und weit verbreitet sind. 


### Gestaltung
Als Icons wurden die Icons des Material Designs verwendet. 
Das Logo der App wurde im Team erstellt. 
Die Farbwahl wurde ebenfalls festgelegt und in Android implementiert. Die Hauptfarbe ist grün.
