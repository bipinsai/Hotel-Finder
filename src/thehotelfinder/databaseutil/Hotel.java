/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thehotelfinder.databaseutil;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Rohith
 */
public class Hotel {
    String name;
    String city;
    String state;
    int noRoomsArr[];
    double costArr[];
    ArrayList ratingArr;
    double avgRating = 0;
    ArrayList hotelDetails;
    ArrayList waitingList;

    public Hotel(String name, String city, String state, int noRoomsArr[], double costArr[], ArrayList ratingArr, ArrayList hotelDetails, ArrayList waitingList) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.noRoomsArr = noRoomsArr;
        this.costArr = costArr;
        this.ratingArr = ratingArr;
        for(Object o: ratingArr){
            avgRating += (int)(double)o;
        }
        avgRating = avgRating/ratingArr.size();
        this.hotelDetails = hotelDetails;
        this.waitingList = waitingList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int[] getNoRoomsArr() {
        return noRoomsArr;
    }

    public void setNoRoomsArr(int[] noRoomsArr) {
        this.noRoomsArr = noRoomsArr;
    }

    public double[] getCostArr() {
        return costArr;
    }

    public void setCostArr(double[] costArr) {
        this.costArr = costArr;
    }

    public ArrayList getRatingArr() {
        return ratingArr;
    }

    public void setRatingArr(ArrayList ratingArr) {
        this.ratingArr = ratingArr;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public ArrayList getHotelDetails() {
        return hotelDetails;
    }

    public void setHotelDetails(ArrayList hotelDetails) {
        this.hotelDetails = hotelDetails;
    }

    public ArrayList getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList waitingList) {
        this.waitingList = waitingList;
    }

    

    
   
}
