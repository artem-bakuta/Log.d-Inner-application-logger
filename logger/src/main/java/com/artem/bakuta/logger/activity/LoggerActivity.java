package com.artem.bakuta.logger.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.artem.bakuta.logger.LogConfig;
import com.artem.bakuta.logger.R;
import com.artem.bakuta.logger.model.LogEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggerActivity extends AppCompatActivity {
    private String DEFAULT = "DEFAULT_ALL_FILTERS";
    private RecyclerView recycler;
    private Adapter adapter;
    private LinearLayoutManager layout;
    private FloatingActionButton mFloatingActionButton;
    private Spinner spinner;
    private ArrayAdapter<String> stringArrayAdapter;
    private List<String> spinnerList;
    private String CURRENT_FILTER_TAG = DEFAULT;
    private LiveData<List<LogEntity>> byTagLiveData;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logger);
        recycler = findViewById(R.id.recycler);
        btn_send = findViewById(R.id.btn_send);
        mFloatingActionButton = findViewById(R.id.floating_action_button);
        spinner = findViewById(R.id.spinner);
        spinnerList = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_drop_down, R.id.tv_tag, spinnerList);
        stringArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinner.setAdapter(stringArrayAdapter);

        final LiveData<List<LogEntity>> allTagsLiveData = LogConfig.getInstance().getDatabase().logEntityDao().getAll();
        allTagsLiveData.observe(this, nameObserver);

        final LiveData<List<String>> justTagsLiveData = LogConfig.getInstance().getDatabase().logEntityDao().getAllTags();
        justTagsLiveData.observe(this, tagsObserver);

        this.adapter = new Adapter();
        recycler.setAdapter(this.adapter);
        layout = new LinearLayoutManager(this);
        recycler.setLayoutManager(layout);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTag = spinnerList.get(position);

                if (!selectedTag.equals(CURRENT_FILTER_TAG)) {
                    if (selectedTag.equals(DEFAULT)) {
                        if (byTagLiveData != null) byTagLiveData.removeObserver(nameObserver);

                        allTagsLiveData.removeObserver(nameObserver);
                        allTagsLiveData.observe(LoggerActivity.this, nameObserver);
                    } else {
                        allTagsLiveData.removeObserver(nameObserver);
                        if (byTagLiveData != null) byTagLiveData.removeObserver(nameObserver);

                        byTagLiveData = LogConfig.getInstance().getDatabase().logEntityDao().getByTag(selectedTag);
                        byTagLiveData.observe(LoggerActivity.this, nameObserver);
                        CURRENT_FILTER_TAG = selectedTag;
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer buffer = new StringBuffer();
                for (LogEntity data : adapter.data) {
                    buffer.append(data.tag).append("|").append(data.date).append("|").append(data.message).append("\n");
                }

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Log Report " + new SimpleDateFormat(LogConfig.getInstance().DATE_FORMAT).format(new Date()));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, buffer.toString());
                startActivity(Intent.createChooser(sharingIntent, "Select how to send log"));
            }
        });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layout.findLastCompletelyVisibleItemPosition() == LoggerActivity.this.adapter.getItemCount() - 1) {
                    mFloatingActionButton.hide();
                } else {
                    mFloatingActionButton.show();

                }
            }
        });


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler.scrollToPosition(LoggerActivity.this.adapter.getItemCount() - 1);
            }
        });

    }

    final Observer<List<LogEntity>> nameObserver = new Observer<List<LogEntity>>() {
        @Override
        public void onChanged(@Nullable final List<LogEntity> newName) {
            // Update the UI, in this case, a TextView.
            if (layout.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                adapter.setData(newName);
                adapter.notifyDataSetChanged();
                recycler.scrollToPosition(adapter.getItemCount() - 1);
            } else {
                adapter.setData(newName);
                adapter.notifyDataSetChanged();
            }
        }
    };

    final Observer<List<String>> tagsObserver = new Observer<List<String>>() {
        @Override
        public void onChanged(@Nullable final List<String> newName) {

            stringArrayAdapter.clear();
            stringArrayAdapter.add(DEFAULT);
            stringArrayAdapter.addAll(newName);
        }
    };


    class Adapter extends RecyclerView.Adapter<Adapter.VhLogEntity> {
        private List<LogEntity> data;

        public void setData(List<LogEntity> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public VhLogEntity onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VhLogEntity(LayoutInflater.from(LoggerActivity.this).inflate(R.layout.layout_log_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VhLogEntity viewHolder, int i) {

            final LogEntity item = data.get(i);
            viewHolder.tvId.setText(String.valueOf(item.id));
            viewHolder.tvTAG.setText(item.tag);

            final String messageText = item.date + "\n" + item.message;
            SpannableString messageSpanned = new SpannableString(messageText);
            messageSpanned.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, item.date.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.tvMessage.setText(messageSpanned);

            switch (item.type) {
                case 0:
                    viewHolder.tvTAG.setTextColor(Color.GRAY);
                    break;
                case 1:
                    viewHolder.tvTAG.setTextColor(Color.BLACK);
                    break;
                case 2:
                    viewHolder.tvTAG.setTextColor(Color.BLUE);
                    break;
                case 3:
                    viewHolder.tvTAG.setTextColor(Color.RED);
                    break;
            }

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.tag);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, messageText);
                    startActivity(Intent.createChooser(sharingIntent, "Select how to send log"));
                    return true;
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).id;
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }


        class VhLogEntity extends RecyclerView.ViewHolder {
            public TextView tvTAG, tvMessage, tvId;

            public VhLogEntity(@NonNull View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.tv_id);
                tvTAG = itemView.findViewById(R.id.tv_tag);
                tvMessage = itemView.findViewById(R.id.tv_message);
            }
        }
    }
}
