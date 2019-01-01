package com.github.zgynhqf.rafy4j.rafy4jtest;

import com.github.zgynhqf.rafy4j.ClassMetaReader;
import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.DataLossOperation;
import com.github.zgynhqf.rafy4j.dbmigration.DbMigrationContext;
import com.github.zgynhqf.rafy4j.dbmigration.model.Database;
import com.github.zgynhqf.rafy4j.dbmigration.model.DestinationDatabase;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;
import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import com.github.zgynhqf.rafy4j.rafy4jtest.config.TestConfig;
import com.github.zgynhqf.rafy4j.rafy4jtest.entity.User;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.JDBCType;
import java.util.List;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rafy4jTestApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void useDestinationDatabase() throws IOException {
        DbSetting dbSetting = RafyEnvironment.getDbSettingRepository().FindOrCreate(TestConfig.MAIN_DBSETTING);
        try (DbMigrationContext context = new DbMigrationContext(dbSetting)) {
            context.setRunDataLossOperation(DataLossOperation.All);

            DestinationDatabase destination = new DestinationDatabase(TestConfig.MAIN_DBSETTING) {{
                List<Table> tables = getTables();
                tables.add(new Table("newTable", this) {{
                    this.AddColumn("id", JDBCType.INTEGER, "4", true, true, null);
                }});
            }};

            context.MigrateTo(destination);
        }
    }

    @Test
    public void useClassMetaReader() throws IOException {
        Test(database -> {
        }, database -> {
        });
    }

    private void Test(Consumer<Database> action, Consumer<Database> assertAction) throws IOException {
        DbSetting dbSetting = RafyEnvironment.getDbSettingRepository().FindOrCreate(TestConfig.MAIN_DBSETTING);
        try (DbMigrationContext context = new DbMigrationContext(dbSetting)) {
//            context.HistoryRepository = new DbHistoryRepository();
            context.setRunDataLossOperation(DataLossOperation.All);

            ClassMetaReader classMetaReader = new ClassMetaReader(User.class.getPackage().getName(), dbSetting);
            DestinationDatabase destination = classMetaReader.Read();

            action.accept(destination);

            try {
                context.MigrateTo(destination);

                var result = context.getDatabaseMetaReader().Read();
                assertAction.accept(result);
            } finally {
            }
        }
    }

}
