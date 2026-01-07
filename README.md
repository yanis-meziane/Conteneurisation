# Conteneurisation

## TP1

> Pour le premier TP, voici les commandes que j'ai utiliser : 

```bash
cd ingnum
```

```bash
cd RentalService
```

* *Pour compiler le projet* : 

```bash
./gradlew build
````

* *Pour exécuter le projet* :

```bash
java -jar build/libs/RentalService-0.0.1-SNAPSHOT.jar --server.port=8085
```

* *Puis dans la barre de recherche :*

```bash
http://localhost:8085/bonjour
````

* *Pour l\'utilisation de Docker :*

> Création du Dockerfile 

```dockerfile
FROM eclipse-temurin:21

VOLUME /tmp

EXPOSE 8080

ADD ./build/libs/RentalService-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
````

> Modification du ìngnum > RentalService > src/main > resources > application.properties

```
server.port=8085
spring.application.name=RentalService
````

* *Création d'une image Docker :*

```bash
docker build –t nomImageDocker .
```

* *Lancer Docker et lancer la commande suivante :*

```bash
docker run –p 8080:8080 nomImageDocker
```



