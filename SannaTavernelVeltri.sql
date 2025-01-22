------------------------------ INITIALISATION DES TABLES ------------------------------

-- Supprimer les tables dans l'ordre inverse des dépendances
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE MaintenanceWorker CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE ZoneMachine CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Maintenance CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Machine CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Worker CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Manager CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Purchaser CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Zone CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Site CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Employee CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Person CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Une erreur est survenue lors de la suppression des tables.');
END;
/

-- Supprimer les séquences
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE Person_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Site_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Machine_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Maintenance_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Zone_seq';
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Une erreur est survenue lors de la suppression des séquences.');
END;
/

-- Création des séquences
BEGIN
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Person_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Site_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Machine_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Maintenance_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Zone_seq START WITH 1 INCREMENT BY 1 NOCACHE';
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Une erreur est survenue lors de la création des séquences.');
END;
/

-- Création des tables
DECLARE
    table_creation_error EXCEPTION;
    PRAGMA EXCEPTION_INIT(table_creation_error, -955);
BEGIN
    -- Création de la table Person
    EXECUTE IMMEDIATE '
        CREATE TABLE Person(
            id_person INT,
            firstName VARCHAR(50),
            lastName VARCHAR(50),
            birthDate DATE,
            phoneNumber VARCHAR(50),
            PRIMARY KEY(id_person)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Person existe déjà.');
END;
/

BEGIN
    -- Création de la table Employee
    EXECUTE IMMEDIATE '
        CREATE TABLE Employee(
            id_person INT,
            registrationCode VARCHAR(50),
            password VARCHAR(255),
            PRIMARY KEY(id_person),
            UNIQUE(registrationCode),
            FOREIGN KEY(id_person) REFERENCES Person(id_person)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Employee existe déjà.');
END;
/

BEGIN
    -- Création de la table Machine
    EXECUTE IMMEDIATE '
        CREATE TABLE Machine(
            id_machine INT,
            type_ VARCHAR(50),
            size_ DECIMAL(15,2),
            state VARCHAR(50),
            PRIMARY KEY(id_machine)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Machine existe déjà.');
END;
/

BEGIN
    -- Création de la table Site
    EXECUTE IMMEDIATE '
        CREATE TABLE Site(
            id_site VARCHAR(50),
            name VARCHAR(50),
            city VARCHAR(50),
            PRIMARY KEY(id_site),
            UNIQUE(city)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Site existe déjà.');
END;
/

BEGIN
    -- Création de la table Zone
    EXECUTE IMMEDIATE '
        CREATE TABLE Zone(
            zone_id INT,
            letter VARCHAR(2),
            color VARCHAR(50),
            id_site VARCHAR(50) NOT NULL,
            PRIMARY KEY(zone_id),
            FOREIGN KEY(id_site) REFERENCES Site(id_site)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Zone existe déjà.');
END;
/

BEGIN
    -- Création de la table Manager
    EXECUTE IMMEDIATE '
        CREATE TABLE Manager(
            id_person INT,
            id_site VARCHAR(50) NOT NULL,
            PRIMARY KEY(id_person),
            UNIQUE(id_site),
            FOREIGN KEY(id_person) REFERENCES Employee(id_person),
            FOREIGN KEY(id_site) REFERENCES Site(id_site)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Manager existe déjà.');
END;
/

BEGIN
    -- Création de la table Worker
    EXECUTE IMMEDIATE '
        CREATE TABLE Worker(
            id_person INT,
            id_site VARCHAR(50) NOT NULL,
            PRIMARY KEY(id_person),
            FOREIGN KEY(id_person) REFERENCES Employee(id_person),
            FOREIGN KEY(id_site) REFERENCES Site(id_site)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Worker existe déjà.');
END;
/

BEGIN
    -- Création de la table Purchaser
    EXECUTE IMMEDIATE '
        CREATE TABLE Purchaser(
            id_person INT,
            PRIMARY KEY(id_person),
            FOREIGN KEY(id_person) REFERENCES Employee(id_person)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Purchaser existe déjà.');
END;
/

BEGIN
    -- Création de la table Maintenance
    EXECUTE IMMEDIATE '
        CREATE TABLE Maintenance(
            id_maintenance INT,
            date_maintenance DATE,
            duration_ INT,
            report_ VARCHAR(50),
            status VARCHAR(50),
            id_person INT NOT NULL,
            id_machine INT NOT NULL,
            PRIMARY KEY(id_maintenance),
            FOREIGN KEY(id_person) REFERENCES Manager(id_person),
            FOREIGN KEY(id_machine) REFERENCES Machine(id_machine)
        )';
EXCEPTION
    WHEN table_creation_error THEN
        DBMS_OUTPUT.PUT_LINE('La table Maintenance existe déjà.');
END;
/

-- Création des triggers pour générer les IDs
CREATE OR REPLACE TRIGGER trg_person_id
BEFORE INSERT ON Person
FOR EACH ROW
BEGIN
    IF :NEW.id_person IS NULL THEN
        SELECT Person_seq.NEXTVAL INTO :NEW.id_person FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_machine_id
BEFORE INSERT ON Machine
FOR EACH ROW
BEGIN
    IF :NEW.id_machine IS NULL THEN
        SELECT Machine_seq.NEXTVAL INTO :NEW.id_machine FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_maintenance_id
BEFORE INSERT ON Maintenance
FOR EACH ROW
BEGIN
    IF :NEW.id_maintenance IS NULL THEN
        SELECT Maintenance_seq.NEXTVAL INTO :NEW.id_maintenance FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_zone_id
BEFORE INSERT ON Zone
FOR EACH ROW
BEGIN
    IF :NEW.zone_id IS NULL THEN
        SELECT Zone_seq.NEXTVAL INTO :NEW.zone_id FROM DUAL;
    END IF;
END;
/

COMMIT;



------------------------------ PROCÉDURES EMPLOYÉ ------------------------------

/* ****************************************** *
 *   Procédure pour authentifier un employé   *
 * ****************************************** */
CREATE OR REPLACE PROCEDURE authenticate_employee(
    p_registrationCode IN VARCHAR2,
    p_password         IN VARCHAR2,
    p_authenticated    OUT INT
) AS
    v_stored_password VARCHAR2(4000);
    v_encrypted_input VARCHAR2(4000);
    v_id_employee     INT;
BEGIN
    SELECT password, id_person
    INTO v_stored_password, v_id_employee
    FROM Employee
    WHERE registrationCode = p_registrationCode;

    v_encrypted_input := crypto.crypt(p_password);

    IF v_stored_password = v_encrypted_input THEN
        p_authenticated := v_id_employee;  -- Authentification réussie
    ELSE
        p_authenticated := -1;  -- Mot de passe incorrect
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_authenticated := -1;  -- Utilisateur non trouvé
END;
/

/* ************************************************ *
 *   Procédure pour obtenir le type d'un employé    *
 * ************************************************ */
CREATE OR REPLACE PROCEDURE find_employee_type(
    p_employee_id IN INT,
    p_employee_type OUT VARCHAR
) AS
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count 
    FROM Worker 
    WHERE id_person = p_employee_id;

    IF v_count > 0 THEN
        p_employee_type := 'Worker';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count 
    FROM Purchaser 
    WHERE id_person = p_employee_id;

    IF v_count > 0 THEN
        p_employee_type := 'Purchaser';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count 
    FROM Manager 
    WHERE id_person = p_employee_id;

    IF v_count > 0 THEN
        p_employee_type := 'Manager';
        RETURN;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_employee_type := 'Unknown'; -- Aucun type trouvé
END;
/

/* ******************************************* *
 *   Package pour crypter et décrypter un mdp  *
 * ******************************************* */
CREATE OR REPLACE PACKAGE BODY crypto AS
    lv_key CONSTANT VARCHAR2(20) := 'MYSECRETKEY'; 
    ln_keylen CONSTANT NUMBER := LENGTH(lv_key); 

    FUNCTION fmy_replacecrypt(p_char VARCHAR2) RETURN NUMBER AS
    BEGIN
        IF p_char = ' ' THEN
            RETURN 0;
        ELSE
            RETURN ASCII(p_char) - 64;
        END IF;
    END;

    FUNCTION fmy_replacedecrypt(p_char NUMBER) RETURN VARCHAR2 AS
    BEGIN
        IF p_char = 0 THEN
            RETURN ' ';
        ELSE
            RETURN CHR(p_char + 64);
        END IF;
    END;

    FUNCTION crypt(message IN VARCHAR2) RETURN VARCHAR2 AS
        lv_crypted VARCHAR2(30000);
        ln_nbblock NUMBER;
        lv_block VARCHAR2(30000);
        ln_block NUMBER;
        ln_key NUMBER;
        ln_crypt NUMBER;
    BEGIN
        ln_nbblock := CEIL(LENGTH(message) / ln_keylen);
        FOR i IN 1 .. ln_nbblock LOOP
            lv_block := SUBSTR(message, (i - 1) * ln_keylen + 1, ln_keylen);
            FOR j IN 1 .. ln_keylen LOOP
                ln_block := fmy_replacecrypt(SUBSTR(lv_block, j, 1));
                ln_key := fmy_replacecrypt(SUBSTR(lv_key, j, 1));
                ln_crypt := (ln_block + ln_key) MOD 27;
                lv_crypted := lv_crypted || fmy_replacedecrypt(ln_crypt);
            END LOOP;
        END LOOP;
        RETURN lv_crypted;
    END;

    FUNCTION decrypt(message IN VARCHAR2) RETURN VARCHAR2 AS
        lv_decrypted VARCHAR2(30000);
        ln_nbblock NUMBER;
        lv_block VARCHAR2(30000);
        ln_block NUMBER;
        ln_key NUMBER;
        ln_decrypt NUMBER;
    BEGIN
        ln_nbblock := CEIL(LENGTH(message) / ln_keylen);
        FOR i IN 1 .. ln_nbblock LOOP
            lv_block := SUBSTR(message, (i - 1) * ln_keylen + 1, ln_keylen);
            FOR j IN 1 .. ln_keylen LOOP
                ln_block := fmy_replacecrypt(SUBSTR(lv_block, j, 1));
                ln_key := fmy_replacecrypt(SUBSTR(lv_key, j, 1));
                ln_decrypt := (ln_block - ln_key) MOD 27;
                IF ln_decrypt < 0 THEN
                    ln_decrypt := ln_decrypt + 27;
                END IF;
                lv_decrypted := lv_decrypted || fmy_replacedecrypt(ln_decrypt);
            END LOOP;
        END LOOP;
        RETURN lv_decrypted;
    END;
END crypto;
/



-------------------------------- PROCÉDURES SITE -------------------------------

/* *********************************** *
 *   Procédure pour ajouter un site    *
 * *********************************** */
CREATE OR REPLACE PROCEDURE create_site(
    p_site_id IN OUT NUMBER,
    p_name IN VARCHAR2,     
    p_city IN VARCHAR2       
) AS
BEGIN
    IF p_site_id IS NULL THEN
        SELECT Site_seq.NEXTVAL INTO p_site_id FROM DUAL;
    END IF;

    INSERT INTO Site (id_site, name, city)
    VALUES (p_site_id, p_name, p_city);
    
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Le site avec cet ID ou cette ville existe déjà.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La séquence Site_seq est introuvable.');
END;
/


/* ***************************************** *
 *   Procédure pour mettre à jour un site    *
 * ***************************************** */
 CREATE OR REPLACE PROCEDURE update_site(
    p_site_id IN INT,
    p_name IN VARCHAR,
    p_city IN VARCHAR,
    p_rows_updated OUT INT
) AS
BEGIN
    UPDATE Site
    SET name = p_name, city = p_city
    WHERE id_site = p_site_id;

    p_rows_updated := SQL%ROWCOUNT;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun site trouvé avec l''ID spécifié.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
END;
/


/* ************************************* *
 *   Procédure pour supprimer un site    *
 * ************************************* */
CREATE OR REPLACE PROCEDURE delete_site(
    p_site_id IN INT
) AS
BEGIN
    DELETE FROM Zone WHERE id_site = p_site_id;
    DELETE FROM Site WHERE id_site = p_site_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Le site ou les zones associées sont introuvables.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la suppression du site.');
END;
/

/* *********************************** *
 *   Procédure pour trouver un site    *
 * *********************************** */
CREATE OR REPLACE TYPE site_type IS OBJECT (
    id_site INT,
    site_name VARCHAR2(100),
    site_city VARCHAR2(100),
    zone_list VARCHAR2(4000),
    worker_list VARCHAR2(4000),
    manager_info VARCHAR2(4000)
);
/
CREATE OR REPLACE TYPE site_table_type IS TABLE OF site_type;
/

/* ********************************************** *
 *   Procédure pour trouver un site spécifique    *
 * ********************************************** */
CREATE OR REPLACE PROCEDURE find_site(
    p_site_id IN INT,
    p_sites OUT site_table_type
) AS
BEGIN
    p_sites := site_table_type();

    FOR rec IN (
        SELECT s.id_site, s.name AS site_name, s.city AS site_city,

               (
                   SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                   WITHIN GROUP (ORDER BY z.zone_id)
                   FROM Zone z
                   WHERE z.id_site = s.id_site
               ) AS zone_list,

               (
                   SELECT LISTAGG(w.id_person || ':' || p.firstName || ':' || p.lastName || ':' || TO_CHAR(p.birthDate, 'YYYY-MM-DD') || ':' || p.phoneNumber, ',') 
                   WITHIN GROUP (ORDER BY w.id_person)
                   FROM Worker w
                   JOIN Person p ON w.id_person = p.id_person
                   WHERE w.id_site = s.id_site
               ) AS worker_list,

               (
                   SELECT m.id_person || ':' || p.firstName || ':' || p.lastName || ':' || TO_CHAR(p.birthDate, 'YYYY-MM-DD') || ':' || p.phoneNumber
                   FROM Manager m
                   JOIN Person p ON m.id_person = p.id_person
                   WHERE m.id_site = s.id_site
               ) AS manager_info

        FROM Site s
        WHERE s.id_site = p_site_id
    ) LOOP
        p_sites.EXTEND;
        p_sites(p_sites.COUNT) := site_type(
            rec.id_site,
            rec.site_name,
            rec.site_city,
            rec.zone_list,
            rec.worker_list,
            rec.manager_info
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Le site avec l''ID spécifié est introuvable.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plusieurs résultats inattendus.');
END;
/

/* ****************************************** *
 *   Procédure pour trouver tous les sites    *
 * ****************************************** */
CREATE OR REPLACE PROCEDURE find_all_sites(
    p_sites OUT site_table_type
) AS
BEGIN
    p_sites := site_table_type();

    FOR rec IN (
        SELECT s.id_site, s.name AS site_name, s.city AS site_city,

               (
                   SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                   WITHIN GROUP (ORDER BY z.zone_id)
                   FROM Zone z
                   WHERE z.id_site = s.id_site
               ) AS zone_list,

               (
                   SELECT LISTAGG(w.id_person || ':' || p.firstName || ':' || p.lastName || ':' || TO_CHAR(p.birthDate, 'YYYY-MM-DD') || ':' || p.phoneNumber, ',') 
                   WITHIN GROUP (ORDER BY w.id_person)
                   FROM Worker w
                   JOIN Person p ON w.id_person = p.id_person
                   WHERE w.id_site = s.id_site
               ) AS worker_list,

               (
                   SELECT m.id_person || ':' || p.firstName || ':' || p.lastName || ':' || TO_CHAR(p.birthDate, 'YYYY-MM-DD') || ':' || p.phoneNumber
                   FROM Manager m
                   JOIN Person p ON m.id_person = p.id_person
                   WHERE m.id_site = s.id_site
               ) AS manager_info

        FROM Site s
    ) LOOP
        p_sites.EXTEND;
        p_sites(p_sites.COUNT) := site_type(
            rec.id_site,
            rec.site_name,
            rec.site_city,
            rec.zone_list,
            rec.worker_list,
            rec.manager_info
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun site trouvé.');
END;
/



-------------------------------- PROCÉDURES ZONE -------------------------------

/* ********************************** *
 *   Procédure pour créer une zone    *
 * ********************************** */
CREATE OR REPLACE PROCEDURE add_zone(
    p_zone_id IN OUT NUMBER,
    p_letter IN VARCHAR2,   
    p_color IN VARCHAR2,     
    p_site_id IN NUMBER      
) AS
BEGIN
    IF p_zone_id IS NULL THEN
        SELECT Zone_seq.NEXTVAL INTO p_zone_id FROM DUAL;
    END IF;

    INSERT INTO Zone (zone_id, letter, color, id_site)
    VALUES (p_zone_id, p_letter, p_color, p_site_id);

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Une zone avec cet ID ou ces caractéristiques existe déjà.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La séquence Zone_seq est introuvable.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Le site associé à cette zone est introuvable.');
END;
/

/* ****************************************** *
 *   Procédure pour mettre à jour une zone    *
 * ****************************************** */
 CREATE OR REPLACE PROCEDURE update_zone(
    p_zone_id IN INT,
    p_letter IN VARCHAR,
    p_color IN VARCHAR,
    p_site_id IN INT,
    p_rows_updated OUT INT
) AS
BEGIN
    UPDATE Zone
    SET letter = p_letter, color = p_color, id_site = p_site_id
    WHERE zone_id = p_zone_id;

    p_rows_updated := SQL%ROWCOUNT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La zone avec l''ID spécifié est introuvable.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
END;
/

/* ************************************** *
 *   Procédure pour supprimer une zone    *
 * ************************************** */
 CREATE OR REPLACE PROCEDURE delete_zone(
    p_zone_id IN INT
) AS
BEGIN
    DELETE FROM ZoneMachine WHERE zone_id = p_zone_id;
    DELETE FROM Zone WHERE zone_id = p_zone_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La zone ou les machines associées sont introuvables.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la suppression de la zone.');
END;
/

/* ************************************ *
 *   Procédure pour trouver une zone    *
 * ************************************ */
CREATE OR REPLACE TYPE zone_type IS OBJECT (
    zone_id INT,
    letter VARCHAR2(10),
    color VARCHAR2(10),
    site_id INT,
    site_name VARCHAR2(100),
    site_city VARCHAR2(100),
    machine_list VARCHAR2(4000)
);
/

CREATE OR REPLACE TYPE zone_table_type IS TABLE OF zone_type;
/

/* *********************************************** *
 *   Procédure pour trouver une zone spécifique    *
 * *********************************************** */
CREATE OR REPLACE PROCEDURE find_zone(
    p_zone_id IN INT,
    p_zones OUT zone_table_type
) AS
BEGIN
    p_zones := zone_table_type(); 

    FOR rec IN (
        SELECT 
            z.zone_id,
            z.letter,
            z.color,
            s.id_site,
            s.name AS site_name,
            s.city AS site_city,
            (
                SELECT LISTAGG(m.id_machine || ':' || m.type_ || ':' || m.size_ || ':' || m.state, ',')
                WITHIN GROUP (ORDER BY m.id_machine)
                FROM Machine m
                JOIN ZoneMachine zm ON m.id_machine = zm.id_machine
                WHERE zm.zone_id = z.zone_id
            ) AS machine_list
        FROM Zone z
        JOIN Site s ON z.id_site = s.id_site
        WHERE z.zone_id = p_zone_id
    ) LOOP
        p_zones.EXTEND;
        p_zones(p_zones.COUNT) := zone_type(
            rec.zone_id,
            rec.letter,
            rec.color,
            rec.id_site,
            rec.site_name,
            rec.site_city,
            rec.machine_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La zone avec l''ID spécifié est introuvable.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plusieurs zones inattendues.');
END;
/

/* ******************************************** *
 *   Procédure pour trouver toutes les zones    *
 * ******************************************** */
CREATE OR REPLACE PROCEDURE find_all_zones(
    p_zones OUT zone_table_type
) AS
BEGIN
    p_zones := zone_table_type();

    FOR rec IN (
        SELECT 
            z.zone_id,
            z.letter,
            z.color,
            s.id_site,
            s.name AS site_name,
            s.city AS site_city,
            (
                SELECT LISTAGG(m.id_machine || ':' || m.type_ || ':' || m.size_ || ':' || m.state, ',') 
                WITHIN GROUP (ORDER BY m.id_machine)
                FROM Machine m
                JOIN ZoneMachine zm ON m.id_machine = zm.id_machine
                WHERE zm.zone_id = z.zone_id
            ) AS machine_list
        FROM Zone z
        JOIN Site s ON z.id_site = s.id_site
    ) LOOP
        p_zones.EXTEND;
        p_zones(p_zones.COUNT) := zone_type(
            rec.zone_id,
            rec.letter,
            rec.color,
            rec.id_site,
            rec.site_name,
            rec.site_city,
            rec.machine_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucune zone trouvée.');
END;
/



----------------------------- PROCÉDURES PURCHASER ------------------------------

/* ************************************* *
 *   Procédure pour créer un purchaser   *
 * ************************************* */
CREATE OR REPLACE PROCEDURE create_purchaser(
    p_purchaser_id IN INT,
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR
) AS
    v_encrypted_password VARCHAR2(4000);
BEGIN
    INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
    VALUES (p_purchaser_id, p_firstName, p_lastName, p_birthDate, p_phoneNumber);

    INSERT INTO Employee (id_person, registrationCode, password)
    VALUES (p_purchaser_id, p_registrationCode, v_encrypted_password);

    INSERT INTO Purchaser (id_person)
    VALUES (p_purchaser_id);
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : ID, téléphone ou code d''enregistrement du purchaser déjà utilisé.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de l''ajout du purchaser.');
END;
/

/* ********************************************* *
 *   Procédure pour mettre à jour un purchaser   *
 * ********************************************* */
CREATE OR REPLACE PROCEDURE update_purchaser(
    p_purchaser_id IN INT,
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR
) AS
    v_encrypted_password VARCHAR2(4000);
BEGIN
    UPDATE Person
    SET firstName = p_firstName, lastName = p_lastName,
        birthDate = p_birthDate, phoneNumber = p_phoneNumber
    WHERE id_person = p_purchaser_id;

    v_encrypted_password := crypto.crypt(p_password);

    UPDATE Employee
    SET registrationCode = p_registrationCode, password = v_encrypted_password
    WHERE id_person = p_purchaser_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Purchaser introuvable pour mise à jour.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
END;
/

/* ***************************************** *
 *   Procédure pour supprimer un purchaser   *
 * ***************************************** */
CREATE OR REPLACE PROCEDURE delete_purchaser(
    p_purchaser_id IN INT
) AS
BEGIN
    DELETE FROM Purchaser WHERE id_person = p_purchaser_id;
    DELETE FROM Employee WHERE id_person = p_purchaser_id;
    DELETE FROM Person WHERE id_person = p_purchaser_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Purchaser introuvable pour suppression.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la suppression du purchaser.');
END;
/

/* *************************************** *
 *   Procédure pour trouver un purchaser   *
 * *************************************** */
CREATE OR REPLACE TYPE purchaser_type IS OBJECT (
    id_person INT,
    firstName VARCHAR2(100),
    lastName VARCHAR2(100),
    birthDate DATE,
    phoneNumber VARCHAR2(20),
    registrationCode VARCHAR2(20),
    password VARCHAR2(100)
);
/

CREATE OR REPLACE TYPE purchaser_table_type IS TABLE OF purchaser_type;
/

/* ************************************************** *
 *   Procédure pour trouver un purchaser spécifique   *
 * ************************************************** */
CREATE OR REPLACE PROCEDURE find_purchaser(
    p_purchaser_id IN INT,
    p_purchasers OUT purchaser_table_type 
) AS
BEGIN
    p_purchasers := purchaser_table_type();

    FOR rec IN (
        SELECT p.id_person, p.firstName, p.lastName, p.birthDate, p.phoneNumber, 
               e.registrationCode, e.password
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Purchaser pu ON e.id_person = pu.id_person
        WHERE p.id_person = p_purchaser_id
    ) LOOP
        p_purchasers.EXTEND;
        p_purchasers(p_purchasers.COUNT) := purchaser_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Purchaser introuvable avec l''ID spécifié.');
END;
/

/* ********************************************** *
 *   Procédure pour trouver tous les purchasers   *
 * ********************************************** */
CREATE OR REPLACE PROCEDURE find_all_purchasers(
    p_purchasers OUT purchaser_table_type 
) AS
BEGIN
    p_purchasers := purchaser_table_type();

    FOR rec IN (
        SELECT p.id_person, p.firstName, p.lastName, p.birthDate, p.phoneNumber, 
               e.registrationCode, e.password
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Purchaser pu ON e.id_person = pu.id_person
    ) LOOP
        p_purchasers.EXTEND;
        p_purchasers(p_purchasers.COUNT) := purchaser_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun purchaser trouvé.');
END;
/



----------------------------- PROCÉDURES MANAGER ------------------------------

/* ************************************* *
 *   Procédure pour créer un manager     *
 * ************************************* */
CREATE OR REPLACE PROCEDURE create_manager(
    p_manager_id IN INT,
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR,
    p_site_id IN INT
) AS
    v_encrypted_password VARCHAR2(4000);
BEGIN
    INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
    VALUES (p_manager_id, p_firstName, p_lastName, p_birthDate, p_phoneNumber);

    v_encrypted_password := crypto.crypt(p_password);

    INSERT INTO Employee (id_person, registrationCode, password)
    VALUES (p_manager_id, p_registrationCode, v_encrypted_password);

    INSERT INTO Manager (id_person, id_site)
    VALUES (p_manager_id, p_site_id);
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Un manager avec cet ID ou ce code d''enregistrement existe déjà.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère avec le site ou les tables associées.');
END;
/

/* ********************************************* *
 *   Procédure pour mettre à jour un manager     *
 * ********************************************* */
CREATE OR REPLACE PROCEDURE update_manager(
    p_manager_id IN INT,
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR,
    p_site_id IN INT
) AS
    v_encrypted_password VARCHAR2(4000);
BEGIN
    UPDATE Person
    SET firstName = p_firstName, lastName = p_lastName,
        birthDate = p_birthDate, phoneNumber = p_phoneNumber
    WHERE id_person = p_manager_id;
    
    v_encrypted_password := crypto.crypt(p_password);

    UPDATE Employee
    SET registrationCode = p_registrationCode, password = v_encrypted_password
    WHERE id_person = p_manager_id;

    UPDATE Manager
    SET id_site = p_site_id
    WHERE id_person = p_manager_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Manager introuvable avec l''ID spécifié.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la mise à jour.');
END;
/

/* ***************************************** *
 *   Procédure pour supprimer un manager     *
 * ***************************************** */
CREATE OR REPLACE PROCEDURE delete_manager(
    p_manager_id IN INT
) AS
BEGIN
    DELETE FROM Manager WHERE id_person = p_manager_id;
    DELETE FROM Employee WHERE id_person = p_manager_id;
    DELETE FROM Person WHERE id_person = p_manager_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Manager introuvable pour suppression.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère avec d''autres tables.');
END;
/

/* *************************************** *
 *   Procédure pour trouver un manager     *
 * *************************************** */
CREATE OR REPLACE TYPE manager_type IS OBJECT (
    id_person INT,
    firstName VARCHAR2(100),
    lastName VARCHAR2(100),
    birthDate DATE,
    phoneNumber VARCHAR2(20),
    registrationCode VARCHAR2(20),
    password VARCHAR2(100),
    site_id INT,
    site_name VARCHAR2(100),
    site_city VARCHAR2(100),
    zone_list VARCHAR2(4000)
);
/

CREATE OR REPLACE TYPE manager_table_type IS TABLE OF manager_type;
/

/* ************************************************* *
 *   Procédure pour trouver un manager spécifique    *
 * ************************************************* */
CREATE OR REPLACE PROCEDURE find_manager(
    p_manager_id IN INT,
    p_managers OUT manager_table_type 
) AS
BEGIN
    p_managers := manager_table_type();

    FOR rec IN (
        SELECT 
            p.id_person, 
            p.firstName, 
            p.lastName, 
            p.birthDate AS birthDate, 
            p.phoneNumber, 
            e.registrationCode, 
            e.password, 
            s.id_site AS site_id, 
            s.name AS site_name, 
            s.city AS site_city, 
            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM Zone z
                WHERE z.id_site = s.id_site
            ) AS zone_list
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Manager m ON e.id_person = m.id_person
        JOIN Site s ON m.id_site = s.id_site
        WHERE p.id_person = p_manager_id
    ) LOOP
        p_managers.EXTEND;
        p_managers(p_managers.COUNT) := manager_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.zone_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Manager introuvable avec l''ID spécifié.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Trop de résultats pour cet ID.');
END;
/

/* ********************************************** *
 *   Procédure pour trouver tous les managers     *
 * ********************************************** */
CREATE OR REPLACE PROCEDURE find_all_managers(
    p_managers OUT manager_table_type
) AS
BEGIN
    p_managers := manager_table_type();

    FOR rec IN (
        SELECT 
            p.id_person, 
            p.firstName, 
            p.lastName, 
            p.birthDate AS birthDate, 
            p.phoneNumber, 
            e.registrationCode, 
            e.password, 
            s.id_site AS site_id, 
            s.name AS site_name, 
            s.city AS site_city, 
            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM Zone z
                WHERE z.id_site = s.id_site
            ) AS zone_list
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Manager m ON e.id_person = m.id_person
        JOIN Site s ON m.id_site = s.id_site
    ) LOOP
        p_managers.EXTEND;
        p_managers(p_managers.COUNT) := manager_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.zone_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun manager trouvé.');
END;
/



------------------------------ PROCÉDURES OUVRIER ------------------------------

/* ************************************ *
 *   Procédure pour créer un ouvrier    *
 * ************************************ */
CREATE OR REPLACE PROCEDURE create_worker(
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR,
    p_site_id IN INT,
    p_worker_id OUT INT
) AS
    v_encrypted_password VARCHAR2(4000);
BEGIN
    INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
    VALUES (Person_seq.NEXTVAL, p_firstName, p_lastName, p_birthDate, p_phoneNumber)
    RETURNING id_person INTO p_worker_id;

    v_encrypted_password := crypto.crypt(p_password);
    
    INSERT INTO Employee (id_person, registrationCode, password)
    VALUES (p_worker_id, p_registrationCode, v_encrypted_password);

    INSERT INTO Worker (id_person, id_site)
    VALUES (p_worker_id, p_site_id);
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : L''ouvrier existe déjà (ID, téléphone ou code d''enregistrement).');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Données nécessaires introuvables.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit avec une clé étrangère (site introuvable).');
END;
/

/* ******************************************** *
 *   Procédure pour mettre à jour un ouvrier    *
 * ******************************************** */
CREATE OR REPLACE PROCEDURE update_worker(
    p_worker_id IN INT,
    p_firstName IN VARCHAR,
    p_lastName IN VARCHAR,
    p_birthDate IN DATE,
    p_phoneNumber IN VARCHAR,
    p_registrationCode IN VARCHAR,
    p_password IN VARCHAR,
    p_site_id IN INT
) AS
    v_encrypted_password VARCHAR2(4000);

BEGIN
    UPDATE Person
    SET firstName = p_firstName, lastName = p_lastName,
        birthDate = p_birthDate, phoneNumber = p_phoneNumber
    WHERE id_person = p_worker_id;

    v_encrypted_password := crypto.crypt(p_password);

    UPDATE Employee
    SET registrationCode = p_registrationCode, password = v_encrypted_password
    WHERE id_person = p_worker_id;
    
    UPDATE Worker
    SET id_site = p_site_id
    WHERE id_person = p_worker_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : L''ouvrier avec l''ID spécifié est introuvable.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes ou conversion invalide.');
END;
/

/* **************************************** *
 *   Procédure pour supprimer un ouvrier    *
 * **************************************** */
CREATE OR REPLACE PROCEDURE delete_worker(
    p_worker_id IN INT
) AS
BEGIN
    DELETE FROM MaintenanceWorker WHERE id_person = p_worker_id;
    DELETE FROM Worker WHERE id_person = p_worker_id;
    DELETE FROM Employee WHERE id_person = p_worker_id;
    DELETE FROM Person WHERE id_person = p_worker_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Ouvrier introuvable pour suppression.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la suppression.');
END;
/

/* ************************************** *
 *   Procédure pour trouver un ouvrier    *
 * ************************************** */
CREATE OR REPLACE TYPE worker_type IS OBJECT (
    id_person NUMBER,
    firstName VARCHAR2(100),
    lastName VARCHAR2(100),
    birthDate DATE,
    phoneNumber VARCHAR2(20),
    registrationCode VARCHAR2(20),
    password VARCHAR2(100),
    site_id NUMBER,
    site_name VARCHAR2(100),
    site_city VARCHAR2(100),
    zone_list VARCHAR2(4000)
);
/

CREATE OR REPLACE TYPE worker_table_type IS TABLE OF worker_type;
/

/* ************************************************* *
 *   Procédure pour trouver un ouvrier spécifique    *
 * ************************************************* */
CREATE OR REPLACE PROCEDURE find_worker(
    p_worker_id IN INT,
    p_workers OUT worker_table_type 
) AS
BEGIN
    p_workers := worker_table_type(); 

    FOR rec IN (
        SELECT 
            p.id_person,
            p.firstName,
            p.lastName,
            p.birthDate,
            p.phoneNumber,
            e.registrationCode,
            e.password,
            s.id_site AS site_id,
            s.name AS site_name,
            s.city AS site_city,
            (SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                WITHIN GROUP (ORDER BY z.zone_id)
             FROM Zone z
             WHERE z.id_site = s.id_site
            ) AS zone_list
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Worker w ON e.id_person = w.id_person
        JOIN Site s ON w.id_site = s.id_site
        WHERE p.id_person = p_worker_id
        GROUP BY 
            p.id_person, p.firstName, p.lastName, p.birthDate, p.phoneNumber, 
            e.registrationCode, e.password,
            s.id_site, s.name, s.city
    ) LOOP
        p_workers.EXTEND; 
        p_workers(p_workers.COUNT) := worker_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.zone_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun ouvrier trouvé avec l''ID spécifié.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Plusieurs résultats inattendus pour cet ID.');
END;
/

/* ********************************************* *
 *   Procédure pour trouver tous les employés    *
 * ********************************************* */
CREATE OR REPLACE PROCEDURE find_all_workers (
    p_workers OUT worker_table_type 
) AS
BEGIN
    p_workers := worker_table_type(); 
    
    FOR rec IN (
        SELECT 
            p.id_person,
            p.firstName,
            p.lastName,
            p.birthDate,
            p.phoneNumber,
            e.registrationCode,
            e.password,
            s.id_site AS site_id,
            s.name AS site_name,
            s.city AS site_city,
            (SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',') 
                WITHIN GROUP (ORDER BY z.zone_id)
             FROM Zone z
             WHERE z.id_site = s.id_site
            ) AS zone_list
        FROM Person p
        JOIN Employee e ON p.id_person = e.id_person
        JOIN Worker w ON e.id_person = w.id_person
        JOIN Site s ON w.id_site = s.id_site
        GROUP BY 
            p.id_person, p.firstName, p.lastName, p.birthDate, p.phoneNumber, 
            e.registrationCode, e.password,
            s.id_site, s.name, s.city
    ) LOOP
        p_workers.EXTEND; 
        p_workers(p_workers.COUNT) := worker_type(
            rec.id_person,
            rec.firstName,
            rec.lastName,
            rec.birthDate,
            rec.phoneNumber,
            rec.registrationCode,
            rec.password,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.zone_list
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucun ouvrier trouvé.');
END;
/



------------------------------ PROCÉDURES MACHINE ------------------------------

/* ******************************************************** *
 *   Procédure pour obtenir le prochain id d'une machine    *
 * ******************************************************** */
 CREATE OR REPLACE PROCEDURE get_next_machine_id(
    p_next_id OUT INT
) AS
BEGIN
    SELECT Machine_seq.NEXTVAL INTO p_next_id FROM DUAL;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La séquence Machine_seq est introuvable.');
END;
/

/* ************************************* *
 *   Procédure pour créer une machine    *
 * ************************************* */
CREATE OR REPLACE PROCEDURE create_machine(
    p_machine_id IN OUT NUMBER,     
    p_type IN VARCHAR2,              
    p_size IN NUMBER,                
    p_state IN VARCHAR2,            
    p_zone_ids IN VARCHAR2          
) AS
    v_zone_id NUMBER;                
BEGIN
    IF p_machine_id IS NULL THEN
        SELECT Machine_seq.NEXTVAL INTO p_machine_id FROM DUAL;
    END IF;

    INSERT INTO Machine (id_machine, type_, size_, state)
    VALUES (p_machine_id, p_type, p_size, p_state);

    FOR zone IN (
        SELECT TO_NUMBER(REGEXP_SUBSTR(p_zone_ids, '[^,]+', 1, LEVEL)) AS zone_id
        FROM DUAL
        CONNECT BY LEVEL <= REGEXP_COUNT(p_zone_ids, ',') + 1
    ) LOOP
        v_zone_id := zone.zone_id;

        INSERT INTO ZoneMachine (id_machine, zone_id)
        VALUES (p_machine_id, v_zone_id);
    END LOOP;
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La machine avec l''ID ' || p_machine_id || ' existe déjà.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Problème de conversion pour les zones.');
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Zone introuvable pour la machine.');
END;
/

/* ********************************************* *
 *   Procédure pour mettre à jour une machine    *
 * ********************************************* */
CREATE OR REPLACE PROCEDURE update_machine(
    p_machine_id IN INT,
    p_type IN VARCHAR,
    p_size IN NUMBER,
    p_state IN VARCHAR,
    p_zone_ids IN VARCHAR
) AS
BEGIN
    UPDATE Machine
    SET type_ = p_type, size_ = p_size, state = p_state
    WHERE id_machine = p_machine_id;

    DELETE FROM ZoneMachine WHERE id_machine = p_machine_id;

    FOR zone IN (
        SELECT TO_NUMBER(REGEXP_SUBSTR(p_zone_ids, '[^,]+', 1, LEVEL)) AS zone_id
        FROM DUAL
        CONNECT BY LEVEL <= REGEXP_COUNT(p_zone_ids, ',') + 1
    ) LOOP
        INSERT INTO ZoneMachine (id_machine, zone_id)
        VALUES (p_machine_id, zone.zone_id);
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Machine ou zones introuvables.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Problème de conversion pour les zones.');
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé unique lors de la mise à jour.');
END;
/

/* ***************************************** *
 *   Procédure pour supprimer une machine    *
 * ***************************************** */
CREATE OR REPLACE PROCEDURE delete_machine(
    p_machine_id IN INT
) AS
BEGIN
    DELETE FROM MaintenanceWorker 
    WHERE id_maintenance IN (
        SELECT id_maintenance FROM Maintenance WHERE id_machine = p_machine_id
    );
    DELETE FROM Maintenance WHERE id_machine = p_machine_id;
    DELETE FROM ZoneMachine WHERE id_machine = p_machine_id;
    DELETE FROM Machine WHERE id_machine = p_machine_id;
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Machine ou maintenance introuvable pour suppression.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit avec une clé étrangère lors de la suppression.');
END;
/

/* *************************************** *
 *   Procédure pour trouver une machine    *
 * *************************************** */
 CREATE OR REPLACE TYPE machine_type IS OBJECT (
    id_machine INT,
    machine_type VARCHAR2(100),
    machine_size DECIMAL(15,2),
    machine_state VARCHAR2(100),
    zone_list VARCHAR2(4000),
    maintenance_list VARCHAR2(4000),
    site_id INT,
    site_name VARCHAR2(100),
    site_city VARCHAR2(100),
    manager_id INT,
    manager_firstName VARCHAR2(100),
    manager_lastName VARCHAR2(100),
    manager_birthDate DATE,
    manager_phoneNumber VARCHAR2(20),
    manager_registrationCode VARCHAR2(20),
    manager_password VARCHAR2(100),
    zone_site VARCHAR2(4000)
);
/

CREATE OR REPLACE TYPE machine_table_type IS TABLE OF machine_type;
/

/* ************************************************** *
 *   Procédure pour trouver une machine spécifique    *
 * ************************************************** */
CREATE OR REPLACE PROCEDURE find_machine(
    p_machine_id IN INT,
    p_machines OUT machine_table_type
) AS
BEGIN
    p_machines := machine_table_type();

    FOR rec IN (
        SELECT 
            m.id_machine,
            m.type_ AS machine_type,
            m.size_ AS machine_size,
            m.state AS machine_state,
            
            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',')
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM ZoneMachine zm
                JOIN Zone z ON zm.zone_id = z.zone_id
                WHERE zm.id_machine = m.id_machine
            ) AS zone_list,

            (
                SELECT LISTAGG(mt.id_maintenance || ':' || mt.date_maintenance || ':' || 
                               mt.duration_ || ':' || mt.report_ || ':' || mt.status, ',')
                WITHIN GROUP (ORDER BY mt.id_maintenance)
                FROM Maintenance mt
                WHERE mt.id_machine = m.id_machine
            ) AS maintenance_list,

            s.id_site AS site_id,
            s.name AS site_name,
            s.city AS site_city,

            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',')
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM Zone z
                WHERE z.id_site = s.id_site -- Relation correcte entre Zone et Site
            ) AS zone_site,

            -- Informations du manager
            mgr.id_person AS manager_id,
            p.firstName AS manager_firstName,
            p.lastName AS manager_lastName,
            p.birthDate AS manager_birthDate,
            p.phoneNumber AS manager_phoneNumber,
            e.registrationCode AS manager_registrationCode,
            e.password AS manager_password
        FROM Machine m
        LEFT JOIN ZoneMachine zm ON m.id_machine = zm.id_machine
        LEFT JOIN Zone z ON zm.zone_id = z.zone_id
        LEFT JOIN Site s ON z.id_site = s.id_site
        LEFT JOIN Manager mgr ON s.id_site = mgr.id_site
        LEFT JOIN Employee e ON mgr.id_person = e.id_person
        LEFT JOIN Person p ON e.id_person = p.id_person
        WHERE m.id_machine = p_machine_id
    ) LOOP
        p_machines.EXTEND;
        p_machines(p_machines.COUNT) := machine_type(
            rec.id_machine,
            rec.machine_type,
            rec.machine_size,
            rec.machine_state,
            rec.zone_list,
            rec.maintenance_list,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.manager_id,
            rec.manager_firstName,
            rec.manager_lastName,
            rec.manager_birthDate,
            rec.manager_phoneNumber,
            rec.manager_registrationCode,
            rec.manager_password,
            rec.zone_site
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Machine introuvable avec l''ID ' || p_machine_id);
END;
/


/* *********************************************** *
 *   Procédure pour trouver toutes les machines    *
 * *********************************************** */
CREATE OR REPLACE PROCEDURE find_all_machines(
    p_machines OUT machine_table_type
) AS
BEGIN
    p_machines := machine_table_type();

    FOR rec IN (
        SELECT 
            m.id_machine,
            m.type_ AS machine_type,
            m.size_ AS machine_size,
            m.state AS machine_state,
            
            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',')
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM ZoneMachine zm
                JOIN Zone z ON zm.zone_id = z.zone_id
                WHERE zm.id_machine = m.id_machine
            ) AS zone_list,

            (
                SELECT LISTAGG(mt.id_maintenance || ':' || mt.date_maintenance || ':' || 
                               mt.duration_ || ':' || mt.report_ || ':' || mt.status, ',')
                WITHIN GROUP (ORDER BY mt.id_maintenance)
                FROM Maintenance mt
                WHERE mt.id_machine = m.id_machine
            ) AS maintenance_list,

            s.id_site AS site_id,
            s.name AS site_name,
            s.city AS site_city,

            (
                SELECT LISTAGG(z.zone_id || ':' || z.letter || ':' || z.color, ',')
                WITHIN GROUP (ORDER BY z.zone_id)
                FROM Zone z
                WHERE z.id_site = s.id_site
            ) AS zone_site,

            mgr.id_person AS manager_id,
            p.firstName AS manager_firstName,
            p.lastName AS manager_lastName,
            p.birthDate AS manager_birthDate,
            p.phoneNumber AS manager_phoneNumber,
            e.registrationCode AS manager_registrationCode,
            e.password AS manager_password
        FROM Machine m
        LEFT JOIN ZoneMachine zm ON m.id_machine = zm.id_machine
        LEFT JOIN Zone z ON zm.zone_id = z.zone_id
        LEFT JOIN Site s ON z.id_site = s.id_site
        LEFT JOIN Manager mgr ON s.id_site = mgr.id_site
        LEFT JOIN Employee e ON mgr.id_person = e.id_person
        LEFT JOIN Person p ON e.id_person = p.id_person
        GROUP BY 
            m.id_machine,
            m.type_,
            m.size_,
            m.state,
            s.id_site,
            s.name,
            s.city,
            mgr.id_person,
            p.firstName,
            p.lastName,
            p.birthDate,
            p.phoneNumber,
            e.registrationCode,
            e.password
    ) LOOP
        p_machines.EXTEND;
        p_machines(p_machines.COUNT) := machine_type(
            rec.id_machine,
            rec.machine_type,
            rec.machine_size,
            rec.machine_state,
            rec.zone_list,
            rec.maintenance_list,
            rec.site_id,
            rec.site_name,
            rec.site_city,
            rec.manager_id,
            rec.manager_firstName,
            rec.manager_lastName,
            rec.manager_birthDate,
            rec.manager_phoneNumber,
            rec.manager_registrationCode,
            rec.manager_password,
            rec.zone_site
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucune machine trouvée.');
END;
/



---------------------------- PROCÉDURES MAINTENANCE ----------------------------

/* ***************************************** *
 *   Procédure pour créer une maintenance    *
 * ***************************************** */
CREATE OR REPLACE PROCEDURE add_maintenance(
    p_maintenance_id IN OUT NUMBER, 
    p_date IN DATE,                
    p_duration IN NUMBER,           
    p_report IN VARCHAR2,          
    p_status IN VARCHAR2,          
    p_manager_id IN NUMBER,         
    p_machine_id IN NUMBER,         
    p_worker_ids IN VARCHAR2       
) AS
BEGIN
    IF p_maintenance_id IS NULL THEN
        SELECT Maintenance_seq.NEXTVAL INTO p_maintenance_id FROM DUAL;
    END IF;

    INSERT INTO Maintenance (id_maintenance, date_maintenance, duration_, report_, status, id_person, id_machine)
    VALUES (p_maintenance_id, p_date, p_duration, p_report, p_status, p_manager_id, p_machine_id);

    FOR worker_rec IN (
        SELECT TO_NUMBER(REGEXP_SUBSTR(p_worker_ids, '[^,]+', 1, LEVEL)) AS worker_id
        FROM DUAL
        CONNECT BY LEVEL <= REGEXP_COUNT(p_worker_ids, ',') + 1
    ) LOOP
        INSERT INTO MaintenanceWorker (id_maintenance, id_person)
        VALUES (p_maintenance_id, worker_rec.worker_id);
    END LOOP;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Les données nécessaires sont introuvables.');
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Tentative d’insertion d’une clé déjà existante.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Problème de conversion ou de format des données.');
    WHEN INVALID_NUMBER THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeur numérique invalide détectée.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plus d''une ligne.');
END;
/

/* ************************************************* *
 *   Procédure pour mettre à jour une maintenance    *
 * ************************************************* */
CREATE OR REPLACE PROCEDURE update_maintenance(
    p_maintenance_id IN INT,
    p_date IN DATE,
    p_duration IN INT,
    p_report IN VARCHAR,
    p_status IN VARCHAR,
    p_machine_id IN INT,
    p_manager_id IN INT,
    p_worker_ids IN VARCHAR
) AS
BEGIN
    UPDATE Maintenance
    SET date_maintenance = p_date,
        duration_ = p_duration,
        report_ = p_report,
        status = p_status,
        id_machine = p_machine_id,
        id_person = p_manager_id
    WHERE id_maintenance = p_maintenance_id;

    DELETE FROM MaintenanceWorker WHERE id_maintenance = p_maintenance_id;

    FOR worker_rec IN (
        SELECT TO_NUMBER(REGEXP_SUBSTR(p_worker_ids, '[^,]+', 1, LEVEL)) AS worker_id
        FROM DUAL
        CONNECT BY LEVEL <= REGEXP_COUNT(p_worker_ids, ',') + 1
    ) LOOP
        INSERT INTO MaintenanceWorker (id_maintenance, id_person)
        VALUES (p_maintenance_id, worker_rec.worker_id);
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Les données nécessaires pour la mise à jour sont introuvables.');
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé unique lors de la mise à jour.');
    WHEN VALUE_ERROR THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Problème de conversion ou de format des données.');
    WHEN INVALID_NUMBER THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Valeur numérique invalide détectée.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plus d''une ligne.');
END;
/

/* ********************************************* *
 *   Procédure pour supprimer une maintenance    *
 * ********************************************* */
CREATE OR REPLACE PROCEDURE delete_maintenance(
    p_maintenance_id IN INT
) AS
BEGIN
    DELETE FROM MaintenanceWorker WHERE id_maintenance = p_maintenance_id;
    DELETE FROM Maintenance WHERE id_maintenance = p_maintenance_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La maintenance avec l''ID spécifié est introuvable.');
    WHEN FOREIGN_KEY_VIOLATION THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Conflit de clé étrangère lors de la suppression.');
END;
/

/* ******************************************* *
 *   Procédure pour trouver une maintenance    *
 * ******************************************* */
 
 CREATE OR REPLACE TYPE maintenance_type IS OBJECT (
    id_maintenance INT,
    maintenance_date DATE,
    maintenance_duration INT,
    maintenance_report VARCHAR2(100),
    maintenance_status VARCHAR2(20),
    machine_id INT,
    machine_type VARCHAR2(100),
    machine_size DECIMAL(15,2),
    machine_state VARCHAR2(100),
    worker_list VARCHAR2(4000),
    manager_id INT,
    manager_firstName VARCHAR2(100),
    manager_lastName VARCHAR2(100),
    manager_birthDate DATE,
    manager_phoneNumber VARCHAR2(20),
    manager_registrationCode VARCHAR2(20),
    manager_password VARCHAR2(100)
);
/

CREATE OR REPLACE TYPE maintenance_table_type IS TABLE OF maintenance_type;
/

/* ***************************************************** *
 *   Procédure pour trouver une maintenance spécifique   *
 * ***************************************************** */
CREATE OR REPLACE PROCEDURE find_maintenance(
    p_maintenance_id IN INT,
    p_maintenances OUT maintenance_table_type
) AS
BEGIN
    p_maintenances := maintenance_table_type(); 

    FOR rec IN (
        SELECT 
            m.id_maintenance,
            m.date_maintenance,
            m.duration_ AS maintenance_duration,
            m.report_ AS maintenance_report, 
            m.status AS maintenance_status, 
            
            ma.id_machine AS machine_id, 
            ma.type_ AS machine_type,  
            ma.size_ AS machine_size, 
            ma.state AS machine_state, 

            (SELECT LISTAGG(
                            w.id_person || ':' || 
                            p.firstName || ':' || 
                            p.lastName || ':' || 
                            p.birthDate || ':' || 
                            p.phoneNumber || ':' || 
                            e.registrationCode || ':' || 
                            e.password, ',' 
                        ) WITHIN GROUP (ORDER BY w.id_person) 
                FROM MaintenanceWorker mw
                JOIN Worker w ON mw.id_person = w.id_person
                JOIN Person p ON w.id_person = p.id_person
                JOIN Employee e ON w.id_person = e.id_person
                WHERE mw.id_maintenance = m.id_maintenance  
            ) AS worker_list,  

            mgr.id_person AS manager_id,
            pers.firstName AS manager_firstName,
            pers.lastName AS manager_lastName,  
            pers.birthDate AS manager_birthDate, 
            pers.phoneNumber AS manager_phoneNumber, 
            emp.registrationCode AS manager_registrationCode,  
            emp.password AS manager_password  

        FROM Maintenance m  
        JOIN Machine ma ON m.id_machine = ma.id_machine  
        LEFT JOIN Manager mgr ON m.id_person = mgr.id_person  
        LEFT JOIN Employee emp ON mgr.id_person = emp.id_person 
        LEFT JOIN Person pers ON emp.id_person = pers.id_person
        WHERE m.id_maintenance = p_maintenance_id  
    ) LOOP
        p_maintenances.EXTEND;
        p_maintenances(p_maintenances.COUNT) := maintenance_type(
            rec.id_maintenance, 
            rec.date_maintenance,  
            rec.maintenance_duration,  
            rec.maintenance_report, 
            rec.maintenance_status,  
            rec.machine_id, 
            rec.machine_type,  
            rec.machine_size, 
            rec.machine_state,  
            rec.worker_list, 
            rec.manager_id,  
            rec.manager_firstName,  
            rec.manager_lastName, 
            rec.manager_birthDate,  
            rec.manager_phoneNumber,  
            rec.manager_registrationCode,  
            rec.manager_password
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La maintenance avec l''ID spécifié est introuvable.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plus d''une ligne pour une maintenance.');
END;
/

/* *************************************************** *
 *   Procédure pour trouver toutes les maintenances    *
 * *************************************************** */
CREATE OR REPLACE PROCEDURE find_all_maintenances(
    p_maintenances OUT maintenance_table_type
) AS
BEGIN
    p_maintenances := maintenance_table_type(); 

    FOR rec IN (
        SELECT 
            m.id_maintenance,
            m.date_maintenance,
            m.duration_ AS maintenance_duration,
            m.report_ AS maintenance_report,
            m.status AS maintenance_status,
            ma.id_machine AS machine_id,
            ma.type_ AS machine_type,
            ma.size_ AS machine_size,
            ma.state AS machine_state,
            (
                SELECT LISTAGG(
                            w.id_person || ':' || 
                            p.firstName || ':' || 
                            p.lastName || ':' || 
                            p.birthDate || ':' || 
                            p.phoneNumber || ':' || 
                            e.registrationCode || ':' || 
                            e.password, ',' 
                        ) WITHIN GROUP (ORDER BY w.id_person)
                FROM MaintenanceWorker mw
                JOIN Worker w ON mw.id_person = w.id_person
                JOIN Person p ON w.id_person = p.id_person
                JOIN Employee e ON w.id_person = e.id_person
                WHERE mw.id_maintenance = m.id_maintenance
            ) AS worker_list,
            mgr.id_person AS manager_id,
            pers.firstName AS manager_firstName,
            pers.lastName AS manager_lastName,  
            pers.birthDate AS manager_birthDate,
            pers.phoneNumber AS manager_phoneNumber, 
            emp.registrationCode AS manager_registrationCode,
            emp.password AS manager_password
        FROM Maintenance m
        JOIN Machine ma ON m.id_machine = ma.id_machine
        LEFT JOIN Manager mgr ON m.id_person = mgr.id_person
        LEFT JOIN Employee emp ON mgr.id_person = emp.id_person
        LEFT JOIN Person pers ON emp.id_person = pers.id_person
    ) LOOP
        p_maintenances.EXTEND;
        p_maintenances(p_maintenances.COUNT) := maintenance_type(
            rec.id_maintenance,
            rec.date_maintenance,
            rec.maintenance_duration,
            rec.maintenance_report,
            rec.maintenance_status,
            rec.machine_id,
            rec.machine_type,
            rec.machine_size,
            rec.machine_state,
            rec.worker_list,
            rec.manager_id,
            rec.manager_firstName,
            rec.manager_lastName,
            rec.manager_birthDate,
            rec.manager_phoneNumber,
            rec.manager_registrationCode,
            rec.manager_password
        );
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : Aucune maintenance trouvée.');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Erreur : La requête a retourné plus d''une ligne.');
END;
/



----------------------------- INSERTION DES DONNÉES ------------------------------

-- Suppression des données dans l'ordre inverse des dépendances
BEGIN
    EXECUTE IMMEDIATE 'DELETE FROM MaintenanceWorker';
    EXECUTE IMMEDIATE 'DELETE FROM ZoneMachine';
    EXECUTE IMMEDIATE 'DELETE FROM Maintenance';
    EXECUTE IMMEDIATE 'DELETE FROM Worker';
    EXECUTE IMMEDIATE 'DELETE FROM Manager';
    EXECUTE IMMEDIATE 'DELETE FROM Purchaser';
    EXECUTE IMMEDIATE 'DELETE FROM Zone';
    EXECUTE IMMEDIATE 'DELETE FROM Site';
    EXECUTE IMMEDIATE 'DELETE FROM Machine';
    EXECUTE IMMEDIATE 'DELETE FROM Employee';
    EXECUTE IMMEDIATE 'DELETE FROM Person';
END;
/

-- Réinitialisation des séquences
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE Person_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Site_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Machine_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Maintenance_seq';
    EXECUTE IMMEDIATE 'DROP SEQUENCE Zone_seq';

    EXECUTE IMMEDIATE 'CREATE SEQUENCE Person_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Site_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Machine_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Maintenance_seq START WITH 1 INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE 'CREATE SEQUENCE Zone_seq START WITH 1 INCREMENT BY 1 NOCACHE';
END;
/
-- Insertion des sites
BEGIN
    FOR site_data IN (
        SELECT 'TechPark' AS name, 'Paris' AS city FROM DUAL UNION ALL
        SELECT 'Industrial Hub', 'London' FROM DUAL UNION ALL
        SELECT 'Manufacturing', 'Berlin' FROM DUAL
    ) LOOP
        BEGIN
            INSERT INTO Site (id_site, name, city)
            VALUES (Site_seq.NEXTVAL, site_data.name, site_data.city);
        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Un site avec ce nom ou cette ville existe déjà.');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Valeur incorrecte lors de l''insertion d''un site.');
        END;
    END LOOP;
END;
/

-- Insertion des zones (4 zones par site)
BEGIN
    FOR site_id IN 1..3 LOOP
        FOR zone_data IN (
            SELECT 'A' AS letter, 'GREEN' AS color FROM DUAL UNION ALL
            SELECT 'B', 'ORANGE' FROM DUAL UNION ALL
            SELECT 'C', 'RED' FROM DUAL UNION ALL
            SELECT 'D', 'BLACK' FROM DUAL
        ) LOOP
            BEGIN
                INSERT INTO Zone (zone_id, letter, color, id_site)
                VALUES (Zone_seq.NEXTVAL, zone_data.letter, zone_data.color, site_id);
            EXCEPTION
                WHEN DUP_VAL_ON_INDEX THEN
                    DBMS_OUTPUT.PUT_LINE('Erreur : Une zone avec cet ID ou ces caractéristiques existe déjà.');
                WHEN VALUE_ERROR THEN
                    DBMS_OUTPUT.PUT_LINE('Erreur : Valeur incorrecte lors de l''insertion d''une zone.');
            END;
        END LOOP;
    END LOOP;
END;
/

-- Insertion des managers
BEGIN
    FOR manager_data IN (
        SELECT 'Alice' AS firstName, 'Martin' AS lastName, '1985-03-01' AS birthDate, '111-111-1111' AS phoneNumber, 
               'MGR01' AS registrationCode, 'password1' AS password_, 1 AS id_site FROM DUAL UNION ALL
        SELECT 'James', 'Smith', '1978-09-15', '222-222-2222', 'MGR02', 'password2', 2 FROM DUAL UNION ALL
        SELECT 'Clara', 'Johnson', '1980-06-10', '333-333-3333', 'MGR03', 'password3', 3 FROM DUAL
    ) LOOP
        BEGIN
            -- Insertion dans la table Person
            INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
            VALUES (Person_seq.NEXTVAL, manager_data.firstName, manager_data.lastName, TO_DATE(manager_data.birthDate, 'YYYY-MM-DD'), manager_data.phoneNumber);

            -- Insertion dans la table Employee
            INSERT INTO Employee (id_person, registrationCode, password)
            VALUES (Person_seq.CURRVAL, manager_data.registrationCode, crypto.crypt(manager_data.password_));

            -- Insertion dans la table Manager
            INSERT INTO Manager (id_person, id_site)
            VALUES (Person_seq.CURRVAL, manager_data.id_site);
        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Ce manager existe déjà (ID ou code d''enregistrement).');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Valeur incorrecte pour l''insertion d''un manager.');
        END;
    END LOOP;
END;
/

-- Insertion des workers pour tous les sites
BEGIN
    FOR worker_data IN (
        -- Workers pour le site 1
        SELECT 'Emma' AS firstName, 'Davis' AS lastName, '1990-01-01' AS birthDate, '444-444-4444' AS phoneNumber, 
               'WRK01' AS registrationCode, 'password4' AS password_, 1 AS id_site FROM DUAL UNION ALL
        SELECT 'Liam', 'Clark', '1991-05-20', '555-555-5555', 'WRK02', 'password5', 1 FROM DUAL UNION ALL
        SELECT 'Sophia', 'Jones', '1992-11-03', '666-666-6666', 'WRK03', 'password6', 1 FROM DUAL UNION ALL
        SELECT 'Noah', 'White', '1989-07-15', '777-777-7777', 'WRK04', 'password7', 1 FROM DUAL UNION ALL
        SELECT 'Olivia', 'Black', '1994-09-25', '888-888-8888', 'WRK05', 'password8', 1 FROM DUAL UNION ALL
        -- Workers pour le site 2
        SELECT 'William', 'Taylor', '1985-06-15', '555-222-1111', 'WRK06', 'password12', 2 FROM DUAL UNION ALL
        SELECT 'Charlotte', 'Harris', '1990-08-10', '555-333-2222', 'WRK07', 'password13', 2 FROM DUAL UNION ALL
        SELECT 'Ethan', 'Brown', '1992-12-25', '555-444-3333', 'WRK08', 'password14', 2 FROM DUAL UNION ALL
        SELECT 'Amelia', 'Wilson', '1988-03-14', '555-555-4444', 'WRK09', 'password15', 2 FROM DUAL UNION ALL
        SELECT 'Mason', 'Thomas', '1995-09-05', '555-666-5555', 'WRK10', 'password16', 2 FROM DUAL UNION ALL
        -- Workers pour le site 3
        SELECT 'Logan', 'Anderson', '1983-11-22', '555-777-6666', 'WRK11', 'password17', 3 FROM DUAL UNION ALL
        SELECT 'Mia', 'Hall', '1991-07-19', '555-888-7777', 'WRK12', 'password18', 3 FROM DUAL UNION ALL
        SELECT 'Harper', 'Adams', '1993-04-28', '555-999-8888', 'WRK13', 'password19', 3 FROM DUAL UNION ALL
        SELECT 'Elijah', 'Mitchell', '1994-10-12', '555-111-9999', 'WRK14', 'password20', 3 FROM DUAL UNION ALL
        SELECT 'Ella', 'Perez', '1989-06-03', '555-222-1112', 'WRK15', 'password21', 3 FROM DUAL
    ) LOOP
        BEGIN
            INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
            VALUES (Person_seq.NEXTVAL, worker_data.firstName, worker_data.lastName, TO_DATE(worker_data.birthDate, 'YYYY-MM-DD'), worker_data.phoneNumber);

            INSERT INTO Employee (id_person, registrationCode, password)
            VALUES (Person_seq.CURRVAL, worker_data.registrationCode, crypto.crypt(worker_data.password_));

            INSERT INTO Worker (id_person, id_site)
            VALUES (Person_seq.CURRVAL, worker_data.id_site);
        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Ce worker existe déjà (ID ou code d''enregistrement).');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Valeur incorrecte pour l''insertion d''un worker.');
        END;
    END LOOP;
END;
/

-- Insertion des purchasers
BEGIN
    FOR purchaser_data IN (
        SELECT 'Oliver' AS firstName, 'Brown' AS lastName, '1985-05-05' AS birthDate, '555-555-5555' AS phoneNumber, 
               'PRC01' AS registrationCode, 'password9' AS password_ FROM DUAL UNION ALL
        SELECT 'Isabella', 'Green', '1987-03-18', '999-999-9999', 'PRC02', 'password10' FROM DUAL UNION ALL
        SELECT 'Ethan', 'Moore', '1988-12-07', '101-101-1010', 'PRC03', 'password11' FROM DUAL
    ) LOOP
        BEGIN
            -- Insertion dans la table Person
            INSERT INTO Person (id_person, firstName, lastName, birthDate, phoneNumber)
            VALUES (Person_seq.NEXTVAL, purchaser_data.firstName, purchaser_data.lastName, TO_DATE(purchaser_data.birthDate, 'YYYY-MM-DD'), purchaser_data.phoneNumber);

            -- Insertion dans la table Employee
            INSERT INTO Employee (id_person, registrationCode, password)
            VALUES (Person_seq.CURRVAL, purchaser_data.registrationCode, crypto.crypt(purchaser_data.password_));

            -- Insertion dans la table Purchaser
            INSERT INTO Purchaser (id_person)
            VALUES (Person_seq.CURRVAL);

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Un purchaser avec cet ID, numéro de téléphone ou code d''enregistrement existe déjà.');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Une valeur incorrecte a été détectée pour ' || purchaser_data.firstName || ' ' || purchaser_data.lastName || '.');
        END;
    END LOOP;
END;
/

-- Insertion des machines et zones associées
BEGIN
    FOR machine_data IN (
        -- Machines pour le site 1
        SELECT 'FABRICATION' AS type_, 15 AS size_, 'OPERATIONAL' AS state, 1 AS zone1, NULL AS zone2, NULL AS zone3 FROM DUAL UNION ALL
        SELECT 'ASSEMBLY', 30, 'NEEDS_MAINTENANCE', 2, 3, NULL FROM DUAL UNION ALL
        SELECT 'SORTING', 45, 'OPERATIONAL', 3, 4, 1 FROM DUAL UNION ALL
        SELECT 'FABRICATION', 10, 'NEEDS_MAINTENANCE', 4, NULL, NULL FROM DUAL UNION ALL
        SELECT 'ASSEMBLY', 20, 'OPERATIONAL', 1, 2, NULL FROM DUAL UNION ALL
        -- Machines pour le site 2
        SELECT 'FABRICATION', 25, 'NEEDS_MAINTENANCE', 5, NULL, NULL FROM DUAL UNION ALL
        SELECT 'ASSEMBLY', 35, 'OPERATIONAL', 6, 7, NULL FROM DUAL UNION ALL
        SELECT 'SORTING', 50, 'NEEDS_MAINTENANCE', 8, 5, 6 FROM DUAL UNION ALL
        SELECT 'FABRICATION', 15, 'OPERATIONAL', 7, NULL, NULL FROM DUAL UNION ALL
        SELECT 'ASSEMBLY', 40, 'NEEDS_MAINTENANCE', 6, 8, NULL FROM DUAL UNION ALL
        -- Machines pour le site 3
        SELECT 'SORTING', 20, 'NEEDS_MAINTENANCE', 9, NULL, NULL FROM DUAL UNION ALL
        SELECT 'FABRICATION', 50, 'OPERATIONAL', 10, 11, NULL FROM DUAL UNION ALL
        SELECT 'ASSEMBLY', 60, 'OPERATIONAL', 12, 9, 10 FROM DUAL UNION ALL
        SELECT 'SORTING', 15, 'NEEDS_MAINTENANCE', 11, NULL, NULL FROM DUAL UNION ALL
        SELECT 'FABRICATION', 35, 'OPERATIONAL', 10, 12, NULL FROM DUAL
    ) LOOP
        BEGIN
            -- Insertion dans la table Machine
            INSERT INTO Machine (id_machine, type_, size_, state)
            VALUES (Machine_seq.NEXTVAL, machine_data.type_, machine_data.size_, machine_data.state);

            -- Insertion dans la table ZoneMachine pour chaque zone associée
            BEGIN
                IF machine_data.zone1 IS NOT NULL THEN
                    INSERT INTO ZoneMachine (id_machine, zone_id)
                    VALUES (Machine_seq.CURRVAL, machine_data.zone1);
                END IF;
            EXCEPTION
                WHEN DUP_VAL_ON_INDEX THEN
                    DBMS_OUTPUT.PUT_LINE('Erreur : Une association machine-zone existe déjà pour Zone 1.');
            END;

            BEGIN
                IF machine_data.zone2 IS NOT NULL THEN
                    INSERT INTO ZoneMachine (id_machine, zone_id)
                    VALUES (Machine_seq.CURRVAL, machine_data.zone2);
                END IF;
            EXCEPTION
                WHEN DUP_VAL_ON_INDEX THEN
                    DBMS_OUTPUT.PUT_LINE('Erreur : Une association machine-zone existe déjà pour Zone 2.');
            END;

            BEGIN
                IF machine_data.zone3 IS NOT NULL THEN
                    INSERT INTO ZoneMachine (id_machine, zone_id)
                    VALUES (Machine_seq.CURRVAL, machine_data.zone3);
                END IF;
            EXCEPTION
                WHEN DUP_VAL_ON_INDEX THEN
                    DBMS_OUTPUT.PUT_LINE('Erreur : Une association machine-zone existe déjà pour Zone 3.');
            END;

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Une machine avec ce type ou ces caractéristiques existe déjà.');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Une valeur incorrecte a été fournie pour une machine.');
        END;
    END LOOP;
END;
/

-- Insertion des maintenances pour toutes les machines
BEGIN
    FOR maintenance_data IN (
        -- Maintenances pour le site 1
        SELECT TO_DATE('2023-12-01', 'YYYY-MM-DD') AS date_maintenance, 4 AS duration_, 'Replaced motor' AS report_,
               'COMPLETED' AS status, 1 AS id_person, 1 AS id_machine FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-01', 'YYYY-MM-DD'), 3, 'Updated software', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-15', 'YYYY-MM-DD'), 5, 'Checked alignment', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-01', 'YYYY-MM-DD'), 3, 'Inspected motor bearings', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-15', 'YYYY-MM-DD'), 4, 'Replaced cooling fans', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-04-01', 'YYYY-MM-DD'), 6, 'Lubricated components', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-05-01', 'YYYY-MM-DD'), 5, 'Firmware update', 'COMPLETED', 1, 1 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 2)
        SELECT TO_DATE('2023-11-20', 'YYYY-MM-DD'), 6, 'Replaced hydraulic system', 'COMPLETED', 1, 2 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-10', 'YYYY-MM-DD'), 4, 'Calibrated sensors', 'COMPLETED', 1, 2 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-01', 'YYYY-MM-DD'), 5, 'Lubricated moving parts', 'IN_PROGRESS', 1, 2 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 3)
        SELECT TO_DATE('2023-12-10', 'YYYY-MM-DD'), 3, 'Cleaned conveyor', 'COMPLETED', 1, 3 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-15', 'YYYY-MM-DD'), 4, 'Updated firmware', 'COMPLETED', 1, 3 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-20', 'YYYY-MM-DD'), 6, 'Replaced worn cables', 'COMPLETED', 1, 3 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 4)
        SELECT TO_DATE('2024-01-01', 'YYYY-MM-DD'), 4, 'Replaced worn components', 'WAITING', 1, 4 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 5)
        SELECT TO_DATE('2023-12-10', 'YYYY-MM-DD'), 3, 'Cleaned filter', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-10', 'YYYY-MM-DD'), 5, 'Lubricated conveyor', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-05', 'YYYY-MM-DD'), 4, 'Inspected motor alignment', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-05', 'YYYY-MM-DD'), 6, 'Replaced conveyor belt', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-25', 'YYYY-MM-DD'), 3, 'Calibrated sensors', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-04-10', 'YYYY-MM-DD'), 4, 'Checked power supply', 'COMPLETED', 1, 5 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 6)
        SELECT TO_DATE('2023-10-01', 'YYYY-MM-DD'), 5, 'Repaired motor', 'COMPLETED', 2, 6 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-05', 'YYYY-MM-DD'), 3, 'Updated control software', 'WAITING', 2, 6 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-01', 'YYYY-MM-DD'), 6, 'Calibrated hydraulic system', 'COMPLETED', 2, 6 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 7)
        SELECT TO_DATE('2023-10-20', 'YYYY-MM-DD'), 3, 'Repaired gearbox', 'COMPLETED', 2, 7 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-01', 'YYYY-MM-DD'), 4, 'Lubricated components', 'COMPLETED', 2, 7 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-01', 'YYYY-MM-DD'), 6, 'Updated software', 'COMPLETED', 2, 7 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-10', 'YYYY-MM-DD'), 5, 'Cleaned cooling system', 'COMPLETED', 2, 7 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-04-01', 'YYYY-MM-DD'), 3, 'Replaced oil filter', 'COMPLETED', 2, 7 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-05-01', 'YYYY-MM-DD'), 4, 'Checked connections', 'COMPLETED', 3, 7 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 8)
        SELECT TO_DATE('2024-03-01', 'YYYY-MM-DD'), 4, 'Replaced worn-out parts', 'IN_PROGRESS', 2, 8 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 10)
        SELECT TO_DATE('2023-11-01', 'YYYY-MM-DD'), 3, 'Checked hydraulic system', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2023-12-01', 'YYYY-MM-DD'), 4, 'Updated firmware', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-01', 'YYYY-MM-DD'), 5, 'Calibrated sensors', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-01', 'YYYY-MM-DD'), 6, 'Replaced cooling fans', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-01', 'YYYY-MM-DD'), 3, 'Inspected conveyor belt', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-04-01', 'YYYY-MM-DD'), 4, 'Lubricated components', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-05-01', 'YYYY-MM-DD'), 5, 'Replaced worn cables', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-06-01', 'YYYY-MM-DD'), 6, 'Tested power supply', 'COMPLETED', 2, 10 FROM DUAL UNION ALL
        -- Maintenances pour Machine (id_machine = 15)
        SELECT TO_DATE('2023-12-01', 'YYYY-MM-DD'), 4, 'Cleaned fans', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-01-01', 'YYYY-MM-DD'), 5, 'Replaced filters', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-02-01', 'YYYY-MM-DD'), 6, 'Calibrated motor', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-03-01', 'YYYY-MM-DD'), 4, 'Updated firmware', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-04-01', 'YYYY-MM-DD'), 5, 'Lubricated components', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-05-01', 'YYYY-MM-DD'), 4, 'Replaced cooling fans', 'COMPLETED', 3, 15 FROM DUAL UNION ALL
        SELECT TO_DATE('2024-06-01', 'YYYY-MM-DD'), 6, 'Checked safety features', 'COMPLETED', 3, 15 FROM DUAL
    ) LOOP
        BEGIN
            -- Insertion dans la table Maintenance
            INSERT INTO Maintenance (id_maintenance, date_maintenance, duration_, report_, status, id_person, id_machine)
            VALUES (Maintenance_seq.NEXTVAL, maintenance_data.date_maintenance, maintenance_data.duration_, maintenance_data.report_,
                    maintenance_data.status, maintenance_data.id_person, maintenance_data.id_machine);

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Une maintenance avec cet ID existe déjà.');
            WHEN VALUE_ERROR THEN
                DBMS_OUTPUT.PUT_LINE('Erreur : Valeurs incorrectes détectées pour la maintenance de la machine ' || maintenance_data.id_machine || '.');
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Erreur inattendue lors de l''insertion de la maintenance : ' || SQLERRM);
        END;
    END LOOP;
END;
/


BEGIN
   FOR i IN 1..20 LOOP -- Les maintenances des machines du site 1
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 4); -- Ouvrier 1
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 5); -- Ouvrier 2
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 6); -- Ouvrier 3
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 7); -- Ouvrier 4
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 8); -- Ouvrier 5
   END LOOP;
END;
/

-- Maintenances pour les machines du site 2 (id_machine de 6 à 10)
BEGIN
   FOR i IN 21..38 LOOP -- Les maintenances des machines du site 2
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 9);  -- Ouvrier 1
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 10); -- Ouvrier 2
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 11); -- Ouvrier 3
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 12); -- Ouvrier 4
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 13); -- Ouvrier 5
   END LOOP;
END;
/

-- Maintenances pour les machines du site 3 (id_machine de 11 à 15)
BEGIN
   FOR i IN 39..55 LOOP -- Les maintenances des machines du site 3
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 14); -- Ouvrier 1
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 15); -- Ouvrier 2
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 16); -- Ouvrier 3
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 17); -- Ouvrier 4
      INSERT INTO MaintenanceWorker (id_maintenance, id_person) VALUES (i, 18); -- Ouvrier 5
   END LOOP;
END;
/

COMMIT;