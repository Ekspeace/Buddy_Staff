package com.ekspeace.buddystaff.Interface;

import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.PickInformation;

import java.util.List;

public interface IDeclineLoadListener {
    void onDeclineLoadSuccess(List<BookingInformation> bookingInformations, List<PickInformation> pickInformations);
    void onDeclineLoadFail(String message);
}
