package com.ekspeace.buddystaff.Model.EventBus;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.Category;
import com.ekspeace.buddystaff.Model.Notification;
import com.ekspeace.buddystaff.Model.PickInformation;
public class DeleteEvent {
    private BookingInformation bookingInformation;
    private PickInformation pickInformation;
    private Category category;

    public DeleteEvent(BookingInformation bookingInformation) {
       this.bookingInformation = bookingInformation;
    }
    public DeleteEvent(PickInformation pickInformation) {
        this.pickInformation = pickInformation;
    }
    public DeleteEvent(Category category) {
        this.category = category;
    }
    public BookingInformation getBookingInformation() {
        return bookingInformation;
    }
    public void setBookingInformation(BookingInformation bookingInformation) {
        this.bookingInformation = bookingInformation;
    }
    public PickInformation getPickInformation() {
        return pickInformation;
    }

    public void setPickInformation(PickInformation pickInformation) {
        this.pickInformation = pickInformation;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
