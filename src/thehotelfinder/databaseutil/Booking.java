/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thehotelfinder.databaseutil;

import java.util.Date;
import thehotelfinder.MyDate;

/**
 *
 * @author Rohith
 */
public class Booking {
    String bookingRef; 
    User customer;
    Hotel hotel;
    Date checkInDate;
    Date checkOutDate;
    Date bookingDate;
    int noRoomsBooked[];
    int noPeople;
    String idProof[];
    double totalAmountPaid;
    int rated = 0;

    public Booking(User customer, Hotel hotel, int noRoomsBooked[], int noPeople, Date checkInDate, Date checkOutDate, String proofType, String proofValue) {
        this.bookingDate = new Date();
        this.bookingRef = MyDate.getRefString(bookingDate);
        this.customer = customer;
        this.hotel = hotel;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.noRoomsBooked = noRoomsBooked;
        this.noPeople = noPeople;
        this.idProof = new String[2];
        idProof[0] = proofType;
        idProof[1] = proofValue;
        totalAmountPaid = hotel.getCostArr()[0]*noRoomsBooked[0]  +  hotel.getCostArr()[1]*noRoomsBooked[1];
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public User getCustomer() {
        return customer;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public int[] getNoRoomsBooked() {
        return noRoomsBooked;
    }

    public int getNoPeople() {
        return noPeople;
    }

    public String[] getIdProof() {
        return idProof;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }
}
