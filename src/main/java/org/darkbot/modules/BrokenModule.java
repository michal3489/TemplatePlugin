package org.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.extensions.features.Feature;
import org.darkbot.VerifierChecker;

@Feature(name = "Broken module", description = "Module that is broken, just to show an error")
public class BrokenModule implements Module {

    @Override
    public void install(Main main) {
        if (!VerifierChecker.getAuthApi().isAuthenticated()) VerifierChecker.getAuthApi().setupAuth();

        throw new UnsupportedOperationException("Oopsie");
    }

    @Override
    public boolean canRefresh() {
        return true;
    }

    @Override
    public String status() {
        return "Broken module";
    }

    @Override
    public void tick() {
        // Literally doing nothing.
    }

}
