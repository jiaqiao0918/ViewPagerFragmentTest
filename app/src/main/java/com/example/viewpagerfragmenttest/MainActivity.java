package com.example.viewpagerfragmenttest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ViewPager view_pager;
    private FragmentAdapter adapter;
    private Button is_random_but;

    private int now_show_position = 0;
    private int state_1_position = -1;
    private boolean is_need_update = false;
    private ArrayList<Integer> randoms = new ArrayList<Integer>();
    private boolean is_random = true;

    private ArrayList<MusicInfo> all_list = new ArrayList<MusicInfo>();
    private ArrayList<Fragment> list_fragment = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        is_random_but = (Button) findViewById(R.id.is_random);
        getAllMusic();
        listSortPinYin(all_list);
        updateUi();
        updateViewPagerFragment();
        adapter = new FragmentAdapter(getSupportFragmentManager(), list_fragment);
        view_pager.setAdapter(adapter);
        view_pager.setCurrentItem(2); //设置当前页是第0页
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滑动中
            }

            @Override
            public void onPageSelected(int position) {

            }

            /* state == 1的时辰默示正在滑动，
             * state == 2的时辰默示滑动完毕了，
             * state == 0的时辰默示什么都没做。
             * 当页面开始滑动的时候，三种状态的变化顺序为（1，2，0），演示如下：
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    is_need_update = true;
                } else if (state == 0) {
                    if (is_need_update) {
                        if (view_pager.getCurrentItem() > state_1_position) {
                                now_show_position = randoms.get(3);
                            viewPagerRight();
                        } else if (view_pager.getCurrentItem() < state_1_position) {
                                now_show_position = randoms.get(1);
                            viewPagerLeft();
                        }
                        adapter.UpdateList(list_fragment);
                        view_pager.setCurrentItem(2, false); //设置当前页是第0页，false为不需要过渡动画，默认为true
                        is_need_update = false;
                    }
                } else if (state == 1) {
                    state_1_position = view_pager.getCurrentItem();
                }

            }
        });

    }

    public void viewPagerRight() {
        list_fragment.remove(0);
        randoms.remove(0);
        int num = -1;
        if (!is_random) {
            num = (now_show_position + 2 + all_list.size()) % all_list.size();
        }else{
            while (true) {
                boolean is_have_num = true;
                num = getRandomForMinMax(0, all_list.size() - 1);
                for (int j = 0; j < randoms.size(); j++) {
                    if (randoms.get(j) == num) {
                        is_have_num = true;
                        break;
                    } else {
                        is_have_num = false;
                    }
                }
                if (!is_have_num) {
                    break;
                }
            }
        }
        MusicInfo music_info_temp = all_list.get(num);
        ShowFragment fragment_temp = new ShowFragment();
        Bitmap bitmap = MusicUtils.getArtwork(this, music_info_temp.getMusic_id(), music_info_temp.getMusic_album_id(), true);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        }
        fragment_temp.setValue(music_info_temp.getMusic_title(), music_info_temp.getMusic_artist(), bitmap);
        list_fragment.add(list_fragment.size(), fragment_temp);
        randoms.add(randoms.size(),num);
    }

    public void viewPagerLeft() {
        list_fragment.remove(list_fragment.size() - 1);
        randoms.remove(randoms.size() - 1);
        int num = -1;
        if (!is_random) {
            num = (now_show_position - 2 + all_list.size()) % all_list.size();
        }else{
            while (true) {
                boolean is_have_num = true;
                num = getRandomForMinMax(0, all_list.size() - 1);
                for (int j = 0; j < randoms.size(); j++) {
                    if (randoms.get(j) == num) {
                        is_have_num = true;
                        break;
                    } else {
                        is_have_num = false;
                    }
                }
                if (!is_have_num) {
                    break;
                }
            }
        }
        MusicInfo music_info_temp = all_list.get(num);
        ShowFragment fragment_temp = new ShowFragment();
        Bitmap bitmap = MusicUtils.getArtwork(this, music_info_temp.getMusic_id(), music_info_temp.getMusic_album_id(), true);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        }
        fragment_temp.setValue(music_info_temp.getMusic_title(), music_info_temp.getMusic_artist(), bitmap);
        list_fragment.add(0, fragment_temp);
        randoms.add(0,num);
    }

    public void click(View view) {
//        Log.i("into", "view_pager当前显示序号：" + view_pager.getCurrentItem() + "");
        if (is_random) {
            is_random = false;
        } else {
            is_random = true;
        }
        updateUi();
        updateViewPagerFragment();
        adapter.UpdateList(list_fragment);
        view_pager.setCurrentItem(2, false); //设置当前页是第0页，false为不需要过渡动画，默认为true
    }

    public ArrayList<Fragment> getFragemntFromList01(ArrayList<MusicInfo> music_list, int position) {
        randoms.clear();
        ArrayList<Fragment> temp = new ArrayList<Fragment>();
        for (int i = 0; i < 5; i++) {
            int now_num = (i - 2 + music_list.size() + position) % music_list.size();
            randoms.add(now_num);
            MusicInfo music_info_temp = music_list.get(now_num);
            ShowFragment fragment_temp = new ShowFragment();
            Bitmap bitmap = MusicUtils.getArtwork(this, music_info_temp.getMusic_id(), music_info_temp.getMusic_album_id(), true);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
            }
            fragment_temp.setValue(music_info_temp.getMusic_title(), music_info_temp.getMusic_artist(), bitmap);
            temp.add(fragment_temp);
        }
        return temp;
    }

    public ArrayList<Fragment> getFragemntFromList02(ArrayList<MusicInfo> music_list, int position) {
        randoms.clear();
        ArrayList<Fragment> temp = new ArrayList<Fragment>();
        int[] random_num_temp = new int[5];
        for (int i=0;i<random_num_temp.length;i++){
            random_num_temp[i]=-1;
        }
        random_num_temp[2]=position;
        for (int i = 0; i < 5; i++) {
            int now_num = -1;
            if (i != 2) {
                while (true) {
                    boolean is_have_num = true;
                    now_num = getRandomForMinMax(0, music_list.size() - 1);
                    for (int j = 0; j < random_num_temp.length; j++) {
                        if (random_num_temp[i] == now_num) {
                            is_have_num = true;
                            break;
                        } else {
                            is_have_num = false;
                        }
                    }
                    if (!is_have_num) {
                        break;
                    }
                }
            } else {
                now_num = position;
            }
            MusicInfo music_info_temp = music_list.get(now_num);
            ShowFragment fragment_temp = new ShowFragment();
            Bitmap bitmap = MusicUtils.getArtwork(this, music_info_temp.getMusic_id(), music_info_temp.getMusic_album_id(), true);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
            }
            fragment_temp.setValue(music_info_temp.getMusic_title(), music_info_temp.getMusic_artist(), bitmap);
            temp.add(fragment_temp);
            randoms.add(now_num);
        }
        return temp;
    }

    public int getRandomForMinMax(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    //从SD卡中读取音频文件，无序
    public void getAllMusic() {
        all_list.clear();
        int big_time = 30 * 1000;//过滤小于30秒
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED
        };
        String where = "mime_type in ('audio/x-wav','application/ogg','audio/mp4','audio/flac','audio/x-ms-wma','audio/x-monkeys-audio','audio/aac','audio/ac3','audio/mpeg')and duration>" + big_time + " and is_music > 0 ";
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, where, null, MediaStore.Audio.Media.TITLE);
        if (cursor == null)
            return;
        if (cursor.moveToLast()) {
            while (cursor.moveToPrevious()) {
                Long music_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));//歌曲ID
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//path
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));//歌名
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));//歌手
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//专辑
                long album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));//专辑ID
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));//总时长（单位：毫秒）
                int date_time = Integer.parseInt(getDateFromSeconds(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))));//添加日期(单位：秒)

                if (artist == "<unknown>" || artist.equals("<unknown>")) {
                    artist = "";
                }
                if (album == "<unknown>" || album.equals("<unknown>")) {
                    album = "";
                }
                if (new File(url).exists()) {
                    all_list.add(new MusicInfo(music_id, url, title, artist, album, album_id, duration, getMusic_pinyin(title), date_time));//英文开头，拼音就是英文
                }
            }
        }
        cursor.close();
    }

    //获取对应的拼音
    public String getMusic_pinyin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String output = "";
        if (inputString != null && inputString.length() > 0 && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {

                    if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                        output += temp[0];
                    } else
                        output += java.lang.Character.toString(input[i]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            return "*";
        }
        return output;
    }

    // 自定义的排序
    public static void listSortPinYin(ArrayList<MusicInfo> resultList) {
        Collections.sort(resultList, new Comparator<MusicInfo>() {
            public int compare(MusicInfo o1, MusicInfo o2) {
                String name1 = o1.getMusic_pinyin();
                String name2 = o2.getMusic_pinyin();
                Collator instance = Collator.getInstance(Locale.CHINA);
                return instance.compare(name1, name2);
            }
        });
    }

    public String getDateFromSeconds(String seconds) {
        if (seconds == null)
            return " ";
        else {
            Date date = new Date();
            try {
                date.setTime(Long.parseLong(seconds) * 1000);
            } catch (NumberFormatException nfe) {

            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);
        }
    }

    public void updateUi() {
        if (is_random) {
            is_random_but.setText("true");
        } else {
            is_random_but.setText("false");
        }
    }

    public void updateViewPagerFragment() {
        if (!is_random) {
            list_fragment = getFragemntFromList01(all_list, now_show_position);
        } else {
            list_fragment = getFragemntFromList02(all_list, now_show_position);
        }
    }

}
