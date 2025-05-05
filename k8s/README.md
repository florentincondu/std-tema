# Instrucțiuni de Deployment Kubernetes pentru Chat Application

Acest director conține fișierele de configurare necesare pentru a deployta aplicația de chat în Kubernetes.

## Arhitectura aplicației

Aplicația este compusă din următoarele componente:
- **MongoDB**: Baza de date NoSQL utilizată pentru stocarea mesajelor și a utilizatorilor
- **Backend Java Spring Boot**: API REST și WebSocket pentru gestionarea mesajelor și a utilizatorilor (portul 88, 4 replici)
- **Nginx**: Proxy pentru backend ce gestionează traficul și distribuie cererile către replicile backend
- **Frontend Angular**: Interfața utilizator pentru aplicația de chat (portul 90, 1 replică)

## Cerințe preliminare

- Kubernetes cluster (Minikube, Docker Desktop, sau un cluster gestionat)
- kubectl configurat pentru a accesa clusterul
- Docker pentru a construi imaginile (opțional, dacă doriți să construiți imaginile local)

## Pași pentru deployment

### 1. Construiți imaginile Docker (opțional)

Dacă doriți să construiți imaginile local:

```bash
# Construiți backend
cd ../chat-backend
docker build -t chat-backend:latest .

# Construiți frontend
cd ../chat-frontend
docker build -t chat-frontend:latest .
```

### 2. Aplicați configurațiile Kubernetes

Aplicați configurațiile în următoarea ordine:

```bash
# Creați persistentVolumeClaim și deployment-ul pentru MongoDB
kubectl apply -f mongodb-deployment.yaml

# Așteptați până când MongoDB este gata
kubectl wait --for=condition=ready pod -l app=mongodb --timeout=120s

# Creați deployment-ul pentru backend (4 replici)
kubectl apply -f chat-backend-deployment.yaml

# Așteptați până când backend-ul este gata
kubectl wait --for=condition=ready pod -l app=chat-backend --timeout=120s

# Creați deployment-ul pentru Nginx proxy pentru backend
kubectl apply -f nginx-backend-proxy.yaml

# Așteptați până când Nginx proxy este gata
kubectl wait --for=condition=ready pod -l app=nginx-backend-proxy --timeout=60s

# Creați deployment-ul pentru frontend
kubectl apply -f chat-frontend-deployment.yaml

# Aplicați configurația Ingress
kubectl apply -f ingress.yaml
```

### 3. Verificați deployment-ul

Verificați dacă toate pod-urile rulează:

```bash
kubectl get pods
```

Verificați serviciile:

```bash
kubectl get services
```

Verificați Ingress:

```bash
kubectl get ingress
```

### 4. Accesați aplicația

Dacă utilizați Minikube, obțineți URL-ul:

```bash
minikube service chat-frontend-service --url
```

Dacă utilizați un cluster cu LoadBalancer, obțineți adresa IP externă:

```bash
kubectl get service chat-frontend-service
```

Pentru Ingress, adăugați în fișierul hosts următoarea linie:

```
127.0.0.1 chat.local
```

Apoi accesați aplicația prin browser la adresa: http://chat.local

## Configurație

Aplicația este configurată cu următoarele specificații:
- Backend rulează pe portul 88 cu 4 replici, conform cerințelor
- Nginx proxy pentru backend gestionează traficul către instanțele backend
- Frontend rulează pe portul 90 cu 1 replică (conform cerințelor)
- MongoDB folosește un volum persistent

## Scalarea aplicației

Pentru a modifica numărul de replici pentru backend (dacă este necesar):

```bash
kubectl scale deployment chat-backend --replicas=6
```

Notă: Frontend-ul trebuie să rămână cu o singură replică conform cerințelor.

## Curățare

Pentru a șterge toate resursele create:

```bash
kubectl delete -f ingress.yaml
kubectl delete -f chat-frontend-deployment.yaml
kubectl delete -f nginx-backend-proxy.yaml
kubectl delete -f chat-backend-deployment.yaml
kubectl delete -f mongodb-deployment.yaml
``` 