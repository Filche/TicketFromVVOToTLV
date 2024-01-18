import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        String departureCity = "Vladivostok";
        String arrivalCity = "Tel-Aviv";

        System.out.printf("Departure city: %s, Arrival city: %s" + System.lineSeparator() + "%n",
                departureCity, arrivalCity);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TicketRepository ticketRepository = objectMapper
                .readValue(new File("tickets.json"), TicketRepository.class);

        TicketService ticketService = new TicketService(ticketRepository);

        long minMinutes = ticketService
                .getMinFlightTime()
                .toMinutes();

        int averagePrice = ticketService.getAveragePrice();

        int medianPrice = ticketService.getMedianPrice();

        HashMap<String, Duration> minFlightList = ticketService.getMinimalTravelTimeBetweenCitiesForEachCarrier();

        Set<String> minFlightSetKeys = minFlightList.keySet();

        System.out.printf("Average price for flights between %s and %s: %d%n",
                departureCity, arrivalCity, averagePrice);

        System.out.printf("Median price for flights between %s and %s: %d%n",
                departureCity, arrivalCity, medianPrice);

        System.out.printf("The difference between the average price and the median price: %d%n",
                Math.abs(averagePrice - medianPrice));

        for (String key :
                minFlightSetKeys) {
            System.out.printf("The minimum time of carrier %s is %dh %dm%n",
                    key, minFlightList.get(key).toMinutes() / 60, minFlightList.get(key).toMinutes() % 60);
        }

    }
}