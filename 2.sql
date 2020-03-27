
DROP TABLE student_studijni_program;
DROP TABLE  predmet_student;
DROP  TABLE okruh_predmetu_predmet;
DROP  TABLE obor_okruh_predmetu;
DROP TABLE doktorand_predmet;
DROP TABLE okruh_predmetu;
DROP TABLE obor;
DROP TABLE predmet;
DROP TABLE studijni_program;
DROP TABLE doktorand;
DROP TABLE student;




CREATE TABLE studijni_program (
    id_programu varchar(20) NOT NULL PRIMARY KEY,
    fakulta varchar(255) NOT NULL,
    delka int NOT NULL,
    forma_studia varchar(11) NOT NULL CHECK(forma_studia IN ('Prezenční','Dálkové'))
);

CREATE TABLE obor (
    id_oboru varchar(10) NOT NULL PRIMARY KEY,
    nazev_oboru varchar(255) NOT NULL,
    akreditace_do date NOT NULL ,
    id_programu varchar(20) NOT NULL, --má
    constraint studijni_program_obor_fk
                FOREIGN KEY (id_programu) REFERENCES studijni_program (id_programu)
                ON DELETE SET NULL
);

CREATE TABLE okruh_predmetu(
    id_okruhu varchar(3) NOT NULL CHECK(id_okruhu IN ('P','PVT','V')),
    semestr varchar(6) NOT NULL CHECK(semestr IN ('Zimní','Letní')),
    id_oboru varchar(10) NOT NULL,
    CONSTRAINT id_oboru_fk
        FOREIGN KEY (id_oboru) REFERENCES obor (id_oboru),
    CONSTRAINT okruh_predmetu_pk
        PRIMARY KEY (id_okruhu,semestr,id_oboru),
    min_kreditu int DEFAULT 0
);


CREATE TABLE obor_okruh_predmetu ( --nabizí
	id_oboru varchar(10) NOT NULL,
	id_okruhu varchar(3) NOT NULL,
	semestr varchar(6) NOT NULL,
	CONSTRAINT obor_okruh_predmetu_pk
		    PRIMARY KEY (id_oboru, id_okruhu,semestr),
	CONSTRAINT obor_okruh_predmetu_obor_fk
		    FOREIGN KEY (id_oboru) REFERENCES obor (id_oboru)
		    ON DELETE CASCADE,
	CONSTRAINT obor_okruh_predmetu_okruh_fk
		    FOREIGN KEY (id_okruhu,semestr,id_oboru) REFERENCES okruh_predmetu (id_okruhu, semestr,id_oboru)
		    ON DELETE CASCADE
);

CREATE TABLE predmet(
    id_predmetu varchar(3) NOT NULL PRIMARY KEY,
    nazev varchar(255) NOT NULL,
    kreditovy_obnos int NOT NULL,
    zpusob_zakonceni varchar(21) NOT NULL CHECK(zpusob_zakonceni IN ('Zápočet','Klasifikovaný zápočet','Zkouška')),
    zapocet varchar(5) NOT NULL CHECK (zapocet IN ('ANO','NE')),
    garant varchar(80)
);

CREATE TABLE okruh_predmetu_predmet ( --zahrnuje
    id_okruhu varchar(3) NOT NULL,
    semestr varchar(6) NOT NULL,
    id_predmetu varchar(3) NOT NULL,
    id_oboru varchar(20) NOT NULL,
    CONSTRAINT okruh_predmetu_predmet_pk
            PRIMARY KEY (id_okruhu,semestr,id_predmetu),
    CONSTRAINT okruh_predmetu_predmet_okruh_fk
            FOREIGN KEY (id_okruhu,semestr,id_oboru) REFERENCES okruh_predmetu (id_okruhu, semestr,id_oboru)
            ON DELETE CASCADE,
    CONSTRAINT okruh_predmetu_predmet_predmet_fk
            FOREIGN KEY (id_predmetu) REFERENCES predmet (id_predmetu)
            ON DELETE CASCADE
);

CREATE TABLE student(
    login varchar(8) NOT NULL PRIMARY KEY CHECK(REGEXP_LIKE(login, 'x[a-z]{5}[0-9]{2}')),
    jmeno varchar(80) NOT NULL,
    primjmeni varchar(80) NOT NULL,
    adresa_ulice varchar(80) NOT NULL,
    adresa_ulice_popisne int NOT NULL,
    adresa_mesto varchar(80) NOT NULL,
    adresa_psc int NOT NULL CHECK (REGEXP_LIKE(adresa_psc,'[0-9]{5}')),
    adresa_stat varchar(80) NOT NULL,
    datum_nar DATE NOT NULL,
    pohlavi varchar(4) NOT NULL CHECK ( pohlavi in ('Muž','Žena'))
);

CREATE TABLE doktorand(
    login varchar(8) NOT NULL CHECK(REGEXP_LIKE(login, 'x[a-z]{5}[0-9]{2}')),
    CONSTRAINT doktorand_fk
        FOREIGN KEY (login) REFERENCES student (login)
        ON DELETE CASCADE,
    CONSTRAINT doktorand_pk
        PRIMARY KEY (login),
    titul varchar(20)
);


CREATE TABLE predmet_student ( --registruje
    login varchar(8),
    id_predmetu varchar(3) NOT NULL,
    CONSTRAINT predmet_student_pk
            PRIMARY KEY (login,id_predmetu),
    CONSTRAINT predmet_student_predmet_fk
            FOREIGN KEY (id_predmetu) REFERENCES predmet (id_predmetu)
            ON DELETE CASCADE,
    CONSTRAINT predmet_student_student_fk
            FOREIGN KEY (login) REFERENCES student (login)
            ON DELETE CASCADE
);

CREATE TABLE student_studijni_program ( --studuje
    login varchar(8),
    id_programu varchar(20) NOT NULL,
    CONSTRAINT student_studijni_program_pk
        PRIMARY KEY (login,id_programu),
    CONSTRAINT student_studijni_program_student_fk
        FOREIGN KEY (login) REFERENCES student (login)
        ON DELETE CASCADE,
    CONSTRAINT student_studijni_program_studijni_program_fk
        FOREIGN KEY (id_programu) REFERENCES studijni_program (id_programu)
        ON DELETE CASCADE
);

CREATE TABLE doktorand_predmet ( --podili se
    login varchar(8),
    id_predmetu varchar(3),
    CONSTRAINT doktorand_predmet_pk
        PRIMARY KEY (login,id_predmetu),
    CONSTRAINT doktorand_predmet_doktorand_fk
        FOREIGN KEY (login) REFERENCES doktorand (login)
        ON DELETE CASCADE,
     CONSTRAINT doktorand_predmet_predmet_fk
        FOREIGN KEY (id_predmetu) REFERENCES predmet (id_predmetu)
        ON DELETE CASCADE
);

INSERT INTO  studijni_program (id_programu, fakulta, delka, forma_studia)
VALUES ('IT-BC-3','Fakulta Informačních technologií',3,'Prezenční');
INSERT INTO  studijni_program (id_programu, fakulta, delka, forma_studia)
VALUES ('IT-MGR-2','Fakulta Informačních technologií',2,'Prezenční');
INSERT INTO  studijni_program (id_programu, fakulta, delka, forma_studia)
VALUES ('MITAI','Fakulta Informačních technologií',2,'Prezenční');
INSERT INTO  studijni_program (id_programu, fakulta, delka, forma_studia)
VALUES ('VTI-DR-4','Fakulta Informačních technologií',4,'Prezenční');

INSERT INTO obor(id_oboru, nazev_oboru, akreditace_do, id_programu)
VALUES ('BIT','Informační technologie',TO_DATE('2025', 'yyyy'),'IT-BC-3');
INSERT INTO obor(id_oboru, nazev_oboru, akreditace_do, id_programu)
VALUES ('NBIO','Bioinformatika a biocomputing',TO_DATE('2029', 'yyyy'),'MITAI');
INSERT INTO obor(id_oboru, nazev_oboru, akreditace_do, id_programu)
VALUES ('DVI4','Výpočetní technika a informatika',TO_DATE('2024', 'yyyy'),'VTI-DR-4');

INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('PVT','Letní','BIT',5);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('P','Letní','BIT',15);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('V','Letní','BIT',0);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('PVT','Zimní','BIT',5);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('P','Zimní','BIT',15);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('V','Zimní','BIT',0);


INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IAL','Algoritmy',5,'Zkouška','ANO',NULL);

INSERT INTO student(login, jmeno, primjmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xhrani02','Jan','Hranický','Josefa Kotase',33,'Ostrava',70030,'České republika',TO_DATE('1999-02-21', 'yyyy/mm/dd'),'Muž');

INSERT INTO student(login, jmeno, primjmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xrando00','Random','Mák','Random Ulice',33,'Ostrava',70030,'České republika',TO_DATE('1999-02-21', 'yyyy/mm/dd'),'Muž');

INSERT INTO doktorand(login, titul)
VALUES ('xrando00','bc.');

INSERT INTO doktorand_predmet(login, id_predmetu)
VALUES ('xrando00','IAL');

INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhrani02','IAL');

INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xhrani02','IT-BC-3');

INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','IAL','BIT');

INSERT INTO obor_okruh_predmetu(id_oboru, id_okruhu, semestr)
VALUES ('BIT','P','Zimní');

SELECT * FROM student_studijni_program;
SELECT * FROM  predmet_student;
SELECT * FROM okruh_predmetu_predmet;
SELECT * FROM obor_okruh_predmetu;
SELECT * FROM doktorand_predmet;
SELECT * FROM okruh_predmetu;
SELECT * FROM obor;
SELECT * FROM predmet;
SELECT * FROM studijni_program;
SELECT * FROM doktorand;
SELECT * FROM student;
