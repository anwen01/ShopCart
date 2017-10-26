package com.bwie.shopcart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.shop_recyclerview)
    RecyclerView rv;
    @BindView(R.id.shop_allselect)
    CheckBox shopAllselect;
    @BindView(R.id.shop_totalprice)
    TextView shopTotalprice;
    @BindView(R.id.shop_totalnum)
    TextView shopTotalnum;
    @BindView(R.id.shop_submit)
    TextView shopSubmit;
    @BindView(R.id.third_pay_linear)
    LinearLayout thirdPayLinear;
    private ShopInfoBean shopInfoBean;
    private List<ShopInfoBean.DatasBean.CartListBean.GoodsBean> list;
    private ShopAdapter shopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rv.setLayoutManager(new LinearLayoutManager(this));

        showGoods();


    }


    //展示商品
    public void showGoods() {
        RequestBody formBody = new FormBody.Builder()
                .add("key", "d7944fbe7ce9c3f58bfe2a3141defaba")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.23.144/mobile/index.php?act=member_cart&op=cart_list")
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println("==============add:" + string);
                Gson gson = new Gson();
                shopInfoBean = gson.fromJson(string, ShopInfoBean.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list = new ArrayList<>();
                        if (shopInfoBean.getCode() == 200) {
                            for (int i = 0; i < shopInfoBean.getDatas().getCart_list().size(); i++) {
                                for (int j = 0; j < shopInfoBean.getDatas().getCart_list().get(i).getGoods().size(); j++) {
                                    list.add(shopInfoBean.getDatas().getCart_list().get(i).getGoods().get(j));
                                }
                            }
                            shopAdapter = new ShopAdapter(MainActivity.this);
                            rv.setAdapter(shopAdapter);
                            setFirstState(list);
                            shopAdapter.setData(list);

                        } else {
                            Toast.makeText(MainActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        }

                        //删除数据回调
                        shopAdapter.setOnDeleteListener(new ShopAdapter.OnDeleteListener() {
                            @Override
                            public void OnDelete(View view, int position, int cartid) {

                            }
                        });


                        shopAdapter.setOnRefershListener(new ShopAdapter.OnRefershListener() {
                            @Override
                            public void onRefersh(boolean isSelect, List<ShopInfoBean.DatasBean.CartListBean.GoodsBean> lists) {

                                //标记底部 全选按钮
                                shopAllselect.setChecked(isSelect);
                                int num = 0;
                                for (int i = 0; i < lists.size(); i++) {
                                    if (lists.get(i).isSelect()) {
                                        num += Integer.parseInt(lists.get(i).getGoods_num());
                                    }
                                }
                                shopTotalnum.setText(num + "");


                            }
                        });
                    }
                });
            }
        });

    }


    /**
     * 标记第一条数据 isfirst 1 显示商户名称 2 隐藏
     *
     * @param list
     */
    public static void setFirstState(List<ShopInfoBean.DatasBean.CartListBean.GoodsBean> list) {

        if (list.size() > 0) {
            list.get(0).setIsFirst(1);
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).getStore_id().equals(list.get(i - 1).getStore_id())) {
                    list.get(i).setIsFirst(2);
                } else {
                    list.get(i).setIsFirst(1);
                }
            }
        }

    }


    @OnClick(R.id.shop_allselect)
    public void onClick() {
        boolean checked = shopAllselect.isChecked();
        shopAdapter.setUnSelected(checked);


    }
}
