package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.richeditor.RichEditor;


public class FreeTextSetting extends WidgetSettingsFragment {

    @Bind(R.id.rich_editor)
    RichEditor richEditor;

    WidgetOption customText;

    public static String CUSTOM_TEXT = "CUSTOM_TEXT";

    public FreeTextSetting() {
        setScrollViewHeader(R.layout.scroll_view_header);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.free_text_settings, container, false);
        ButterKnife.bind(this, view);

        customText = loadOrInitOption(CUSTOM_TEXT);

        richEditor.setHtml(customText.value);
        richEditor.setEditorHeight(500);
        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                customText.update(text);
                updateWidgetProperty(CUSTOM_TEXT, text);
            }
        });

        
        getActivity().findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.undo();
            }
        });


        getActivity().findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setBold();
            }
        });

        getActivity().findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setItalic();
            }
        });


        getActivity().findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setStrikeThrough();
            }
        });

        getActivity().findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setUnderline();
            }
        });

        getActivity().findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setHeading(1);
            }
        });

        getActivity().findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setHeading(2);
            }
        });

        getActivity().findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setHeading(3);
            }
        });

        getActivity().findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setHeading(4);
            }
        });

        getActivity().findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setHeading(5);
            }
        });

        getActivity().findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setAlignLeft();
            }
        });

        getActivity().findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setAlignCenter();
            }
        });

        getActivity().findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                richEditor.setAlignRight();
            }
        });

        return view;
    }

}