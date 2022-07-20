#include <iostream>
#include <fstream>
#include <string>
#include <unordered_map>
#include <mpi.h>
#include <cmath>
#include <cctype>
#include <cstring>
#include <string.h>

#define MAX_LENGTH_WORD	30
#define TAG_FINISH		1
#define TAG_GENERAL		0
#define MASTER_NODE		0

using namespace std;

MPI_Datatype mpi_string_int_pair;

// Pair to be send between processes
struct string_int_pair {
	int count;
	char stringWord[MAX_LENGTH_WORD];
};

// Function to compute hash on string
long long compute_hash(const string& s) {
    const int p = 31;
    const int m = 1e9 + 9;
    long long hash_value = 0;
    long long p_pow = 1;
    const int n = s.length();
    for (int i = 0; i < n; ++i) {
            hash_value = (hash_value + (s[i] - 'a' + 1) * p_pow) % m;
            p_pow = (p_pow * p) % m;
        }
    return hash_value;
}

// Send a message only to the master node
void sendToMaster(unordered_map<string, int> HashMap) {

	unordered_map<string, int>::iterator itr;
	int TAG = TAG_GENERAL;

	for (itr = HashMap.begin(); itr != HashMap.end(); ++itr) {
		string_int_pair local_pair;
		local_pair.count = itr->second;

		itr->first.copy(local_pair.stringWord, sizeof(itr->first));
		local_pair.stringWord[itr->first.length()] = '\0';

		MPI_Send(
	    &local_pair,
	    1,
	    mpi_string_int_pair,
	    MASTER_NODE,
	    TAG,
	    MPI_COMM_WORLD
	    );

	}

// Send a final message with a different tag to tell the recv node the messages are done
	string_int_pair local_pair_void;
	local_pair_void.count = -1;
	MPI_Send(
		&local_pair_void,
		1,
		mpi_string_int_pair,
		MASTER_NODE,
		TAG_FINISH,
		MPI_COMM_WORLD);

}

// To partition the words across processes we need a hashing function to ensure *a somewhat* load balancing 
// without using a master process as an intermediate

// We choose to send by applying mod on the hash, which is an int

void sendOnHash(unordered_map<string, int> HashMap, int rank, int world_size) {
	unordered_map<string, int>::iterator itr;

	int DEST_PROC;
	int TAG = TAG_GENERAL;

	for (itr = HashMap.begin(); itr != HashMap.end(); ++itr) {
		long long hash = compute_hash(itr->first);

		DEST_PROC = hash % world_size;

		string_int_pair local_pair;
		local_pair.count = itr->second;

		itr->first.copy(local_pair.stringWord, sizeof(itr->first));
		local_pair.stringWord[itr->first.length()] = '\0';

		MPI_Send(
	    	&local_pair,
	    	1,
	    	mpi_string_int_pair,
	    	DEST_PROC,
	    	TAG,
	    	MPI_COMM_WORLD);
	}

// Send a final message with a different tag to tell the recv node the messages are done
	string_int_pair local_pair_void;
	local_pair_void.count = -1;
	for (int i = 0; i < world_size; i++) {

		MPI_Send(
		&local_pair_void,
		1,
		mpi_string_int_pair,
		i,
		TAG_FINISH,
		MPI_COMM_WORLD);
	}
}

// Print a hashmap to a new file
void printHashMap(unordered_map<string, int> HashMap, int rank) {
	unordered_map<string, int>::iterator itr;
	ofstream OutputFile;
	string FilePath = "output/file";
	string FileExtension = ".out";
	string NoOfFile = to_string(rank);

	FilePath.append(NoOfFile).append(FileExtension);

	OutputFile.open(FilePath, ios::out);
		if (!OutputFile) {
		cout << "File not created!";
	}

	OutputFile << "\nThe HashMap is : \n";
	OutputFile << "\tKEY\t\tELEMENTn";
	for (itr = HashMap.begin(); itr != HashMap.end(); ++itr) {
		OutputFile << '\t' << itr->first << '\t' << '\t' << itr->second << '\n';
	}
	OutputFile << endl;
}

int main(int argc, char *argv[]) {
	int rank, world_size;

	MPI_Init(&argc, &argv);

	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	printf("Hello, MPI! ");
	printf("I am process %d out of %d\n", rank, world_size);

	// Define custom Data Type for sending tuples of form (string, int)
	int lengths[2] = {1, MAX_LENGTH_WORD};

	MPI_Aint displacements[2];
	struct string_int_pair mpiPair;
	MPI_Aint base_address;
	MPI_Get_address(&mpiPair, &base_address);
	MPI_Get_address(&mpiPair.count, &displacements[0]);
	MPI_Get_address(&mpiPair.stringWord[0], &displacements[1]);
	displacements[0] = MPI_Aint_diff(displacements[0], base_address);
	displacements[1] = MPI_Aint_diff(displacements[1], base_address);

	MPI_Datatype types[2] = { MPI_INT, MPI_CHAR };
	MPI_Type_create_struct(2, lengths, displacements, types, &mpi_string_int_pair);
	MPI_Type_commit(&mpi_string_int_pair);

	// Each process opens a file
	// Computes the word count and stores it in ProcessLocalHashMap
	std::unordered_map<string, int> ProcessLocalHashMap;
	string ReadWord;
	string FilePath = "input/file";
	string FileExtension = ".in";
	string NoOfFile = to_string(rank);

	FilePath.append(NoOfFile).append(FileExtension);

	ifstream file(FilePath);

	if (!file) {
		cerr << "Incorrect FilePath. \n";
		return -1;
	}

// ---------------------------------------------------- CREATE LOCAL COUNT ----------------------------------------------------//

	while (file >> ReadWord)
	{
		// First do some cleaning of the text, strip the punctuation
		transform(ReadWord.begin(), ReadWord.end(), ReadWord.begin(), ::tolower);
		ReadWord.erase(std::remove_if(ReadWord.begin(), ReadWord.end(),  ::ispunct), ReadWord.end());

		// Check if word is already in the map
		// If the word is added for the first time
		if (ProcessLocalHashMap.count(ReadWord) < 1) {
			ProcessLocalHashMap.insert(pair<string, int>(ReadWord, 1));

		}
		// If the word is already in the map
		else {
			unordered_map<string, int>::iterator itr = ProcessLocalHashMap.find(ReadWord);
			if(itr != ProcessLocalHashMap.end()) 
				++itr->second;
		}
		
	}

	MPI_Barrier(MPI_COMM_WORLD); 

// ---------------------------------------------------- SEND TO WORKERS ----------------------------------------------------//

	// Send the word count to each process depending on the hash of the string
	sendOnHash(ProcessLocalHashMap, rank, world_size);

// ---------------------------------------------------- RECV FROM WORKERS ----------------------------------------------------//

	MPI_Request recv_request_proc;
	MPI_Status recv_status_proc;
	int flag_proc;

	std::unordered_map<string, int> PartialResultsHashMap;

	// we use a counter to decide when to stop receiving from the other processes
	int stop_receive = world_size;

	while (stop_receive > 0) {

		string_int_pair recv_local;
		MPI_Irecv(
			&recv_local, 
			1, 
			mpi_string_int_pair, 
			MPI_ANY_SOURCE, 
			MPI_ANY_TAG, 
			MPI_COMM_WORLD, 
			&recv_request_proc);

		MPI_Request_get_status(recv_request_proc, &flag_proc, &recv_status_proc);

		if (recv_status_proc.MPI_TAG == TAG_FINISH || recv_local.count == -1) {
			stop_receive--;
			continue;
			
		}

		if (PartialResultsHashMap.count(recv_local.stringWord) < 1) {
			PartialResultsHashMap.insert(pair<string, int>(recv_local.stringWord, recv_local.count));

		}
		// If the word is already in the map
		else {
			unordered_map<string, int>::iterator itr = PartialResultsHashMap.find(recv_local.stringWord);
			if(itr != PartialResultsHashMap.end()) 
				itr->second += recv_local.count;
		}

		printHashMap(PartialResultsHashMap, rank);

	}

	MPI_Wait(&recv_request_proc, &recv_status_proc);

// ---------------------------------------------------- SEND TO MASTER ----------------------------------------------------//

	MPI_Barrier(MPI_COMM_WORLD); 

	printHashMap(PartialResultsHashMap, rank);

	sendToMaster(PartialResultsHashMap);

	// This is the last step - Master Node collecting everything
	MPI_Barrier(MPI_COMM_WORLD); 
	
	if (rank == 0) {

		MPI_Request recv_request;
		MPI_Status recv_status;
		int flag;

		//Create a separate hashmap which will contain the final result
		std::unordered_map<string, int> MasterHashMap;

		int stop_receive = world_size;

		while (stop_receive > 0) {
			string_int_pair recv_local;
			MPI_Irecv(
				&recv_local, 
				1, 
				mpi_string_int_pair, 
				MPI_ANY_SOURCE, 
				MPI_ANY_TAG, 
				MPI_COMM_WORLD, 
				&recv_request);
			
			MPI_Request_get_status(recv_request, &flag, &recv_status);

			if (recv_status.MPI_TAG == TAG_FINISH){
				stop_receive--;
			}

			MasterHashMap.insert(pair<string, int>(recv_local.stringWord, recv_local.count));

		}

		MPI_Wait(&recv_request, &recv_status);

		printHashMap(MasterHashMap, 5);
	}
	
	// Free all the structs
	MPI_Type_free(&mpi_string_int_pair);
	MPI_Finalize();
	return 0;

}