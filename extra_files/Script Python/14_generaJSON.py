import pandas as pd
import os
import json

# Load the new dataset
file_path_new = 'Dataset_with_Decimal_Duration.csv'
df_new = pd.read_csv(file_path_new)

# Create a directory to store JSON files for each category and year
output_dir_new = 'category_year_jsons/'
os.makedirs(output_dir_new, exist_ok=True)

# Iterate through each unique combination of category and year
for (category, year), group in df_new.groupby(['Category', 'Date']):
    # Define the path for the JSON file directly in the output directory
    json_path = os.path.join(output_dir_new, f"{category}_{year}.json")
    
    # Save the group as a JSON file
    group_records = group.to_dict(orient='records')
    with open(json_path, 'w') as json_file:
        json.dump(group_records, json_file, indent=4)

output_dir_new
