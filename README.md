# Aplicație de Chat Multi-utilizator

Această aplicație permite mai multor utilizatori să se conecteze și să comunice în timp real.

## Funcționalități

- Autentificare cu nume de utilizator
- Mesagerie în timp real
- Vizualizarea utilizatorilor conectați
- Notificări de conectare/deconectare
- Interfață receptivă și prietenoasă

## Componente

- **Backend**: Java Spring Boot + Nginx, WebSocket, MongoDB (portul 88, 4 replici)
- **Frontend**: Angular, RxJS (portul 90, 1 replică)

## Cerințe preliminare

- Java 11+
- Node.js și npm
- MongoDB
- Docker (opțional)
- Kubernetes (opțional)

## Structura proiectului

```
/
├── chat-backend/        # Aplicația Java Spring Boot
├── chat-frontend/       # Aplicația Angular
├── nginx/               # Configurații Nginx
├── docker-compose.yml   # Configurație Docker Compose
└── k8s/                 # Configurații Kubernetes
```

## Pornirea aplicației

Există trei moduri de a porni aplicația:

### 1. Dezvoltare locală

#### Pasul 1: Pornirea MongoDB

Asigurați-vă că MongoDB rulează local pe portul implicit 27017.

```bash
# Dacă MongoDB este instalat local, puteți porni serviciul
# Pe Windows:
net start MongoDB

# Pe Linux/Mac:
sudo systemctl start mongod
```

Alternativ, puteți utiliza Docker:

```bash
docker run -d -p 27017:27017 --name mongodb mongo:4.4
```

#### Pasul 2: Pornirea backend-ului

```bash
cd chat-backend
./mvnw spring-boot:run
```

Serverul va porni pe http://localhost:88

#### Pasul 3: Configurarea și pornirea Nginx

```bash
# Asigurați-vă că Nginx este instalat
sudo cp nginx/backend.conf /etc/nginx/conf.d/
sudo nginx -s reload
```

#### Pasul 4: Pornirea frontend-ului

```bash
cd chat-frontend
npm install
npm start
```

Accesați aplicația la http://localhost:90

### 2. Utilizând Docker Compose

Cel mai simplu mod de a porni aplicația completă este utilizând Docker Compose:

```bash
docker-compose up -d
```

Aceasta va porni:
- 1 instanță MongoDB
- 4 instanțe de backend Spring Boot
- 1 proxy Nginx pentru backend
- 1 instanță frontend Angular

Accesați aplicația la http://localhost:90

Pentru a opri aplicația:

```bash
docker-compose down
```

### 3. Deployment cu Kubernetes

Pentru informații detaliate despre deployment-ul cu Kubernetes, consultați [k8s/README.md](k8s/README.md).

Instrucțiuni pe scurt:

```bash
# Aplicați deployment-urile în ordine
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/chat-backend-deployment.yaml
kubectl apply -f k8s/nginx-backend-proxy.yaml
kubectl apply -f k8s/chat-frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```

## Dezvoltare

### Construirea imaginilor Docker

```bash
# Backend
cd chat-backend
docker build -t chat-backend:latest .

# Frontend
cd chat-frontend
docker build -t chat-frontend:latest .
```

### Testare

```bash
# Backend
cd chat-backend
./mvnw test

# Frontend
cd chat-frontend
npm test
```

## Licență

Acest proiect este licențiat sub licența MIT - consultați fișierul LICENSE pentru detalii.

## Utilizare

1. Accesați aplicația în browser la adresa http://localhost:90
2. Introduceți numele dvs. și apăsați "Intră în Chat"
3. Începeți să trimiteți mesaje
4. Veți putea vedea lista utilizatorilor conectați în panoul din stânga

## Note

- Backend-ul este configurat să ruleze pe portul 88 cu 4 replici, cu un proxy Nginx în față
- Frontend-ul este configurat să ruleze pe portul 90 cu o singură replică
- Pentru a modifica configurațiile, editați fișierul `chat-backend/src/main/resources/application.properties`
- Pentru a modifica configurațiile frontend-ului, editați `chat-frontend/src/app/chat/chat.service.ts` și `chat-frontend/src/app/chat/chat.component.ts` 