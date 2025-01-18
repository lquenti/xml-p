Im weiteren wird davon ausgegangen, dass als Webapplikationsserver Tomcat
verwendet wird. Wenn ein anderes Produkt verwendet werden soll (z.B. IBM Websphere),
dann muessten einzelne Konfigurationen eventuell angepasst werden.

- tomcat downloaden, z.B. hier:  http://tomcat.apache.org
- Entpacken / Installation:
  tar xzf apache-tomcat-xxxx.tar.gz
  Sie koennen den Tomcat-Server auf den Pool-Rechnern installieren und starten.
- Umgebungsvariable setzen (hier: direkt im HOME Verzeichnis)
  export CATALINA_HOME=~/apache-tomcat-xxxx

- Server konfigurieren:
  o edit $CATALINA_HOME/conf/server.xml
    Port festlegen (im Beispiel wird immer Port 8080 verwendet):
      <!-- Define a non-SSL HTTP/1.1 Connector on port 8080 -->
      <Connector port="8080" ...

  o optional: Benutzer bzw Admin eintragen in $CATALINA_HOME/conf/tomcat-users.xml
    <tomcat-users>
      <user username="admin" password="mysecretpassword" roles="admin,manager"/>
    </tomcat-users>
    Damit kann auf das Administrations-Interface via Webbrowser zugegriffen werden:
    http://localhost:8080/manager/html

  o Start/Stop des Webservers mit $CATALINA_HOME/bin/startup.sh bzw $CATALINA_HOME/bin/shutdown.sh
    (bzw. entsprechende *.bat unter Windows)

- Fehlermeldungen und Serverausgaben gehen nach $CATALINA_HOME/logs/catalina.out .
  Das Beispielservlet loggt ausserdem nach $CATALINA_HOME/logs/servletdemo.log

- CIP-Pool: Beachten Sie bitte folgendes:
  o Terminieren Sie den Tomcat, wenn Sie sich ausloggen
  o Wenn zwei Personen auf dem gleichen Rechner eingeloggt sind (z.B. einer von remote, einer lokal)
    und beide einen Tomcat-Prozess starten, gibt es Konflikte, wenn der gleiche Port
    fuer Tomcat verwendet werden soll.

Servlet-Entwicklung

- web.xml: definiert Servlet-Name und Servlet-Mapping
  Das web.xml File gehoert in das WEB-INF Verzeichnis, welches wiederum im
  Web-Wurzelverzeichnis des Servlets liegen muss.
- die Binaries (*.class Files) muessen in WEB-INF/classes liegen

  ==>  Im Beispiel-Projekt wird diese Struktur mit Hilfe des Ant-Build-Skripts angelegt.
         (build.xml).
       Dieses Build-Skript liest die Umgebungsvariablen JAVA_HOME und CATALINA_HOME
       aus. Wenn diese nicht gesetzt oder nicht verfuegbar sind, muessen
       diese Parameter im Buildfile selbst angepasst werden.

       Aufruf mit 'ant compile|dist|deploy'

- benoetigte Java-Bibliotheken: javax.servlet.jar
  Diese findet sich im lib-Verzeichnis des Beispiel-Projekts.

- Debugging/Logging mit log4j, Konfiguration ueber log4j.properties
  (liegt in conf, wird automatisch vom build-skript in das WEB-INF/classes
   Verzeichnis kopiert).

Compilieren und Deployen eines Servlets:
Servlet-Code in $CATALINA_HOME/webapps ablegen:

- Fileformat eines Servlets: *.war (im Grunde ein jar-File)
- compilieren/packen mit ant (siehe build.xml)
- deployen (= installieren im Webapplikationsserver)
  via Frontend des Servers (z.B. localhost:8080/manager/html)
  oder manuelles kopieren nach $CATALINA_HOME/webapps
  oder mit ant deploy.




