package pers.dongchangzhang.todayisbeautiful;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pers.dongchangzhang.todayisbeautiful.adapter.PlanAdapter;
import pers.dongchangzhang.todayisbeautiful.dao.MyDatabaseOperator;
import pers.dongchangzhang.todayisbeautiful.entity.Event;
import pers.dongchangzhang.todayisbeautiful.inter.MyItemOnClickListener;
import pers.dongchangzhang.todayisbeautiful.todayisbeautiful.R;
import pers.dongchangzhang.todayisbeautiful.utils.MyDecoration;

import static pers.dongchangzhang.todayisbeautiful.Config.DB_NAME;
import static pers.dongchangzhang.todayisbeautiful.Config.DB_VERSION;
import static pers.dongchangzhang.todayisbeautiful.utils.Tools.changeStringToCalendar;

/**
 * Created by cc on 17-7-20.
 */

public class PlanPage extends Fragment {

    private static final String TAG = "PlanPage";
    private PlanAdapter adapter;
    List<Event> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plan_page, container, false);

        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final RecyclerView plans = (RecyclerView) view.findViewById(R.id.plan_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager((MainActivity) getActivity());
        plans.setLayoutManager(layoutManager);
        adapter = new PlanAdapter((MainActivity) getActivity(), list);

        plans.addItemDecoration(new MyDecoration((MainActivity) getActivity(), MyDecoration.VERTICAL_LIST));

        adapter.setItemOnClickListener(new MyItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int position) {
                ((MainActivity)getActivity()).editPlan(list.get(position).getId());

            }

            @Override
            public boolean onItemOnLongClick(View view, final int postion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("删除计划" + list.get(postion).getmTitle());
                builder.setMessage("确定删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String id = list.get(postion).getId();
                        String[] what = {id,};
                        list.remove(postion);
                        MyDatabaseOperator operator = new MyDatabaseOperator((MainActivity) getActivity(), DB_NAME, DB_VERSION);
                        operator.erase("Events", "id = ?", what);

                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                builder.show();
                return false;
            }
        });
        plans.setAdapter(adapter);

        CardView cardView = (CardView) view.findViewById(R.id.add_plan);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlan();
            }
        });

        return view;
    }

    private void addPlan() {
        Toast.makeText((MainActivity) getActivity(), "add", Toast.LENGTH_SHORT).show();
        ((MainActivity) getActivity()).newAPlan();
    }

    void initData() throws ParseException {
        list.clear();
        MyDatabaseOperator operator = new MyDatabaseOperator((MainActivity) getActivity(), DB_NAME, DB_VERSION);
        List<Map> maps = operator.search("Events");
        for (Map m : maps) {

            Event event = new Event(m.get("title").toString(),
                    m.get("description").toString(),
                    m.get("location").toString(),
                    R.color.blue_selected,
                    changeStringToCalendar(m.get("startTime").toString()),
                    changeStringToCalendar(m.get("startTime").toString()),
                    true);
            event.setId(m.get("id").toString());
            list.add(event);
            Log.d(TAG, "onCreateView: " + m.get("time"));
            Log.d(TAG, "onCreateView: " + m.get("title"));
            Log.d(TAG, "onCreateView: " + m.get("content"));
        }
    }

    public void reRefresh() throws ParseException {
        initData();
        adapter.notifyDataSetChanged();
    }
}