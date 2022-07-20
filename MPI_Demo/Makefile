CFLAGS = -Wall -lc++ -std=c++17

all: compile_mpi

compile_mpi: count_words.cpp 	
	mpicc $(CFLAGS) count_words.cpp -o count_words

run: count_words
	mpirun -np 4 count_words	

clean:
	rm count_words 
	rm output/file*.out
