-- =============================================================
-- USUWANIE I TWORZENIE BAZY DANYCH
-- =============================================================
DROP DATABASE IF EXISTS StonkaDB;
CREATE DATABASE StonkaDB;
USE StonkaDB;

-- =============================================================
-- TABELA: Adresy
-- =============================================================
CREATE TABLE IF NOT EXISTS Adresy (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Miejscowosc       VARCHAR(100),
    Numer_domu        VARCHAR(10),
    Numer_mieszkania  VARCHAR(10),
    Kod_pocztowy      VARCHAR(10),
    Miasto            VARCHAR(100)
);

-- =============================================================
-- TABELA: Pracownicy
-- =============================================================
CREATE TABLE IF NOT EXISTS Pracownicy (
    Id                   INT PRIMARY KEY AUTO_INCREMENT,
    Imie                 VARCHAR(100),
    Nazwisko             VARCHAR(100),
    Wiek                 INT,
    Id_adresu            INT,
    Login                VARCHAR(100),
    Haslo                VARCHAR(100),
    Email                VARCHAR(100),
    Zarobki              DECIMAL(10,2),
    Stanowisko           VARCHAR(100),
    onSickLeave          BOOLEAN DEFAULT FALSE,
    sickLeaveStartDate   DATE,
    usuniety             BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (Id_adresu) REFERENCES Adresy(Id)
);

-- =============================================================
-- TABELA: Zgłoszenia techniczne (kaskadowe usuwanie)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zgloszenia_techniczne (
    Id               INT PRIMARY KEY AUTO_INCREMENT,
    Typ              VARCHAR(100),
    Opis             TEXT,
    Data_zgloszenia  DATE DEFAULT (CURRENT_DATE),
    Id_pracownika    INT,
    Status           VARCHAR(50) DEFAULT 'Nowe',
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE
);

-- =============================================================
-- TABELA: Zadania
-- =============================================================
CREATE TABLE IF NOT EXISTS Zadania (
    Id     INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa  VARCHAR(100),
    Data   DATE,
    Status VARCHAR(50),
    Opis   TEXT,
    czas_trwania_zmiany TIME NULL COMMENT 'Czas trwania zmiany pracownika przy zadaniu'
);

-- =============================================================
-- TABELA: Wnioski o nieobecność (kaskadowe usuwanie)
-- =============================================================
CREATE TABLE IF NOT EXISTS Wnioski_o_nieobecnosc (
    Id                 INT PRIMARY KEY AUTO_INCREMENT,
    Typ_wniosku        VARCHAR(100),
    Data_rozpoczecia   DATE,
    Data_zakonczenia   DATE,
    Opis               TEXT,
    Status             ENUM('Oczekuje', 'Nie przyjęty', 'Przyjęty') DEFAULT 'Oczekuje',
    Id_pracownika      INT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE
);

-- =============================================================
-- TABELA: Produkty
-- =============================================================
CREATE TABLE IF NOT EXISTS Produkty (
    Id        INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa     VARCHAR(100),
    Kategoria VARCHAR(100),
    Cena      DECIMAL(10,2)
);

-- =============================================================
-- TABELA: Stany magazynowe
-- =============================================================
CREATE TABLE IF NOT EXISTS StanyMagazynowe (
    Id_produktu INT PRIMARY KEY,
    Ilosc       INT NOT NULL,
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id) ON DELETE CASCADE
);

-- =============================================================
-- TABELA: Zamówienia (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zamowienia (
    Id            INT PRIMARY KEY AUTO_INCREMENT,
    Id_produktu   INT,
    Id_pracownika INT,
    Ilosc         INT,
    Cena          DECIMAL(10,2),
    Data          DATE,
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- =============================================================
-- TABELA: Transakcje (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Transakcje (
    Id             INT PRIMARY KEY AUTO_INCREMENT,
    Id_pracownika  INT,
    Data           DATE,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- =============================================================
-- TABELA: Relacja Transakcje <-> Produkty
-- =============================================================
CREATE TABLE IF NOT EXISTS Transakcje_Produkty (
    Id_transakcji  INT,
    Id_produktu    INT,
    PRIMARY KEY (Id_transakcji, Id_produktu),
    FOREIGN KEY (Id_transakcji) REFERENCES Transakcje(Id),
    FOREIGN KEY (Id_produktu)   REFERENCES Produkty(Id)
);

-- =============================================================
-- TABELA: Raporty (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Raporty (
    Id              INT PRIMARY KEY AUTO_INCREMENT,
    Typ_raportu     VARCHAR(100),
    Data_poczatku   DATE,
    Data_zakonczenia DATE,
    Id_pracownika   INT,
    Plik            TEXT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- =============================================================
-- TABELA: Relacja Zadania <-> Pracownicy (wiele-do-wielu)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zadania_Pracownicy (
    Id_pracownika INT,
    Id_zadania    INT,
    PRIMARY KEY (Id_pracownika, Id_zadania),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE,
    FOREIGN KEY (Id_zadania)    REFERENCES Zadania(Id)
);

-- =============================================================
-- WSTAWIANIE DANYCH
-- =============================================================

-- Adresy
INSERT INTO Adresy (Miejscowosc, Numer_domu, Numer_mieszkania, Kod_pocztowy, Miasto) VALUES
('Warszawa', '12A', '3', '00-001', 'Warszawa'),
('Kraków',   '5',   NULL, '30-002', 'Kraków'),
('Gdańsk',   '7B',  '10', '80-001', 'Gdańsk'),
('Wrocław',  '20',  NULL, '50-003', 'Wrocław'),
('Poznań',   '16',  '8',  '60-002', 'Poznań'),
('Łódź',     '3C',  '2',  '90-003', 'Łódź'),
('Szczecin', '10',  NULL, '70-005', 'Szczecin'),
('Lublin',   '4',   '1',  '20-001', 'Lublin'),
('Katowice', '9A',  NULL, '40-002', 'Katowice'),
('Rzeszów',  '22',  '5',  '35-003', 'Rzeszów');

-- Pracownicy
INSERT INTO Pracownicy
(Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Email, Zarobki, Stanowisko, onSickLeave, sickLeaveStartDate)
VALUES
    ('Jan', 'Kowalski', 35, 1, 'admin', 'admin123', 'jan.kowalski@example.com', 4500.00, 'Kierownik', FALSE, NULL),
    ('Anna', 'Nowak', 28, 2, 'anowak', 'nowak456', 'anna.nowak@example.com', 3500.00, 'Kasjer', TRUE, '2025-04-20'),
    ('Marek', 'Wiśniewski', 40, 3, 'mwis', 'marek123', 'marek.w@example.com', 4000.00, 'Pracownik', FALSE, NULL),
    ('Zofia', 'Maj', 33, 4, 'zmaj', 'zofia789', 'z.maj@example.com', 3700.00, 'Logistyk', FALSE, NULL),
    ('Adam', 'Nowicki', 29, 5, 'anowicki', 'adam321', 'adam.nowicki@example.com', 3600.00, 'Kasjer', FALSE, NULL),
    ('Ewa', 'Jankowska', 31, 6, 'ejanko', 'ewa456', 'ewa.j@example.com', 3900.00, 'Pracownik', FALSE, NULL),
    ('Kamil', 'Kowalczyk', 45, 7, 'kkowal', 'kamil888', 'kamil.k@example.com', 4700.00, 'Pracownik', FALSE, NULL),
    ('Barbara', 'Kaczmarek', 27, 8, 'bkacz', 'barbara987', 'b.kaczmarek@example.com', 3400.00, 'Kasjer', TRUE, '2025-04-18'),
    ('Piotr', 'Zieliński', 38, 9, 'pziel', 'piotr111', 'piotr.z@example.com', 4100.00, 'Pracownik', FALSE, NULL),
    ('Magda', 'Szymańska', 36, 10, 'mszym', 'magda654', 'magda.s@example.com', 4300.00, 'Logistyk', FALSE, NULL),
    ('Janusz', 'Kowalik', 35, 1, 'admin2', 'admin2', 'janusz.kowalik@example.com', 4500.00, 'Admin', FALSE, NULL),
    ('root', 'root', 35, 1, 'root', 'root', 'root.root@example.com', 4500.00, 'root', FALSE, NULL);

-- Produkty
INSERT INTO Produkty (Nazwa, Kategoria, Cena) VALUES
('Mleko',            'Nabiał',              2.99),
('Chleb',            'Pieczywo',            3.49),
('Masło',            'Nabiał',              5.79),
('Jajka',            'Nabiał',              6.99),
('Ser',              'Nabiał',              4.59),
('Jogurt',           'Nabiał',              1.99),
('Sok pomarańczowy', 'Napoje',              3.99),
('Makaron',          'Produkty zbożowe',    2.49),
('Ryż',              'Produkty zbożowe',    2.89),
('Olej',             'Tłuszcze',            5.99);

-- Stany magazynowe
INSERT INTO StanyMagazynowe (Id_produktu, Ilosc) VALUES
(1, 150),
(2, 200),
(3, 80),
(4, 100),
(5, 90),
(6, 120),
(7, 110),
(8, 140),
(9, 160),
(10, 70);

-- Zadania
INSERT INTO Zadania (Nazwa, Data, Status, Opis, czas_trwania_zmiany) VALUES
('Sprawdzenie stanu magazynu', '2025-04-01', 'Nowe',      'Weryfikacja towaru przed dostawą', NULL),
('Inwentaryzacja',             '2025-04-15', 'Nowe',      'Spis towarów', '00:30:00'),
('Dostawa mleka',              '2025-04-10', 'Zakończone','Odbiór dostawy', '01:15:00'),
('Wymiana regałów',            '2025-04-12', 'W trakcie', 'Wymiana regałów w dziale nabiałowym', '02:45:00'),
('Porządkowanie zaplecza',     '2025-04-08', 'Zakończone','Czyszczenie i sortowanie towarów', '01:30:00');

-- Zadania_Pracownicy
INSERT INTO Zadania_Pracownicy (Id_pracownika, Id_zadania) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Wnioski o nieobecność
INSERT INTO Wnioski_o_nieobecnosc (Typ_wniosku, Data_rozpoczecia, Data_zakonczenia, Opis, Id_pracownika, Status) VALUES
('Urlop wypoczynkowy', '2025-05-01', '2025-05-10', 'Wakacje w górach',    1, 'Oczekuje'),
('Zwolnienie lekarskie','2025-04-20','2025-04-25', 'Przeziębienie',       2, 'Oczekuje'),
('Urlop bezpłatny',     '2025-06-01','2025-06-15', 'Wyjazd zagraniczny',  3, 'Oczekuje'),
('Urlop na żądanie',    '2025-04-22','2025-04-22', 'Sprawy rodzinne',     4, 'Oczekuje'),
('Opieka nad dzieckiem','2025-05-05','2025-05-07', 'Chore dziecko',       5, 'Oczekuje');

-- Zamówienia
INSERT INTO Zamowienia (Id_produktu, Id_pracownika, Ilosc, Cena, Data) VALUES
(1, 1, 50, 149.50, '2025-04-05'),
(2, 2, 100, 349.00, '2025-04-06'),
(3, 3, 30, 173.70, '2025-04-07'),
(4, 4, 40, 279.60, '2025-04-08'),
(5, 5, 25, 114.75, '2025-04-09');

-- Transakcje
INSERT INTO Transakcje (Id_pracownika, Data) VALUES
(1, '2025-04-10'),
(2, '2025-04-11'),
(3, '2025-04-12'),
(4, '2025-04-13'),
(5, '2025-04-14');

-- Transakcje_Produkty
INSERT INTO Transakcje_Produkty (Id_transakcji, Id_produktu) VALUES
(1, 1), (1, 2), (2, 3), (3, 4), (4, 5), (5, 1), (5, 5);

-- Raporty
INSERT INTO Raporty (Typ_raportu, Data_poczatku, Data_zakonczenia, Id_pracownika, Plik) VALUES
('Raport miesięczny',      '2025-04-01', '2025-04-30', 1, 'raport_kwiecien.pdf'),
('Stan magazynu',          '2025-04-01', '2025-04-15', 2, 'magazyn_15kwiecien.pdf'),
('Sprzedaż dzienna',       '2025-04-10', '2025-04-10', 3, 'sprzedaz_10kwietnia.pdf'),
('Wnioski o nieobecność',  '2025-04-01', '2025-04-30', 4, 'wnioski_kwiecien.pdf'),
('Zamówienia i dostawy',   '2025-04-01', '2025-04-20', 5, 'zamowienia_dostawy.pdf');

-- Zgłoszenia techniczne
INSERT INTO Zgloszenia_techniczne (Typ, Opis, Id_pracownika) VALUES
('Awaria sprzętu',        'Nie działa drukarka fiskalna przy kasie nr 1', 2),
('Błąd oprogramowania',   'Błąd przy finalizacji sprzedaży - aplikacja się zamyka', 5),
('Inne',                  'Proszę o aktualizację systemu do najnowszej wersji', 1);