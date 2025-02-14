import requests
from login import managerLogin

USER = "mustang_roy"
RESULT_URL = "http://localhost:8080/api/LiveMatch/insertMatchResult/segreto_mattia-calzolari_federico/segreto_mattia/A4"

def insertWin():
    token = managerLogin(USER)
    # Chiamata finale per inserire il risultato della partita
    try:
        result_response = requests.post(RESULT_URL, headers = {"Authorization": f"Bearer {token}"})
        print(f"Risultato partita inviato. Status: {result_response.status_code}, Response: {result_response.text}")
    except Exception as e:
        print(f"Errore durante l'invio del risultato della partita: {e}")

if __name__ == "__main__":
    insertWin()
