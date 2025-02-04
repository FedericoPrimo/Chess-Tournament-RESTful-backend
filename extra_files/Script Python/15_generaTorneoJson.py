import os
import json
from datetime import date, timedelta

# Function to calculate the second Sunday of February
def get_second_sunday_of_february(year):
    february_start = date(year, 2, 1)
    first_sunday = february_start + timedelta(days=(6 - february_start.weekday()))
    second_sunday = first_sunday + timedelta(days=7)
    return second_sunday.isoformat()

# Directory containing the RawMatches JSON files
raw_matches_dir = 'category_year_jsons/'
output_tournament_dir = 'tournament_jsons/'
os.makedirs(output_tournament_dir, exist_ok=True)

# List all JSON files in the directory
raw_match_files = [f for f in os.listdir(raw_matches_dir) if f.endswith('.json')]

# European capitals for location assignment
european_capitals = [
    "Paris", "Berlin", "Madrid", "Rome", "Amsterdam", "Vienna", "Prague",
    "Warsaw", "Budapest", "Stockholm", "Helsinki", "Copenhagen", "Lisbon",
    "Brussels", "Dublin", "Athens", "Oslo", "Reykjavik", "Tallinn", "Riga"
]
used_capitals = {}

# Process each RawMatch file
for raw_match_file in raw_match_files:
    # Extract category and year from the file name (e.g., Blitz_2004.json)
    file_name = os.path.splitext(raw_match_file)[0]
    category, year = file_name.split('_')
    year = int(year)

    # Determine location for the year
    if year not in used_capitals:
        used_capitals[year] = european_capitals.pop(0)
    location = used_capitals[year]

    # Load the RawMatches data
    raw_match_path = os.path.join(raw_matches_dir, raw_match_file)
    with open(raw_match_path, 'r') as json_file:
        raw_matches = json.load(json_file)

    # Create the tournament structure
    tournament = {
        "Edition": year,
        "Category": category,
        "Entry_closing_date": get_second_sunday_of_february(year),
        "Location": location,
        "Winner": None,
        "Raw-Matches": raw_matches
    }

    # Save the tournament data to a new JSON file
    output_tournament_file = os.path.join(output_tournament_dir, f"tournament_{category}_{year}.json")
    with open(output_tournament_file, 'w') as json_file:
        json.dump(tournament, json_file, indent=4)

print(f"Tournament files saved in: {output_tournament_dir}")
