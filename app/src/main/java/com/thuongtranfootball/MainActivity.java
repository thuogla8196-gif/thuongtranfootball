package com.thuongtranfootball;

import android.app.*;
import android.os.*;
import android.webkit.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {
    WebView web;
    LinearLayout root, bar;
    EditText address;
    android.content.SharedPreferences prefs;
    ArrayList<String> sources = new ArrayList<>();
    ArrayList<String> history = new ArrayList<>();
    ArrayList<String> favorites = new ArrayList<>();
    String current = "https://yanhh3d.ee";
    public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("ttf",0);
        sources.addAll(prefs.getStringSet("sources", new HashSet<>(Arrays.asList("https://yanhh3d.ee","https://hhpanda.st","https://yanhh3d.now"))));
        history.addAll(prefs.getStringSet("history",new HashSet<String>()));
        favorites.addAll(prefs.getStringSet("favorites",new HashSet<String>()));
        build();
        open(current);
    }
    void build() {
        root=new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(0xff10131c);
        TextView title=new TextView(this); title.setText("⚽ THƯỜNG TRẦN FOOTBALL"); title.setTextColor(-1); title.setTextSize(20); title.setPadding(16,18,8,18); root.addView(title);
        address=new EditText(this); address.setSingleLine(true); address.setHint("Nhập đường dẫn website"); address.setTextColor(-1); address.setHintTextColor(0xffaaaaaa); root.addView(address);
        Button go=new Button(this); go.setText("MỞ WEBSITE"); go.setOnClickListener(v->open(address.getText().toString())); root.addView(go);
        bar=new LinearLayout(this); root.addView(bar);
        addButton("Nguồn",()->chooseSource()); addButton("Thêm nguồn",()->addSource()); addButton("Lịch sử",()->showList("Lịch sử",history)); addButton("Yêu thích",()->showList("Yêu thích",favorites));
        web=new WebView(this); web.getSettings().setJavaScriptEnabled(true); web.getSettings().setDomStorageEnabled(true); web.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView v,String url){ current=url; address.setText(url); if(!history.contains(url)){history.add(0,url);while(history.size()>100)history.remove(history.size()-1);save();}}
        });
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        Button fav=new Button(this); fav.setText("♥ Thêm / bỏ yêu thích trang hiện tại"); fav.setOnClickListener(v->{if(favorites.contains(current))favorites.remove(current);else favorites.add(current);save();Toast.makeText(this,"Đã cập nhật yêu thích",0).show();}); root.addView(fav);
        setContentView(root);
    }
    void addButton(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setTextSize(11);bar.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->r.run());}
    void open(String url){if(url==null)return;url=url.trim();if(!url.startsWith("http://")&&!url.startsWith("https://"))url="https://"+url;if(url.length()>8)web.loadUrl(url);}
    void addSource(){EditText e=new EditText(this);e.setHint("Tên hoặc URL website");new AlertDialog.Builder(this).setTitle("Thêm nguồn website").setView(e).setPositiveButton("Lưu",(d,w)->{String s=e.getText().toString().trim();if(!s.isEmpty()){if(!s.startsWith("http"))s="https://"+s;sources.add(s);save();open(s);}}).setNegativeButton("Hủy",null).show();}
    void chooseSource(){String[] a=sources.toArray(new String[0]);new AlertDialog.Builder(this).setTitle("Chọn nguồn").setItems(a,(d,w)->open(a[w])).setNegativeButton("Đóng",null).show();}
    void showList(String name,ArrayList<String> list){String[] a=list.toArray(new String[0]);new AlertDialog.Builder(this).setTitle(name).setItems(a,(d,w)->open(a[w])).setNegativeButton("Đóng",null).show();}
    void save(){prefs.edit().putStringSet("sources",new HashSet<>(sources)).putStringSet("history",new HashSet<>(history)).putStringSet("favorites",new HashSet<>(favorites)).apply();}
    @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}
