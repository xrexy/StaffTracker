package me.xrexy.stafftracker.files;

import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.xrexy.stafftracker.utils.CustomDate;

public class DailyConfig {
    private final Json json;
    private final CustomDate date;

    public DailyConfig(CustomDate date) {
        this.date = date;
        this.json = LightningBuilder.fromPath(date.getFormatted(), "plugins/StaffTracker/data")
                .setDataType(DataType.UNSORTED)
                .setReloadSettings(ReloadSettings.INTELLIGENT)
                .createJson();
    }

    public Json getJson() {
        return json;
    }

    public CustomDate getDate() {
        return date;
    }
}
