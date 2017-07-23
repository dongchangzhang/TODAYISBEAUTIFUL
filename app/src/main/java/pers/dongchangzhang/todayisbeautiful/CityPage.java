package pers.dongchangzhang.todayisbeautiful;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pers.dongchangzhang.todayisbeautiful.adapter.CityAdapter;
import pers.dongchangzhang.todayisbeautiful.entity.CityBean;
import pers.dongchangzhang.todayisbeautiful.inter.MyItemOnClickListener;
import pers.dongchangzhang.todayisbeautiful.todayisbeautiful.R;
import pers.dongchangzhang.todayisbeautiful.utils.GetHttpInfo;
import pers.dongchangzhang.todayisbeautiful.utils.MyDecoration;

import static android.content.ContentValues.TAG;


/**
 * Created by cc on 17-7-20.
 */

public class CityPage extends Fragment  {
    private CityAdapter adapter;
    List<CityBean> list = new ArrayList<>();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ((MainActivity)getActivity()).jumpToWeatherPage((String)msg.obj);
                    break;
                default:
                    list.clear();

                    List<CityBean> tmp = (List<CityBean>) msg.obj;
                    Log.d(TAG, "handleMessage: " + tmp.size());
                    for (int i = 0; i < tmp.size(); ++i) {
                        list.add(tmp.get(i));
                    }
                    adapter.notifyDataSetChanged();
            }
        }
    };
    public CityPage() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_page,container,false);



        RecyclerView citys = (RecyclerView) view.findViewById(R.id.city_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager((MainActivity)getActivity());
        citys.setLayoutManager(layoutManager);
        adapter = new CityAdapter((MainActivity)getActivity(), list);

        citys.addItemDecoration(new MyDecoration((MainActivity)getActivity(), MyDecoration.VERTICAL_LIST));

        final String code = getArguments().getString("code");
        final String url = getArguments().getString("url");
        final String city = getArguments().getString("city");
        GetHttpInfo.getCityInfo(handler, url, code, city);

        adapter.setItemOnClickListener(new MyItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int position) {
                ((MainActivity)getActivity()).changeToCity(url + "/" + code, list.get(position).getId(), list.get(position).getName());

            }
        });
        citys.setAdapter(adapter);

        return view;
    }

}
