------------------------------------ Word Counter in MPI - DEMO ------------------------------------
MTDS Project 2021-2022
Authors: Simone Tagliente, Alessandro Messori, Daria-Maria Preda

Directory structure:
	- input: contains 4 text files which will be read by the processes
	- output: after running the code there would be created 5 new text files
		- outputX.txt which contains local results of each process after local counting
		- output5.txt which contains the final results gathered at the master level
	- main source code: count_words.cpp
	- all_words.txt: contains all the words which are actually spread in the 4 .in files
		- we used this file to compare the word count with the one for output5.txt
	- makefile

For compiling : make 
	- it automatically runs the code with 4 workers that can be increased
	- for more than 4 workers there are necessary more input files as each process will read the one
	containg its rank number


Implementation Details:
	STEP 1 - LOCAL COUNT FOR PARTIAL RESULTS
	- each process reads a different file (the one which contains its rank number)
	- each process will compute a "local" count of the words which will be stored in an unordered map structure (ProcessLocalHashMap) that contains pairs of type <string, int> with the words and their partial count
	- we use a barrier to wait for all the processes to finish this step 

	STEP 2 - PARTITIONING ACROSS PROCESSES
	- the words are partitioned across processes based on a hash which is computed on the string (compute_hash());
	- each process will call sendOnHash() which will call compute_hash() first and then send the data to the corresponding process computed with a mod between the hash and the number of processes
	- to send the pair in MPI we created a custom data type: MPI_Datatype mpi_string_int_pair based on a structure which contains a int for the count and an array of MAX_LENGTH_WORD for the string
	- this max length is the one mentioned to be known at compile time since it hard to know a-priori how long the string to be received is
	- the other problem solved in this step is the one of deciding how many messages are received - to solve this, after sending all the pairs to the corresponding processes, we send an additional message at the end with a different tag (TAG_FINISH) and an empty data pair;

	STEP 3 - RECEIVING THE PARTIAL RESULTS
	- at the receiving side, after sending all the partial results and waiting for all with another barrier, each process will receive the pairs corresponding to its rank decided by the hash;
	- each process creates a new local map: PartialResultsHashMap where it sums up the partial results

	STEP 4 - SEND TO MASTER AND COLLECTING THE FINAL RESULTS
	- after all the partial results are collected and after another barrier, all processes will send their new hashmap only to the master node
	- the master node creates a new hashmap, collects everything and writes to a new file

Design Choices:
	- to ease up the work with the strings and their count we chose an unordered map from the c++ library;
	- since the sends don't use large data (only a char array and a int) we used normal MPI_Send;
	- at the receiving side we chose MPI_Irecv so we can actually add the data in the hashmaps in while we wait for all the messages because it's non-blocking;
	- the barriers are necessary to ensure that we have all results/partial results ready from all processes before moving to a next step;
	- since we don't know the number of messages, an easy way to tell the received that all the data was sent is by checing the tag;






