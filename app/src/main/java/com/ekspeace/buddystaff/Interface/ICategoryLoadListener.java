package com.ekspeace.buddystaff.Interface;

import com.ekspeace.buddystaff.Model.Category;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccessCarWash(List<Category> categoryList);
    void onCategoryLoadSuccessCleaning(List<Category> categoryList);
    void onCategoryLoadSuccessGardening(List<Category> categoryList);
    void onCategoryLoadFailed(String message);
}
