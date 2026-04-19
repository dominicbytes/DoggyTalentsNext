import os
import re
from collections import defaultdict

error_pattern = re.compile(r'^(.*?\.java):(\d+): error: (.*)$')

errors_by_file = defaultdict(list)
with open('compile_errors.log', 'r', encoding='utf-8') as f:
    for line in f:
        match = error_pattern.match(line)
        if match:
            file_path = match.group(1)
            line_num = match.group(2)
            message = match.group(3)
            errors_by_file[file_path].append({'line': line_num, 'message': message})

os.makedirs('migration_plan', exist_ok=True)

def get_clues(message):
    if 'cannot find symbol' in message:
        return "Check for renamed classes, methods, or fields in 26.1.2 mappings. If related to SynchedEntityData, update to use SynchedEntityData.Builder and StreamCodec."
    elif 'does not override or implement a method from a supertype' in message:
        return "The supertype method signature has changed. Check the new parameters or return type in the 26.1.2 source."
    elif 'no suitable method found' in message:
        return "Method signature changed. For GuiGraphics.blit, it may need a RenderPipeline. For RecipeBuilder, it may need HolderGetter<Item>."
    elif 'incompatible types' in message:
        return "Type mismatch. Check if generics changed, or if a class needs to implement a new interface (e.g., SyncedDataHolder)."
    elif 'cannot be applied to given types' in message:
        return "Arguments passed to the method are incorrect for 26.1.2. Check the new method signature."
    elif 'is not abstract and does not override abstract method' in message:
        return "A new abstract method was added to the superclass/interface. Implement the missing method (e.g., extractWidgetRenderState for GUI widgets)."
    elif 'has private access' in message:
        return "Field is now private. Use the corresponding getter/setter method."
    else:
        return "Analyze the specific error message and compare with 26.1.2 sources."

for file_path, errs in errors_by_file.items():
    # Extract relative path from src/main/java/ if possible
    rel_path = file_path
    if 'src/main/java/' in file_path:
        rel_path = file_path.split('src/main/java/')[1]
    elif 'src/api/java/' in file_path:
        rel_path = file_path.split('src/api/java/')[1]
        
    safe_name = rel_path.replace('/', '_').replace('\\', '_')
    
    plan_path = os.path.join('migration_plan', f"{safe_name}.md")
    
    with open(plan_path, 'w', encoding='utf-8') as f:
        f.write(f"# Migration Plan for `{file_path}`\n\n")
        f.write(f"Total Errors: {len(errs)}\n\n")
        
        errors_by_msg = defaultdict(list)
        for err in errs:
            errors_by_msg[err['message']].append(err['line'])
            
        for msg, lines in errors_by_msg.items():
            f.write(f"## Error: {msg}\n")
            # Limit lines displayed if there are too many
            if len(lines) > 20:
                lines_str = ', '.join(lines[:20]) + f" ... and {len(lines)-20} more"
            else:
                lines_str = ', '.join(lines)
            f.write(f"- **Lines:** {lines_str}\n")
            f.write(f"- **Suggested Fix:** {get_clues(msg)}\n\n")

print(f"Generated {len(errors_by_file)} file plans in migration_plan/")
