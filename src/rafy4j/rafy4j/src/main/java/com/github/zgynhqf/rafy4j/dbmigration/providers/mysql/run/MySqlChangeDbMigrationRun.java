package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.run;

import com.github.zgynhqf.rafy4j.data.DbAccessor;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

/**
 * MySql改变当前使用的数据库
 */
public final class MySqlChangeDbMigrationRun extends MigrationRun {
    /**
     * 获取或者设置当前要操作的数据库
     */
    private String database;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String value) {
        database = value;
    }

    /**
     * 运行修改当前运行的数据库操作
     *
     * @param db 数据库访问对象
     */
    @Override
    protected void runCore(DbAccessor db) {
        db.executeText("USE " + this.getDatabase() + ";");
    }
}