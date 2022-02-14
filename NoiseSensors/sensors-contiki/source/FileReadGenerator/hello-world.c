/*
 * Copyright (c) 2006, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 */

/**
 * \file
 *         A very simple Contiki application showing how Contiki programs look
 * \author
 *         Adam Dunkels <adam@sics.se>
 */

#include "contiki.h"
#include "cfs/cfs.h"
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

/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{

  static struct etimer timer;
  static int arr_length = 0;
  static int sensorData[ARR_MAX_SIZE];
  static int fd;
  static char buf[500];
  static char num[3];
  static int cursor = 0;
  static int subCursor;
  static int done = 0;
  static int value;

  PROCESS_BEGIN();

  /* Open a file. */
  fd = cfs_open("measure", CFS_READ);
  if(fd >= 0) {
    cfs_seek(fd, 0, CFS_SEEK_SET);
    cfs_read(fd, buf, sizeof(buf));
  }
  else{
    printf("Cannot read the file\n");
  }

  /* Setup a periodic timer that expires after 2 seconds. */
  etimer_set(&timer, CLOCK_SECOND * 2);

  while (1)
  {
    /* Wait for the periodic timer to expire and then restart the timer. */
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_reset(&timer);

    subCursor = 0;
    while(done == 0){
      if(buf[cursor] == '\n'){
        num[subCursor] = '\0';
        value = atoi(num);
        done = 1;
      }
      else{
        num[subCursor] = buf[cursor];
      }
      cursor++;
      subCursor++;
    }
    done = 0;

    updateSensorData(sensorData, &arr_length, value);
    printSensorData(sensorData, arr_length);
    int avg = computeArrayAvg(sensorData, arr_length);

    printf("Average: %d\n", avg);
  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
