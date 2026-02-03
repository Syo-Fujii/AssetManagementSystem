-- 1. 備品一覧参照ビュー
CREATE OR ALTER VIEW equip_list_view AS 
WITH equip_work AS ( 
    SELECT
        stock_type_master.name AS lending_name
        , COUNT(stock_master.stock_type) AS lending_count
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = 0
        AND stock_data.staff_no IS NOT NULL 
        AND stock_data.return_date IS NULL 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, stock_work AS ( 
    SELECT
        stock_type_master.name AS stock_name
        , COUNT(stock_master.stock_type) AS stock_count
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = 0
        AND stock_data.staff_no IS NULL 
        AND stock_data.return_date IS NULL 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, unknown_work AS ( 
    SELECT
        stock_type_master.name AS unknown_name
        , COUNT(stock_master.stock_type) AS unknown_count
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = 0
        AND DATEDIFF(day, stock_data.confirmed_date, GETDATE()) > 365 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, aggregate_work AS ( 
    SELECT
        stock_type_master.id
        , stock_type_master.name AS 名称
        , CASE 
            WHEN equip_work.lending_count IS NULL THEN 0 
            ELSE equip_work.lending_count 
          END AS 貸出
        , CASE 
            WHEN stock_work.stock_count IS NULL THEN 0 
            ELSE stock_work.stock_count 
          END AS 在庫
        , CASE 
            WHEN unknown_work.unknown_count IS NULL THEN 0 
            ELSE unknown_work.unknown_count 
          END AS 不明
        , COUNT(stock_master.stock_type) AS 合計 
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
        LEFT JOIN equip_work 
            ON equip_work.lending_name = stock_type_master.name 
        LEFT JOIN stock_work 
            ON stock_work.stock_name = stock_type_master.name 
        LEFT JOIN unknown_work 
            ON unknown_work.unknown_name = stock_type_master.name 
    WHERE
        stock_master.del = 0
        AND stock_data.return_date IS NULL 
    GROUP BY
        equip_work.lending_count
        , stock_work.stock_count
        , unknown_work.unknown_count
        , stock_type_master.id
        , stock_type_master.name
        , stock_master.stock_type 
) 
SELECT
    名称
    , 貸出
    , 在庫
    , 不明
    , 合計 
FROM
    aggregate_work;

GO

-- 2. 備品詳細参照ビュー
CREATE OR ALTER VIEW equip_detail_view AS 
SELECT DISTINCT
    stock_type_master.name AS 備品名
    , stock_data.stock_code AS シリアルナンバー
    , staff_data.name AS 使用者
    , CASE 
        -- 日付計算の変更
        WHEN DATEDIFF(day, stock_data.confirmed_date, GETDATE()) > 365 
            THEN '不明' 
        -- bit型の比較
        WHEN stock_master.rent_flg = 1
            THEN '不可' 
        WHEN staff_data.name IS NULL 
            THEN '可' 
        WHEN staff_data.name IS NOT NULL 
            THEN '貸出中' 
        ELSE '不可' 
      END AS 貸出可否
    , stock_data.start_date AS 貸出開始日
    , stock_data.limit_date AS 返却予定日
    , stock_data.confirmed_date AS 最終所在確認日
    , stock_master.remarks AS 備考 
    , stock_data.id AS 備品データID
    , staff_data.staff_no AS 社員番号
FROM
    stock_type_master 
    INNER JOIN stock_type_data 
        ON stock_type_master.stock_type = stock_type_data.stock_type 
    INNER JOIN stock_master 
        ON stock_type_master.stock_type = stock_master.stock_type 
    LEFT JOIN stock_data 
        ON stock_master.stock_code = stock_data.stock_code 
    LEFT JOIN staff_data 
        ON stock_data.staff_no = staff_data.staff_no 
WHERE
    stock_master.del = 0 
    AND stock_data.del = 0 
    AND stock_data.return_date IS NULL;
GO