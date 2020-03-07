package org.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.NpcExtraFlag;
import com.github.manolo8.darkbot.config.types.Editor;
import com.github.manolo8.darkbot.config.types.Num;
import com.github.manolo8.darkbot.config.types.Option;
import com.github.manolo8.darkbot.core.itf.Configurable;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.itf.NpcExtraProvider;
import com.github.manolo8.darkbot.extensions.features.Feature;
import com.github.manolo8.darkbot.gui.tree.components.JPercentField;
import org.darkbot.VerifierChecker;

@Feature(name = "Sample module", description = "Module that does nothing, just to show how to create a module")
public class SampleModule implements Module, Configurable<SampleModule.SampleConfig>, NpcExtraProvider {

    private SampleConfig config;
    private Main main;

    @Override
    public void install(Main main) {
        if (!VerifierChecker.getAuthApi().isAuthenticated()) VerifierChecker.getAuthApi().setupAuth();

        this.main = main;
    }

    @Override
    public void setConfig(SampleConfig config) {
        this.config = config;
    }

    @Override
    public boolean canRefresh() {
        return true;
    }

    @Override
    public String status() {
        return "Sample module - Not doing anything: " + config.MUCH_CONFIG;
    }

    @Override
    public void tick() {
        // Literally doing nothing.

        if (config.RANDOM_CONFIG) config.CONFIG_INDEED = 0.9;

        System.out.println(main.statsManager.credits);
    }

    @Override
    public NpcExtraFlag[] values() {
        return Extra.values();
    }

    private enum Extra implements NpcExtraFlag {
        WEIRD_FLAG("WF", "Weird flag", "Some weird testing flag provided by sample module");

        private final String shortName, name, description;

        Extra(String shortName, String name, String description) {
            this.shortName = shortName;
            this.name = name;
            this.description = description;
        }

        @Override
        public String getShortName() {
            return shortName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    public static class SampleConfig {
        @Option("Some random config")
        public boolean RANDOM_CONFIG = true;
        @Option("How much config?")
        @Num(min = 1, max = 999)
        public int MUCH_CONFIG = 1;
        @Option("Very config indeed")
        @Editor(JPercentField.class)
        public double CONFIG_INDEED = 0.5;
    }

}
