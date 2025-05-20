-- =============================================================
-- USUWANIE I TWORZENIE BAZY DANYCH
-- =============================================================
CREATE DATABASE IF NOT EXISTS StonkaDB ;
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
INSERT INTO Adresy (Miejscowosc, Numer_domu, Numer_mieszkania, Kod_pocztowy, Miasto)
SELECT * FROM (
    SELECT 'Warszawa' AS Miejscowosc, '12A' AS Numer_domu, '3' AS Numer_mieszkania, '00-001' AS Kod_pocztowy, 'Warszawa' AS Miasto UNION ALL
    SELECT 'Kraków', '5', NULL, '30-002', 'Kraków' UNION ALL
    SELECT 'Gdańsk', '7B', '10', '80-001', 'Gdańsk' UNION ALL
    SELECT 'Wrocław', '20', NULL, '50-003', 'Wrocław' UNION ALL
    SELECT 'Poznań', '16', '8', '60-002', 'Poznań' UNION ALL
    SELECT 'Łódź', '3C', '2', '90-003', 'Łódź' UNION ALL
    SELECT 'Szczecin', '10', NULL, '70-005', 'Szczecin' UNION ALL
    SELECT 'Lublin', '4', '1', '20-001', 'Lublin' UNION ALL
    SELECT 'Katowice', '9A', NULL, '40-002', 'Katowice' UNION ALL
    SELECT 'Rzeszów', '22', '5', '35-003', 'Rzeszów'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Adresy a
    WHERE a.Miejscowosc = tmp.Miejscowosc
      AND a.Numer_domu = tmp.Numer_domu
      AND COALESCE(a.Numer_mieszkania, '') = COALESCE(tmp.Numer_mieszkania, '')
      AND a.Kod_pocztowy = tmp.Kod_pocztowy
      AND a.Miasto = tmp.Miasto
);

-- Pracownicy
INSERT INTO Pracownicy (Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Email, Zarobki, Stanowisko, onSickLeave, sickLeaveStartDate)
SELECT * FROM (
    SELECT 'Jan', 'Kowalski', 35, 1, 'admin', 'admin123', 'jan.kowalski@example.com', 4500.00, 'Kierownik', FALSE, NULL UNION ALL
    SELECT 'Anna', 'Nowak', 28, 2, 'anowak', 'nowak456', 'anna.nowak@example.com', 3500.00, 'Kasjer', TRUE, '2025-04-20' UNION ALL
    SELECT 'Marek', 'Wiśniewski', 40, 3, 'mwis', 'marek123', 'marek.w@example.com', 4000.00, 'Pracownik', FALSE, NULL UNION ALL
    SELECT 'Zofia', 'Maj', 33, 4, 'zmaj', 'zofia789', 'z.maj@example.com', 3700.00, 'Logistyk', FALSE, NULL UNION ALL
    SELECT 'Adam', 'Nowicki', 29, 5, 'anowicki', 'adam321', 'adam.nowicki@example.com', 3600.00, 'Kasjer', FALSE, NULL UNION ALL
    SELECT 'Ewa', 'Jankowska', 31, 6, 'ejanko', 'ewa456', 'ewa.j@example.com', 3900.00, 'Pracownik', FALSE, NULL UNION ALL
    SELECT 'Kamil', 'Kowalczyk', 45, 7, 'kkowal', 'kamil888', 'kamil.k@example.com', 4700.00, 'Pracownik', FALSE, NULL UNION ALL
    SELECT 'Barbara', 'Kaczmarek', 27, 8, 'bkacz', 'barbara987', 'b.kaczmarek@example.com', 3400.00, 'Kasjer', TRUE, '2025-04-18' UNION ALL
    SELECT 'Piotr', 'Zieliński', 38, 9, 'pziel', 'piotr111', 'piotr.z@example.com', 4100.00, 'Pracownik', FALSE, NULL UNION ALL
    SELECT 'Magda', 'Szymańska', 36, 10, 'mszym', 'magda654', 'magda.s@example.com', 4300.00, 'Logistyk', FALSE, NULL UNION ALL
    SELECT 'Janusz', 'Kowalik', 35, 1, 'admin2', 'admin2', 'janusz.kowalik@example.com', 4500.00, 'Admin', FALSE, NULL UNION ALL
    SELECT 'root', 'root', 35, 1, 'root', 'root', 'root.root@example.com', 4500.00, 'root', FALSE, NULL
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Pracownicy p
    WHERE p.Login = tmp.Login
);
-- Produkty
INSERT INTO Produkty (Nazwa, Kategoria, Cena)
SELECT * FROM (
    SELECT 'Mleko', 'Nabiał', 2.99 UNION ALL
    SELECT 'Chleb', 'Pieczywo', 3.49 UNION ALL
    SELECT 'Masło', 'Nabiał', 5.79 UNION ALL
    SELECT 'Jajka', 'Nabiał', 6.99 UNION ALL
    SELECT 'Ser', 'Nabiał', 4.59 UNION ALL
    SELECT 'Jogurt', 'Nabiał', 1.99 UNION ALL
    SELECT 'Sok pomarańczowy', 'Napoje', 3.99 UNION ALL
    SELECT 'Makaron', 'Produkty zbożowe', 2.49 UNION ALL
    SELECT 'Ryż', 'Produkty zbożowe', 2.89 UNION ALL
    SELECT 'Olej', 'Tłuszcze', 5.99
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Produkty pr
    WHERE pr.Nazwa = tmp.Nazwa
);
-- Stany magazynowe
INSERT INTO StanyMagazynowe (Id_produktu, Ilosc)
SELECT * FROM (
    SELECT 1, 150 UNION ALL
    SELECT 2, 200 UNION ALL
    SELECT 3, 80 UNION ALL
    SELECT 4, 100 UNION ALL
    SELECT 5, 90 UNION ALL
    SELECT 6, 120 UNION ALL
    SELECT 7, 110 UNION ALL
    SELECT 8, 140 UNION ALL
    SELECT 9, 160 UNION ALL
    SELECT 10, 70
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM StanyMagazynowe sm
    WHERE sm.Id_produktu = tmp.Id_produktu
);

-- Zadania
INSERT INTO Zadania (Nazwa, Data, Status, Opis, czas_trwania_zmiany)
SELECT * FROM (
    SELECT 'Sprawdzenie stanu magazynu', '2025-04-01', 'Nowe', 'Weryfikacja towaru przed dostawą', NULL UNION ALL
    SELECT 'Inwentaryzacja',             '2025-04-15', 'Nowe', 'Spis towarów', '00:30:00' UNION ALL
    SELECT 'Dostawa mleka',              '2025-04-10', 'Zakończone', 'Odbiór dostawy', '01:15:00' UNION ALL
    SELECT 'Wymiana regałów',            '2025-04-12', 'W trakcie', 'Wymiana regałów w dziale nabiałowym', '02:45:00' UNION ALL
    SELECT 'Porządkowanie zaplecza',     '2025-04-08', 'Zakończone', 'Czyszczenie i sortowanie towarów', '01:30:00'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Zadania z
    WHERE z.Nazwa = tmp.Nazwa AND z.Data = tmp.Data
);

-- Zadania_Pracownicy
INSERT INTO Zadania_Pracownicy (Id_pracownika, Id_zadania)
SELECT * FROM (
    SELECT 1, 1 UNION ALL
    SELECT 2, 2 UNION ALL
    SELECT 3, 3 UNION ALL
    SELECT 4, 4 UNION ALL
    SELECT 5, 5
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Zadania_Pracownicy zp
    WHERE zp.Id_pracownika = tmp.Id_pracownika AND zp.Id_zadania = tmp.Id_zadania
);

-- Wnioski o nieobecność
INSERT INTO Wnioski_o_nieobecnosc (Typ_wniosku, Data_rozpoczecia, Data_zakonczenia, Opis, Id_pracownika, Status)
SELECT * FROM (
    SELECT 'Urlop wypoczynkowy', '2025-05-01', '2025-05-10', 'Wakacje w górach', 1, 'Oczekuje' UNION ALL
    SELECT 'Zwolnienie lekarskie', '2025-04-20', '2025-04-25', 'Przeziębienie', 2, 'Oczekuje' UNION ALL
    SELECT 'Urlop bezpłatny', '2025-06-01', '2025-06-15', 'Wyjazd zagraniczny', 3, 'Oczekuje' UNION ALL
    SELECT 'Urlop na żądanie', '2025-04-22', '2025-04-22', 'Sprawy rodzinne', 4, 'Oczekuje' UNION ALL
    SELECT 'Opieka nad dzieckiem', '2025-05-05', '2025-05-07', 'Chore dziecko', 5, 'Oczekuje'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Wnioski_o_nieobecnosc wn
    WHERE wn.Typ_wniosku = tmp.Typ_wniosku AND wn.Data_rozpoczecia = tmp.Data_rozpoczecia AND wn.Id_pracownika = tmp.Id_pracownika
);

-- Zamówienia
INSERT INTO Zamowienia (Id_produktu, Id_pracownika, Ilosc, Cena, Data)
SELECT * FROM (
    SELECT 1, 1, 50, 149.50, '2025-04-05' UNION ALL
    SELECT 2, 2, 100, 349.00, '2025-04-06' UNION ALL
    SELECT 3, 3, 30, 173.70, '2025-04-07' UNION ALL
    SELECT 4, 4, 40, 279.60, '2025-04-08' UNION ALL
    SELECT 5, 5, 25, 114.75, '2025-04-09'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Zamowienia z
    WHERE z.Id_produktu = tmp.Id_produktu AND z.Id_pracownika = tmp.Id_pracownika AND z.Data = tmp.Data
);

-- Transakcje
INSERT INTO Transakcje (Id_pracownika, Data)
SELECT * FROM (
    SELECT 1, '2025-04-10' UNION ALL
    SELECT 2, '2025-04-11' UNION ALL
    SELECT 3, '2025-04-12' UNION ALL
    SELECT 4, '2025-04-13' UNION ALL
    SELECT 5, '2025-04-14'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Transakcje t
    WHERE t.Id_pracownika = tmp.Id_pracownika AND t.Data = tmp.Data
);

-- Transakcje_Produkty
INSERT INTO Transakcje_Produkty (Id_transakcji, Id_produktu, Ilosc)
SELECT * FROM (
    SELECT 1, 1, 3 UNION ALL
    SELECT 1, 2, 2 UNION ALL
    SELECT 2, 3, 1 UNION ALL
    SELECT 3, 4, 5 UNION ALL
    SELECT 4, 5, 2 UNION ALL
    SELECT 5, 1, 4 UNION ALL
    SELECT 5, 5, 3
) AS tmp (Id_transakcji, Id_produktu, Ilosc)
WHERE NOT EXISTS (
    SELECT 1 FROM Transakcje_Produkty tp
    WHERE tp.Id_transakcji = tmp.Id_transakcji AND tp.Id_produktu = tmp.Id_produktu
);

-- Raporty
INSERT INTO Raporty (Typ_raportu, Data_poczatku, Data_zakonczenia, Id_pracownika, Plik)
SELECT * FROM (
    SELECT 'Raport miesięczny',      '2025-04-01', '2025-04-30', 1, 'raport_kwiecien.pdf' UNION ALL
    SELECT 'Stan magazynu',          '2025-04-01', '2025-04-15', 2, 'magazyn_15kwiecien.pdf' UNION ALL
    SELECT 'Sprzedaż dzienna',       '2025-04-10', '2025-04-10', 3, 'sprzedaz_10kwietnia.pdf' UNION ALL
    SELECT 'Wnioski o nieobecność',  '2025-04-01', '2025-04-30', 4, 'wnioski_kwiecien.pdf' UNION ALL
    SELECT 'Zamówienia i dostawy',   '2025-04-01', '2025-04-20', 5, 'zamowienia_dostawy.pdf'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Raporty r
    WHERE r.Typ_raportu = tmp.Typ_raportu AND r.Data_poczatku = tmp.Data_poczatku AND r.Id_pracownika = tmp.Id_pracownika
);

-- Zgłoszenia techniczne
INSERT INTO Zgloszenia_techniczne (Typ, Opis, Id_pracownika)
SELECT * FROM (
    SELECT 'Awaria sprzętu',        'Nie działa drukarka fiskalna przy kasie nr 1', 2 UNION ALL
    SELECT 'Błąd oprogramowania',   'Błąd przy finalizacji sprzedaży - aplikacja się zamyka', 5 UNION ALL
    SELECT 'Inne',                  'Proszę o aktualizację systemu do najnowszej wersji', 1
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM Zgloszenia_techniczne zg
    WHERE zg.Typ = tmp.Typ AND zg.Opis = tmp.Opis AND zg.Id_pracownika = tmp.Id_pracownika
);