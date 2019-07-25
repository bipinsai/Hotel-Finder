/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thehotelfinder.databaseutil;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.combine;
import com.mongodb.client.result.UpdateResult;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.bson.Document;
import thehotelfinder.frames.HotelCard;
import thehotelfinder.frames.MyBookingCard;
import thehotelfinder.MyDate;
import thehotelfinder.TheHotelFinder;


/**
 *
 * @author Rohith
 */
public class DBConnect {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public DBConnect(){
        try {
            mongoClient = new MongoClient("localhost", 27017);
            System.out.println("Database connection created");
            mongoClient.getAddress();
        }catch(MongoSocketOpenException e){
                    System.out.println("Database unavailable!");
        }catch (Exception e) {
            System.out.println("Database unavailable!");
            mongoClient.close();
            JOptionPane.showMessageDialog(TheHotelFinder.getLogRegFrame(),"Unable to connect to database");
            System.exit(0);
            return;
        }
        database = mongoClient.getDatabase("mydb");
        System.out.println("Connected to mydb.");
//        initRating();
    }
    
    public boolean addToWaitingList(String username, String hotelName, int noPeople, String checkIn, String checkOut){
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");
        FindIterable<Document> iterDoc = hotelCollection.find();
        
        ArrayList waitingList;
        for(Document d: iterDoc){
            if(d.get("name").equals(hotelName)){
                waitingList = (ArrayList) d.get("waitingList");
                ArrayList arrlist = new ArrayList();
                arrlist.add(username);
                arrlist.add(noPeople);
                arrlist.add(checkIn);
                arrlist.add(checkOut);
                waitingList.add(arrlist);
                hotelCollection.updateOne(Filters.eq("name",hotelName), Updates.set("waitingList", waitingList));
                return true;
            }
        }
        
        return false;
    }
    
    public ArrayList checkWaitingList(){
        ArrayList resList = new ArrayList();
        String username = TheHotelFinder.getCurUser().getUsername();
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");
        FindIterable<Document> iterDoc = hotelCollection.find();
        
        ArrayList waitingList;
        for(Document d: iterDoc){
            waitingList = (ArrayList)d.get("waitingList");
            if(!waitingList.isEmpty()){
                ArrayList user = (ArrayList)waitingList.get(0);
                if(user.get(0).equals(username)){
                    int noPeople = (int)user.get(1);
                    String checkIn = (String)user.get(2);
                    String checkOut = (String)user.get(3);
                    String hotelName = (String)d.get("name");
                    int roomsArr[] = getMaxRooms(hotelName, checkIn, checkOut);
                    int x= 0; int y=0;
                    for(x=0; x<=roomsArr[0]; x++){
                        for(y=0; y<=roomsArr[1]; y++){
                            if((x + 2*y) >= noPeople){
                                resList.add(username);
                                resList.add(noPeople);
                                resList.add(checkIn);
                                resList.add(checkOut);
                                resList.add(hotelName);
                                waitingList.remove(0);
                                hotelCollection.updateOne(Filters.eq("name",(String)d.get("name")), Updates.set("waitingList",waitingList));
                                return resList;
                            }
                        }
                    }
                    break;
                }
            }
        }
        
        return resList;
    }
    
    public Hotel getHotelByName(String hotelName){
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");
        
        FindIterable<Document> iterDoc = hotelCollection.find();

        for(Document d: iterDoc){
            if(d.get("name").equals(hotelName)){
                String name = (String)d.get("name");
                String city = (String)d.get("city");
                String state = (String)d.get("state");
                ArrayList noRoomsList = (ArrayList) d.get("noRooms");
                int noRooms[] = {0,0}; 
                noRooms[0] = (int)(double)(noRoomsList.get(0));
                noRooms[1] = (int)(double)(noRoomsList.get(1));
                ArrayList costArrList = (ArrayList)d.get("costArr");
                double costArr[] = {0,0}; 
                costArr[0] = (double)costArrList.get(0);
                costArr[1] = (double)costArrList.get(1);
                ArrayList ratingArr = (ArrayList)d.get("ratingArr");
                ArrayList hotelDetails = (ArrayList)d.get("details");
                ArrayList waitingList = (ArrayList)d.get("waitingList");
                
                return new Hotel(name, city, state, noRooms, costArr, ratingArr, hotelDetails, waitingList);
            }
        }
        
        
        return null;
    }
    
    public boolean cancelBooking(String bookingRef){
        MongoCollection<Document> bookingCollection = database.getCollection("booking");
        bookingCollection.deleteOne(Filters.eq("bookingRef",bookingRef));
        return true;
    }
    
    public boolean modifyBooking(String bookingRef, String newCheckIn, String newCheckOut, int newsingle, int newdouble){
        
        MongoCollection<Document> bookingCollection = database.getCollection("booking");
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");

        FindIterable<Document> biterDoc = bookingCollection.find();
        FindIterable<Document> hiterDoc = hotelCollection.find();
        for(Document bDoc: biterDoc){
            if(bDoc.get("bookingRef").equals(bookingRef)){
                for(Document hDoc: hiterDoc){
                    if(bDoc.get("hotel").equals(hDoc.get("name"))){
                        ArrayList costArr = (ArrayList)hDoc.get("costArr");
                        double totalAmount  = newsingle * (double)costArr.get(0) + newdouble*(double)costArr.get(1);
                        System.out.println("before update");
                        bookingCollection.updateOne(Filters.eq("bookingRef",bookingRef), combine(Updates.set("checkInDate", newCheckIn), 
                                                                                                 Updates.set("checkOutDate", newCheckOut),
                                                                                                 Updates.set("nsingle", newsingle),
                                                                                                 Updates.set("ndouble", newdouble),
                                                                                                 Updates.set("totalAmountPaid", totalAmount))
                        );
                        return true;
                    }
                }
                break;
            }
        }              
        
        return false;
    }
    
    public boolean giveRating(int rating, String hotelName, String bookingRef){
        MongoCollection<Document> collection = database.getCollection("hotels");
        FindIterable<Document> iterDoc = collection.find();
        
        MongoCollection<Document> bookingCollection = database.getCollection("booking");
        FindIterable<Document> biterDoc = bookingCollection.find();
        
        for(Document bd: biterDoc){
            if(bd.get("bookingRef").equals(bookingRef)){
                if((int)bd.get("rated") == 0){
                    for(Document d: iterDoc){
                        if(d.get("name").equals(hotelName)){
                            ArrayList ratingArr = (ArrayList)d.get("ratingArr");
                            ratingArr.add((double)rating);
                            collection.updateOne(Filters.eq("name", (String)d.get("name")), Updates.set("ratingArr", ratingArr));
                            bookingCollection.updateOne(Filters.eq("hotel", (String)d.get("name")), Updates.set("rated", (int)1));
                            break;
                        }
                    }        
                }else{
                    return false;
                }
                break;
            }
        }
        
        return true;
    }

    
    public boolean addBooking(Booking b){
        MongoCollection<Document> collection = database.getCollection("booking");
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");

        Document document = new Document("bookingRef",b.getBookingRef())
                                        .append("customer", b.getCustomer().getUsername())
                                        .append("hotel", b.getHotel().getName())
                                        .append("checkInDate", MyDate.toStringInit(b.getCheckInDate()))
                                        .append("checkOutDate", MyDate.toStringInit(b.getCheckOutDate()))
                                        .append("bookingDate", MyDate.toStringInit(b.getBookingDate()))
                                        .append("nsingle", b.getNoRoomsBooked()[0])
                                        .append("ndouble", b.getNoRoomsBooked()[1])
                                        .append("noPeople", b.getNoPeople())
                                        .append("proofType", b.getIdProof()[0])
                                        .append("proofValue", b.getIdProof()[1])
                                        .append("totalAmountPaid", b.getTotalAmountPaid())
                                        .append("rated", (int)0);
        collection.insertOne(document);
        return true;
    }
    
    private int arrMax(int arr[]){
        int m = 0;
        for(int x: arr){
            if(x>m) m = x;
        }
        return m;
    }
    
    public int[] getMaxRooms(String hotelName, String a, String b){
        int res[] = new int[2];
        
        int maxSingle = 0;
        int maxDouble = 0;
        
        Date aDate = MyDate.toDate(a);
        Date bDate = MyDate.toDate(b);
        int days = MyDate.getDays(a, b);
        int singleBookedArr[] = new int[days];
        int doubleBookedArr[] = new int[days];
        
        MongoCollection<Document> bookingCollection = database.getCollection("booking");
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");

        FindIterable<Document> biterDoc = bookingCollection.find();
        FindIterable<Document> hiterDoc = hotelCollection.find();

        for(Document hDoc: hiterDoc){
            if(hotelName.equals(hDoc.get("name"))){
                for(int i=0; i<days; i++){
                    Date x = MyDate.addDay(aDate, i);
                    Date y = MyDate.addDay(aDate, i+1);
                    for(Document bDoc: biterDoc){
                        if(hotelName.equals(bDoc.get("hotel"))){
                            String checkIn = (String)bDoc.get("checkInDate");
                            String checkOut = (String)bDoc.get("checkOutDate");
                            Date checkInDate = MyDate.toDate(checkIn);
                            Date checkOutDate = MyDate.toDate(checkOut);
                            if(MyDate.hasOverlap(x, y, checkInDate, checkOutDate)){
                                int nsingle = (int)bDoc.get("nsingle");
                                int ndouble = (int)bDoc.get("ndouble");
                                singleBookedArr[i] += nsingle;
                                doubleBookedArr[i] += ndouble;
                            }
                        }
                    }
                    
                }
//                System.out.print("\nSingle arr: ");
//                for(int x: singleBookedArr) {System.out.print(x+" ");}
//                System.out.print("\ndouble arr: ");
//                for(int x: doubleBookedArr) {System.out.print(x+" ");}
                maxSingle = arrMax(singleBookedArr);
                maxDouble = arrMax(doubleBookedArr);
                
                
//                for(Document bDoc: biterDoc){
//                    if(hotelName.equals(bDoc.get("hotel"))){
//                        String checkIn = (String)bDoc.get("checkInDate");
//                        String checkOut = (String)bDoc.get("checkOutDate");
//                        Date checkInDate = MyDate.toDate(checkIn);
//                        Date checkOutDate = MyDate.toDate(checkOut);
//                        if(((checkInDate.after(aDate) || checkInDate.equals(aDate)) && checkInDate.before(bDate) && (checkOutDate.equals(bDate) || checkOutDate.after(bDate))) ||
//                           ((checkInDate.before(aDate) || checkInDate.equals(aDate)) && (checkOutDate.before(bDate) || checkOutDate.equals(bDate)) && checkOutDate.after(aDate)) ||
//                           ((checkInDate.after(aDate) || checkInDate.equals(aDate)) && checkInDate.before(bDate) && (checkOutDate.before(bDate) || checkOutDate.equals(bDate)) && checkOutDate.after(aDate)) ||
//                           (checkInDate.before(aDate) && checkOutDate.after(bDate)) ||
//                           (checkInDate.equals(aDate) && checkOutDate.equals(bDate))
//                          ){
//                            int nsingle = (int)bDoc.get("nsingle");
//                            int ndouble = (int)bDoc.get("ndouble");
//                                maxSingle += nsingle;
//                                maxDouble += ndouble;
////                            if(maxSingle < nsingle) maxSingle = nsingle;
////                            if(maxDouble < ndouble) maxDouble = ndouble;
//                        }
                        
//                        if(MyDate.hasOverlap(aDate, bDate, checkInDate, checkOutDate)){
//                            int nsingle = (int)bDoc.get("nsingle");
//                            int ndouble = (int)bDoc.get("ndouble");
//                            maxSingle += nsingle;
//                            maxDouble += ndouble;
//                        }

//                        
//                    }
//                }
                ArrayList hotelRooms = (ArrayList)hDoc.get("noRooms");
                res[0] = (int)(double)(hotelRooms.get(0)) - maxSingle;
                res[1] = (int)(double)(hotelRooms.get(1)) - maxDouble;
                break;
            }
        }        
        
        return res;
    }
    
    public ArrayList getBookings(String username){
        ArrayList bookingList = new ArrayList();
        MongoCollection<Document> collection = database.getCollection("booking");
        MongoCollection<Document> hotelCollection = database.getCollection("hotels");

        FindIterable<Document> iterDoc = collection.find();
        FindIterable<Document> hiterDoc = hotelCollection.find();

        for(Document d: iterDoc){
            if(username.equals(d.get("customer"))){
                String bookingRef = (String)d.get("bookingRef");
                String hotelName = (String)d.get("hotel");
                String city = "";
                String state = "";
                for(Document hd: hiterDoc){
                    if((hd.get("name")).equals(hotelName)){
                        city += (String)hd.get("city");
                        state += (String)hd.get("state");
                        break;
                    }
                }
                int nsingle = (int)d.get("nsingle");
                int ndouble = (int)d.get("ndouble");
                String checkIn = (String)d.get("checkInDate");
                String checkOut = (String)d.get("checkOutDate");
                String bookingDate = (String)d.get("bookingDate");
                double totalAmount = (double)d.get("totalAmountPaid");
                bookingList.add(new MyBookingCard(bookingRef, hotelName, city, state, checkIn, checkOut, bookingDate,
                                                  nsingle, ndouble, totalAmount));
            }
        }
        Collections.reverse(bookingList);
        return bookingList;
    }
    
    public ArrayList getHotels(String location, int noRoomsUser, int noPeople, int nights ,Date checkInDate, Date checkOutDate){
        ArrayList hotelList = new ArrayList();
        MongoCollection<Document> collection = database.getCollection("hotels");
        FindIterable<Document> iterDoc = collection.find();
        
        for(Document d: iterDoc){
            if(location.equals(d.get("city"))){
                String name = (String)d.get("name");
                String city = (String)d.get("city");
                String state = (String)d.get("state");
                ArrayList noRoomsList = (ArrayList) d.get("noRooms");
                int noRooms[] = {0,0}; 
                noRooms[0] = (int)(double)(noRoomsList.get(0));
                noRooms[1] = (int)(double)(noRoomsList.get(1));
                ArrayList costArrList = (ArrayList)d.get("costArr");
                double costArr[] = {0,0}; 
                costArr[0] = (double)costArrList.get(0);
                costArr[1] = (double)costArrList.get(1);
                ArrayList ratingArr = (ArrayList)d.get("ratingArr");
                ArrayList hotelDetails = (ArrayList)d.get("details");
                ArrayList waitingList = (ArrayList)d.get("waitingList");
                System.out.println("added: " + name);
                
                Hotel hotel = new Hotel(name, city, state, noRooms, costArr, ratingArr, hotelDetails, waitingList);
                hotelList.add(new HotelCard(hotel, noRoomsUser, noPeople, nights,checkInDate,checkOutDate));
            }
        }
        return hotelList;
    }
    
    public boolean registerUser(User u){
        MongoCollection<Document> collection = database.getCollection("users");
        
        FindIterable<Document> iterDoc = collection.find();
        for(Document d: iterDoc){
            if(u.getUsername().equals(d.get("username"))){
                TheHotelFinder.getLogRegFrame().showMessage("Username already exists.");
                return false;
            }else if(u.getEmail().equals(d.get("email"))){
                TheHotelFinder.getLogRegFrame().showMessage("Email already exists.");
                return false;
            }
        }
        Document document = new Document("name", u.getName())
        //.append("id", u.getId())
        .append("dob", u.getDob())
        .append("street", u.getAddress()[0])
        .append("city", u.getAddress()[1])
        .append("state", u.getAddress()[2])                
        .append("email", u.getEmail())
        .append("username", u.getUsername())
        .append("password", u.getPassword());
        collection.insertOne(document);
        System.out.println("Inserted");
        return true;
    }
    
    public boolean loginUser(String username, String password){
        MongoCollection<Document> collection = database.getCollection("users");
        FindIterable<Document> iterDoc = collection.find(); 
        for(Document d:iterDoc){
            if(username.equals(d.get("username"))){
                if(password.equals(d.get("password"))){
                    TheHotelFinder.setCurUser(getUser(d));
                    return true;
                }
            }
        }
       
        return false;
    }
    
    public User getUser(Document d){
        String address[] = new String[3];
        address[0] = (String)d.get("street");
        address[1] = (String)d.get("city");
        address[2] = (String)d.get("state");
        User u = new User((String)d.get("name"), (String)d.get("dob"), 
                          address, (String)d.get("email"),
                          (String)d.get("username"),(String)d.get("password"));
        
        return u;
    }
    
    public void closeConnection(){
        mongoClient.close();
        System.out.println("Connection closed.");
    }

}
