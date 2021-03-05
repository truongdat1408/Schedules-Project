package com.huy3999.schedules.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huy3999.schedules.MainActivity;
import com.huy3999.schedules.NewProject;
import com.huy3999.schedules.R;
import com.huy3999.schedules.apiservice.BaseApiService;
import com.huy3999.schedules.apiservice.UtilsApi;
import com.huy3999.schedules.dragboardview.adapter.VerticalAdapter;
import com.huy3999.schedules.dragboardview.helper.DragHelper;
import com.huy3999.schedules.dragboardview.model.DragItem;
import com.huy3999.schedules.model.CreateProjectInfo;
import com.huy3999.schedules.model.CreateTaskInfo;
import com.huy3999.schedules.model.Item;
import com.huy3999.schedules.roomcache.AppDatabase;
import com.huy3999.schedules.roomcache.AppExecutors;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ItemAdapter extends VerticalAdapter<ItemAdapter.ViewHolder> {
    BaseApiService mApiService;
    AppDatabase db;

    public ItemAdapter(Context context, DragHelper dragHelper) {
        super(context, dragHelper);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(Context context, final ViewHolder holder, @NonNull DragItem item, final int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dragItem(holder);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(item, position);
            }
        });
        holder.item_title.setText(((Item) item).name);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;

        public ViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
        }
    }

    private void openDialog(DragItem item, final int position) {
        db = AppDatabase.getInstance(mContext);
        mApiService = UtilsApi.getAPIService();
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_task);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAtrributes = window.getAttributes();
        windowAtrributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAtrributes);
        dialog.setCancelable(false);
        EditText name = dialog.findViewById(R.id.name_task);
        EditText description = dialog.findViewById(R.id.description_task);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnAdd = dialog.findViewById(R.id.btn_ok);
        TextView title = dialog.findViewById(R.id.title_dialog);

        //set data dialog
        btnAdd.setText("Update");
        title.setText("Update Task");
        name.setText(((Item) item).name);
        description.setText(((Item) item).description);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().trim().equals("") && !description.getText().toString().trim().equals("")) {
                    if (!name.getText().toString().trim().equals("") && !description.getText().toString().trim().equals("")) {
                        ((Item) item).name = name.getText().toString().trim();
                        ((Item) item).description = description.getText().toString().trim();
                        CreateTaskInfo taskInfo = new CreateTaskInfo(((Item) item).name, ((Item) item).description, ((Item) item).state, ((Item) item).project_id, ((Item) item).member);
                        updateTask(((Item) item).id, taskInfo);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("item", "updated" + ((Item) item).name);
                                db.itemDao().insertItem((Item) item);
                            }
                        });
                        notifyDataSetChanged();
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateTask(String id, CreateTaskInfo task) {
        Log.d("updateTask", id);
        mApiService.updateTask(id, task)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Response<ResponseBody> response) {
                        Log.d("update", "update success");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException error = (HttpException) e;
                            try {
                                Toast.makeText(mContext, error.response().errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                });
    }
}
