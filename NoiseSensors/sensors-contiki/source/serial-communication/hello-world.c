#include "contiki.h"
 #include "dev/serial-line.h"
 #include <stdio.h>

 PROCESS(test_serial, "Serial line test process");
 AUTOSTART_PROCESSES(&test_serial);

 PROCESS_THREAD(test_serial, ev, data)
 {
 	 static struct etimer timer;
 	 
 	 
   PROCESS_BEGIN();
   
   etimer_set(&timer, CLOCK_SECOND * 2);

   for(;;) {
     PROCESS_YIELD();
     if(ev == serial_line_event_message) {
       printf("received line: %s\n", (char *)data);
     }
     PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
     etimer_reset(&timer);
   }
   PROCESS_END();
 }
