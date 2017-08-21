package com.example.viewpagerfragmenttest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jiaqiao on 2017/8/21/0021.
 */

public class ShowFragment extends Fragment {
    private String music_title, music_artis;
    private Bitmap music_image_bitmap;

    public String getMusic_title() {
        return music_title;
    }

    private TextView show_music_title, show_music_artis;
    private ImageView show_image;

    public void setValue(String music_title, String music_artis, Bitmap music_image_bitmap) {
        this.music_title = music_title;
        this.music_artis = music_artis;
        this.music_image_bitmap = music_image_bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_fragment, container, false);
        show_music_title = (TextView) view.findViewById(R.id.show_music_title);
        show_music_artis = (TextView) view.findViewById(R.id.show_music_artis);
        show_image = (ImageView) view.findViewById(R.id.show_image);
        if (show_music_title != null) {
            show_music_title.setText(music_title + "");
        }
        if (show_music_artis != null) {
            show_music_artis.setText(music_artis + "");
        }
        if (show_image != null) {
            show_image.setImageBitmap(music_image_bitmap);
        }
        return view;
    }
}
