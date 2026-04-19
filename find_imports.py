import os
import re
import glob
import csv

# Directories containing extracted sources
MC_SOURCES = '/tmp/minecraft_sources'
NF_SOURCES = '/tmp/neoforge_sources'

# Find all java files in the project
project_java_files = glob.glob('src/main/java/**/*.java', recursive=True)

# Regex to match imports
import_pattern = re.compile(r'^import\s+(net\.minecraft\.[a-zA-Z0-9_.]+|net\.neoforged\.[a-zA-Z0-9_.]+|com\.mojang\.[a-zA-Z0-9_.]+);')

unique_imports = set()

for file_path in project_java_files:
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            match = import_pattern.match(line.strip())
            if match:
                unique_imports.add(match.group(1))

# Build a map of ClassName -> List of full package paths in the new sources
class_to_new_paths = {}
for src_dir in [MC_SOURCES, NF_SOURCES]:
    if not os.path.exists(src_dir):
        print(f"Warning: Source directory {src_dir} does not exist.")
        continue
    for root, _, files in os.walk(src_dir):
        for file in files:
            if file.endswith('.java'):
                class_name = file[:-5]
                # Calculate package path
                rel_path = os.path.relpath(os.path.join(root, file), src_dir)
                package_path = rel_path[:-5].replace(os.sep, '.')
                if class_name not in class_to_new_paths:
                    class_to_new_paths[class_name] = []
                if package_path not in class_to_new_paths[class_name]:
                    class_to_new_paths[class_name].append(package_path)

mapping = []

for imp in unique_imports:
    # Check if the import exists in the new sources
    rel_file_path = imp.replace('.', os.sep) + '.java'
    exists = os.path.exists(os.path.join(MC_SOURCES, rel_file_path)) or \
             os.path.exists(os.path.join(NF_SOURCES, rel_file_path))
    
    if not exists:
        class_name = imp.split('.')[-1]
        new_paths = class_to_new_paths.get(class_name, [])
        
        if len(new_paths) == 1:
            mapping.append((imp, new_paths[0]))
        elif len(new_paths) > 1:
            # Try to find the best match
            # Prefer exact class name match in a similar package, or just the first one in net.minecraft/net.neoforged
            best_match = new_paths[0]
            for p in new_paths:
                if 'net.minecraft' in p or 'net.neoforged' in p:
                    best_match = p
                    # If the package structure is somewhat similar, prefer it
                    if imp.split('.')[2] in p: # e.g. 'world', 'client', 'client'
                        best_match = p
                        break
            mapping.append((imp, best_match))
        else:
            mapping.append((imp, "NOT_FOUND"))

# Write to CSV
with open('import_mapping.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['Old Import', 'New Import'])
    for old, new in sorted(mapping):
        writer.writerow([old, new])

print(f"Found {len(unique_imports)} unique imports.")
print(f"Found {len(mapping)} missing imports.")
print("Mapping written to import_mapping.csv")