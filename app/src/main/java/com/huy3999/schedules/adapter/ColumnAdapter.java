package com.huy3999.schedules.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huy3999.schedules.dragboardview.adapter.HorizontalAdapter;
import com.huy3999.schedules.dragboardview.model.DragColumn;
import com.huy3999.schedules.dragboardview.model.DragItem;
import com.huy3999.schedules.MainActivity;
import com.huy3999.schedules.NewProject;
import com.huy3999.schedules.R;
import com.huy3999.schedules.apiservice.BaseApiService;
import com.huy3999.schedules.model.CreateProjectInfo;
import com.huy3999.schedules.model.CreateTaskInfo;
import com.huy3999.schedules.model.Entry;
import com.huy3999.schedules.model.Item;
import com.huy3999.schedules.model.Project;
import com.huy3999.schedules.roomcache.AppDatabase;
import com.huy3999.schedules.roomcache.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ColumnAdapter extends HorizontalAdapter<ColumnAdapter.ViewHolder>  {
    BaseApiService mApiService;
    Project project;
    String itemName, itemDes;
    AppDatabase db;
    public ColumnAdapter(Context context, BaseApiService mApiService, Project project) {
        super(context,mApiService,project);
        this.mApiService = mApiService;
        this.project = project;
    }
//        public ColumnAdapter(Context context) {
//        super(context);
//    }

    @Override
    public boolean needFooter() {
        return true;
    }

    @Override
    public int getContentLayoutRes() {
        return R.layout.recyclerview_item_entry;
    }

    @Override
    public int getFooterLayoutRes() {
        return R.layout.recyclerview_footer_addlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(View parent, int viewType) {
        return new ViewHolder(parent, viewType);
    }
    @Override
    public void onBindContentViewHolder(final ViewHolder holder, DragColumn dragColumn, int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dragCol(holder);
                return true;
            }
        });
        db = AppDatabase.getInstance(mContext);
        final Entry entry = (Entry) dragColumn;
        holder.tv_title.setText(entry.getName());
        final List<DragItem> itemList = entry.getItemList();
        holder.tv_title_count.setText(""+itemList.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.rv_item.setLayoutManager(layoutManager);
        final ItemAdapter itemAdapter = new ItemAdapter(mContext, dragHelper);
        itemAdapter.setData(itemList);
        holder.rv_item.setAdapter(itemAdapter);
        holder.add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_task);
                Window window = dialog.getWindow();
                if(window == null) {
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
                btnAdd.setText("Add");
                title.setText("Add Task");
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!name.getText().toString().trim().equals("") && !description.getText().toString().trim().equals("")){
                            itemName = name.getText().toString().trim();
                            itemDes = description.getText().toString().trim();
                            CreateTaskInfo taskInfo = new CreateTaskInfo(itemName,itemDes,entry.getName(),project.id,project.member);
                            Log.d("create task", "proj id: "+project.id+ "state: "+ entry.getName()+" name: "+itemName);
                            Item item = new Item("1",itemName,itemDes,entry.getName(),project.id,project.member);
                            itemList.add(item);
                            itemAdapter.notifyItemInserted(itemAdapter.getItemCount() - 1);
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("item", "inserted" + item.name);
                                    db.itemDao().insertItem(item);
                                }
                            });
                            createTask(taskInfo);
                            holder.tv_title_count.setText(""+itemList.size());
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onBindFooterViewHolder(final ViewHolder holder, int position) {
        holder.add_subPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add_subPlan.setVisibility(View.GONE);
                holder.edit_sub_plan.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add_subPlan.setVisibility(View.VISIBLE);
                holder.edit_sub_plan.setVisibility(View.GONE);
            }
        });
        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    appendNewColumn(new Entry(
                            "entry id " + name,
                            "name : new entry",
                            new ArrayList<DragItem>()));
                }
            }
        });
    }


    public class ViewHolder extends HorizontalAdapter.ViewHolder {

        RelativeLayout col_content_container;
        ImageView title_icon;
        TextView tv_title, tv_title_count;
        RecyclerView rv_item;
        RelativeLayout add_task;

        RelativeLayout add_subPlan;
        RelativeLayout edit_sub_plan;
        Button btn_cancel;
        Button btn_ok;
        EditText editText;

        public ViewHolder(View convertView, int itemType) {
            super(convertView, itemType);
        }

        @Override
        public RecyclerView getRecyclerView() {
            return rv_item;
        }

        @Override
        public void findViewForContent(View convertView) {
            col_content_container = convertView.findViewById(R.id.col_content_container);
            title_icon = convertView.findViewById(R.id.title_icon);
            tv_title = convertView.findViewById(R.id.tv_title);
            tv_title_count = convertView.findViewById(R.id.tv_title_count);
            rv_item = convertView.findViewById(R.id.rv);
            add_task = convertView.findViewById(R.id.add);
        }

        @Override
        public void findViewForFooter(View convertView) {
            add_subPlan = convertView.findViewById(R.id.add_sub_plan);
            edit_sub_plan = convertView.findViewById(R.id.edit_sub_plan);
            btn_cancel = convertView.findViewById(R.id.add_cancel);
            btn_ok = convertView.findViewById(R.id.add_ok);
            editText = convertView.findViewById(R.id.add_et);
        }
    }

    public void createTask(CreateTaskInfo task) {
        mApiService.createTask(task)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("DEBUGADDSC", "subcrie");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("DEBUGADDSC", "OK");
                        Toast.makeText(mContext, "Create success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("DEBUGADDSC", "ERROR");
                        //Toast.makeText(mContext, "Create fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("DEBUGADDSC", "COMPLETE");

                    }
                });
    }


}


