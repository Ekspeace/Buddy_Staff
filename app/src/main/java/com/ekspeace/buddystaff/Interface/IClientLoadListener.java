package com.ekspeace.buddystaff.Interface;


import com.ekspeace.buddystaff.Model.Client;

import java.util.List;

public interface IClientLoadListener {
    void onClientLoadSuccess(List<Client> clients);
    void onClientLoadFailed(String message);
}
