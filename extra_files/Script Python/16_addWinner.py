import pandas as pd

def add_winner_column(input_dataset_path, output_dataset_path):
    # Carica il dataset dal file CSV
    df = pd.read_csv(input_dataset_path)

    # Aggiungi la colonna 'winner' in base al risultato della partita
    def determine_winner(row):
        if row['Result'] == '1-0':
            return row['White']
        elif row['Result'] == '0-1':
            return row['Black']
        elif row['Result'] == '1/2-1/2':
            return 'draw'
        else:
            return 'unknown'

    df['Winner'] = df.apply(determine_winner, axis=1)

    # Salva il dataset modificato in un nuovo file CSV
    df.to_csv(output_dataset_path, index=False)

if __name__ == "__main__":
    input_dataset_path = 'Final.csv'  # Cambia il percorso al tuo file di input
    output_dataset_path = 'Final_with_winner.csv'  # Nome del file CSV di output
    add_winner_column(input_dataset_path, output_dataset_path)
    print(f"File CSV '{output_dataset_path}' generato con successo.")
