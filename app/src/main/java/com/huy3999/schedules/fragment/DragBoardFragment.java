package com.huy3999.schedules.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huy3999.schedules.adapter.ProjectAdapter;
import com.huy3999.schedules.dragboardview.DragBoardView;
import com.huy3999.schedules.dragboardview.model.DragColumn;
import com.huy3999.schedules.dragboardview.model.DragItem;
import com.huy3999.schedules.R;
import com.huy3999.schedules.adapter.ColumnAdapter;
import com.huy3999.schedules.apiservice.BaseApiService;
import com.huy3999.schedules.apiservice.UtilsApi;
import com.huy3999.schedules.model.Entry;
import com.huy3999.schedules.model.Item;
import com.huy3999.schedules.model.Project;
import com.huy3999.schedules.roomcache.AppDatabase;
import com.huy3999.schedules.roomcache.AppExecutors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragBoardFragment extends Fragment {
    private static final String TODO = "Todo";
    private static final String DOING = "Doing";
    private static final String DONE = "Done";
    private BaseApiService mApiService;
    private ColumnAdapter mAdapter;
    DragBoardView dragBoardView;
    private Project project;
    private List<DragColumn> mData = new ArrayList<>();
    private static final String ARG_PROJECT = "project";
    List<DragItem> todoList;
    List<DragItem> doingList;
    List<DragItem> doneList;
    private String email;
    private FirebaseAuth auth;
    AppDatabase db;

    public DragBoardFragment() {
        // Required empty public constructor
    }

    public static DragBoardFragment newInstance(Project project) {
        DragBoardFragment fragment = new DragBoardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = (Project) getArguments().getParcelable(ARG_PROJECT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drag_board, container, false);
        dragBoardView = view.findViewById(R.id.drag_board);
        db = AppDatabase.getInstance(getContext());
        mApiService = UtilsApi.getAPIService();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        mAdapter = new ColumnAdapter(getContext(), mApiService, project);
        todoList = new ArrayList<>();
        doingList = new ArrayList<>();
        doneList = new ArrayList<>();
        mData.add(new Entry("0", "Todo", todoList));
        mData.add(new Entry("1", "Doing", doingList));
        mData.add(new Entry("2", "Done", doneList));
        mAdapter.setData(mData);
        getActivity().setTitle(project.name);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(project.name);
        dragBoardView.setHorizontalAdapter(mAdapter);
        getDataFromCache();
        getData(TODO);
        return view;
    }

    public void getDataFromCache() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //mData.clear();
                List<Item> itemList = new ArrayList<>();
                itemList = db.itemDao().loadAllItemByState(project.id,TODO);
                for (Item item : itemList) {
                    if (item.member.contains(email)) {
                        todoList.add(item);
                    }
                }
                itemList.clear();
                itemList = db.itemDao().loadAllItemByState(project.id,DOING);
                for (Item item : itemList) {
                    if (item.member.contains(email)) {
                        doingList.add(item);
                    }
                }
                itemList.clear();
                itemList = db.itemDao().loadAllItemByState(project.id,DONE);
                for (Item item : itemList) {
                    if (item.member.contains(email)) {
                        doneList.add(item);
                    }
                }
                itemList.clear();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dragBoardView.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        });
    }

    public void getData(String state) {
        //List<DragItem> items = new ArrayList<>();
        mApiService.getTaskByState(project.id, state)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Item>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Item> itemList) {
                        for (Item item : itemList) {
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    db.itemDao().insertItem(item);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        if (state == TODO) {
                            getData(DOING);
                        }
                        if (state == DOING) {
                            getData(DONE);
                        }if(state == DONE){
                            todoList.clear();
                            doingList.clear();
                            doneList.clear();
                            getDataFromCache();
                        }

                    }
                });
    }

}