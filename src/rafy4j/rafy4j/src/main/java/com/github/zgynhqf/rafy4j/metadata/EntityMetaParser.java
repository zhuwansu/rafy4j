package com.github.zgynhqf.rafy4j.metadata;

import com.github.zgynhqf.rafy4j.annotation.MappingColumn;
import com.github.zgynhqf.rafy4j.annotation.MappingTable;
import com.github.zgynhqf.rafy4j.utils.AnnotationHelper;
import com.github.zgynhqf.rafy4j.utils.NameUtils;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 实体元数据的解析器。
 *
 * @author: huqingfang
 * @date: 2018-12-30 22:09
 **/
public class EntityMetaParser {
    private boolean mapCamelToUnderline = true;

    //region properties
    public boolean isMapCamelToUnderline() {
        return mapCamelToUnderline;
    }

    public void setMapCamelToUnderline(boolean mapCamelToUnderline) {
        this.mapCamelToUnderline = mapCamelToUnderline;
    }
    //endregion

    public EntityMeta parse(Class<?> type) {
        EntityMeta meta = new EntityMeta();

        MappingTable annotation = AnnotationHelper.findAnnotation(type, MappingTable.class);

        meta.setTableName(annotation.name());

        //默认映射的表名。
        if (StringUtils.isBlank(meta.getTableName())) {
            String name = type.getSimpleName();
            if (mapCamelToUnderline) {
                name = NameUtils.camelToUnderline(name);
            }
            meta.setTableName(name);
        }

        meta.setMapAllFieldsToColumn(annotation.mapAllFieldsToColumn());

        parseProperties(meta, type);

        return meta;
    }

    private void parseProperties(EntityMeta meta, Class<?> type) {
        List<Field> fields = TypeHelper.getMembers(type, t -> t.getDeclaredFields());
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            EntityFieldMeta fieldMeta = new EntityFieldMeta();
            fieldMeta.setField(field);

            MappingColumn columnAnnotation = AnnotationHelper.findAnnotation(field, MappingColumn.class);
            com.github.zgynhqf.rafy4j.annotation.IgnoreColumn ignoreColumnAnnotation = AnnotationHelper.findAnnotation(field, com.github.zgynhqf.rafy4j.annotation.IgnoreColumn.class);
            boolean isMappingColumn = (columnAnnotation != null || meta.isMapAllFieldsToColumn()) && ignoreColumnAnnotation == null;
            if (isMappingColumn) {
                if (columnAnnotation != null) {
                    fieldMeta.setColumnName(columnAnnotation.name());
                    fieldMeta.setColumnLength(columnAnnotation.length());
//                    fieldMeta.setColumnType(columnAnnotation.type());
//                    fieldMeta.setIsNullable(columnAnnotation.isNullable());
//                    fieldMeta.setIsPrimaryKey(columnAnnotation.isKey());
//                    fieldMeta.setIsAutoIncrement(columnAnnotation.isAutoIncrement());
                    fieldMeta.setDefaultValue(columnAnnotation.defaultValue());
                }

                if (StringUtils.isBlank(fieldMeta.getColumnName())) {
                    String name = field.getName();
                    if (mapCamelToUnderline) {
                        name = NameUtils.camelToUnderline(name);
                    }
                    fieldMeta.setColumnName(name);
                }
            }

            meta.getFields().add(fieldMeta);
        }
    }
}