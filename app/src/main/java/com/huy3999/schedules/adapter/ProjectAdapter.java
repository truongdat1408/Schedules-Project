package com.huy3999.schedules.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huy3999.schedules.MainActivity;
import com.huy3999.schedules.NewProject;
import com.huy3999.schedules.R;
import com.huy3999.schedules.apiservice.BaseApiService;
import com.huy3999.schedules.apiservice.UtilsApi;
import com.huy3999.schedules.fragment.DragBoardFragment;
import com.huy3999.schedules.model.Project;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Project> arrProjects;
    private ArrayList<Project> arrProjectsAll;
    private Context mContext;
    private BaseApiService mApiService;


    public ProjectAdapter(ArrayList<Project> arrProjects, Context mContext) {
        Log.d("Constructor", arrProjects.toString());
        this.arrProjects = arrProjects;
        this.arrProjectsAll = new ArrayList<>(arrProjects);
        this.mContext = mContext;
        mApiService = UtilsApi.getAPIService();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        ProjectAdapter.MyViewHolder vh = new ProjectAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvName.setText(arrProjects.get(position).name);
        holder.tvCollaborators.setText(arrProjects.get(position).member.size() + " collaborators");
        holder.itemProject.setBackgroundColor(Color.parseColor(arrProjects.get(position).color));
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.option);
                popupMenu.inflate(R.menu.menu_project);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.view:
                                DragBoardFragment dragBoardFragment = (DragBoardFragment.newInstance(arrProjects.get(position)));
                                FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.nav_host_fragment, dragBoardFragment).commit();
                                break;
                            case R.id.edit:
                                Intent intent = new Intent(mContext, NewProject.class);
                                intent.putExtra("id", arrProjects.get(position).id);
                                mContext.startActivity(intent);
                                break;
                            case R.id.delete:
                                mApiService.deleteProject(arrProjects.get(position).id)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                                            @Override
                                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Response<ResponseBody> response) {
                                                Toast.makeText(mContext, "Delete successfully", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                                if (e instanceof HttpException) {
                                                    HttpException error = (HttpException)e;
                                                    try {
                                                        Toast.makeText(mContext, error.response().errorBody().string(), Toast.LENGTH_SHORT).show();
                                                    } catch (IOException ioException) {
                                                        ioException.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DragBoardFragment dragBoardFragment = (DragBoardFragment.newInstance(arrProjects.get(position)));
                FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.nav_host_fragment, dragBoardFragment).commit(); }
        });
    }

    @Override
    public int getItemCount() {
        return arrProjects.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Project> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty()) {
                filteredList.addAll(arrProjectsAll);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Project project : arrProjectsAll) {
                    if(project.name.toLowerCase().contains(filterPattern)) {
                        filteredList.add(project);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        //run on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arrProjects.clear();
            arrProjects.addAll((Collection<? extends Project>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvName;
        public TextView tvCollaborators;
        public LinearLayout itemProject;
        public ImageView option;
        public CardView itemCard;

        public MyViewHolder(View view) {
            super(view);
            view = view;
            tvName = view.findViewById(R.id.tv_name);
            tvCollaborators = view.findViewById(R.id.tv_collaborators);
            itemProject = view.findViewById(R.id.item_project);
            option = view.findViewById(R.id.option);
            itemCard = view.findViewById(R.id.item_card);
        }
    }
}
