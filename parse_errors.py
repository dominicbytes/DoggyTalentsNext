import re
from collections import defaultdict

error_pattern = re.compile(r'^(.*?\.java):(\d+): error: (.*)$')

errors = []
with open('compile_errors.log', 'r', encoding='utf-8') as f:
    for line in f:
        match = error_pattern.match(line)
        if match:
            file_path = match.group(1)
            line_num = match.group(2)
            message = match.group(3)
            errors.append({
                'file': file_path,
                'line': line_num,
                'message': message
            })

# Categorize errors
categories = defaultdict(list)
for error in errors:
    msg = error['message']
    if 'cannot find symbol' in msg:
        categories['cannot find symbol'].append(error)
    elif 'does not override or implement a method from a supertype' in msg:
        categories['does not override or implement a method from a supertype'].append(error)
    elif 'incompatible types' in msg:
        categories['incompatible types'].append(error)
    elif 'package' in msg and 'does not exist' in msg:
        categories['package does not exist'].append(error)
    elif 'method does not override or implement a method from a supertype' in msg:
        categories['method does not override or implement a method from a supertype'].append(error)
    elif 'no suitable method found' in msg:
        categories['no suitable method found'].append(error)
    elif 'has private access in' in msg:
        categories['has private access'].append(error)
    elif 'is not abstract and does not override abstract method' in msg:
        categories['is not abstract and does not override abstract method'].append(error)
    elif 'cannot be applied to given types' in msg:
        categories['cannot be applied to given types'].append(error)
    else:
        # Try to generalize the message
        # e.g., "class X is public, should be declared in a file named X.java"
        categories['other'].append(error)

# Write report
with open('MINECRAFT_MIGRATION_PLAN.md', 'w', encoding='utf-8') as f:
    f.write('# Minecraft 26.1.2 Migration Plan\n\n')
    f.write('## Error Summary\n\n')
    f.write(f'Total Errors: {len(errors)}\n\n')
    
    for category, errs in sorted(categories.items(), key=lambda x: len(x[1]), reverse=True):
        f.write(f'### {category} ({len(errs)} errors)\n')
        
        # Group by file
        file_errors = defaultdict(list)
        for err in errs:
            file_errors[err['file']].append(err)
            
        for file_path, f_errs in sorted(file_errors.items(), key=lambda x: len(x[1]), reverse=True)[:10]: # Show top 10 files per category
            f.write(f'- `{file_path}`: {len(f_errs)} errors\n')
            # Show a few examples
            for err in f_errs[:2]:
                f.write(f'  - Line {err["line"]}: {err["message"]}\n')
        if len(file_errors) > 10:
            f.write(f'- ... and {len(file_errors) - 10} more files\n')
        f.write('\n')

print(f"Parsed {len(errors)} errors.")