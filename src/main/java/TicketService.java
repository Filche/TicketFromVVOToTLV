import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TicketService {

    private final List<Ticket> tickets;

    public TicketService(TicketRepository ticketRepository) {
        this(ticketRepository.getTickets());
    }

    public TicketService(List<Ticket> ticketList) {
        List<Ticket> VVOTLVTickets = new ArrayList<>();
        for (Ticket ticket :
                ticketList) {
            if (ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV")) {
                VVOTLVTickets.add(ticket);
            }
        }
        this.tickets = VVOTLVTickets;
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
        List<Ticket> ticketList = tickets.stream()
                .sorted((Comparator.comparingInt(Ticket::getPrice)))
                .collect(Collectors.toList());
        int result = 0;

        if (ticketList.size() % 2 == 0){
             result += ticketList.get(ticketList.size()/2).getPrice();
             result += ticketList.get(ticketList.size()/2 - 1).getPrice();
             return result / 2;
        } else {
            result += ticketList.get(ticketList.size()/2).getPrice();
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
