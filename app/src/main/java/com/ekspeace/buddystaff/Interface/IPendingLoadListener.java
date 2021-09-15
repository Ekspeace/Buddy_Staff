package com.ekspeace.buddystaff.Interface;

import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.PickInformation;

import java.util.List;

public interface IPendingLoadListener {
    void onPendingLoadSuccess(List<BookingInformation> bookingInformations, List<PickInformation> pickInformations);
    void onPendingLoadFail(String message);
}
