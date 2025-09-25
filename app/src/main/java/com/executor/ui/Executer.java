package com.executor.ui;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.support.Preferences;
import com.executor.material.widgets.GalaxyLinearLayout;
import com.executor.material.widgets.MaterialAlertDialogBuilder;
import com.executor.material.widgets.MaterialButton;
import com.executor.material.widgets.MaterialSwitch;
import com.executor.material.widgets.ParticleLinearLayout;
import com.executor.material.widgets.ZevText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Executer {

    private final Context context;
    private LinearLayout mainLayout;
    private EditText codeEditor;
    private TextView lineCounter;
    private ParticleLinearLayout root;
    private PopupWindow suggestionPopup;
    private final String[] completions = {
        "print", "pairs", "ipairs", "function", "local", "end", "if", "then", "else", "elseif", "for", "while", "repeat", "until", "return"
    };
    private onCloseListener onCloseClickListener;

    public Executer(Context ctx) {
        this.context = ctx;
        createUI();
    }

    private void createUI() {
        if (true) {
            SharedPreferences prefs = context.getSharedPreferences("bgfx.prefs", context.MODE_PRIVATE);
            String scriptName = prefs.getString("defaultScript", "AdPanelTeam.lua");
            String pkg = context.getPackageName();
            String path = "/sdcard/BGFX/Scripts/" + scriptName;
            Preferences.changeFeatureString("loadDefault", 29, path);
        }
        root = new ParticleLinearLayout(context);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 LinearLayout.LayoutParams.MATCH_PARENT
                             ));
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setColor(Color.parseColor("#1E1E1E"));
        root.setPadding(10, 10, 10, 10);
        root.setGravity(Gravity.CENTER);

        LinearLayout editorLayout = new LinearLayout(context);
        editorLayout.setOrientation(LinearLayout.VERTICAL);
        editorLayout.setPadding(5, 5, 5, 5);
        
        final LinearLayout codeTabContent = new LinearLayout(context);
        codeTabContent.setOrientation(LinearLayout.VERTICAL);
        codeTabContent.setVisibility(View.VISIBLE);

        final LinearLayout scriptsTabContent = new LinearLayout(context);
        scriptsTabContent.setOrientation(LinearLayout.VERTICAL);
        scriptsTabContent.setVisibility(View.GONE);

        final LinearLayout settingsTabContent = new LinearLayout(context);
        settingsTabContent.setOrientation(LinearLayout.VERTICAL);
        settingsTabContent.setVisibility(View.GONE);
        
        
        
        final LinearLayout consoleTabContent = new LinearLayout(context);
        consoleTabContent.setOrientation(LinearLayout.VERTICAL);
        consoleTabContent.setVisibility(View.GONE);   
        

        final GalaxyLinearLayout aboutTabContent = new GalaxyLinearLayout(context);
        aboutTabContent.setOrientation(LinearLayout.VERTICAL);
        aboutTabContent.setVisibility(View.GONE);
        aboutTabContent.setPadding(dp(16), dp(16), dp(16), dp(16));

        String[] names = {
            "ZNFDev", "youtube.com/@znfdev",
            "Hacker King", "youtube.com/@hacker-king78",
            "Comical", "youtube.com/@comicalboss"
        };

        for (int i = 0; i < names.length; i++) {
            ZevText creditText = new ZevText(context);
            creditText.setText(names[i]);
            creditText.setTextColor(Color.WHITE);
            creditText.setTextSize(16);
            creditText.setPadding(10,5,10,5);
            creditText.setGravity(Gravity.CENTER);
            creditText.setIndex(i * 200);
            creditText.setTypeface(null, Typeface.BOLD);
            aboutTabContent.addView(creditText);
        }

        GradientDrawable editorBg = new GradientDrawable();
        editorBg.setColor(Color.parseColor("#2D2D2D"));
        editorBg.setCornerRadius(40);

        LinearLayout codeContainer = new LinearLayout(context);
        codeContainer.setOrientation(LinearLayout.HORIZONTAL);
        codeContainer.setBackgroundDrawable(editorBg);
        codeContainer.setPadding(10, 10, 10, 10);
        codeContainer.setLayoutParams(new LinearLayout.LayoutParams(dp(300), dp(200)));

        lineCounter = new TextView(context);
        lineCounter.setTextColor(Color.GRAY);
        lineCounter.setText("1");
        lineCounter.setPadding(5, 5, 10, 5);
        lineCounter.setTypeface(Typeface.MONOSPACE);
        lineCounter.setGravity(Gravity.TOP);

        codeEditor = new EditText(context);
        codeEditor.setBackground(null);
        codeEditor.setTextColor(Color.WHITE);
        codeEditor.setHint("Code here...");
        codeEditor.setSingleLine(false);
        codeEditor.setGravity(Gravity.TOP | Gravity.START);
        
        codeEditor.setMovementMethod(new ScrollingMovementMethod());
        codeEditor.setHorizontallyScrolling(true);
        codeEditor.setTypeface(Typeface.MONOSPACE);
        codeEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        codeEditor.setLayoutParams(new LinearLayout.LayoutParams(
                                       ViewGroup.LayoutParams.MATCH_PARENT,
                                       ViewGroup.LayoutParams.MATCH_PARENT
                                   ));
        codeEditor.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        codeEditor.setFocusableInTouchMode(true);
        codeEditor.requestFocus();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(codeEditor, InputMethodManager.SHOW_IMPLICIT);
        codeContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                          ViewGroup.LayoutParams.MATCH_PARENT,
                                          dp(260) // or whatever fixed height you want
                                      ));
        
        
        
        codeEditor.addTextChangedListener(new TextWatcher() {
                private int lastCursor = 0;

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Use `start` instead of getSelectionStart() to avoid bad values
                    lastCursor = start;
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateLineCount();
                    highlightSyntax();
                }

                public void afterTextChanged(Editable s) {
                    int safeCursor = Math.min(lastCursor, s.length());
                    handleAutoPairing(s, safeCursor);
                    showSuggestions(s, safeCursor);
                }
            });
        codeEditor.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    lineCounter.scrollTo(0, codeEditor.getScrollY());
                }
            });

        codeContainer.addView(lineCounter);
        codeContainer.addView(codeEditor);

        MaterialButton executeBtn = new MaterialButton(context);
        executeBtn.setText("Execute");
        executeBtn.setAllCaps(false);
        executeBtn.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams execParams = new LinearLayout.LayoutParams(-1, dp(40));
        execParams.topMargin = dp(10);
        executeBtn.setLayoutParams(execParams);
        executeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.changeFeatureString("Execute", 27, codeEditor.getText().toString());
                }
            });
            

        MaterialButton ClipboardexeBtn = new MaterialButton(context);
ClipboardexeBtn.setText("Execute Clipboard");
ClipboardexeBtn.setAllCaps(false);
ClipboardexeBtn.setPadding(10, 10, 10, 10);
LinearLayout.LayoutParams ClipexecParams = new LinearLayout.LayoutParams(-1, dp(40));
ClipexecParams.topMargin = dp(10);
ClipboardexeBtn.setLayoutParams(ClipexecParams);

ClipboardexeBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence clipboardText = clipData.getItemAt(0).getText();
                if (clipboardText != null) {
                    Preferences.changeFeatureString("Execute Clipboard", 32, clipboardText.toString());
                }
            }
        }
    }
});

        codeTabContent.addView(codeContainer);
        codeTabContent.addView(executeBtn);
        codeTabContent.addView(ClipboardexeBtn);
        
        editorLayout.addView(codeTabContent);
        editorLayout.addView(scriptsTabContent);
        editorLayout.addView(settingsTabContent);
        editorLayout.addView(consoleTabContent);
        editorLayout.addView(aboutTabContent);

        LinearLayout tabLayout = new LinearLayout(context);
        tabLayout.setOrientation(LinearLayout.VERTICAL);
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setPadding(10, 10, 10, 10);
        GradientDrawable tabBg = new GradientDrawable();
        tabBg.setColor(Color.parseColor("#2D2D2D")); // dark background
        tabBg.setCornerRadius(80); // round edges
        tabLayout.setBackground(tabBg); // <-- add this
        

        // Add different views for tab content
        final TextView codeTabView = new TextView(context);
        codeTabView.setText("Code Tab Content");
        codeTabView.setTextColor(Color.WHITE);
        codeTabView.setVisibility(View.VISIBLE);
        
        GridView scriptGrid = new GridView(context);
        scriptGrid.setNumColumns(2);
        scriptGrid.setVerticalSpacing(dp(12));
        scriptGrid.setHorizontalSpacing(dp(12));
        scriptGrid.setPadding(dp(10), dp(10), dp(10), dp(10));
        scriptGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        scriptGrid.setBackgroundColor(Color.parseColor("#1E1E1E")); // background for scripts tab

        final ArrayList<File> scriptFiles = new ArrayList<>();

String pkg = context.getPackageName();
File baseDir = new File("/sdcard/BGFX");
if (!baseDir.exists()) baseDir.mkdirs();

File scriptsDir = new File(baseDir, "Scripts");
File autoexecDir = new File(baseDir, "Autoexec");

scriptsDir.mkdirs();
autoexecDir.mkdirs();

if (scriptsDir.exists() && scriptsDir.isDirectory()) {
    File[] files = scriptsDir.listFiles();
    if (files != null) {
        for (File file : files) {
            if (file.getName().endsWith(".lua") || file.getName().endsWith(".txt")) {
                scriptFiles.add(file);
            }
        }
    }
}

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return scriptFiles.size();
            }

            @Override
            public Object getItem(int i) {
                return scriptFiles.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final File file = scriptFiles.get(position);

                TextView tile = new TextView(context);
                tile.setText(file.getName());
                tile.setTextColor(Color.WHITE);
                tile.setTextSize(14);
                tile.setTypeface(Typeface.MONOSPACE);
                tile.setPadding(dp(20), dp(20), dp(20), dp(20));
                tile.setGravity(Gravity.CENTER);
                tile.setLayoutParams(new GridView.LayoutParams(
                                         ViewGroup.LayoutParams.MATCH_PARENT, dp(100)));

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.parseColor("#3C3C3C"));
                bg.setCornerRadius(dp(45));

                RippleDrawable ripple = new RippleDrawable(
                    ColorStateList.valueOf(Color.parseColor("#40FFFFFF")),
                    bg,
                    null
                );
                tile.setBackground(ripple);

                tile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
                            builder.setTitle("Load Script");
                            builder.setMessage("Do you want to load this script?\n\n" + file.getName());

                            builder.setPositiveButton("Load", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Preferences.changeFeatureString("loadFile", 28, file.getPath());
                                    }
                                });

                            builder.setNeutralButton("Set as Default", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences prefs = context.getSharedPreferences("bgfx.prefs", context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("defaultScript", file.getName());
                                        editor.apply();
                                        Toast.makeText(context, "Now This Script Starts When You Enter A Game", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            builder.setNegativeButton("Cancel", null);
                            //builder.show();
                            AlertDialog dialog = builder.create(); // display the dialog
                            dialog.getWindow().setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                            dialog.show();
                        }
                    });

                return tile;
            }
        };

        scriptGrid.setAdapter(adapter);
        scriptsTabContent.addView(scriptGrid);

        final TextView settingsTabView = new TextView(context);
        settingsTabView.setText("Soon");
        settingsTabView.setTextColor(Color.WHITE);
        ZevText versionLabel = new ZevText(context);
        versionLabel.setText("BETA");
        versionLabel.setTextColor(Color.WHITE);
        versionLabel.setTextSize(16);
        versionLabel.setTypeface(null, Typeface.BOLD);
        versionLabel.setPadding(10, 20, 10, 20);
        settingsTabContent.addView(versionLabel);
        
        

        final TextView consoleTabView = new TextView(context);
        consoleTabView.setText("Soon");
        consoleTabView.setTextColor(Color.WHITE);
        ZevText consolelabel = new ZevText(context);
        consolelabel.setText("BETA");
        consolelabel.setTextColor(Color.WHITE);
        consolelabel.setTextSize(16);
        consolelabel.setTypeface(null, Typeface.BOLD);
        consolelabel.setPadding(10, 20, 10, 20);
        consoleTabContent.addView(consolelabel);
        
        
        
        
final ScrollView scrollView = new ScrollView(context);
final LinearLayout consoleContainer = new LinearLayout(context);
consoleContainer.setOrientation(LinearLayout.VERTICAL);
consoleContainer.setPadding(20, 20, 20, 20);
scrollView.addView(consoleContainer);
consoleTabContent.addView(scrollView);

final Handler handler = new Handler();
final Runnable logReader = new Runnable() {
    String lastContent = "";

    @Override
    public void run() {
        try {
            File logFile = new File("/sdcard/Android/data/com.sandboxol.blockymods.official/files/Download/SandboxOL/BlockMan/config/client.log");
            File saveDir = new File("/sdcard/BGFX");
            File saveFile = new File(saveDir, "bgfx_console.txt");

            StringBuilder contentBuilder = new StringBuilder();
            StringBuilder fileBuilder = new StringBuilder();

            if (logFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                boolean inError = false;
                StringBuilder errorBlock = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    if (line.contains("SCRIPT_EXCEPTION")) {
                        inError = true;
                        errorBlock = new StringBuilder();
                    } else if (inError && (line.trim().isEmpty() || line.matches(".*\\[.+\\]:.*"))) {
                        if (errorBlock.length() > 0) {
                            String cleaned = errorBlock.toString().trim();
                            contentBuilder.append("[ERROR]").append(cleaned).append("\n\n");
                            fileBuilder.append("[ERROR]").append(cleaned).append("\n\n");
                        }
                        inError = false;
                    }

                    if (inError) {
                        errorBlock.append(line).append("\n");
                    }

                    if (line.contains("[INFO]: [Lua]")) {
                        String lower = line.toLowerCase();

                        boolean skip = line.contains("[Init]") ||
                                       line.contains("[ClientInfo]") ||
                                       line.contains("[Info]") ||
                                       line.contains("[Error]") ||
                                       line.contains("GCGame") ||
                                       line.contains("CGame::") ||
                                       lower.contains("fixostime") ||
                                       line.contains("====") ||
                                       line.matches(".*\\b(map|game|engine)\\s+/.*");

                        if (!skip) {
                            int index = line.indexOf("[Lua]");
                            String msg = index != -1 ? line.substring(index + 6).trim() : line.trim();
                            contentBuilder.append("[PRINT] ").append(msg).append("\n\n");
                            fileBuilder.append("[PRINT] ").append(msg).append("\n\n");
                        }
                    }
                }

                if (inError && errorBlock.length() > 0) {
                    String cleaned = errorBlock.toString().trim();
                    contentBuilder.append("[ERROR]").append(cleaned).append("\n\n");
                    fileBuilder.append("[ERROR]").append(cleaned).append("\n\n");
                }

                reader.close();

                final String newContent = contentBuilder.toString();
                if (!newContent.equals(lastContent)) {
                    lastContent = newContent;
                    consoleContainer.removeAllViews();

                    String[] logs = newContent.split("\n\n");
                    for (final String raw : logs) {
                        if (!raw.trim().isEmpty()) {
                            final String display = raw.trim();
                            final String toCopy;
                            final int color;

                            if (display.startsWith("[ERROR]")) {
                                toCopy = display.substring(7).trim();
                                color = Color.RED;
                            } else if (display.startsWith("[PRINT]")) {
                                toCopy = display.substring(7).trim();
                                color = Color.GREEN;
                            } else {
                                continue;
                            }

                            TextView logView = new TextView(context);
                            logView.setText(display);
                            logView.setTextColor(color);
                            logView.setTextSize(14);
                            logView.setPadding(10, 20, 10, 20);
                            logView.setBackgroundColor(Color.argb(50, 255, 255, 255));

                            logView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("", toCopy);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                                }
                            });

                            consoleContainer.addView(logView);
                        }
                    }

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });

                    if (saveDir.exists()) {
                        FileWriter writer = new FileWriter(saveFile, false);
                        writer.write(fileBuilder.toString());
                        writer.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.postDelayed(this, 5000);
    }
};

handler.post(logReader);
        
        
        
        final MaterialSwitch switchDevFly = new MaterialSwitch(context);
        switchDevFly.setText("Quick DevFly");
        switchDevFly.setTextColor(Color.WHITE);
        switchDevFly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.changeFeatureBool("Quick DevFly", 35, switchDevFly.isChecked());
                }
            });
        settingsTabContent.addView(switchDevFly);


        final MaterialSwitch switchSmoothFps = new MaterialSwitch(context);
        switchSmoothFps.setText("Smooth FPS");
        switchSmoothFps.setTextColor(Color.WHITE);
        switchSmoothFps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.changeFeatureBool("Smooth FPS", 20, switchSmoothFps.isChecked());
                }
            });
        settingsTabContent.addView(switchSmoothFps);
        
        
        final MaterialSwitch switchAutoExec = new MaterialSwitch(context);
switchAutoExec.setText("Auto Execute");
switchAutoExec.setTextColor(Color.WHITE);

File settingsFile = new File("/sdcard/BGFX/settings.txt");
if (settingsFile.exists()) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
        String line = reader.readLine();
        reader.close();
        if ("true".equalsIgnoreCase(line)) {
            switchAutoExec.setChecked(true);
        } else {
            switchAutoExec.setChecked(false);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

switchAutoExec.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Preferences.changeFeatureBool("Auto Execute", 90, switchAutoExec.isChecked());
    }
});

settingsTabContent.addView(switchAutoExec);

        final MaterialSwitch switchRgbName = new MaterialSwitch(context);
        switchRgbName.setText("RGB Name");
        switchRgbName.setTextColor(Color.WHITE);
        switchRgbName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.changeFeatureBool("RGB Name", 21, switchRgbName.isChecked());
                }
            });
        settingsTabContent.addView(switchRgbName);

        LinearLayout tabContentLayout = new LinearLayout(context);
        tabContentLayout.setOrientation(LinearLayout.VERTICAL);
        tabContentLayout.setPadding(20, 20, 20, 20);
//        tabContentLayout.addView(codeTabView);
        //tabContentLayout.addView(settingsTabView);
        //tabContentLayout.addView(aboutTabView);
        editorLayout.addView(tabContentLayout);  // 👈 add to editorLayout

        String[] tabs = {"Code", "Scripts", "Settings","Console", "Credits", "Close"};
        for (String tab : tabs) {
            final String tabName = tab;

            TextView tabItem = new ZevText(context);
            tabItem.setText(tab);
            tabItem.setTextColor(Color.WHITE);
            tabItem.setPadding(10, 10, 10, 10);
            tabItem.setTextSize(16);
            tabItem.setGravity(Gravity.CENTER);

            // Add gap between tabs
            LinearLayout.LayoutParams tabItemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tabItemParams.setMargins(0, dp(10), 0, dp(10));
            tabItem.setLayoutParams(tabItemParams);

            if (tab.equals("Close")) {
                tabItem.setTextColor(Color.RED);
                tabItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            root.setVisibility(View.GONE);
                            onCloseClickListener.onClose();
                        }
                    });
            } else {
                tabItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            codeTabContent.setVisibility(tabName.equals("Code") ? View.VISIBLE : View.GONE);
                            scriptsTabContent.setVisibility(tabName.equals("Scripts") ? View.VISIBLE : View.GONE);
                            settingsTabContent.setVisibility(tabName.equals("Settings") ? View.VISIBLE : View.GONE);
                        consoleTabContent.setVisibility(tabName.equals("Console") ? View.VISIBLE : View.GONE);
                            aboutTabContent.setVisibility(tabName.equals("Credits") ? View.VISIBLE : View.GONE);
                        }
                    });
            }

            tabLayout.addView(tabItem);
        }
        
        TextView tvScripts = new TextView(context);
        tvScripts.setText("Scripts");
        tvScripts.setTextColor(Color.WHITE);
        scriptsTabContent.addView(tvScripts);

        TextView tvSettings = new TextView(context);
        tvSettings.setText("Settings");
        tvSettings.setTextColor(Color.WHITE);



        TextView tvconsole = new TextView(context);
        tvconsole.setText("Console");
        tvconsole.setTextColor(Color.WHITE);


        TextView tvAbout = new TextView(context);
        tvAbout.setText("Credits");
        tvAbout.setTextColor(Color.WHITE);
        //aboutTabContent.addView(tvAbout);

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams editorParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
        editorParams.setMargins(dp(20), dp(20), dp(10), dp(20));
        mainLayout.addView(editorLayout, editorParams);
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(dp(80), ViewGroup.LayoutParams.MATCH_PARENT);
        tabParams.setMargins(dp(10), dp(20), dp(20), dp(20));
        mainLayout.addView(tabLayout, tabParams);
        
        root.addView(mainLayout, new LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT
                     ));
    }

    private void updateLineCount() {
        String text = codeEditor.getText().toString();
        int lines = text.split("\n", -1).length;
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            builder.append(i).append("\n");
        }
        lineCounter.setText(builder.toString());
    }

    private void highlightSyntax() {
        String src = codeEditor.getText().toString();
        Editable editable = codeEditor.getText();

        android.text.style.CharacterStyle[] spans = editable.getSpans(0, editable.length(), android.text.style.CharacterStyle.class);
        for (android.text.style.CharacterStyle span : spans) {
            editable.removeSpan(span);
        }

        String[] keywords = {"if", "then", "else", "elseif", "end", "while", "do", "for", "in", "repeat", "until", "goto"};
        applySpan(editable, src, keywords, "#80CBC4", false); // teal-blue

        String[] globals = {"true", "false", "nil", "not", "and", "or", "local", "return", "function"};
        applySpan(editable, src, globals, "#C792EA", false); // purple

        String[] functions = {"print", "pairs", "ipairs", "type", "tonumber", "tostring", "error", "pcall", "xpcall", "next", "rawset", "rawget"};
        applySpan(editable, src, functions, "#FFCB6B", false); // yellow-orange

        // Numbers (highlight all numeric constants)
        Matcher nm = Pattern.compile("\\b\\d+(\\.\\d+)?\\b").matcher(src);
        while (nm.find()) {
            editable.setSpan(
                new android.text.style.ForegroundColorSpan(Color.parseColor("#89DDFF")), // light blue
                nm.start(), nm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Single-line comments
        Matcher cm = Pattern.compile("--[^\n]*").matcher(src);
        while (cm.find()) {
            editable.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#6A9955")), cm.start(), cm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editable.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), cm.start(), cm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Multiline comments
        Matcher mm = Pattern.compile("--\\[\\[[\\s\\S]*?]]").matcher(src);
        while (mm.find()) {
            editable.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#6A9955")), mm.start(), mm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editable.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), mm.start(), mm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Strings (single or double quoted)
        Matcher sm = Pattern.compile("\"(.*?)\"|'(.*?)'").matcher(src);
        while (sm.find()) {
            editable.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#CE9178")), sm.start(), sm.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void applySpan(Editable editable, String src, String[] words, String color, boolean italic) {
        for (String word : words) {
            Matcher matcher = Pattern.compile("\\b" + word + "\\b").matcher(src);
            while (matcher.find()) {
                editable.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor(color)), matcher.start(), matcher.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (italic) {
                    editable.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), matcher.start(), matcher.end(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private void handleAutoPairing(final Editable s, final int cursorPos) {
    }

    private void showSuggestions(CharSequence s, final int cursorPos) {
        if (cursorPos <= 0) return;

        String beforeCursor = s.subSequence(0, cursorPos).toString();
        String[] words = beforeCursor.split("\\W+");
        if (words.length == 0) return;

        final String current = words[words.length - 1];

        // Combine predefined completions and local variables
        ArrayList<String> completionsList = new ArrayList<>();
        for (int i = 0; i < completions.length; i++) {
            completionsList.add(completions[i]);
        }

        // Find local variable definitions before the cursor
        java.util.regex.Pattern localPattern = java.util.regex.Pattern.compile("\\blocal\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
        java.util.regex.Matcher matcher = localPattern.matcher(beforeCursor);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (!completionsList.contains(varName)) {
                completionsList.add(varName);
            }
        }

        // Match current word
        final ArrayList<String> matches = new ArrayList<>();
        for (int i = 0; i < completionsList.size(); i++) {
            String kw = completionsList.get(i);
            if (kw.startsWith(current) && !kw.equals(current)) {
                matches.add(kw);
            }
        }

        if (matches.isEmpty()) {
            if (suggestionPopup != null && suggestionPopup.isShowing()) {
                suggestionPopup.dismiss();
            }
            return;
        }

        ListView listView = new ListView(context);
        listView.setBackgroundColor(Color.parseColor("#333333"));
        listView.setDivider(null);
        listView.setPadding(8, 8, 8, 8);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, matches) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(14);
                tv.setPadding(20, 10, 20, 10);
                tv.setBackgroundColor(Color.parseColor("#444444"));
                return tv;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
                    String selected = matches.get(position);
                    Editable edit = codeEditor.getText();

                    int start = cursorPos;
                    while (start > 0 && Character.isLetterOrDigit(edit.charAt(start - 1))) {
                        start--;
                    }

                    int end = cursorPos;
                    while (end < edit.length() && Character.isLetterOrDigit(edit.charAt(end))) {
                        end++;
                    }

                    edit.replace(start, end, selected);
                    codeEditor.setSelection(start + selected.length());

                    if (suggestionPopup != null) suggestionPopup.dismiss();
                }
            });

        if (suggestionPopup != null) suggestionPopup.dismiss();

        suggestionPopup = new PopupWindow(listView, dp(120), dp(100), false);
        suggestionPopup.setBackgroundDrawable(new GradientDrawable());
        suggestionPopup.setOutsideTouchable(true);
        suggestionPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        suggestionPopup.showAsDropDown(codeEditor);
    }

    private int dp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    public View getView() {
        return root;
    }
    
    public void setOnCloseListener(onCloseListener listener) {onCloseClickListener = listener;}
    
    public interface onCloseListener {
        public void onClose();
    }
}

