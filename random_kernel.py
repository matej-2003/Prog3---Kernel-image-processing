import random
import math
import os

def trippy_kernel(height, width, scale=15):
	kernel = []
	center_i, center_j = height / 2, width / 2
	for i in range(height):
		row = []
		for j in range(width):
			dist = math.sqrt((i - center_i)**2 + (j - center_j)**2)
			val = (
				int(scale * math.sin(i + dist / 2) +
					scale * math.cos(j + dist / 3) +
					scale * math.sin(i*j / (dist + 1)) +
					random.randint(-scale, scale))
			)
			row.append(val)
		kernel.append(row)
	return kernel

# Configuration
num_kernels = 10
filename = "kernels.txt"

with open(filename, "w") as f:
	for _ in range(num_kernels):
		h = random.randint(3, 17) # Kept small for performance
		w = random.randint(3, 17)
		kernel = trippy_kernel(h, w)
		
		# Write metadata: height width
		f.write(f"{h} {w}\n")
		# Write kernel rows
		for row in kernel:
			f.write(" ".join(map(str, row)) + "\n")
		# Separator
		f.write("---\n")

print(f"Generated {num_kernels} kernels in {filename}")