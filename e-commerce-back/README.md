<div id="top">

<!-- HEADER STYLE: CLASSIC -->
<div align="center">


# ELECTIBACK

<em>Empowering seamless e-commerce experiences for everyone.</em>

<!-- BADGES -->
<img src="https://img.shields.io/github/last-commit/danielStrielnikow/ElectiBack?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
<img src="https://img.shields.io/github/languages/top/danielStrielnikow/ElectiBack?style=flat&color=0080ff" alt="repo-top-language">
<img src="https://img.shields.io/github/languages/count/danielStrielnikow/ElectiBack?style=flat&color=0080ff" alt="repo-language-count">

<em>Built with the tools and technologies:</em>

<img src="https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white" alt="Docker">
<img src="https://img.shields.io/badge/XML-005FAD.svg?style=flat&logo=XML&logoColor=white" alt="XML">

</div>
<br>

---

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Usage](#usage)
    - [Testing](#testing)

---

## Overview

ElectiBack is a powerful e-commerce application framework built with Spring Boot, designed to streamline the development process for modern online stores.

**Why ElectiBack?**

This project empowers developers to create scalable e-commerce solutions effortlessly. The core features include:

- 🛠️ **Spring Boot Integration:** Simplifies the development of robust e-commerce applications.
- 📦 **Maven Wrapper:** Ensures consistent build environments, reducing setup time for developers.
- 🔒 **JWT Security:** Provides secure user authentication and authorization mechanisms.
- 🐳 **Docker Support:** Facilitates easy deployment and scaling of applications in production environments.
- 📡 **Comprehensive API:** Offers a RESTful interface for managing products, orders, and user data efficiently.
- 📊 **Data Transfer Objects (DTOs):** Streamlines data handling between layers, enhancing maintainability.

---

## Getting Started

### Prerequisites

This project requires the following dependencies:

- **Programming Language:** Java
- **Package Manager:** Maven
- **Container Runtime:** Docker

### Installation

Build ElectiBack from the source and intsall dependencies:

1. **Clone the repository:**

    ```sh
    ❯ git clone https://github.com/danielStrielnikow/ElectiBack
    ```

2. **Navigate to the project directory:**

    ```sh
    ❯ cd ElectiBack
    ```

3. **Install the dependencies:**

**Using [docker](https://www.docker.com/):**

```sh
❯ docker build -t danielStrielnikow/ElectiBack .
```
**Using [maven](https://maven.apache.org/):**

```sh
❯ mvn install
```

### Usage

Run the project with:

**Using [docker](https://www.docker.com/):**

```sh
docker run -it {image_name}
```
**Using [maven](https://maven.apache.org/):**

```sh
mvn exec:java
```

### Testing

Electiback uses the {__test_framework__} test framework. Run the test suite with:

**Using [docker](https://www.docker.com/):**

```sh
echo 'INSERT-TEST-COMMAND-HERE'
```
**Using [maven](https://maven.apache.org/):**

```sh
mvn test
```

---

<div align="left"><a href="#top">⬆ Return</a></div>

---
