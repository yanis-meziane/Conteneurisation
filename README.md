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

## TP2 

# Projet Microservices avec Docker Compose

## Définition 

Un **microservice** est un composant logiciel autonome, centré sur une capacité métier spécifique, disposant de son propre cycle de vie (développement, déploiement, mise à jour) et souvent de ses propres données. Il s’intègre dans une architecture distribuée où les services communiquent entre eux par des protocoles légers (REST, gRPC, messagerie). Cette approche favorise la modularité, la résilience, la scalabilité indépendante et la liberté technologique.

*Exemple :* 

Dans une plateforme e-commerce, on peut avoir :

- Un microservice « Catalogue » pour gérer les produits,
- Un microservice « Commandes » pour traiter les achats,
- Un microservice « Paiement » pour les transactions,
- Un microservice « Livraison » pour le suivi des expéditions.

Chaque microservice peut être développé par une équipe différente, utiliser une technologie différente et évoluer sans impacter directement les autres.

### Étape 1 : Création du deuxième microservice

```bash 
mkdir PHPService
cd PHPService
```

```bash
touch index.php
````

> Dans le fichier `index.php`

```php
<?php
    header('Content-Type: text/plain');
    echo "Yanis";
?>
```

> Création du Dockerfile

```dockerfile
FROM php:8.2-apache

COPY index.php /var/www/html/

EXPOSE 80
```

### Étape 2 : Création et test d'une image Docker

```bash
# Depuis le dossier PhpService
docker build -t phpservice .

# Tester l'image
docker run -p 8082:80 phpservice
```

> Dans le navigateur : 

```bash
http://localhost:8082/
```

Il devrait être retourné : `Yanis`.

### Étape 3 : Publication de l'image Docker sur Docker Hub

```bash
# Se connecter à Docker Hub
docker login

# Taguer l'image (remplacez 'votre-username' par votre username Docker Hub)
docker tag phpservice votre-username/phpservice:latest

# Pousser l'image
docker push votre-username/phpservice:latest
```

### Étape 4 : Communication entre les deux microservices (RentalService et PHPService)

> Il sera nécessaire de rajouter l'URL du code PHP

```bash
server.port=8085
spring.application.name=RentalService
php.service.url=http://php-service
```

> Modification du controller 

```bash
package com.ingnum.rentalservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BonjourController {

    @Value("${php.service.url}")
    private String phpServiceUrl;

    @GetMapping("/bonjour")
    public String bonjour() {
        RestTemplate restTemplate = new RestTemplate();
        String prenom = restTemplate.getForObject(phpServiceUrl, String.class);
        return "Bonjour " + prenom;
    }
}
```

### Étape 5 : Inclure le service PHP docke-compose.yml

```bash
version: '3.8'

services:
  php-service:
    build:
      context: ./PhpService
      dockerfile: Dockerfile
    container_name: php-service
    ports:
      - "8082:80"
    networks:
      - microservices-network
    restart: unless-stopped

  rental-service:
    build:
      context: ./RentalService
      dockerfile: Dockerfile
    container_name: rental-service
    ports:
      - "8085:8085"
    environment:
      - SPRING_APPLICATION_NAME=RentalService
      - SERVER_PORT=8085
      - PHP_SERVICE_URL=http://php-service
    networks:
      - microservices-network
    depends_on:
      - php-service
    restart: unless-stopped

networks:
  microservices-network:
    driver: bridge
```

### Étape 6 : Reconstruction du build

```bash
cd RentalService
./gradlew clean build
cd ..
```

### Étape 7 : Lancement Docker Compose 

```bash
docker-compose up --build
```

Ouvrez votre navigateur et testez :
- Service PHP : `http://localhost:8082/` → devrait afficher "Yanis"
- Service Java : `http://localhost:8085/bonjour` → devrait afficher "Bonjour Yanis"

 **Récapitulatif de la structure du projet**

```
ingnum/
├── PhpService/
│   ├── Dockerfile
│   └── index.php
├── RentalService/
│   ├── Dockerfile
│   ├── build.gradle
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/ingnum/rentalservice/
│   │       │       ├── RentalServiceApplication.java
│   │       │       └── controller/
│   │       │           └── BonjourController.java
│   │       └── resources/
│   │           └── application.properties
│   └── gradlew
└── docker-compose.yml
```

