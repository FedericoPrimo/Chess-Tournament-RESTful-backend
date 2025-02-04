import os
import re
import csv

# Function to parse a PGN file and extract metadata for each game
def parse_pgn_file(pgn_content):
    # Split the PGN content into individual games using double newlines as the separator
    games = pgn_content.split("\n\n")
    game_data = []
    
    # Regex pattern to extract metadata fields enclosed in square brackets (e.g., [Event "Live Chess"])
    metadata_pattern = re.compile(r"\[(\w+) \"(.*?)\"]")
    for game in games:
        # Extract all metadata key-value pairs from the current game
        metadata = dict(metadata_pattern.findall(game))
        if metadata:  # Only process games that contain metadata
            game_data.append(metadata)  # Append the extracted metadata to the game data list
    
    return game_data

# Function to convert one or more PGN files in a folder into a single CSV file
def convert_pgn_to_csv(pgn_folder, output_csv_file):
    all_games = []  # List to store data for all games
    
    # Iterate through all files in the specified folder
    for filename in os.listdir(pgn_folder):
        # Only process files with a .pgn extension
        if filename.endswith(".pgn"):
            # Open and read the PGN file
            with open(os.path.join(pgn_folder, filename), "r", encoding="utf-8") as pgn_file:
                pgn_content = pgn_file.read()
                # Parse the PGN content to extract game data
                games = parse_pgn_file(pgn_content)
                # Add the parsed game data to the cumulative list
                all_games.extend(games)
    
    # If there are any games to write, proceed to create the CSV file
    if all_games:
        # Extract all unique keys (fields) from the game metadata
        headers = set(key for game in all_games for key in game.keys())
        headers = list(headers)  # Convert the set to a list for ordering
        
        # Create and write to the CSV file
        with open(output_csv_file, "w", newline="", encoding="utf-8") as csv_file:
            writer = csv.DictWriter(csv_file, fieldnames=headers)  # Use DictWriter for structured writing
            writer.writeheader()  # Write the header row
            for game in all_games:
                writer.writerow(game)  # Write each game as a row in the CSV file

# Main entry point of the program
if __name__ == "__main__":
    # Path to the folder containing PGN files (replace with your actual folder path)
    pgn_folder = "pgn"
    # Path to the output CSV file (replace with your desired file path)
    output_csv_file = "output.csv"
    
    # Convert all PGN files in the specified folder into a single CSV file
    convert_pgn_to_csv(pgn_folder, output_csv_file)
    print(f"PGN files from {pgn_folder} have been successfully converted to {output_csv_file}")
