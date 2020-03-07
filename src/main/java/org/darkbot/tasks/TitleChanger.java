package org.darkbot.tasks;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.BackpageManager;
import com.github.manolo8.darkbot.config.types.Editor;
import com.github.manolo8.darkbot.config.types.Num;
import com.github.manolo8.darkbot.config.types.Option;
import com.github.manolo8.darkbot.core.itf.Configurable;
import com.github.manolo8.darkbot.core.itf.Task;
import com.github.manolo8.darkbot.core.utils.Lazy;
import com.github.manolo8.darkbot.extensions.features.Feature;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Feature(name = "Title Changer", description = "Random title changer")
public class TitleChanger implements Task, Configurable<TitleChanger.Config> {
    private static final Pattern RELOAD_PATTERN = Pattern.compile("reloadToken\\\\\" value=\\\\\"([^\"]*)");
    private static final Pattern TITLE_PATTERN = Pattern.compile("value=\\\\\"(title[^\"]*)");

    private static final String GET_TITLES_PARAMS = "command=getAchievementPage&action=showTitles";
    private static final String SET_TITLES_PARAMS = "reloadToken={0}&radioUserTitle={1}";

    private Config config;
    private BackpageManager backpage;
    private long titleUpdatedUntil;

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void install(Main main) {
        this.backpage = main.backpage;
    }

    @Override
    public void tick() {
        if (titleUpdatedUntil == 0) {
            List<String> list = collectMatches(sendPost("ajax/user.php", GET_TITLES_PARAMS), TITLE_PATTERN);

            if (list.isEmpty()) return;
            titleUpdatedUntil = 1;

            list.add("noTitle");
            config.TITLE_INFO.keySet().retainAll(list);
            list.forEach(this::putTitle);

        } else if (titleUpdatedUntil < (System.currentTimeMillis() - (config.CHANGE_EVERY * 1000))) setTitle();
    }

    private void putTitle(String s) {
        config.TITLE_INFO.put(s, new TitleInfo());
        config.TITLE_MODIFIED.send(s);
    }

    private void setTitle() {
        String input = sendPost("ajax/user.php", GET_TITLES_PARAMS);
        titleUpdatedUntil = System.currentTimeMillis();

        if (input == null || input.isEmpty()) return;

        List<String> titlesToUse = config.TITLE_INFO.entrySet().stream()
                .filter(t -> t.getValue().use)
                .map(Map.Entry::getKey).collect(Collectors.toList());

        if (titlesToUse.size() < 1) return;

        String reloadToken = collectMatches(input, RELOAD_PATTERN).get(0);
        String randomTitle = titlesToUse.get(new Random().nextInt(titlesToUse.size()));

        sendPost("indexInternal.es?action=editTitle", MessageFormat.format(SET_TITLES_PARAMS, reloadToken, randomTitle));
    }

    private List<String> collectMatches(String input, Pattern pattern) {
        Matcher m = pattern.matcher(input);
        List<String> result = new ArrayList<>();
        while (m.find()) result.add(deleteLastChar(m.group(1)));

        return result;
    }

    private String sendPost(String param, String data) {
        try {
            HttpURLConnection conn = backpage.getConnection(param, 500);
            conn.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(data.getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return br.lines().collect(Collectors.joining());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String deleteLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    public static class Config {
        @Option("Try to change title every(sec)")
        @Num(max = 500, step = 1, min = 2)
        public int CHANGE_EVERY = 5;
        @Option()
        @Editor(value = TitleInfoTable.class, shared = true)
        public Map<String, TitleInfo> TITLE_INFO = new HashMap<>();
        public transient Lazy<String> TITLE_MODIFIED = new Lazy<>();
    }
}


