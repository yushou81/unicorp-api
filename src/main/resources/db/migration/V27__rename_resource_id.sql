SET @dbname = DATABASE();
SET @tablename = "resources";
SET @old_columnname = "id";
SET @new_columnname = "resource_id";
SET @column_type = "VARCHAR(255)";
SET @column_comment = "资源ID";
SET @preparedStatement = NULL;

-- 检查旧列 'id' 是否存在
SELECT IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE
            (TABLE_SCHEMA = @dbname)
            AND (TABLE_NAME = @tablename)
            AND (COLUMN_NAME = @old_columnname)
    ) > 0,
    -- 如果 'id' 存在，则继续重命名或修改
    (SELECT IF(
        -- 检查 'resource_id' 是否已经存在
        (
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE
                (TABLE_SCHEMA = @dbname)
                AND (TABLE_NAME = @tablename)
                AND (COLUMN_NAME = @new_columnname)
        ) > 0,
        -- 如果 'resource_id' 存在，只需修改其类型和注释（如果需要）
        CONCAT("ALTER TABLE ", @tablename, " MODIFY COLUMN ", @new_columnname, " ", @column_type, " COMMENT '", @column_comment, "';"),
        -- 如果 'resource_id' 不存在，将 'id' 重命名为 'resource_id' 并设置其类型/注释
        CONCAT("ALTER TABLE ", @tablename, " CHANGE COLUMN ", @old_columnname, " ", @new_columnname, " ", @column_type, " COMMENT '", @column_comment, "';")
    )),
    -- 如果 'id' 不存在，则不执行任何操作或记录错误/日志
    "SELECT 'Column id does not exist. No action taken to rename.'"
) INTO @preparedStatement;

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;