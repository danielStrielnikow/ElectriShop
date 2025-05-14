<div id="top"></div>

<div align="center">

# ElectriShop

<em>Nowoczesne i skalowalne zaplecze e-commerce stworzone w Spring Boot.</em>

<!-- BADGES -->

<img src="https://img.shields.io/github/last-commit/danielStrielnikow/ElectriShop?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
<img src="https://img.shields.io/github/languages/top/danielStrielnikow/ElectriShop?style=flat&color=0080ff" alt="repo-top-language">
<img src="https://img.shields.io/github/languages/count/danielStrielnikow/ElectriShop?style=flat&color=0080ff" alt="repo-language-count">

<em>Zbudowany z wykorzystaniem:</em><br><br>

<img src="https://img.shields.io/badge/Spring_Boot-6DB33F.svg?style=flat&logo=Spring-Boot&logoColor=white" alt="Spring Boot">
<img src="https://img.shields.io/badge/Java-007396.svg?style=flat&logo=java&logoColor=white" alt="Java">
<img src="https://img.shields.io/badge/Maven-C71A36.svg?style=flat&logo=apache-maven&logoColor=white" alt="Maven">
<img src="https://img.shields.io/badge/PostgreSQL-4169E1.svg?style=flat&logo=postgresql&logoColor=white" alt="PostgreSQL">
<img src="https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white" alt="Docker">
<img src="https://img.shields.io/badge/JWT-black?style=flat&logo=jsonwebtokens&logoColor=white" alt="JWT">

</div>

---

## 📚 Spis treści

* [📖 Opis projektu](#-opis-projektu)
* [✨ Funkcje](#-funkcje)
* [🚀 Jak zacząć](#-jak-zacząć)

  * [✅ Wymagania](#-wymagania)
  * [🔧 Instalacja](#-instalacja)
  * [▶️ Uruchomienie](#️-uruchomienie)
* [🔐 Plik .env](#-plik-env)
* [📡 Przykładowe API](#-przykładowe-api)
* [🤝 Współpraca](#-współpraca)
* [📄 Licencja](#-licencja)

---

## 📖 Opis projektu

**ElectriShop** to nowoczesny backend aplikacji e-commerce napisany w Spring Boot. Projekt ten stanowi solidną podstawę do budowy funkcjonalnych, bezpiecznych i łatwych w rozwoju sklepów internetowych.

---

## ✨ Funkcje

* ✅ Architektura warstwowa (Controller → Service → Repository)
* 🔐 JWT Authentication & Authorization
* 💾 Wsparcie dla bazy danych PostgreSQL
* 🧾 REST API z DTO
* 🐋 Docker-ready (Spring + PostgreSQL)
* 📄 Maven Wrapper

---

## 🚀 Jak zacząć

### ✅ Wymagania

Aby uruchomić projekt, potrzebujesz:

* [Java 21](https://www.oracle.com/java/)
* [Maven](https://maven.apache.org/)
* [PostgreSQL 16+](https://www.postgresql.org/)
* [Docker](https://www.docker.com/) *(opcjonalnie)*

---

### 🔧 Instalacja

1. Sklonuj repozytorium:

```bash
git clone https://github.com/danielStrielnikow/ElectiBack
cd ElectriShop
```

2. Zbuduj projekt przy użyciu Mavena:

```bash
mvn clean install
```

3. (Opcjonalnie) Zbuduj obraz Dockera:

```bash
docker build -t electriShop .
```

---

### ▶️ Uruchomienie

#### 🐘 Konfiguracja bazy PostgreSQL lokalnie

Utwórz bazę danych lokalnie (np. w pgAdmin lub CLI):

```sql
CREATE DATABASE ecommerce;
CREATE USER daniel WITH PASSWORD 'twoje_hasło';
GRANT ALL PRIVILEGES ON DATABASE ecommerce TO twoja_nazwa;
```

#### ▶️ Uruchomienie lokalne:

```bash
mvn spring-boot:run
```

#### 🐳 Uruchomienie w Dockerze (Spring + PostgreSQL):

```bash
docker-compose up --build
```

> Aplikacja będzie dostępna pod `http://localhost:8080`

---

## 🔐 Plik .env

Utwórz plik `.env` w katalogu głównym projektu i uzupełnij go według poniższego wzoru:

```
# Dane logowania administratora
SECURITY_NAME=twoja_nazwa_uzytkownika
SECURITY_PASSWORD=twoje_haslo

# Dane połączenia z bazą PostgreSQL
DATASOURCE_URL=jdbc:postgresql://localhost:5432/nazwa_bazy
DATASOURCE_USER=uzytkownik_bazy
DATASOURCE_PASSWORD=haslo_bazy

# Konfiguracja JWT
JWT_SECRET=twoj_tajny_klucz
JWT_COOKIE=nazwa_cookie

# Stripe API
STRIPE_API_KEY=twoj_klucz_stripe

# Adres frontendowy i dla plików statycznych
FRONT_URL=http://localhost:5173
BASE_URL_IMAGES=http://localhost:8080/images
```

> Upewnij się, że plik `.env` nie jest dołączony do repozytorium (znajduje się w `.gitignore`).

---

## 📡 Przykładowe API

### 🔐 Rejestracja użytkownika:

`POST /api/auth/signup`
```json
{
    "username": "twoje_nazwa",
    "email": "twój@_email",
    "password": "twoje_haslo"
}
```
Odpowiedź:

```json
{
  "User registered successfully!"
}
```

### 🔐 Logowanie użytkownika:

`POST /api/auth/signin`

```json
{
  "username": "twoje_nazwa",
  "password": "twoje_haslo"
}
```

Odpowiedź:

```json
{
    "id": 1,
    "jwtToken": "jwtCookieName=TWÓj_JWT",
    "username": "twoje_nazwa",
    "roles": [
        "TWOJA_ROLA"
    ]
}
```

---

### 📦 Lista produktów

`GET /api/public/products`

Odpowiedź:

```json
{
    "content": [
        {
            "productId": 1,
            "productName": "Sennheiser HD 450BT",
            "image": "http://localhost:8080/images/headphones.jpeg",
            "description": "Headphones with excellent sound quality and noise reduction.",
            "quantity": 20,
            "price": 600.0,
            "discount": 20.0,
            "specialPrice": 480.0
        }
    ],
    "pageNumber": 0,
    "pageSize": 8,
    "totalElements": 1,
    "totalPages": 1,
    "lastPage": true
}
```

### 📦 Lista kategorii
`GET /api/public/categories`

Odpowiedź:

```json
{
    "content": [
        {
            "categoryId": 1,
            "categoryName": "Headphones"
        }
    ],
    "pageNumber": 0,
    "pageSize": 8,
    "totalElements": 1,
    "totalPages": 1,
    "lastPage": true
}
```
---

## 🤝 Współpraca

1. Fork repozytorium
2. Utwórz nową gałąź: `git checkout -b nowa-funkcjonalnosc`
3. Commit: `git commit -m 'Dodano nową funkcję'`
4. Push: `git push origin nowa-funkcjonalnosc`
5. Otwórz Pull Request 🚀

---

<div align="right"><a href="#top">⬆ Wróć na górę</a></div>
