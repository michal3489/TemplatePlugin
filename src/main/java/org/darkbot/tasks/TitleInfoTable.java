package org.darkbot.tasks;

import com.github.manolo8.darkbot.gui.tree.OptionEditor;
import com.github.manolo8.darkbot.gui.tree.components.InfoTable;
import com.github.manolo8.darkbot.gui.utils.GenericTableModel;

public class TitleInfoTable extends InfoTable<GenericTableModel, TitleInfo> implements OptionEditor {
    public TitleInfoTable(TitleChanger.Config config) {
        super(TitleInfo.class, config.TITLE_INFO, config.TITLE_MODIFIED, null);

        getComponent().remove(0);
    }
}
