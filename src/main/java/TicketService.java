import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class TicketService {

    private List<Ticket> tickets;

    public TicketService(TicketRepository ticketRepository) {
        this(ticketRepository.getTickets());
    }

    public Duration getMinFlightTime(){
        Ticket ticket = tickets.stream()
                .min(((o1, o2) -> calculateFlightTime(o1).compareTo(calculateFlightTime(o2)))).get();

        LocalDateTime departure = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime());

        return Duration.between(departure, arrival);
    }

    public Duration getMinFlightTime(List<Ticket> ticketList){
        Ticket ticket = ticketList.stream()
                .min(((o1, o2) -> calculateFlightTime(o1).compareTo(calculateFlightTime(o2)))).get();

        LocalDateTime departure = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime());

        return Duration.between(departure, arrival);
    }

    private Duration calculateFlightTime(Ticket ticket) {
        LocalDateTime departure = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime());

        return Duration.between(departure, arrival);
    }

    public int getAveragePrice(){
        AtomicInteger averagePrice = new AtomicInteger();

        tickets.forEach(ticket -> averagePrice.addAndGet(ticket.getPrice()));

        return averagePrice.get() / tickets.size();
    }

    public int getMedianPrice(){
        int result = 0;

        if (tickets.size() % 2 == 0){
             result += tickets.get(tickets.size()/2).getPrice();
             result += tickets.get(tickets.size()/2 + 1).getPrice();
             return result / 2;
        } else {
            result += tickets.get(tickets.size()/2).getPrice();
            return result;
        }

    }

    private HashSet<String> getCarrierList(){
        HashSet<String> carrierList = new HashSet<>();
        tickets.forEach(ticket -> carrierList.add(ticket.getCarrier()));
        return carrierList;
    }

    public HashMap<String, Duration> getMinimalTravelTimeBetweenCitiesForEachCarrier(){
        HashSet<String> carrierList = getCarrierList();
        List<Ticket> ticketList = new ArrayList<>();
        HashMap<String,Duration> durationMap = new HashMap<>();

        for (String carrier :
                carrierList) {
            for (Ticket ticket :
                    tickets) {
                if (ticket.getCarrier().equals(carrier)){
                    ticketList.add(ticket);
                }
            }
            durationMap.put(carrier, getMinFlightTime(ticketList));
            ticketList.clear();
        }

        return durationMap;
    }
}
