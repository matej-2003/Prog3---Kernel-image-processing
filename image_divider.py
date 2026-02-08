import multiprocessing
import math


w, h = 184, 274

def make_chunks(size, n):
	chunks = []

	r = size % n
	chunk_size = math.floor((size - r) / n)

	for i in range(0, size-r, chunk_size):
		chunks.append([i, i + chunk_size])
	
	chunks[-1][1] += r
	print(chunks)


def find_chunk_sizes(thread_n):
	factors = []

	for i in range(1, math.ceil(thread_n/2)):
		if thread_n % i == 0:
			factors.append([i, int(thread_n/i)])

	bc = factors[0]
	min_df = thread_n

	for w_f, h_f in factors[0:]:
		df = abs(w_f - h_f)
		if df  < min_df:
			min_df = df
			bc = [w_f, h_f]
	
	a, b = bc
	if (w % a) + (h % b) < (w % b) + (h % a):
		return [a, b]
	return [b, a]

thread_n = multiprocessing.cpu_count()
print(thread_n)

a, b = find_chunk_sizes(thread_n)

make_chunks(w, a)
make_chunks(h, b)
