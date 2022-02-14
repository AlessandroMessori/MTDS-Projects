#include "contiki.h"
#include <stdio.h>
#include <stdlib.h>
#define ARR_MAX_SIZE 6

static void updateSensorData(int *sensorData, int *arr_length, int counter)
{

  if (*arr_length >= ARR_MAX_SIZE)
  {
    int i;
    for (i = 0; i < ARR_MAX_SIZE - 1; i++)
    {
      sensorData[i] = sensorData[i + 1];
    }
  }
  else
  {
    *arr_length = *arr_length + 1;
  }

  sensorData[*arr_length - 1] = counter;
}

static void printSensorData(int *sensorData, int arr_length)
{

  printf("[ ");

  int i;
  for (i = 0; i < arr_length; i++)
  {
    printf("%d ", sensorData[i]);
  }

  printf("]\n");
}

static int computeArrayAvg(int *array, int arr_length)
{
  int i, total = 0;
  for (i = 0; i < arr_length; i++)
  {
    total += array[i];
  }

  return total / arr_length;
}

//generate number in range [min,max)
int random_number(int min, int max){
    int number = min + rand() % (max - min);
    return number; 
}


/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{

  static struct etimer timer;
  static int arr_length = 0;
  static int sensorData[ARR_MAX_SIZE];
  static int value;

  PROCESS_BEGIN();

  /* Setup a periodic timer that expires after 2 seconds. */
  etimer_set(&timer, CLOCK_SECOND * 2);

  while (1)
  {
    /* Wait for the periodic timer to expire and then restart the timer. */
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_reset(&timer);

    value = random_number(0, 100);

    updateSensorData(sensorData, &arr_length, value);
    printSensorData(sensorData, arr_length);
    int avg = computeArrayAvg(sensorData, arr_length);

    printf("Average: %d\n", avg);
  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
