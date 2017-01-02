package com.silver.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Database(name = WidgetDatabase.NAME, version = WidgetDatabase.VERSION)

public class WidgetDatabase {

    public static final String NAME = "Widgets2";

    static final int VERSION = 2;

    @Migration(version = 2, database = WidgetDatabase.class)
    public static class Migration2 extends AlterTableMigration<Widget> {
        public Migration2(Class<Widget> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "guid");
        }
    }
}
