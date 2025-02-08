import os
import json

# Directory containing the tournament JSON files
tournament_dir = 'tournament_jsons/'

# Read all JSON files in the directory
tournament_files = [f for f in os.listdir(tournament_dir) if f.endswith('.json')]
documents = []

for tournament_file in tournament_files:
    file_path = os.path.join(tournament_dir, tournament_file)
    with open(file_path, 'r') as json_file:
        document = json.load(json_file)
        documents.append(document)

# Sort the documents by year (Edition)
documents = sorted(documents, key=lambda doc: doc['Edition'])

# Generate the insertMany query
insert_query = "db.tournaments.insertMany([\n"
for doc in documents:
    insert_query += f"    {json.dumps(doc, indent=4)},\n"
insert_query = insert_query.rstrip(",\n") + "\n]);"

# Save the query to a file
query_file_path = 'insert_tournaments_query_sorted.js'
with open(query_file_path, 'w') as query_file:
    query_file.write(insert_query)

print(f"InsertMany query saved to: {query_file_path}")
