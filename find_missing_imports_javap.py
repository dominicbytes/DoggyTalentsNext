import os
import re
import csv
import subprocess

# Define JAR paths
MC_CLIENT_JAR = '/home/aptd/.gradle/caches/neoformruntime/artifacts/minecraft_26.1.2_client.jar'
NF_USERDEV_JAR = '/home/aptd/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/26.1.2.11-beta/21a77885683e2ff2cc48afaed92882bcc56956d0/neoforge-26.1.2.11-beta-userdev.jar'

def find_full_class_name_in_jar(simple_class_name, jar_path):
    """
    Searches for a class (simple name or inner class name) within a JAR file
    and returns its full package path (e.g., net.minecraft.nbt.NbtUtils) if found.
    """
    if not os.path.exists(jar_path):
        return None

    try:
        result = subprocess.run(
            ['jar', 'tf', jar_path],
            capture_output=True, text=True, check=True, errors='ignore'
        )
        for line in result.stdout.splitlines():
            if line.endswith('.class'):
                # Extract the simple class name from the .class file path
                # e.g., net/minecraft/nbt/NbtUtils.class -> NbtUtils
                # e.g., com/mojang/blaze3d/vertex/VertexFormat$Mode.class -> VertexFormat$Mode
                file_class_name = line.split('/')[-1].replace('.class', '')
                
                if file_class_name == simple_class_name:
                    # Convert path to package name
                    package_path = line.replace('/', '.').replace('.class', '')
                    return package_path
                # Also check for inner classes where simple_class_name is just "Mode"
                # and file_class_name is "VertexFormat$Mode"
                if '$' in file_class_name and file_class_name.endswith(f'${simple_class_name}'):
                    package_path = line.replace('/', '.').replace('.class', '')
                    return package_path

    except subprocess.CalledProcessError:
        pass
    return None

def update_missing_imports_javap():
    updated_mappings = []
    
    with open('import_mapping.csv', 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        updated_mappings.append(header) # Keep the header

        for row in reader:
            old_import, new_import = row[0], row[1]

            if new_import == "NOT_FOUND":
                # For inner classes like "com.mojang.blaze3d.vertex.VertexFormat.Mode"
                # The simple_class_name to search for would be "Mode"
                # The actual class file might be "VertexFormat$Mode.class"
                parts = old_import.split('.')
                simple_class_name = parts[-1] # e.g., Mode
                
                found_path = find_full_class_name_in_jar(simple_class_name, MC_CLIENT_JAR)
                if found_path:
                    new_import = found_path
                else:
                    found_path = find_full_class_name_in_jar(simple_class_name, NF_USERDEV_JAR)
                    if found_path:
                        new_import = found_path
            
            updated_mappings.append([old_import, new_import])

    with open('import_mapping.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerows(updated_mappings)
    
    print("Updated import_mapping.csv with javap results.")

update_missing_imports_javap()
