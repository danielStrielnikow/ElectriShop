# Etap 1: Użycie obrazu Node.js
FROM node:18

# Utwórz katalog roboczy w kontenerze
WORKDIR /app

# Kopiuj pliki package.json oraz package-lock.json do kontenera
COPY package*.json ./

# Zainstaluj zależności
RUN npm install

# Skopiuj pozostałe pliki projektu
COPY . .

# Otwórz port, na którym działa aplikacja Vite (domyślnie 5173)
EXPOSE 5173

# Uruchom serwer deweloperski Vite, nasłuchujący na wszystkich interfejsach
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
