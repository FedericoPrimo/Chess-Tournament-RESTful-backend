import requests
import time

URL = "http://localhost:8080/api/LiveMatch/insertMoves/segreto_mattia/segreto_mattia-frattacci_jonathan"  # Sostituisci con l'URL effettivo
INTERVAL = 3  # Secondi tra una mossa e l'altra

# Lista di mosse (simulazione di una partita)
WHITE_MOVES = [
    "e2e4", "g1f3", "d2d4", "c2c3", "f1e2", "e1g1", "b1d2", "c1b2",
    "d1c2", "h2h3", "g2g3", "f3h2", "d2f3", "a2a3", "c3c4", "h3h4",
    "g3g4", "f3d2", "h1h3", "c4c5"
]

def white():
    headers = {"Content-Type": "text/plain"}  # Indichiamo che stiamo inviando solo testo
    for move in WHITE_MOVES:
        try:
            response = requests.post(URL, data=move, headers=headers)  # Invio solo la stringa della mossa
            print(f"White - Move: {move} | Status: {response.status_code}, Response: {response.text}")
        except Exception as e:
            print(f"Errore: {e}")

        time.sleep(INTERVAL)  # Attendere prima della prossima mossa

    print("White ha terminato tutte le mosse.")

if __name__ == "__main__":
    white()
