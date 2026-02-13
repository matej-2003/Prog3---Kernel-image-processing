import random


n = 13
x = -10
y = 10

for _ in range(n):
	print(" ".join([str(random.randint(x, y)) for _ in range(n)]))