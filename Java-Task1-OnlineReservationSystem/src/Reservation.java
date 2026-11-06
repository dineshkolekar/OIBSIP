public class Reservation {
    public final long pnr;
    public final String passengerName;
    public final int trainNo;
    public final String trainName;
    public final String classType;
    public final String journeyDate;
    public final String source;
    public final String destination;

    public Reservation(long pnr, String passengerName, int trainNo, String trainName,
                       String classType, String journeyDate, String source, String destination) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNo = trainNo;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.source = source;
        this.destination = destination;
    }

    public String toDisplay() {
        return "PNR Number      : " + pnr + "\n"
             + "Passenger Name  : " + passengerName + "\n"
             + "Train           : " + trainNo + " - " + trainName + "\n"
             + "Class           : " + classType + "\n"
             + "Date of Journey : " + journeyDate + "\n"
             + "From            : " + source + "\n"
             + "To              : " + destination;
    }
}