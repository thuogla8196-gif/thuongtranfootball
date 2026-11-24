package com.thuongtranfootball;

import android.app.*;
import android.os.*;
import android.webkit.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.net.Uri;
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
        if(!sources.contains("https://yang3d.ee")) sources.add("https://yang3d.ee");
        save();
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
        Button yang3d=new Button(this); yang3d.setText("MỞ YANG3D.EE"); yang3d.setOnClickListener(v->open("https://yang3d.ee")); root.addView(yang3d);
        web=new WebView(this);
        WebSettings settings=web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        web.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }
            public void onPageFinished(WebView v,String url){
                current=url; address.setText(url);
                if(!history.contains(url)){history.add(0,url);while(history.size()>100)history.remove(history.size()-1);save();}
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        Button fav=new Button(this); fav.setText("♥ Thêm / bỏ yêu thích trang hiện tại"); fav.setOnClickListener(v->{if(favorites.contains(current))favorites.remove(current);else favorites.add(current);save();Toast.makeText(this,"Đã cập nhật yêu thích",0).show();}); root.addView(fav);
        setContentView(root);
    }

    boolean handleUrl(String url) {
        if(url == null) return false;
        if(url.startsWith("intent://")) {
            try {
                Intent intent=Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                if(intent.resolveActivity(getPackageManager())!=null) startActivity(intent);
                else {
                    String fallback=intent.getStringExtra("browser_fallback_url");
                    if(fallback!=null) web.loadUrl(fallback);
                }
                return true;
            } catch(Exception e) { return false; }
        }
        if(isDirectVideo(url)) {
            playExternal(url);
            return true;
        }
        if(url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("market:")) {
            try { startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); return true; }
            catch(Exception e) { return false; }
        }
        return false;
    }

    boolean isDirectVideo(String url) {
        String path;
        try { path=Uri.parse(url).getPath(); } catch(Exception e) { return false; }
        if(path==null) return false;
        path=path.toLowerCase(Locale.ROOT);
        String[] extensions={".mp4",".mkv",".webm",".m3u8",".mpd",".avi",".mov",".flv",".3gp",".ts",".mpeg",".mpg"};
        for(String ext:extensions) if(path.endsWith(ext)) return true;
        return false;
    }

    void playExternal(String url) {
        String path=Uri.parse(url).getPath();
        String p=path==null?"":path.toLowerCase(Locale.ROOT);
        String mime="video/*";
        if(p.endsWith(".m3u8")) mime="application/vnd.apple.mpegurl";
        else if(p.endsWith(".mpd")) mime="application/dash+xml";
        else if(p.endsWith(".mp4") || p.endsWith(".m4v")) mime="video/mp4";
        else if(p.endsWith(".webm")) mime="video/webm";
        else if(p.endsWith(".mkv")) mime="video/x-matroska";
        try {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url),mime);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent,"Phát video bằng"));
        } catch(Exception e) {
            Toast.makeText(this,"Bạn chưa cài ứng dụng phát video ngoài (ví dụ VLC).",Toast.LENGTH_LONG).show();
        }
    }

    void addButton(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setTextSize(11);bar.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->r.run());}
    void open(String url){if(url==null)return;url=url.trim();if(!url.startsWith("http://")&&!url.startsWith("https://"))url="https://"+url;if(url.length()>8)web.loadUrl(url);}
    void addSource(){EditText e=new EditText(this);e.setHint("Tên hoặc URL website");new AlertDialog.Builder(this).setTitle("Thêm nguồn website").setView(e).setPositiveButton("Lưu",(d,w)->{String s=e.getText().toString().trim();if(!s.isEmpty()){if(!s.startsWith("http"))s="https://"+s;sources.add(s);save();open(s);}}).setNegativeButton("Hủy",null).show();}
    void chooseSource(){String[] a=sources.toArray(new String[0]);new AlertDialog.Builder(this).setTitle("Chọn nguồn").setItems(a,(d,w)->open(a[w])).setNegativeButton("Đóng",null).show();}
    void showList(String name,ArrayList<String> list){String[] a=list.toArray(new String[0]);new AlertDialog.Builder(this).setTitle(name).setItems(a,(d,w)->open(a[w])).setNegativeButton("Đóng",null).show();}
    void save(){prefs.edit().putStringSet("sources",new HashSet<>(sources)).putStringSet("history",new HashSet<>(history)).putStringSet("favorites",new HashSet<>(favorites)).apply();}
    @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}
