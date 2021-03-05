package com.huy3999.schedules.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huy3999.schedules.NewProject;
import com.huy3999.schedules.R;
import com.huy3999.schedules.adapter.ProjectAdapter;
import com.huy3999.schedules.apiservice.BaseApiService;
import com.huy3999.schedules.apiservice.UtilsApi;
import com.huy3999.schedules.model.Project;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FloatingActionButton addButton;
    private RecyclerView rv_projects;
    private ProjectAdapter adapter;
    private ArrayList<Project> arrProjects;
    private FirebaseAuth auth;
    private BaseApiService mApiService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String email;
    private static final int REQUEST_CODE_EXAMPLE = 0x9345;
    private static View root;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_home_fragment, container, false);
        setHasOptionsMenu(true);
        init();
        //Listen realtime
        getAllDocumentWithRealtimeUpdates(root);
        return root;
    }


    public void init() {
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        addButton = root.findViewById(R.id.btn_add_project);
        progressBar = root.findViewById(R.id.progress_projecr);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddProject(view);
            }
        });

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        rv_projects = root.findViewById(R.id.list_project);
        rv_projects.setLayoutManager(new LinearLayoutManager(getContext()));
        mApiService = UtilsApi.getAPIService();
        arrProjects = new ArrayList<Project>();
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
    }

    public void fetchData(String email) {
        mApiService.getAllProjects(email)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Project> projects) {
                        arrProjects.removeAll(arrProjects);
                        for(Project project : projects) {
                            arrProjects.add(project);
                        }
                        adapter = new ProjectAdapter(arrProjects, getContext());
                        rv_projects.setAdapter(adapter);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onAddProject(View view) {
        Intent i = new Intent(getContext(), NewProject.class);
        startActivityForResult(i, REQUEST_CODE_EXAMPLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        fetchData(email);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem mSearch = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

        });
    }

    public void getAllDocumentWithRealtimeUpdates(View view) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection("projects")
                .whereArrayContains("member", email)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null ) {
                            Log.d("REALTIME", "Listen failed");
                        }
                        if(value != null) {
                            arrProjects.removeAll(arrProjects);

                            for (QueryDocumentSnapshot doc : value) {
                                Log.d("REALTIME", doc.getId());
                                Project project = new Project(doc.getId() , doc.get("name").toString(), doc.get("color").toString(), (ArrayList<String>) doc.get("member"));
                                arrProjects.add(project);
                            }
                            adapter = new ProjectAdapter(arrProjects, getContext());
                            rv_projects.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}