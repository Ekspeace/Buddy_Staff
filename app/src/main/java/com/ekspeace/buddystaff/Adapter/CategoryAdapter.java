package com.ekspeace.buddystaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.Category;
import com.ekspeace.buddystaff.Model.EventBus.DeleteEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.firebase.Timestamp;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private static ClickListener clickListener;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category_recycler, parent, false);
        return new CategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryAdapter.MyViewHolder holder, int position) {
       Category category = categoryList.get(position);
       holder.database_category_name.setText(category.getName());
       holder.database_category_delete.setOnClickListener(view -> {
           EventBus.getDefault().postSticky(new DeleteEvent(category));
       });
    }

    @Override
    public int getItemCount() {
       return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView database_category_delete;
        private TextView database_category_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            database_category_delete = itemView.findViewById(R.id.database_category_delete);
            database_category_name = itemView.findViewById(R.id.database_category_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view,getAdapterPosition());
        }

    }
    public void setOnItemClickListener(CategoryAdapter.ClickListener clickListener){
        CategoryAdapter.clickListener = clickListener;
    }
    public interface ClickListener{
        void onClick(View v, int pos);
    }

}
