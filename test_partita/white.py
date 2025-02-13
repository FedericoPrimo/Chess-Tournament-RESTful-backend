import requests
import time
from login import playerLogin

USER = "segreto_mattia"
URL = "http://localhost:8080/api/LiveMatch/insertMoves/segreto_mattia/segreto_mattia-calzolari_federico"
RESULT_URL = "http://localhost:8080/api/LiveMatch/insertMatchResult/segreto_mattia-calzolari_federico/segreto_mattia/A4"
INTERVAL = 3  # Secondi tra una mossa e l'altra
END_DELAY = 5  # Secondi di attesa prima di inviare il risultato della partita

# Lista di mosse (simulazione di una partita)
WHITE_MOVES = [
    "e2e4", "g1f3", "d2d4", "c2c3", "f1e2", "e1g1", "b1d2", "c1b2",
    "d1c2", "h2h3", "g2g3", "f3h2", "d2f3", "a2a3", "c3c4", "h3h4",
    "g3g4", "f3d2", "h1h3", "c4c5"
]

def white():
    token = playerLogin(USER)
    headers = {
        "Content-Type": "text/plain",
        "Authorization": f"Bearer {token}"
        } 
    for move in WHITE_MOVES:
        try:
            response = requests.post(URL, data=move, headers=headers)
            print(f"White - Move: {move} | Status: {response.status_code}, Response: {response.text}")
        except Exception as e:
            print(f"Errore: {e}")

        time.sleep(INTERVAL)  # Attendere prima della prossima mossa

    print("White ha terminato tutte le mosse.")

    # Attendere 5 secondi prima di inviare il risultato della partita
    time.sleep(END_DELAY)

    # Chiamata finale per inserire il risultato della partita
    try:
        result_response = requests.post(RESULT_URL, headers = {"Authorization": f"Bearer {token}"})
        print(f"Risultato partita inviato. Status: {result_response.status_code}, Response: {result_response.text}")
    except Exception as e:
        print(f"Errore durante l'invio del risultato della partita: {e}")

if __name__ == "__main__":
    white()
