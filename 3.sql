--SMAZANI TABULEK--
DROP TABLE student_studijni_program;
DROP TABLE  predmet_student;
DROP TABLE okruh_predmetu_predmet;
DROP TABLE doktorand_predmet;
DROP TABLE okruh_predmetu;
DROP TABLE obor;
DROP TABLE predmet;
DROP TABLE studijni_program;
DROP TABLE doktorand;
DROP TABLE student;

--VYTVORENI TABULEK--
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
    login varchar(8) NOT NULL PRIMARY KEY CHECK(REGEXP_LIKE(login, 'x[a-z]{5}[0-9,a-z]{2}')),
    jmeno varchar(80) NOT NULL,
    prijmeni varchar(80) NOT NULL,
    adresa_ulice varchar(80) NOT NULL,
    adresa_ulice_popisne int NOT NULL,
    adresa_mesto varchar(80) NOT NULL,
    adresa_psc int NOT NULL CHECK (REGEXP_LIKE(adresa_psc,'[0-9]{5}')),
    adresa_stat varchar(80) NOT NULL,
    datum_nar DATE NOT NULL,
    pohlavi varchar(5) NOT NULL CHECK ( pohlavi in ('Muž','Žena'))
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
    login varchar(8) NOT NULL,
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
    login varchar(8) NOT NULL,
    id_predmetu varchar(3) NOT NULL,
    CONSTRAINT doktorand_predmet_pk
        PRIMARY KEY (login,id_predmetu),
    CONSTRAINT doktorand_predmet_doktorand_fk
        FOREIGN KEY (login) REFERENCES doktorand (login)
        ON DELETE CASCADE,
     CONSTRAINT doktorand_predmet_predmet_fk
        FOREIGN KEY (id_predmetu) REFERENCES predmet (id_predmetu)
        ON DELETE CASCADE
);


--NAPLNENI TABULEK--

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
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('P','Zimní','NBIO',15);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('V','Zimní','NBIO',0);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('P','Letní','NBIO',15);
INSERT INTO okruh_predmetu(id_okruhu, semestr, id_oboru, min_kreditu)
VALUES ('V','Letní','NBIO',0);

INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('TST','TestPredmet',5,'Zkouška','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IAL','Algoritmy',5,'Zkouška','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('ISS','Signály a systémy',6,'Zkouška','NE',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('HVR','Manažerské vedení lidí',3,'Zápočet','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IJA','Seminář Java',4,'Zápočet','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('ITW','Tvorba webových stránek',5,'Zápočet','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IZU','Základy uměnlé inteligence',4,'Zkouška','ANO',NULL);
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('AVS','Architektury výpočetních systémů',5,'Zkouška','ANO','doc. Ing. Jiří Jaroš, Ph.D.');
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('BIF','	Bioinformatika',5,'Zkouška','NE','Ing. Tomáš Martínek, Ph.D.');
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('PBI','Pokročilá bioinformatika',4,'Zkouška','NE','Ing. Matej Lexa, Ph.D.');
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IW1','Desktop systémy Microsoft Windows',5,'Zkouška','ANO','Ing. Igor Hnízdo, Ph.D.');
INSERT INTO predmet(id_predmetu, nazev, kreditovy_obnos, zpusob_zakonceni, zapocet, garant)
VALUES ('IPZ','Periferní zařízení',3,'Zkouška','NE','Ing. Ivo Perifer, Ph.D.');

--okruhy predmetu
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','IAL','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','ISS','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('V','Zimní','HVR','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('PVT','Letní','IJA','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('V','Letní','ITW','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Letní','IZU','BIT');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','AVS','NBIO');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','BIF','NBIO');
INSERT INTO okruh_predmetu_predmet(id_okruhu, semestr, id_predmetu, id_oboru)
VALUES ('P','Zimní','PBI','NBIO');

--studenti
INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xhrani02','Jan','Hranický','Josefa Kotase',33,'Ostrava',70030,'České republika',TO_DATE('1999-02-21', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xhrani02','IT-BC-3');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhrani02','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhrani02','IJA');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhrani02','IZU');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhrani02','IAL');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xsvobo1t','Jonáš','Svoboda','Na Rybníčku',8,'Opava',74601,'České republika',TO_DATE('1999-08-08', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xsvobo1t','IT-BC-3');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xsvobo1t','IAL');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xsvobo1t','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xsvobo1t','HVR');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xsvobo1t','IJA');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xsvobo1t','IZU');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xradko01','Radek','Radkovič','Palackého náměstí',42,'Praha',19000,'České republika',TO_DATE('1999-01-18', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xradko01','IT-BC-3');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xradko01','IAL');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xradko01','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xradko01','HVR');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xradko01','IJA');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xradko01','ITW');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xuhran00','Xénie','Uhrančivá','Dolní náměstí',18,'Opava',74601,'České republika',TO_DATE('1999-05-05', 'yyyy/mm/dd'),'Žena');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xuhran00','IT-BC-3');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xuhran00','IAL');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xuhran00','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xuhran00','IZU');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xstrud02','Greta','Štrůdlová','Frýdko-místecká ulice',10,'Frýdek Místek',74800,'České republika',TO_DATE('1998-12-24', 'yyyy/mm/dd'),'Žena');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xstrud02','IT-BC-3');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xstrud02','IAL');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xstrud02','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xstrud02','IZU');

--doktorandi

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xnezve00','Tereza','Nezvěstná','Božetěchova',42,'Brno',61200,'České republika',TO_DATE('1992-12-12', 'yyyy/mm/dd'),'Žena');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xnezve00','MITAI');
INSERT INTO doktorand(login, titul)
VALUES ('xnezve00','ing.');
INSERT INTO doktorand_predmet(login, id_predmetu)
VALUES ('xnezve00','ISS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xnezve00','BIF');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xnezve00','AVS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xnezve00','PBI');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xhonnn00','Josef','Hon','Božetěchova',42,'Brno',61200,'České republika',TO_DATE('1992-05-07', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xhonnn00','MITAI');
INSERT INTO doktorand(login, titul)
VALUES ('xhonnn00','ing.');
INSERT INTO doktorand_predmet(login, id_predmetu)
VALUES ('xhonnn00','ITW');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhonnn00','AVS');
INSERT INTO predmet_student(login, id_predmetu)
VALUES ('xhonnn00','PBI');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xmarti00','Filip','Martiček','Božetěchova',42,'Brno',61200,'České republika',TO_DATE('1993-01-01', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xmarti00','MITAI');
INSERT INTO doktorand(login, titul)
VALUES ('xmarti00','ing.');
INSERT INTO doktorand_predmet(login, id_predmetu)
VALUES ('xmarti00','IZU');

INSERT INTO student(login, jmeno, prijmeni, adresa_ulice, adresa_ulice_popisne, adresa_mesto, adresa_psc, adresa_stat, datum_nar, pohlavi)
VALUES('xrando00','Random','Mák','Random Ulice',33,'Ostrava',70030,'České republika',TO_DATE('1992-02-21', 'yyyy/mm/dd'),'Muž');
INSERT INTO student_studijni_program(login, id_programu)
VALUES ('xrando00','MITAI');
INSERT INTO doktorand(login, titul)
VALUES ('xrando00','ing.');
INSERT INTO doktorand_predmet(login, id_predmetu)
VALUES ('xrando00','ISS');
--=================================================================================
--3. ČÁST SELECT
--
--1/2 Join 2 tabulek. Které obory nabízí magisterský studijní program ?
SELECT obor.id_oboru,
       obor.nazev_oboru
from obor JOIN studijni_program sp on sp.id_programu = obor.id_programu
WHERE sp.delka = '2';
--2/2 Join 2 tabulek. Kteří studenti jsou doktorandi ?
SELECT student.login,
       student.jmeno,
       student.prijmeni
FROM student JOIN doktorand on student.login = doktorand.login;

--3/2 Join dvou tabulek. Studenti s datumem narození 1992 jsou automaticky doktorandy.
SELECT * from student JOIN doktorand on student.login = doktorand.login
WHERE EXTRACT(YEAR FROM TO_DATE(datum_nar, 'yyyy/mm/dd')) = 1992-01-01;

--1/1 dotaz s predikatem IN. Ktere předmety si nikdo nezaregistroval ?
SELECT *
FROM predmet
WHERE predmet.id_predmetu NOT IN (
    SELECT ps.id_predmetu
    FROM predmet_student ps
    );

--1/1 dotaz s predikátem EXISTS. Výpis studentů žijících v Brně.
SELECT student.login,
       student.jmeno,
       student.prijmeni
FROM student
WHERE EXISTS (SELECT adresa_mesto FROM student WHERE adresa_mesto = 'Brno');

--1/2 GROUP BY s agregační fcí COUNT. Počet studentů v rámci jednotlivých programů.
SELECT COUNT(student_studijni_program.login), id_programu
FROM student_studijni_program
GROUP BY id_programu
ORDER BY COUNT(student_studijni_program.login) DESC;

--2/2 GROUP BY s agreg. fcí COUNT. Procentuální zastoupení pohlaví v seznamu studentů.
SELECT student.pohlavi, COUNT(*) / CAST( SUM(count(*)) over () as float)
FROM student
GROUP BY student.pohlavi;

--1/1 spojeni tří tabulek. Výpis počtu kreditrů, které mají zaregistrováni jednotliví studenti
SELECT student.login,
       sum(p.kreditovy_obnos)
From student
Join predmet_student ps ON student.login = ps.login
Join predmet p ON p.id_predmetu = ps.id_predmetu
GROUP BY student.login
ORDER BY sum(p.kreditovy_obnos) DESC;

