import requests
import time
from login import playerLogin
USER = "calzolari_federico"
URL = "http://localhost:8080/api/LiveMatch/insertMoves/calzolari_federico/segreto_mattia-calzolari_federico"  # Sostituisci con l'URL effettivo
INTERVAL = 3  # Secondi tra una mossa e l'altra

# Lista di mosse (simulazione di una partita)
BLACK_MOVES = [
    "e7e5", "g8f6", "d7d6", "f8e7", "c7c6", "b7b6", "c8b7", "d6d5",
    "e8g8", "h7h6", "g7g6", "f8e8", "d8c7", "b8d7", "a7a6", "h6h5",
    "g6g5", "d7f6", "h8h7", "c6c5"
]

def black():
    token = playerLogin(USER)
    headers = {
        "Content-Type": "text/plain",
        "Authorization": f"Bearer {token}"
      }
    for move in BLACK_MOVES:
        try:
            response = requests.post(URL, data=move, headers=headers)  # Invio solo la stringa della mossa
            print(f"Black - Move: {move} | Status: {response.status_code}, Response: {response.text}")
        except Exception as e:
            print(f"Errore: {e}")

        time.sleep(INTERVAL)  # Attendere prima della prossima mossa

    print("Black ha terminato tutte le mosse.")

if __name__ == "__main__":
    black()