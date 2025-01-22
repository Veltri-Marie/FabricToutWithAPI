# 🏭 FabricTout

FabricTout est une application de gestion interne pour les entreprises, permettant aux **managers**, **travailleurs** et **acheteurs** de collaborer efficacement sur des tâches liées aux machines, aux maintenances et aux réapprovisionnements. Chaque rôle dispose de fonctionnalités spécifiques pour gérer et suivre les opérations en temps réel, de manière simple et intuitive.

Cette application est construite avec **JEE**, **PL/SQL** pour la base de données Oracle, une **API personnalisée**, et utilise **Maven** pour la gestion des dépendances. **Tomcat** est utilisé comme serveur pour déployer l'application.

## Tech Stack

- **Backend** : Java EE (JEE), PL/SQL (Base de données Oracle)
- **API** : API personnalisée pour la gestion des opérations
- **Gestion des dépendances** : Maven
- **Serveur** : Apache Tomcat
- **Base de données** : Oracle (PL/SQL)

## Fonctionnalités

### Partie du Manager
Après s’être authentifié, le **manager** peut accéder aux fonctionnalités suivantes :
- **Visualisation des machines** : Le manager peut voir toutes les machines et leurs statuts, triées par priorité (celles nécessitant une validation en premier).
- **Valider ou refuser une maintenance terminée** : En cliquant sur "See Maintenances", le manager peut consulter la liste des maintenances d'une machine. Il peut ensuite cliquer sur "Validate" pour valider ou sur "Refuse" pour refuser une maintenance. Lorsqu'une action est effectuée, un message de succès apparaît pour confirmer l'opération.
- **Signaler une machine nécessitant une maintenance** : Le manager peut signaler qu'une machine nécessite une maintenance en cliquant sur "Report Maintenance".
- **Se déconnecter** : Le manager peut se déconnecter de l'application en cliquant sur "Logout".

### Partie de l'Ouvrier
Après s’être authentifié, l'**ouvrier** peut accéder aux fonctionnalités suivantes :
- **Visualisation des notifications** : L'ouvrier peut consulter toutes les maintenances en cours ou terminées, triées par priorité (celles nécessitant une intervention en premier).
- **Signaler une maintenance terminée** : L'ouvrier peut signaler qu'une maintenance est terminée en cliquant sur "Report Completion". Un formulaire s'affiche pour entrer le nombre d'heures de maintenance et rédiger un rapport. Une fois les informations saisies, il suffit de cliquer sur "Submit Report" pour valider.
- **Se déconnecter** : L'ouvrier peut se déconnecter de l'application en cliquant sur "Logout".

### Partie de l'Acheteur
Après s’être authentifié, l'**acheteur** peut accéder aux fonctionnalités suivantes :
- **Voir l’historique des maintenances** : L’acheteur peut consulter l’historique des maintenances des machines en cliquant sur "History". Les maintenances sont triées par priorité, en mettant en avant celles nécessitant un remplacement en premier.
- **Re-commander une machine** : Après avoir consulté l’historique des maintenances, l’acheteur peut racheter une machine en cliquant sur "Re-order Machine".


## Prérequis

- **Java JDK** (version recommandée : 8 ou supérieure)
- **Apache Tomcat** (pour déployer l'application)
- **Oracle Database** (avec PL/SQL)
- **Maven** pour gérer les dépendances

## Structure du Code

- **`src/`** : Contient les sources du projet, y compris les servlets, l'API et les classes de gestion.
- **`webapp/`** : Contient les fichiers de l'interface utilisateur (JSP, HTML, CSS).
- **`pom.xml`** : Le fichier Maven de configuration des dépendances.
- **`sql/`** : Contient les scripts PL/SQL pour la création de la base de données et les procédures stockées.

## Auteurs
- **SANNA Pietro**
- **TAVERNEL Raoul**
- **VELTRI Marie**
