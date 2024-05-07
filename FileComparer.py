RED = '\033[91m'
GREEN = '\033[92m'
YELLOW = '\033[93m'
BLUE = '\033[94m'
MAGENTA = '\033[95m'
CYAN = '\033[96m'
RESET = '\033[0m'

import re

def is_float(s):
    return bool(re.match(r"^-?\d+\.\d+$", s))

def compare_files_ordered(file1, file2):
    with open(file1, 'r') as f1, open(file2, 'r') as f2:
        file1_lines = f1.readlines()
        file2_lines = f2.readlines()

    # Omit the last line if it's a float
    if file2_lines and is_float(file2_lines[-1].strip()):
        file2_lines.pop()

    max_length = max(len(file1_lines), len(file2_lines))

    for i in range(max_length):
        line1 = file1_lines[i] if i < len(file1_lines) else None
        line2 = file2_lines[i] if i < len(file2_lines) else None

        if line1 != line2:
            return max(0, i - 2), min(i + 3, max_length)

    return None

def visualize_differences(file1, file2, start, end):
    with open(file1, 'r') as f1, open(file2, 'r') as f2:
        file1_lines = f1.readlines()
        file2_lines = f2.readlines()

    middle_index = start + (end - start) // 2  # Calculate the index of the middle line

    for i in range(start, end):
        line1_text = file1_lines[i].strip() if i < len(file1_lines) else ""
        line2_text = file2_lines[i].strip() if i < len(file2_lines) else ""

        if i == middle_index:
            # Print the middle line in blue
            print(f"{BLUE}Line {i+1}: {RESET}")
            print(f"{BLUE}{file1:<50} | {file2}{RESET}")
            print(f"{BLUE}{line1_text:<50} | {line2_text}{RESET}")
        else:
            print(f"Line {i+1}:")
            print(f"{file1:<50} | {file2}")
            print(f"{line1_text:<50} | {line2_text}")

        print("-" * 100)

file1 = 'src/output.txt'
file2 = 'src/output9.txt'


difference_range = compare_files_ordered(file1, file2)

if difference_range:
    start, end = difference_range
    print("Context around the first difference found:")
    visualize_differences(file1, file2, start, end)
else:
    print("The files are identical (including line order).")
