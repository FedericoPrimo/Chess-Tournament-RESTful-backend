import requests
import time
from login import spectatorLogin

USER = "green_robert"
URL = "http://localhost:8080/api/LiveMatch/retrieveMoveList/segreto_mattia-calzolari_federico"  # Sostituisci con l'URL corretto
RESULT_URL = "http://localhost:8080/api/LiveMatch/matchDetails/segreto_mattia-calzolari_federico"
INTERVAL = 1  # Secondi tra le richieste
MAX_NO_CHANGE_COUNT = 7  # Numero massimo di tentativi senza cambiamenti
END_DELAY = 6

def spettatore():
    last_response = None  # Memorizza l'ultima risposta ricevuta
    no_change_count = 0  # Conta i tentativi senza cambiamenti
    token = spectatorLogin(USER)

    while True:
        try:
            headers = {
                "Content-Type": "application/json",
                "Authorization": f"Bearer {token}"
            }
            response = requests.get(URL, headers=headers)
            
            if response.status_code == 200:
                try:
                    data = response.json()  # Assumiamo che il server risponda con un JSON
                    if isinstance(data, list):  # Assicuriamoci che sia una lista di stringhe
                        data_string = ', '.join(data)  # Convertiamo la lista in una stringa leggibile

                        if data_string != last_response:
                            print(f"Nuova risposta ricevuta: {data}")
                            last_response = data_string
                            no_change_count = 0  # Resetta il contatore
                        else:
                            print("Nessun cambiamento nei dati ricevuti.")
                            no_change_count += 1

                        if no_change_count >= MAX_NO_CHANGE_COUNT:
                            print(f"Nessun cambiamento per {MAX_NO_CHANGE_COUNT} tentativi consecutivi. Terminazione.")
                            break

                    else:
                        print(f"Formato JSON inatteso: {data}")
                except ValueError:
                    print("Errore nel parsing del JSON.")
            else:
                print(f"Errore HTTP {response.status_code}: {response.text}")

        except Exception as e:
            print(f"Errore durante la richiesta: {e}")

        time.sleep(INTERVAL)
    
    time.sleep(END_DELAY)

    # Chiamata finale per inserire il risultato della partita
    try:
        result_response = requests.get(RESULT_URL, headers={"Authorization": f"Bearer {token}"})
        print(f"Risultato partita ottenuto. Status: {result_response.status_code}, Response: {result_response.text}")
    except Exception as e:
        print(f"Errore durante la ricezione del risultato della partita: {e}")

if __name__ == "__main__":
    spettatore()

